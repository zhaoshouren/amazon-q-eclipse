import { ProtocolNotificationType } from 'vscode-languageserver-protocol'
import { SsoTokenId } from './getSsoToken'

export type Created = 'Created'
export type Refreshed = 'Refreshed'
export type Expired = 'Expired'
export type Invalidated = 'Invalidated'

export type SsoTokenChangedKind =
	Created |
    Refreshed |
    Expired |
    Invalidated

export const SsoTokenChangedKind = {
	Created: 'Created',
	Refreshed: 'Refreshed',
	Expired: 'Expired',
	Invalidated: 'Invalidated'
} as const	

export interface SsoTokenChangedParams {
	readonly kind: SsoTokenChangedKind
    readonly ssoTokenId: SsoTokenId
}

export class SsoTokenChangedNotificationType extends 
	ProtocolNotificationType<SsoTokenChangedParams, void> {
	constructor()
	{
		super('aws/credentials/token/changed');
	}
}