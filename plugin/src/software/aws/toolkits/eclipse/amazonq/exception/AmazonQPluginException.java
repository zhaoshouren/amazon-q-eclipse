// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.exception;

@SuppressWarnings("serial")
public class AmazonQPluginException extends RuntimeException {

    public AmazonQPluginException(final String reason) {
        super(reason);
    }

    public AmazonQPluginException(final Throwable cause) {
        super(cause);
    }

    public AmazonQPluginException(final String reason, final Throwable cause) {
        super(reason, cause);
    }

}
