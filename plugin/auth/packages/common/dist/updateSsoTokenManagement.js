"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.UpdateSsoTokenManagementRequestType = void 0;
const vscode_languageserver_protocol_1 = require("vscode-languageserver-protocol");
class UpdateSsoTokenManagementRequestType extends vscode_languageserver_protocol_1.ProtocolRequestType {
    constructor() {
        super('aws/credentials/token/management/update');
    }
}
exports.UpdateSsoTokenManagementRequestType = UpdateSsoTokenManagementRequestType;
//# sourceMappingURL=updateSsoTokenManagement.js.map