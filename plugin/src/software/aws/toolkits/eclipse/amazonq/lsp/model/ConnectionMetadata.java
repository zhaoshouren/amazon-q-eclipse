// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

public class ConnectionMetadata {

    private SsoProfileData sso;

    public final SsoProfileData getSso() {
        return sso;
    }

    public final void setSso(final SsoProfileData sso) {
        this.sso = sso;
    }


}
