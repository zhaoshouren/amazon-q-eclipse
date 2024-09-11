```mermaind
sequenceDiagram
    participant D       as Destination
    participant FS      as Flare server
    participant OIDC    as SSO-OIDC service

    D-)+FS:             GetSsoToken
    Note left of D:     LSP request
    FS->>+OIDC:         RegisterClient
    Note right of OIDC: RegisterClient only called if<br>cached client not found.
    OIDC-->>-FS:        Client details
    create participant  WS as Local web server
    FS-)WS:             Start local web server for auth redirect URL
    FS-->>-D:           ShowDocument with SSO-OIDC Authorize API URL
    activate D
    Note left of D:     LSP notification
    D->>-OIDC:          Show SSO-OIDC Authorize API URL in browser
    activate OIDC
    create actor        U as User
    OIDC->>U:           User is presented with SSO-OIDC login website
    destroy U
    U-->>OIDC:          User completes the login process on website
    OIDC-->>-D:         Redirect to auth redirect URL in browser
    activate D
    D->>+WS:            Browser requests auth redirect URL
    WS--)D:             Auth success/error web page
    deactivate D
    WS-->>+FS:          Provides the authorization code from the auth redirect URL
    deactivate WS
    destroy WS
    FS-)WS:             Terminate web server
    FS->>+OIDC:         CreateToken with authorization code
    OIDC-->>-FS:        Returns access and refresh tokens
    FS--)-D:            Returns access token
    activate D
    Note left of D:     LSP result
    D-)-FS:             Call update with access token
```