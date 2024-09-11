// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;
import software.aws.toolkits.eclipse.amazonq.lsp.model.InlineCompletionItem;
import software.aws.toolkits.eclipse.amazonq.providers.LspProvider;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static software.aws.toolkits.eclipse.amazonq.util.QConstants.Q_INLINE_HINT_TEXT_STYLE;
import static software.aws.toolkits.eclipse.amazonq.util.QEclipseEditorUtils.getActiveTextViewer;
import static software.aws.toolkits.eclipse.amazonq.util.QEclipseEditorUtils.isLastLine;

public final class QInvocationSession extends QResource {

    // Static variable to hold the single instance
    private static QInvocationSession instance;

    private QInvocationSessionState state = QInvocationSessionState.INACTIVE;

    private QSuggestionsContext suggestionsContext = null;

    private ITextEditor editor = null;
    private ITextViewer viewer = null;
    private Font inlineTextFont = null;
    private int invocationOffset = -1;
    private long invocationTimeInMs = -1L;
    private QInlineRendererListener listener = null;

    // Private constructor to prevent instantiation
    private QInvocationSession() {
        // Initialization code here
    }

    // Method to get the single instance
    public static synchronized QInvocationSession getInstance() {
        if (instance == null) {
            instance = new QInvocationSession();
        }
        return instance;
    }

    // TODO: separation of concerns between session attributes, session management, and remote invocation logic
    // Method to start the session
    public synchronized boolean start(final ITextEditor editor) {
        if (!isActive()) {
            state = QInvocationSessionState.INVOKING;

            // Start session logic here
            this.editor = editor;
            viewer = getActiveTextViewer(editor);
            if (viewer == null) {
                // cannot continue the invocation
                throw new IllegalStateException("no viewer available");
            }

            var widget = viewer.getTextWidget();

            suggestionsContext = new QSuggestionsContext();
            inlineTextFont = getInlineTextFont(widget);
            invocationOffset = widget.getCaretOffset();
            invocationTimeInMs = System.currentTimeMillis();
            System.out.println("Session started.");

            var listeners = widget.getTypedListeners(SWT.Paint, QInlineRendererListener.class).collect(Collectors.toList());
            System.out.println("Current listeners for " + widget);
            listeners.forEach(System.out::println);
            if (listeners.isEmpty()) {
                listener = new QInlineRendererListener();
                widget.addPaintListener(listener);
            }
            widget.addCaretListener(new CaretListener() {
                @Override
                public void caretMoved(final CaretEvent event) {
                    if (QInvocationSession.getInstance().isPreviewingSuggestions()) {
                        QInvocationSession.getInstance().transitionToDecisionMade();
                        QInvocationSession.getInstance().getViewer().getTextWidget().redraw();
                        //QInvocationSession.getInstance().end();
                    }
                }
            });
            return true;
        } else {
            System.out.println("Session is already active.");
            return false;
        }
    }

    public void invoke() {
        var session = QInvocationSession.getInstance();

        try {
            var params = InlineCompletionUtils.cwParamsFromContext(
                    session.getEditor(),
                    session.getViewer(),
                    session.getInvocationOffset()
            );

            ThreadingUtils.executeAsyncTask(() -> {
                try {
                    if (!AuthUtils.isLoggedIn().get()) {
                        this.end();
                        return;
                    } else {
                        AuthUtils.updateToken().get();
                    }

                    List<String> newSuggestions = LspProvider.getAmazonQServer().get().inlineCompletionWithReferences(params)
                            .thenApply(result -> result.getItems().stream()
                                    .map(InlineCompletionItem::getInsertText)
                                    .collect(Collectors.toList()))
                            .get();

                    Display.getDefault().asyncExec(() -> {
                        if (newSuggestions == null || newSuggestions.isEmpty()) {
                            end();
                            return;
                        }

                        suggestionsContext.getDetails().addAll(
                                newSuggestions.stream()
                                        .map(QSuggestionContext::new)
                                        .collect(Collectors.toList())
                        );

                        suggestionsContext.setCurrentIndex(0);

                        // TODO: remove print
                        // Update the UI with the results
                        System.out.println("Suggestions: " + newSuggestions);

                        transitionToPreviewingState();
                        session.getViewer().getTextWidget().redraw();
                    });
                } catch (InterruptedException e) {
                    PluginLogger.error("Inline completion interrupted", e);
                } catch (ExecutionException e) {
                    PluginLogger.error("Error executing inline completion", e);
                }
            });
        } catch (BadLocationException e) {
            PluginLogger.error("Unable to compute inline completion request from document", e);
        }
    }

