// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.model;

public class InlineCompletionContext {

    private InlineCompletionTriggerKind triggerKind;
    private SelectedCompletionInfo selectedCompletionInfo;

    public final InlineCompletionTriggerKind getTriggerKind() {
        return triggerKind;
    }

    public final void setTriggerKind(final InlineCompletionTriggerKind triggerKind) {
        this.triggerKind = triggerKind;
    }

    public final SelectedCompletionInfo getSelectedCompletionInfo() {
        return selectedCompletionInfo;
    }

    public final void setSelectedCompletionInfo(final SelectedCompletionInfo selectedCompletionInfo) {
        this.selectedCompletionInfo = selectedCompletionInfo;
    }
}
