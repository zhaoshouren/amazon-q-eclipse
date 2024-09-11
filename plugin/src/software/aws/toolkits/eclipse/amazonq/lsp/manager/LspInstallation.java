// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.manager;

import java.nio.file.Path;

public record LspInstallation(Path nodeExecutable, Path lspJs) { }
