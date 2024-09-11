import * as path from 'path'

import * as vscode from 'vscode'

import {
    CancellationTokenSource,
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind
} from 'vscode-languageclient/node'

import {
    SsoTokenChangedNotificationType,
    SsoTokenChangedParams
} from '@tkc-auth/common/dist/ssoTokenChanged'

import {
    AwsBuilderIdSsoTokenSource,
    GetSsoTokenError,
    GetSsoTokenParams,
    GetSsoTokenRequestType,
    GetSsoTokenResult,
    SsoToken,
    SsoTokenSourceKind,
} from '@tkc-auth/common/dist/getSsoToken'

import {
    ListProfilesError,
    ListProfilesParams,
    ListProfilesRequestType,
    ListProfilesResult,
    ProfileKind
} from '@tkc-auth/common/dist/listProfiles'

import {
    InvalidateSsoTokenError,
    InvalidateSsoTokenParams,
    InvalidateSsoTokenRequestType,
    InvalidateSsoTokenResult
} from '@tkc-auth/common/dist/invalidateSsoToken'

const listProfilesCommand = 'tkc-auth.listProfiles'
const getSsoTokenCommand = 'tkc-auth.getSsoToken'
const invalidateSsoTokenCommand = 'tkc-auth.invalidateSsoToken'

let client: LanguageClient
let ssoToken: SsoToken

export function activate(context: vscode.ExtensionContext) {

    vscode.commands.registerCommand(listProfilesCommand, executeListProfiles)
    vscode.commands.registerCommand(getSsoTokenCommand, executeGetSsoToken)
    vscode.commands.registerCommand(invalidateSsoTokenCommand, executeInvalidateSsoToken)

    // The server is implemented in node
	const serverModule = context.asAbsolutePath(
		path.join('packages', 'server', 'dist', 'server.js')
	)

	// If the extension is launched in debug mode then the debug server options are used
	// Otherwise the run options are used
	const serverOptions: ServerOptions = {
		run: { module: serverModule, transport: TransportKind.ipc },
		debug: {
			module: serverModule,
			transport: TransportKind.ipc,
		}
	}

	// Options to control the language client
	const clientOptions: LanguageClientOptions = { }

	// Create the language client and start the client.
	client = new LanguageClient(
		'tkcAuthExtension',
		'TKC Auth Extension',
		serverOptions,
		clientOptions
	)

    client.onNotification<SsoTokenChangedParams, void>(new SsoTokenChangedNotificationType(), (params) => {
        console.log(`SSO token changed: ${params.ssoTokenId} ${params.kind}`, params)
    })

	// Start the client. This will also launch the server
	client.start()
}

export function deactivate(): Thenable<void> | undefined {
	return client?.stop()
}

async function executeGetSsoToken()
{
    console.log('Get SSO token command invoked')

    const params = {
        clientName: 'tkc-auth-poc',
        source: {
            kind: SsoTokenSourceKind.AwsBuilderId,
            clientName: 'tkc-auth'
        } satisfies AwsBuilderIdSsoTokenSource
    }

    const tokenSource = new CancellationTokenSource();

    try
    {
        const result = await client!.sendRequest<GetSsoTokenParams, GetSsoTokenResult, void, GetSsoTokenError, void>(new GetSsoTokenRequestType(), params, tokenSource.token)
        console.log(JSON.stringify(result))

        ssoToken = result.ssoToken as SsoToken
    }
    catch (e)
    {
        console.log(e)
    }
}

async function executeListProfiles()
{
    console.log('List profiles command invoked')

    const params = {
        filter: {
            profileKind: ProfileKind.SsoToken
        }
    } satisfies ListProfilesParams

    const tokenSource = new CancellationTokenSource();

    const result = await client!.sendRequest<ListProfilesParams, ListProfilesResult, void, ListProfilesError, void>(new ListProfilesRequestType(), params, tokenSource.token)

    console.log(JSON.stringify(result))
}

async function executeInvalidateSsoToken()
{
    console.log('Invalidate command invoked')

    const params = {
        ssoTokenId: ssoToken.id
    }

    const tokenSource = new CancellationTokenSource();

    await client!.sendRequest<InvalidateSsoTokenParams, InvalidateSsoTokenResult, void, InvalidateSsoTokenError, void>(new InvalidateSsoTokenRequestType(), params, tokenSource.token)
}