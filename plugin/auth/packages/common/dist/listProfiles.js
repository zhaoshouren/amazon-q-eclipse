"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ListProfilesRequestType = exports.ProfileKind = void 0;
const vscode_languageserver_protocol_1 = require("vscode-languageserver-protocol");
exports.ProfileKind = {
    SsoToken: 'SsoToken'
};
class ListProfilesRequestType extends vscode_languageserver_protocol_1.ProtocolRequestType {
    constructor() {
        super('aws/credentials/profile/list');
    }
}
exports.ListProfilesRequestType = ListProfilesRequestType;
//# sourceMappingURL=listProfiles.js.map