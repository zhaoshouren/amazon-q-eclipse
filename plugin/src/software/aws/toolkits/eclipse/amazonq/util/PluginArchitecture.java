// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

public enum PluginArchitecture {
    X86_64("x64"),
    ARM_64("arm64");

    private final String value;

    PluginArchitecture(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
