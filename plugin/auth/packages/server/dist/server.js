"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const node_1 = require("vscode-languageserver/node");
const ssoTokenChanged_1 = require("@tkc-auth/common/dist/ssoTokenChanged");
const getSsoToken_1 = require("@tkc-auth/common/dist/getSsoToken");
const listProfiles_1 = require("@tkc-auth/common/dist/listProfiles");
const invalidateSsoToken_1 = require("@tkc-auth/common/dist/invalidateSsoToken");
const shared_ini_file_loader_1 = require("@smithy/shared-ini-file-loader");
const promises_1 = require("fs/promises");
const client_sso_oidc_1 = require("@aws-sdk/client-sso-oidc");
const crypto_1 = require("crypto");
const http = __importStar(require("node:http"));
const os = __importStar(require("node:os"));
const node_path_1 = __importDefault(require("node:path"));
const node_fs_1 = __importDefault(require("node:fs"));
const AwsBuilderIdUrl = 'https://view.awsapps.com/start';
const AwsBuilderIdRegion = 'us-east-1';
const connection = (0, node_1.createConnection)(node_1.ProposedFeatures.all);
connection.onInitialize(() => {
    return {
        capabilities: {}
    };
});
connection.onRequest(new listProfiles_1.ListProfilesRequestType(), async (params, token) => {
    token.onCancellationRequested(() => {
        connection.window.showInformationMessage('CANCELLED!! WOO HOO!!!');
    });
    const profiles = await (0, shared_ini_file_loader_1.parseKnownFiles)({});
    const ssoSessions = await (0, shared_ini_file_loader_1.loadSsoSessionData)();
    const result = { profiles: [], ssoSessions: [] };
    for (const [profileName, profile] of Object.entries(profiles)) {
        const ssoSessionName = profile['sso_session'];
        if (!ssoSessionName || profile['sso_account_id'] || profile['sso_role_name']) {
            continue;
        }
        const ssoSession = ssoSessions[ssoSessionName];
        if (!ssoSession || !ssoSession['sso_start_url'] || !ssoSession['sso_region'] || !ssoSession['sso_registration_scopes']) {
            continue;
        }
        result.profiles.push({
            kind: listProfiles_1.ProfileKind.SsoToken,
            name: profileName,
            region: profile.region,
            ssoSessionName
        });
        result.ssoSessions?.push({
            name: ssoSessionName,
            ssoStartUrl: ssoSession['sso_start_url'],
            ssoRegion: ssoSession['sso_region'],
            ssoRegistrationScopes: ssoSession['sso_registration_scopes'].split(',')
        });
    }
    //throw new ResponseError<ListProfilesError>(66, "some message", { whatever: "blah", guess: 10 })
    return result;
});
const getCacheFilePath = (id) => node_path_1.default.join(os.homedir(), '.aws', 'sso', 'cache', id + '.json');
connection.onRequest(new getSsoToken_1.GetSsoTokenRequestType(), async (params, token) => {
    token.onCancellationRequested(() => {
        connection.window.showInformationMessage('CANCELLED!! WOO HOO!!!');
    });
    let idcSource;
    // Build up profile object
    switch (params.source.kind) {
        case getSsoToken_1.SsoTokenSourceKind.IamIdentityCenter:
            idcSource = params.source;
            break;
        case getSsoToken_1.SsoTokenSourceKind.AwsBuilderId:
            idcSource = {
                kind: getSsoToken_1.SsoTokenSourceKind.IamIdentityCenter,
                clientName: params.source.clientName,
                issuerUrl: AwsBuilderIdUrl,
                region: AwsBuilderIdRegion
            };
            break;
        default:
            throw new Error('Unsupported source');
    }
    // https://code.amazon.com/packages/AwsDrSeps/blobs/main/--/seps/accepted/shared/sso-login-flow.md
    // https://code.amazon.com/packages/AwsDrSeps/blobs/main/--/seps/accepted/shared/token-providers.md
    // https://quip-amazon.com/SlOfAat54xwt/Tinkerbell-Developer-Guide
    const ssoTokenId = (0, crypto_1.createHash)('sha1').update(JSON.stringify({
        region: idcSource.region,
        startUrl: idcSource.issuerUrl,
        tool: idcSource.clientName
    })).digest('hex');
    const cacheFilepath = getCacheFilePath(ssoTokenId);
    if (node_fs_1.default.existsSync(cacheFilepath)) {
        const tokenFromCache = JSON.parse(node_fs_1.default.readFileSync(cacheFilepath, 'utf-8'));
        // TODO Check validity and refresh if needed
        return {
            ssoToken: {
                id: ssoTokenId,
                accessToken: tokenFromCache.accessToken
            }
        };
    }
    else if (!params.options?.loginOnInvalidToken) {
        return {
            ssoToken: undefined
        };
    }
    const oidc = new client_sso_oidc_1.SSOOIDC({ region: idcSource.region });
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
    });
    // Generate code_verifier, code_challenge, and CSRF state
    const csrfState = (0, crypto_1.randomUUID)();
    const codeVerifier = (0, crypto_1.randomBytes)(32).toString('base64url');
    const codeChallenge = (0, crypto_1.createHash)('sha256').update(codeVerifier).digest().toString('base64url');
    // Launch local web server; use port 0 for arbitrary port selection
    // Should be capable of handling http://127.0.0.1:8000?code=${IDC_AUTHORIZATION_CODE}&state=${CSRF_PREVENTION_STRING}
    // and either redirecting to error or continue with login
    // Return LSP error at this point if user cancels or fails login process
    let authorizationCode = undefined;
    const authed = new Promise((resolve) => {
        const httpServer = http.createServer(async (req, res) => {
            authorizationCode = new URL(req.url, 'http://127.0.0.1:8000').searchParams.get('code');
            res.writeHead(200, { 'Content-Type': 'text/plain' });
            res.end('Authed');
            httpServer.close();
            resolve({});
        }).listen(8000, '127.0.0.1');
    });
    // Call OIDC Authorize API; how to get OIDC endpoint?
    // https://quip-amazon.com/SlOfAat54xwt/Tinkerbell-Developer-Guide#temp:C:BKF5d4a3b00a74440fd98e53be8a
    const authorizeUrl = new URL(`https://oidc.${await oidc.config.region()}.amazonaws.com/authorize`);
    authorizeUrl.searchParams.append('response_type', 'code');
    authorizeUrl.searchParams.append('client_id', registerClientResponse.clientId);
    authorizeUrl.searchParams.append('redirect_uri', 'http://127.0.0.1:8000/oauth/callback');
    authorizeUrl.searchParams.append('scopes', 'codewhisperer:conversations codewhisperer:transformations codewhisperer:taskassist codewhisperer:completions codewhisperer:analysis');
    authorizeUrl.searchParams.append('state', csrfState);
    authorizeUrl.searchParams.append('code_challenge', codeChallenge);
    authorizeUrl.searchParams.append('code_challenge_method', 'S256');
    connection.window.showDocument({
        uri: authorizeUrl.toString(),
        external: true
    });
    await authed;
    // Call CreateToken API
    const res = await oidc.createToken({
        clientId: registerClientResponse.clientId,
        clientSecret: registerClientResponse.clientSecret,
        grantType: 'authorization_code',
        redirectUri: 'http://127.0.0.1:8000/oauth/callback',
        codeVerifier,
        code: authorizationCode,
    });
    const ssoToken = {
        accessToken: res.accessToken,
        expiresAt: new Date(Date.now() + res.expiresIn * 1000).toISOString(),
        clientId: registerClientResponse.clientId,
        clientSecret: registerClientResponse.clientSecret,
        refreshToken: res.refreshToken,
        region: idcSource.region,
        startUrl: idcSource.issuerUrl
    };
    // Write SSO token cache file
    const tokenString = JSON.stringify(ssoToken, null, 2);
    (0, promises_1.writeFile)(cacheFilepath, tokenString);
    // TODO Setup timers for expiration and handling refresh
    return {
        ssoToken: {
            id: ssoTokenId,
            accessToken: ssoToken.accessToken
        }
    };
});
connection.onRequest(new invalidateSsoToken_1.InvalidateSsoTokenRequestType(), async (params, token) => {
    token.onCancellationRequested(() => {
        connection.window.showInformationMessage('CANCELLED!! WOO HOO!!!');
    });
    // TODO How was the token file name encoded and where can those values be obtained?
    const cacheFilepath = getCacheFilePath(params.ssoTokenId);
    if (node_fs_1.default.existsSync(cacheFilepath)) {
        await (0, promises_1.unlink)(cacheFilepath);
    }
    await connection.sendNotification(new ssoTokenChanged_1.SsoTokenChangedNotificationType(), { ssoTokenId: params.ssoTokenId, kind: ssoTokenChanged_1.SsoTokenChangedKind.Invalidated });
    return {};
});
connection.listen();
//# sourceMappingURL=server.js.map