    private Font getInlineTextFont(final StyledText widget) {
        FontData[] fontData = widget.getFont().getFontData();
        for (FontData fontDatum : fontData) {
            fontDatum.setStyle(Q_INLINE_HINT_TEXT_STYLE);
        }
        return new Font(widget.getDisplay(), fontData);
    }

    // Method to end the session
    public synchronized void end() {
        if (isActive()) {
            state = QInvocationSessionState.INACTIVE;
            dispose();
            // End session logic here
            System.out.println("Session ended.");
        } else {
            System.out.println("Session is not active.");
        }
    }

    // Method to check if session is active
    public boolean isActive() {
        return state != QInvocationSessionState.INACTIVE;
    }

    public boolean isPreviewingSuggestions() {
        return state == QInvocationSessionState.SUGGESTION_PREVIEWING;
    }

    public boolean isDecisionMade() {
        return state == QInvocationSessionState.DECISION_MADE;
    }

    public void transitionToPreviewingState() {
        assert state == QInvocationSessionState.INVOKING;
        state = QInvocationSessionState.SUGGESTION_PREVIEWING;
    }

    public void transitionToInvokingState() {
        assert state == QInvocationSessionState.INACTIVE;
        state = QInvocationSessionState.INVOKING;
    }

    public void transitionToInactiveState() {
        state = QInvocationSessionState.INACTIVE;
    }

    public void transitionToDecisionMade() {
        if (state != QInvocationSessionState.SUGGESTION_PREVIEWING) {
            return;
        }
        state = QInvocationSessionState.DECISION_MADE;

        // Clear previous next line indent in certain cases (always for now?)
        var widget = viewer.getTextWidget();
        var caretLine = widget.getLineAtOffset(widget.getCaretOffset());
        if (!isLastLine(widget, caretLine + 1)) {
            widget.setLineVerticalIndent(caretLine + 1, 0);
        }
    }

    public Font getInlineTextFont() {
        return inlineTextFont;
    }

    public int getInvocationOffset() {
        return invocationOffset;
    }

    public ITextEditor getEditor() {
        return editor;
    }

    public ITextViewer getViewer() {
        return viewer;
    }

    public String getCurrentSuggestion() {
        if (suggestionsContext == null) {
            PluginLogger.warn("QSuggestion context is null");
            return null;
        }
        var details = suggestionsContext.getDetails();
        var index = suggestionsContext.getCurrentIndex();
        if (details.isEmpty() && index != -1 || !details.isEmpty() && (index < 0 || index >= details.size())) {
            PluginLogger.warn("QSuggestion context index is incorrect");
            return null;
        }
        var detail = details.get(index);
        if (detail.getState() == QSuggestionState.DISCARD) {
            throw new IllegalStateException("QSuggestion showing discarded suggestions");
        }

        return details.get(index).getSuggestion();
    }

    // Additional methods for the session can be added here
    @Override
    public void dispose() {
        suggestionsContext = null;
        inlineTextFont.dispose();
        inlineTextFont = null;
        viewer.getTextWidget().removePaintListener(listener);
        listener = null;
        invocationOffset = -1;
        invocationTimeInMs = -1L;
        editor = null;
        viewer = null;
    }
}
