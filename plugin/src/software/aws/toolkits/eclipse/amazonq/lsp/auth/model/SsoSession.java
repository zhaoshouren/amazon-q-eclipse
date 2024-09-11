// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.auth.model;

import java.util.List;

public class SsoSession extends Section {
    private String ssoStartUrl;
    private String ssoRegion;
    private List<String> ssoRegistrationScopes;

    public final String getSsoStartUrl() {
        return ssoStartUrl;
    }

    public final void setSsoStartUrl(final String ssoStartUrl) {
        this.ssoStartUrl = ssoStartUrl;
    }

    public final String getSsoRegion() {
        return ssoRegion;
    }

    public final void setSsoRegion(final String ssoRegion) {
        this.ssoRegion = ssoRegion;
    }

    public final List<String> getSsoRegistrationScopes() {
        return ssoRegistrationScopes;
    }

    public final void setSsoRegistrationScopes(final List<String> ssoRegistrationScopes) {
        this.ssoRegistrationScopes = ssoRegistrationScopes;
    }
}
