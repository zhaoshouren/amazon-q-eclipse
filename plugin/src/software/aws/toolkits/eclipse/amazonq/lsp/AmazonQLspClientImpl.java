// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4e.LanguageClientImpl;

import software.aws.toolkits.eclipse.amazonq.lsp.model.ConnectionMetadata;
import software.aws.toolkits.eclipse.amazonq.lsp.model.SsoProfileData;

@SuppressWarnings("restriction")
public class AmazonQLspClientImpl extends LanguageClientImpl implements AmazonQLspClient {

    @Override
    public final CompletableFuture<ConnectionMetadata> getConnectionMetadata() {
        // TODO don't hardcode start URL
        SsoProfileData sso = new SsoProfileData();
        sso.setStartUrl("https://view.awsapps.com/start");
        ConnectionMetadata metadata = new ConnectionMetadata();
        metadata.setSso(sso);
        return CompletableFuture.completedFuture(metadata);
    }

}
