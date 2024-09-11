// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.messages.ResponseMessage;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;

import software.aws.toolkits.eclipse.amazonq.lsp.model.InlineCompletionParams;
import software.aws.toolkits.eclipse.amazonq.lsp.model.InlineCompletionResponse;
import software.aws.toolkits.eclipse.amazonq.lsp.model.UpdateCredentialsPayload;

public interface AmazonQLspServer extends LanguageServer {

    @JsonRequest("aws/textDocument/inlineCompletionWithReferences")
    CompletableFuture<InlineCompletionResponse> inlineCompletionWithReferences(InlineCompletionParams params);

    @JsonRequest("aws/credentials/token/update")
    CompletableFuture<ResponseMessage> updateTokenCredentials(UpdateCredentialsPayload payload);

}
