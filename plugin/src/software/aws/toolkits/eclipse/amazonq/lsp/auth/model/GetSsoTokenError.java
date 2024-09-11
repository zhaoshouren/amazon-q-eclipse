// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.auth.model;

@SuppressWarnings("serial")
public class GetSsoTokenError extends RuntimeException {
    private ErrorCode errorCode;

    public final ErrorCode getErrorCode() {
        return errorCode;
    }

    public final void setErrorCode(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
