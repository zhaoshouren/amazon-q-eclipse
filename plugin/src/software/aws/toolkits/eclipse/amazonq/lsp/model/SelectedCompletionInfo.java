// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

import org.eclipse.lsp4j.Range;

public class SelectedCompletionInfo {

    private String text;
    private Range range;

    public final String getText() {
        return text;
    }

    public final void setText(final String text) {
        this.text = text;
    }

    public final Range getRange() {
        return range;
    }

    public final void setRange(final Range range) {
        this.range = range;
    }
}
