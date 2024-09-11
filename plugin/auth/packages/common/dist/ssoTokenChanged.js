"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.SsoTokenChangedNotificationType = exports.SsoTokenChangedKind = void 0;
const vscode_languageserver_protocol_1 = require("vscode-languageserver-protocol");
exports.SsoTokenChangedKind = {
    Created: 'Created',
    Refreshed: 'Refreshed',
    Expired: 'Expired',
    Invalidated: 'Invalidated'
};
class SsoTokenChangedNotificationType extends vscode_languageserver_protocol_1.ProtocolNotificationType {
    constructor() {
        super('aws/credentials/token/changed');
    }
}
exports.SsoTokenChangedNotificationType = SsoTokenChangedNotificationType;
//# sourceMappingURL=ssoTokenChanged.js.map