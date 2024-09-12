// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package software.aws.toolkits.eclipse.amazonq.util;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;

public final class QInlineCaretListener implements CaretListener {
    private StyledText widget = null;

    public QInlineCaretListener(final StyledText widget) {
        this.widget = widget;
    }

    @Override
    public void caretMoved(final CaretEvent event) {
        var qInvocationSessionInstance = QInvocationSession.getInstance();
        CaretMovementReason caretMovementReason = qInvocationSessionInstance.getCaretMovementReason();

        // We want to ignore caret movements induced by text editing
        if (caretMovementReason == CaretMovementReason.TEXT_INPUT) {
            qInvocationSessionInstance.setCaretMovementReason(CaretMovementReason.UNEXAMINED);
            return;
        }

        // There are instances where the caret movement was induced by sources other than user input
        // Under these instances, it is observed that the caret would revert back to a position that was last
        // placed by the user in the same rendering cycle.
        // We want to preserve the preview state and prevent it from terminating by these non-user instructed movements
        if (event.caretOffset != widget.getCaretOffset() && qInvocationSessionInstance.isPreviewingSuggestions()) {
            qInvocationSessionInstance.transitionToDecisionMade();
            qInvocationSessionInstance.end();
        }
    }
}

