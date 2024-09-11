// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

public class UpdateCredentialsPayload {

    private BearerCredentials data;
    private boolean encrypted;

    public final BearerCredentials getData() {
        return data;
    }

    public final void setData(final BearerCredentials data) {
        this.data = data;
    }

    public final boolean isEncrypted() {
        return encrypted;
    }

    public final void setEncrypted(final boolean encrypted) {
        this.encrypted = encrypted;
    }
}
