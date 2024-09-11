// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

import software.aws.toolkits.eclipse.amazonq.util.PluginUtils;

public class AuthLspConnectionProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {

    public AuthLspConnectionProvider() throws IOException {
        setWorkingDirectory(System.getProperty("user.dir"));
        var authJs = PluginUtils.getResource("auth/packages/server/dist/index.js");

        List<String> commands = new ArrayList<>();
        commands.add("/opt/homebrew/bin/node"); //TODO: don't hardcode - this should be bundled and platform specific
        commands.add(authJs.getPath());
        commands.add("--nolazy");
        commands.add("--inspect=5599");
        commands.add("--stdio");
        setCommands(commands);
    }

    @Override
    protected final ProcessBuilder createProcessBuilder() {
        final var builder = new ProcessBuilder(getCommands());
        if (getWorkingDirectory() != null) {
            builder.directory(new File(getWorkingDirectory()));
        }
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder;
    }

}
