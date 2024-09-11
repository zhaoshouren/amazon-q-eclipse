// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

public class BearerCredentials {

    private String token;

    public final String getToken() {
        return token;
    }

    public final void setToken(final String token) {
        this.token = token;
    }
}
