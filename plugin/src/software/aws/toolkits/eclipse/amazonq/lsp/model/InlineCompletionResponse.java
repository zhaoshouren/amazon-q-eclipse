// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

import java.util.List;

public class InlineCompletionResponse {

    private List<InlineCompletionItem> items;
    private String sessionId;

    public final List<InlineCompletionItem> getItems() {
        return items;
    }
    public final void setItems(final List<InlineCompletionItem> items) {
        this.items = items;
    }
    public final String getSessionId() {
        return sessionId;
    }
    public final void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

}
