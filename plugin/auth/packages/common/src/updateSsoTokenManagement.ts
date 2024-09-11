import { ProtocolRequestType } from 'vscode-languageserver-protocol'
import { E_UNKNOWN, E_TIMEOUT, E_INVALID_TOKEN } from './errors'
import { SsoTokenId } from './getSsoToken'

export interface UpdateSsoTokenManagementParams {
    readonly ssoTokenId: SsoTokenId
    autoRefresh?: boolean // no change if not set
    changeNotifications?: boolean // no change if not set
}

export interface UpdateSsoTokenManagementResult {
    readonly ssoTokenId: SsoTokenId // same as supplied request params value
    readonly autoRefresh: boolean // returns state after call
    readonly changeNotifications: boolean // returns state after call
}

export interface UpdateSsoTokenManagementError {
	errorCode: 
		E_UNKNOWN | 
		E_TIMEOUT | 
		E_INVALID_TOKEN
}

export class UpdateSsoTokenManagementRequestType extends 
	ProtocolRequestType<UpdateSsoTokenManagementParams, UpdateSsoTokenManagementResult, void, UpdateSsoTokenManagementError, void> {
	constructor()
	{
		super('aws/credentials/token/management/update');
	}
}