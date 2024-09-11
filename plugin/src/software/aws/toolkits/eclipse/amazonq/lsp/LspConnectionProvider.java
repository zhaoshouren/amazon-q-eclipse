// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

import software.aws.toolkits.eclipse.amazonq.lsp.manager.DefaultLspManager;
import software.aws.toolkits.eclipse.amazonq.lsp.manager.LspConstants;
import software.aws.toolkits.eclipse.amazonq.lsp.manager.LspManager;
import software.aws.toolkits.eclipse.amazonq.lsp.manager.fetcher.RemoteManifestLspFetcher;

public class LspConnectionProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {

    public LspConnectionProvider() throws IOException {
        setWorkingDirectory(System.getProperty("user.dir"));

        LspManager lspManager = DefaultLspManager.builder()
            .withLspExecutablePrefix(LspConstants.CW_LSP_FILENAME)
            .withFetcher(RemoteManifestLspFetcher.builder()
                .withManifestUrl(LspConstants.CW_MANIFEST_URL)
                .build())
            .build();

        var cwLspInstallation = lspManager.getLspInstallation();
        List<String> commands = new ArrayList<>();
        commands.add(cwLspInstallation.nodeExecutable().toString());
        commands.add(cwLspInstallation.lspJs().toString());
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
        Map<String, String> env = builder.environment();
        env.put("ENABLE_INLINE_COMPLETION", "true");
        env.put("ENABLE_TOKEN_PROVIDER", "true");
        return builder;
    }
}
