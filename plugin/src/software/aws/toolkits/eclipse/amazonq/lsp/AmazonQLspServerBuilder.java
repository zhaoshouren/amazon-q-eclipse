// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.Launcher.Builder;

import software.aws.toolkits.eclipse.amazonq.providers.LspProvider;

public class AmazonQLspServerBuilder extends Builder<AmazonQLspServer> {

    @Override
    public final Launcher<AmazonQLspServer> create() {
        super.setRemoteInterface(AmazonQLspServer.class);
        Launcher<AmazonQLspServer> launcher = super.create();
        LspProvider.setServer(AmazonQLspServer.class, launcher.getRemoteProxy());
        return launcher;
    }

}
