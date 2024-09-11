// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.views;

import org.eclipse.swt.browser.Browser;

import software.aws.toolkits.eclipse.amazonq.views.model.Command;

public interface ViewActionHandler {
    void handleCommand(Command command, Browser browser);
}
