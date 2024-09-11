// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import org.eclipse.core.runtime.Platform;

public final class ClientMetadata {
    private ClientMetadata() {
        // Prevent instantiation
    }
    
    private static final String osName = System.getProperty("os.name");
    private static final String osVersion = System.getProperty("os.version");
    private static final String ideName = Platform.getProduct().getName();
    private static final String ideVersion = Platform.getProduct().getDefiningBundle().getVersion().toString();
    
    public static String getOSName() {
        return osName;
    }
    
    public static String getOSVersion() {
        return osVersion;
    }
    
    public static String getIdeName() {
        return ideName;
    }
    
    public static String getIdeVersion() {
        return ideVersion;
    }
}
