// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public final class PluginLogger {

    private PluginLogger() {
        // Prevent instantiation
    }

    private static final Bundle BUNDLE = FrameworkUtil.getBundle(PluginLogger.class);
    private static final ILog LOGGER = Platform.getLog(BUNDLE);

    public static void info(final String message) {
        info(message, null);
    }

    public static void info(final String message, final Throwable ex) {
        LOGGER.info(message, ex);
    }

    public static void warn(final String message) {
        warn(message, null);
    }

    public static void warn(final String message, final Throwable ex) {
        LOGGER.warn(message, ex);
    }

    public static void error(final String message) {
        error(message, null);
    }

    public static void error(final String message, final Throwable ex) {
        LOGGER.error(message, ex);
    }

}
