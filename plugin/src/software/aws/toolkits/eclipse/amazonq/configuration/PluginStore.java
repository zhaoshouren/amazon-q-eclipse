// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.configuration;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public final class PluginStore {
    private static final IEclipsePreferences PREFERENCES = ConfigurationScope.INSTANCE.getNode("software.aws.toolkits.eclipse");

    private PluginStore() {
        // Prevent instantiation
    }

    public static void put(final String key, final String value) {
        PREFERENCES.put(key, value);
    }

    public static String get(final String key) {
        return PREFERENCES.get(key, null);
    }

}
