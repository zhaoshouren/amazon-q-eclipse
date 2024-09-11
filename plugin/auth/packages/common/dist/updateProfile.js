"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.UpdateProfileRequestType = void 0;
const vscode_languageserver_protocol_1 = require("vscode-languageserver-protocol");
class UpdateProfileRequestType extends vscode_languageserver_protocol_1.ProtocolRequestType {
    constructor() {
        super('aws/credentials/profile/update');
    }
}
exports.UpdateProfileRequestType = UpdateProfileRequestType;
//# sourceMappingURL=updateProfile.js.map