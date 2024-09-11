// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.Launcher.Builder;

import software.aws.toolkits.eclipse.amazonq.providers.LspProvider;

public class AuthLspServerBuilder extends Builder<AuthLspServer> {

    @Override
    public final Launcher<AuthLspServer> create() {
        super.setRemoteInterface(AuthLspServer.class);
        Launcher<AuthLspServer> launcher = super.create();
        LspProvider.setServer(AuthLspServer.class, launcher.getRemoteProxy());
        return launcher;
    }

}
