// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;

import software.aws.toolkits.eclipse.amazonq.lsp.model.ConnectionMetadata;

public interface AmazonQLspClient extends LanguageClient {

    @JsonRequest("aws/credentials/getConnectionMetadata")
    CompletableFuture<ConnectionMetadata> getConnectionMetadata();

}
