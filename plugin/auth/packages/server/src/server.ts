import {
    CancellationToken,
	createConnection,
	ProposedFeatures,
} from 'vscode-languageserver/node'

import {
    SsoTokenChangedKind,
    SsoTokenChangedNotificationType,
    SsoTokenChangedParams
} from '@tkc-auth/common/dist/ssoTokenChanged'

import {
    GetSsoTokenRequestType,
    GetSsoTokenParams,
    GetSsoTokenResult,
    SsoTokenSourceKind,
    GetSsoTokenError,
    IamIdentityCenterSsoTokenSource,
    SsoTokenId
} from '@tkc-auth/common/dist/getSsoToken'

import {
    ListProfilesRequestType,
    ListProfilesParams,
    ListProfilesResult,
    ProfileKind,
    ListProfilesError
} from '@tkc-auth/common/dist/listProfiles'

import {
    InvalidateSsoTokenRequestType,
    InvalidateSsoTokenParams,
    InvalidateSsoTokenResult,
    InvalidateSsoTokenError
} from '@tkc-auth/common/dist/invalidateSsoToken'

import { loadSsoSessionData, parseKnownFiles, SSOToken } from '@smithy/shared-ini-file-loader'
import { unlink, writeFile } from 'fs/promises'
import { SSOOIDC } from '@aws-sdk/client-sso-oidc'
import { createHash, randomBytes, randomUUID } from 'crypto'
import * as http from 'node:http'
import * as os from 'node:os'
import path from 'node:path'
import fs from 'node:fs'

const AwsBuilderIdUrl = 'https://view.awsapps.com/start'
const AwsBuilderIdRegion = 'us-east-1'

const connection = createConnection(ProposedFeatures.all)

connection.onInitialize(() => {
	return {
		capabilities: {}
	}
})

connection.onRequest<ListProfilesParams, ListProfilesResult, void, ListProfilesError, void>(new ListProfilesRequestType(), async (params: ListProfilesParams, token: CancellationToken) => {
	token.onCancellationRequested(() => {
		connection.window.showInformationMessage('CANCELLED!! WOO HOO!!!');
	})
    const profiles = await parseKnownFiles({})
    const ssoSessions = await loadSsoSessionData()
    const result: ListProfilesResult = { profiles: [], ssoSessions: [] }

    for (const [profileName, profile] of Object.entries(profiles)) {
        const ssoSessionName = profile['sso_session']

        if (!ssoSessionName || profile['sso_account_id'] || profile['sso_role_name']) {
            continue
        }

        const ssoSession = ssoSessions[ssoSessionName]

        if (!ssoSession || !ssoSession['sso_start_url'] || !ssoSession['sso_region'] || !ssoSession['sso_registration_scopes']) {
            continue
        }

        result.profiles!.push({ 
            kind: ProfileKind.SsoToken,
            name: profileName, 
            region: profile.region,
            ssoSessionName
        })
      
        result.ssoSessions?.push({
            name: ssoSessionName,
            ssoStartUrl: ssoSession['sso_start_url'],
            ssoRegion: ssoSession['sso_region'],
            ssoRegistrationScopes: ssoSession['sso_registration_scopes'].split(',')
        })
    }
    //throw new ResponseError<ListProfilesError>(66, "some message", { whatever: "blah", guess: 10 })
	return result
})

const getCacheFilePath = (id: SsoTokenId) => path.join(os.homedir(), '.aws', 'sso', 'cache', id + '.json')

