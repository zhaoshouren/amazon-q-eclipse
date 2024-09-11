"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.InvalidateSsoTokenRequestType = void 0;
const vscode_languageserver_protocol_1 = require("vscode-languageserver-protocol");
class InvalidateSsoTokenRequestType extends vscode_languageserver_protocol_1.ProtocolRequestType {
    constructor() {
        super('aws/credentials/token/invalidate');
    }
}
exports.InvalidateSsoTokenRequestType = InvalidateSsoTokenRequestType;
//# sourceMappingURL=invalidateSsoToken.js.map