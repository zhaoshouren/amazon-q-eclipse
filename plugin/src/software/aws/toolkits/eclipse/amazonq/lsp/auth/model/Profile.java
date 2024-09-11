// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.auth.model;

public class Profile extends Section {
    private String region;

    public final String getRegion() {
        return region;
    }

    public final void setRegion(final String region) {
        this.region = region;
    }
}
