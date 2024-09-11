import { ProtocolRequestType } from 'vscode-languageserver-protocol';
import { SsoTokenId } from './getSsoToken';
import { E_CANNOT_READ_SSO_CACHE, E_CANNOT_WRITE_SSO_CACHE, E_INVALID_TOKEN, E_TIMEOUT, E_UNKNOWN } from './errors';
export interface InvalidateSsoTokenParams {
    readonly ssoTokenId: SsoTokenId;
}
export interface InvalidateSsoTokenResult {
}
export interface InvalidateSsoTokenError {
    errorCode: E_UNKNOWN | E_TIMEOUT | E_CANNOT_READ_SSO_CACHE | E_CANNOT_WRITE_SSO_CACHE | E_INVALID_TOKEN;
}
export declare class InvalidateSsoTokenRequestType extends ProtocolRequestType<InvalidateSsoTokenParams, InvalidateSsoTokenResult, void, InvalidateSsoTokenError, void> {
    constructor();
}
