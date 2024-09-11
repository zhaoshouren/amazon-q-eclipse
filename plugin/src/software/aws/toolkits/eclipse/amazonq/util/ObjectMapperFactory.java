// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperFactory {

    private static final ObjectMapper INSTANCE = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ObjectMapperFactory() {
        // Prevent instantiation
    }

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }
}
