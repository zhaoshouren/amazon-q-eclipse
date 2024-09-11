import { ProtocolRequestType } from 'vscode-languageserver-protocol';
import { E_UNKNOWN, E_TIMEOUT, E_RUNTIME_NOT_SUPPORTED, E_CANNOT_READ_SHARED_CONFIG, E_CANNOT_WRITE_SHARED_CONFIG, E_INVALID_PROFILE, E_INVALID_SSO_SESSION, E_CANNOT_OVERWRITE_PROFILE, E_CANNOT_OVERWRITE_SSO_SESSION } from './errors';
import { SsoTokenProfile, SsoSession } from './listProfiles';
export interface UpdateProfileOptions {
    createNonexistentProfile?: boolean;
    createNonexistentSsoSession?: boolean;
    updateSharedSsoSession?: boolean;
}
export interface UpdateProfileParams {
    profile: SsoTokenProfile;
    ssoSession?: SsoSession;
    options?: UpdateProfileOptions;
}
export interface UpdateProfileResult {
}
export interface UpdateProfileError {
    errorCode: E_UNKNOWN | E_TIMEOUT | E_RUNTIME_NOT_SUPPORTED | E_CANNOT_READ_SHARED_CONFIG | E_CANNOT_WRITE_SHARED_CONFIG | E_CANNOT_OVERWRITE_PROFILE | E_CANNOT_OVERWRITE_SSO_SESSION | E_INVALID_PROFILE | E_INVALID_SSO_SESSION;
}
export declare class UpdateProfileRequestType extends ProtocolRequestType<UpdateProfileParams, UpdateProfileResult, void, UpdateProfileError, void> {
    constructor();
}
