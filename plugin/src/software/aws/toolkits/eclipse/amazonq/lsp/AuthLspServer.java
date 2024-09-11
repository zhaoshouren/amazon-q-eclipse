// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;

import software.aws.toolkits.eclipse.amazonq.lsp.auth.model.GetSsoTokenError;
import software.aws.toolkits.eclipse.amazonq.lsp.auth.model.GetSsoTokenParams;
import software.aws.toolkits.eclipse.amazonq.lsp.auth.model.GetSsoTokenResult;
import software.aws.toolkits.eclipse.amazonq.lsp.auth.model.InvalidateSsoTokenParams;
import software.aws.toolkits.eclipse.amazonq.lsp.auth.model.InvalidateSsoTokenResult;
import software.aws.toolkits.eclipse.amazonq.lsp.auth.model.ListProfilesResult;

public interface AuthLspServer extends LanguageServer {

    @JsonRequest("aws/credentials/profile/list")
    CompletableFuture<ListProfilesResult> listProfiles() throws GetSsoTokenError;

    @JsonRequest("aws/credentials/token/get")
    CompletableFuture<GetSsoTokenResult> getSsoToken(GetSsoTokenParams params);

    @JsonRequest("aws/credentials/token/invalidate")
    CompletableFuture<InvalidateSsoTokenResult> invalidateSsoToken(InvalidateSsoTokenParams params);
}
