// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import java.net.http.HttpClient;

public final class HttpClientFactory {

    private static final HttpClient INSTANCE = HttpClient.newHttpClient();

    private HttpClientFactory() {
        // Prevent instantiation
    }

    public static HttpClient getInstance() {
        return INSTANCE;
    }
}
