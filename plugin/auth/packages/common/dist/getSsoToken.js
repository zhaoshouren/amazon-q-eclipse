"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.GetSsoTokenRequestType = exports.SsoTokenSourceKind = void 0;
const vscode_languageserver_protocol_1 = require("vscode-languageserver-protocol");
exports.SsoTokenSourceKind = {
    IamIdentityCenter: 'IamIdentityCenter',
    AwsBuilderId: 'AwsBuilderId'
};
class GetSsoTokenRequestType extends vscode_languageserver_protocol_1.ProtocolRequestType {
    constructor() {
        super('aws/credentials/token/get');
    }
}
exports.GetSsoTokenRequestType = GetSsoTokenRequestType;
//# sourceMappingURL=getSsoToken.js.map