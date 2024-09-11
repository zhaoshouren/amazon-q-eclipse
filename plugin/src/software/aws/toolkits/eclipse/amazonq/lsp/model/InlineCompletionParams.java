// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

import org.eclipse.lsp4j.TextDocumentPositionAndWorkDoneProgressParams;

public class InlineCompletionParams extends TextDocumentPositionAndWorkDoneProgressParams {

    private InlineCompletionContext context;

    public final InlineCompletionContext getContext() {
        return context;
    }

    public final void setContext(final InlineCompletionContext context) {
        this.context = context;
    }

}
