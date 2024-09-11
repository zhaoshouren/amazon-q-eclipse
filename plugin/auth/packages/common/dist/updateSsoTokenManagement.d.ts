import { ProtocolRequestType } from 'vscode-languageserver-protocol';
import { E_UNKNOWN, E_TIMEOUT, E_INVALID_TOKEN } from './errors';
import { SsoTokenId } from './getSsoToken';
export interface UpdateSsoTokenManagementParams {
    readonly ssoTokenId: SsoTokenId;
    autoRefresh?: boolean;
    changeNotifications?: boolean;
}
export interface UpdateSsoTokenManagementResult {
    readonly ssoTokenId: SsoTokenId;
    readonly autoRefresh: boolean;
    readonly changeNotifications: boolean;
}
export interface UpdateSsoTokenManagementError {
    errorCode: E_UNKNOWN | E_TIMEOUT | E_INVALID_TOKEN;
}
export declare class UpdateSsoTokenManagementRequestType extends ProtocolRequestType<UpdateSsoTokenManagementParams, UpdateSsoTokenManagementResult, void, UpdateSsoTokenManagementError, void> {
    constructor();
}
