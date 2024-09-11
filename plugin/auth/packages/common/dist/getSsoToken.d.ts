import { ProtocolRequestType } from 'vscode-languageserver-protocol';
import { E_ENCRYPTION_REQUIRED, E_INVALID_TOKEN, E_TIMEOUT, E_UNKNOWN } from './errors';
export type SsoTokenId = string;
export interface SsoToken {
    readonly id: SsoTokenId;
    readonly accessToken: string;
}
export type IamIdentityCenterSsoTokenSourceKind = 'IamIdentityCenter';
export type AwsBuilderIdSsoTokenSourceKind = 'AwsBuilderId';
export type SsoTokenSourceKind = IamIdentityCenterSsoTokenSourceKind | AwsBuilderIdSsoTokenSourceKind;
export declare const SsoTokenSourceKind: {
    readonly IamIdentityCenter: "IamIdentityCenter";
    readonly AwsBuilderId: "AwsBuilderId";
};
export interface AwsBuilderIdSsoTokenSource {
    readonly kind: AwsBuilderIdSsoTokenSourceKind;
    clientName: string;
}
export interface IamIdentityCenterSsoTokenSource {
    readonly kind: IamIdentityCenterSsoTokenSourceKind;
    clientName: string;
    issuerUrl: string;
    region: string;
}
export interface GetSsoTokenOptions {
    autoRefresh?: boolean;
    changeNotifications?: boolean;
    loginOnInvalidToken?: boolean;
}
export interface GetSsoTokenParams {
    source: IamIdentityCenterSsoTokenSource | AwsBuilderIdSsoTokenSource;
    scopes?: string[];
    options?: GetSsoTokenOptions;
}
export interface GetSsoTokenResult {
    readonly ssoToken?: SsoToken;
}
export interface GetSsoTokenError {
    errorCode: E_UNKNOWN | E_TIMEOUT | E_ENCRYPTION_REQUIRED | E_INVALID_TOKEN;
}
export declare class GetSsoTokenRequestType extends ProtocolRequestType<GetSsoTokenParams, GetSsoTokenResult, void, GetSsoTokenError, void> {
    constructor();
}
