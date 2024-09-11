import { ProtocolRequestType } from 'vscode-languageserver-protocol'
import { E_ENCRYPTION_REQUIRED, E_INVALID_TOKEN, E_TIMEOUT, E_UNKNOWN } from './errors'

export type SsoTokenId = string // Opaque identifier

export interface SsoToken {
	readonly id: SsoTokenId 
    readonly accessToken: string // This field is encrypted with JWT like 'update'
	// Additional fields captured in token cache file may be added here in the future
}

export type IamIdentityCenterSsoTokenSourceKind = 'IamIdentityCenter'
export type AwsBuilderIdSsoTokenSourceKind = 'AwsBuilderId'

export type SsoTokenSourceKind = IamIdentityCenterSsoTokenSourceKind | AwsBuilderIdSsoTokenSourceKind

export const SsoTokenSourceKind = {
    IamIdentityCenter: 'IamIdentityCenter',
	AwsBuilderId: 'AwsBuilderId'
} as const

export interface AwsBuilderIdSsoTokenSource {
	readonly kind: AwsBuilderIdSsoTokenSourceKind
	clientName: string
}

export interface IamIdentityCenterSsoTokenSource {
	readonly kind: IamIdentityCenterSsoTokenSourceKind
	clientName: string
    issuerUrl: string
    region: string
}

export interface GetSsoTokenOptions {
	autoRefresh?: boolean // default is true
	changeNotifications?: boolean // default is true
	loginOnInvalidToken?: boolean // default is true
}

export interface GetSsoTokenParams {
	source: IamIdentityCenterSsoTokenSource | AwsBuilderIdSsoTokenSource
	scopes?: string[]
	options?: GetSsoTokenOptions
}

export interface GetSsoTokenResult {
    readonly ssoToken?: SsoToken
}

export interface GetSsoTokenError {
	errorCode: 
		E_UNKNOWN | 
		E_TIMEOUT | 
		E_ENCRYPTION_REQUIRED |
		E_INVALID_TOKEN
}

export class GetSsoTokenRequestType extends 
	ProtocolRequestType<GetSsoTokenParams, GetSsoTokenResult, void, GetSsoTokenError, void> {
	constructor()
	{
		super('aws/credentials/token/get');
	}
}