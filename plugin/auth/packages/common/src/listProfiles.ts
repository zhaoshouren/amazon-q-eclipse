import { ProtocolRequestType } from 'vscode-languageserver-protocol'
import { E_CANNOT_READ_SHARED_CONFIG, E_RUNTIME_NOT_SUPPORTED, E_TIMEOUT, E_UNKNOWN } from './errors'

export type SsoTokenProfileKind = 'SsoToken'

export type ProfileKind = SsoTokenProfileKind

export const ProfileKind = {
    SsoToken: 'SsoToken'
} as const

export interface Section {
    name: string
}

export interface Profile extends Section {
    readonly kind: ProfileKind
    region?: string
}

export interface SsoTokenProfile extends Profile {
    readonly kind: SsoTokenProfileKind
    ssoSessionName: string
}

export interface SsoSession extends Section {
    ssoStartUrl: string
    ssoRegion: string
    ssoRegistrationScopes?: string[]
}

export interface ListProfilesParams {
	// Intentionally left blank
}

export interface ListProfilesResult {
    profiles?: (Profile | SsoTokenProfile)[]
    ssoSessions?: SsoSession[]
}

export interface ListProfilesError {
    errorCode: 
        E_UNKNOWN |
        E_TIMEOUT |
        E_RUNTIME_NOT_SUPPORTED |
        E_CANNOT_READ_SHARED_CONFIG
}

export class ListProfilesRequestType extends 
	ProtocolRequestType<ListProfilesParams, ListProfilesResult, void, ListProfilesError, void> {
	constructor()
	{
		super('aws/credentials/profile/list');
	}
}