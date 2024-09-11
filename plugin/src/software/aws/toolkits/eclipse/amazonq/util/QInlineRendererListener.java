// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;

import static software.aws.toolkits.eclipse.amazonq.util.QConstants.Q_INLINE_HINT_TEXT_COLOR;
import static software.aws.toolkits.eclipse.amazonq.util.QEclipseEditorUtils.isLastLine;

public class QInlineRendererListener implements PaintListener {
    @Override
    public final void paintControl(final PaintEvent e) {
        if (!QInvocationSession.getInstance().isPreviewingSuggestions()) {
            return;
        }

        var gc = e.gc;
        gc.setForeground(Q_INLINE_HINT_TEXT_COLOR);
        gc.setFont(QInvocationSession.getInstance().getInlineTextFont());
        var widget = QInvocationSession.getInstance().getViewer().getTextWidget();

        var location = widget.getLocationAtOffset(widget.getCaretOffset());
        var suggestion = QInvocationSession.getInstance().getCurrentSuggestion();
        var suggestionParts = suggestion.split("\\R", 2);

        // Draw first line inline
        if (suggestionParts.length > 0) {
            gc.drawText(suggestionParts[0], location.x, location.y, true);
        }

        // Draw other lines inline
        if (suggestionParts.length > 1) {
            // For last line case doesn't need to indent next line vertically
            var caretLine = widget.getLineAtOffset(widget.getCaretOffset());
            if (!isLastLine(widget, caretLine + 1)) {
                // when showing the suggestion need to add next line indent
                Point textExtent = gc.stringExtent(" ");
                int height = textExtent.y * suggestionParts[1].split("\\R").length;
                widget.setLineVerticalIndent(caretLine + 1, height);
            }

            int lineHt = widget.getLineHeight();
            int fontHt = gc.getFontMetrics().getHeight();
            int x = widget.getLeftMargin();
            int y = location.y + lineHt * 2 - fontHt;
            gc.drawText(suggestionParts[1], x, y, true);
        }
    }

}
