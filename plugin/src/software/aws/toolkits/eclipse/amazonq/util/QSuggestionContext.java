// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

public final class QSuggestionContext {
    private String suggestion;
    private QSuggestionState state;

    public QSuggestionContext(final String suggestion) {
        this.suggestion = suggestion;
        state = QSuggestionState.UNSEEN;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public QSuggestionState getState() {
        return state;
    }

}
