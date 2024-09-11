// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.auth.model;

public class SsoTokenProfile extends Profile {
    private String ssoSessionName;

    public final String getSsoSessionName() {
        return ssoSessionName;
    }

    public final void setSsoSessionName(final String ssoSessionName) {
        this.ssoSessionName = ssoSessionName;
    }
}
