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
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = activate;
exports.deactivate = deactivate;
const path = __importStar(require("path"));
const vscode = __importStar(require("vscode"));
const node_1 = require("vscode-languageclient/node");
const ssoTokenChanged_1 = require("@tkc-auth/common/dist/ssoTokenChanged");
const getSsoToken_1 = require("@tkc-auth/common/dist/getSsoToken");
const listProfiles_1 = require("@tkc-auth/common/dist/listProfiles");
const invalidateSsoToken_1 = require("@tkc-auth/common/dist/invalidateSsoToken");
const listProfilesCommand = 'tkc-auth.listProfiles';
const getSsoTokenCommand = 'tkc-auth.getSsoToken';
const invalidateSsoTokenCommand = 'tkc-auth.invalidateSsoToken';
let client;
let ssoToken;
function activate(context) {
    vscode.commands.registerCommand(listProfilesCommand, executeListProfiles);
    vscode.commands.registerCommand(getSsoTokenCommand, executeGetSsoToken);
    vscode.commands.registerCommand(invalidateSsoTokenCommand, executeInvalidateSsoToken);
    // The server is implemented in node
    const serverModule = context.asAbsolutePath(path.join('packages', 'server', 'dist', 'server.js'));
    // If the extension is launched in debug mode then the debug server options are used
    // Otherwise the run options are used
    const serverOptions = {
        run: { module: serverModule, transport: node_1.TransportKind.ipc },
        debug: {
            module: serverModule,
            transport: node_1.TransportKind.ipc,
        }
    };
    // Options to control the language client
    const clientOptions = {};
    // Create the language client and start the client.
    client = new node_1.LanguageClient('tkcAuthExtension', 'TKC Auth Extension', serverOptions, clientOptions);
    client.onNotification(new ssoTokenChanged_1.SsoTokenChangedNotificationType(), (params) => {
        console.log(`SSO token changed: ${params.ssoTokenId} ${params.kind}`, params);
    });
    // Start the client. This will also launch the server
    client.start();
}
function deactivate() {
    return client?.stop();
}
async function executeGetSsoToken() {
    console.log('Get SSO token command invoked');
    const params = {
        clientName: 'tkc-auth-poc',
        source: {
            kind: getSsoToken_1.SsoTokenSourceKind.AwsBuilderId,
            clientName: 'tkc-auth'
        }
    };
    const tokenSource = new node_1.CancellationTokenSource();
    try {
        const result = await client.sendRequest(new getSsoToken_1.GetSsoTokenRequestType(), params, tokenSource.token);
        console.log(JSON.stringify(result));
        ssoToken = result.ssoToken;
    }
    catch (e) {
        console.log(e);
    }
}
async function executeListProfiles() {
    console.log('List profiles command invoked');
    const params = {
        filter: {
            profileKind: listProfiles_1.ProfileKind.SsoToken
        }
    };
    const tokenSource = new node_1.CancellationTokenSource();
    const result = await client.sendRequest(new listProfiles_1.ListProfilesRequestType(), params, tokenSource.token);
    console.log(JSON.stringify(result));
}
async function executeInvalidateSsoToken() {
    console.log('Invalidate command invoked');
    const params = {
        ssoTokenId: ssoToken.id
    };
    const tokenSource = new node_1.CancellationTokenSource();
    await client.sendRequest(new invalidateSsoToken_1.InvalidateSsoTokenRequestType(), params, tokenSource.token);
}
//# sourceMappingURL=client.js.map