connection.onRequest<GetSsoTokenParams, GetSsoTokenResult, void, GetSsoTokenError, void>(new GetSsoTokenRequestType(), async (params: GetSsoTokenParams, token: CancellationToken) => {
	token.onCancellationRequested(() => {
		connection.window.showInformationMessage('CANCELLED!! WOO HOO!!!');
	})

    let idcSource: IamIdentityCenterSsoTokenSource

    // Build up profile object
    switch (params.source.kind) {
        case SsoTokenSourceKind.IamIdentityCenter:
            idcSource = params.source
            break
        case SsoTokenSourceKind.AwsBuilderId:
            idcSource = {
                kind: SsoTokenSourceKind.IamIdentityCenter,
                clientName: params.source.clientName,
                issuerUrl: AwsBuilderIdUrl,
                region: AwsBuilderIdRegion
            }
            break
        default:
            throw new Error('Unsupported source')
    }

    // https://code.amazon.com/packages/AwsDrSeps/blobs/main/--/seps/accepted/shared/sso-login-flow.md
    // https://code.amazon.com/packages/AwsDrSeps/blobs/main/--/seps/accepted/shared/token-providers.md
    // https://quip-amazon.com/SlOfAat54xwt/Tinkerbell-Developer-Guide

    const ssoTokenId: string = createHash('sha1').update(JSON.stringify({
        region: idcSource.region,
        startUrl: idcSource.issuerUrl,
        tool: idcSource.clientName
    })).digest('hex')

    const cacheFilepath: string = getCacheFilePath(ssoTokenId)
    
    if (fs.existsSync(cacheFilepath)) {
        const tokenFromCache = JSON.parse(fs.readFileSync(cacheFilepath, 'utf-8'))

        // TODO Check validity and refresh if needed
        return {
            ssoToken: {
                id: ssoTokenId,
                accessToken: tokenFromCache.accessToken
            }
        }
    } else if (!params.options?.loginOnInvalidToken) {
        return {
            ssoToken: undefined
        }
    }

    const oidc = new SSOOIDC({ region: idcSource.region })

    // Attempt to retrieve OIDC client; otherwise create new one and cache
    // https://docs.aws.amazon.com/singlesignon/latest/OIDCAPIReference/API_RegisterClient.html
    // TODO Need OIDC client details provided by client.ts through API somehow
    const registerClientResponse = await oidc.registerClient({
        clientName: idcSource.clientName,
        clientType: 'public',
        grantTypes: ['authorization_code', 'refresh_token'],
        issuerUrl: idcSource.issuerUrl,
        redirectUris: ['http://127.0.0.1:8000/oauth/callback'],
        scopes: ['codewhisperer:conversations', 'codewhisperer:transformations', 'codewhisperer:taskassist', 'codewhisperer:completions', 'codewhisperer:analysis']
    })

    // Generate code_verifier, code_challenge, and CSRF state
    const csrfState = randomUUID()
    const codeVerifier = randomBytes(32).toString('base64url')
    const codeChallenge = createHash('sha256').update(codeVerifier).digest().toString('base64url')    

    // Launch local web server; use port 0 for arbitrary port selection
    // Should be capable of handling http://127.0.0.1:8000?code=${IDC_AUTHORIZATION_CODE}&state=${CSRF_PREVENTION_STRING}
    // and either redirecting to error or continue with login
    // Return LSP error at this point if user cancels or fails login process
    let authorizationCode: string | undefined = undefined

    const authed = new Promise((resolve) => {
        const httpServer = http.createServer(async (req, res) => {
            authorizationCode = new URL(req.url!, 'http://127.0.0.1:8000').searchParams.get('code')!

            res.writeHead(200, { 'Content-Type': 'text/plain' })
            res.end('Authed')

            httpServer.close()
            resolve({})
        }).listen(8000, '127.0.0.1')
    })

    // Call OIDC Authorize API; how to get OIDC endpoint?
    // https://quip-amazon.com/SlOfAat54xwt/Tinkerbell-Developer-Guide#temp:C:BKF5d4a3b00a74440fd98e53be8a
    const authorizeUrl = new URL(`https://oidc.${await oidc.config.region()}.amazonaws.com/authorize`)
    authorizeUrl.searchParams.append('response_type', 'code')
    authorizeUrl.searchParams.append('client_id', registerClientResponse.clientId!)
    authorizeUrl.searchParams.append('redirect_uri', 'http://127.0.0.1:8000/oauth/callback')
    authorizeUrl.searchParams.append('scopes', 'codewhisperer:conversations codewhisperer:transformations codewhisperer:taskassist codewhisperer:completions codewhisperer:analysis')
    authorizeUrl.searchParams.append('state', csrfState)
    authorizeUrl.searchParams.append('code_challenge', codeChallenge)
    authorizeUrl.searchParams.append('code_challenge_method', 'S256')

    connection.window.showDocument({
        uri: authorizeUrl.toString(),
        external: true
    })

    await authed

    // Call CreateToken API
    const res = await oidc.createToken({
        clientId: registerClientResponse.clientId,
        clientSecret: registerClientResponse.clientSecret,
        grantType: 'authorization_code',
        redirectUri: 'http://127.0.0.1:8000/oauth/callback',
        codeVerifier,
        code: authorizationCode,
    })

    const ssoToken: SSOToken = {
        accessToken: res.accessToken!,
        expiresAt: new Date(Date.now() + res.expiresIn! * 1000).toISOString(),
        clientId: registerClientResponse.clientId,
        clientSecret: registerClientResponse.clientSecret,
        refreshToken: res.refreshToken,
        region: idcSource.region,
        startUrl: idcSource.issuerUrl
    }

    // Write SSO token cache file
    const tokenString = JSON.stringify(ssoToken, null, 2);
    writeFile(cacheFilepath, tokenString);    

    // TODO Setup timers for expiration and handling refresh

    return {
        ssoToken: {
            id: ssoTokenId,
            accessToken: ssoToken!.accessToken
        }
    }
})

connection.onRequest<InvalidateSsoTokenParams, InvalidateSsoTokenResult, void, InvalidateSsoTokenError, void>
    (new InvalidateSsoTokenRequestType(), async (params: InvalidateSsoTokenParams, token: CancellationToken) => {
	token.onCancellationRequested(() => {
		connection.window.showInformationMessage('CANCELLED!! WOO HOO!!!');
	})

    // TODO How was the token file name encoded and where can those values be obtained?

    const cacheFilepath: string = getCacheFilePath(params.ssoTokenId)
    if (fs.existsSync(cacheFilepath)) {
        await unlink(cacheFilepath)
    }

    await connection.sendNotification<SsoTokenChangedParams, void>(new SsoTokenChangedNotificationType(), { ssoTokenId: params.ssoTokenId, kind: SsoTokenChangedKind.Invalidated })

    return {}
})

connection.listen();