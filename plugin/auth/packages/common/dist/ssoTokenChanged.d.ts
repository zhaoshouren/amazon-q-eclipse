import { ProtocolNotificationType } from 'vscode-languageserver-protocol';
import { SsoTokenId } from './getSsoToken';
export type Created = 'Created';
export type Refreshed = 'Refreshed';
export type Expired = 'Expired';
export type Invalidated = 'Invalidated';
export type SsoTokenChangedKind = Created | Refreshed | Expired | Invalidated;
export declare const SsoTokenChangedKind: {
    readonly Created: "Created";
    readonly Refreshed: "Refreshed";
    readonly Expired: "Expired";
    readonly Invalidated: "Invalidated";
};
export interface SsoTokenChangedParams {
    readonly kind: SsoTokenChangedKind;
    readonly ssoTokenId: SsoTokenId;
}
export declare class SsoTokenChangedNotificationType extends ProtocolNotificationType<SsoTokenChangedParams, void> {
    constructor();
}
