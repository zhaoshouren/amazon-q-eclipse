// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

public enum InlineCompletionTriggerKind {
    Invoke(0),
    Automatic(1);

    private final int value;

    InlineCompletionTriggerKind(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
