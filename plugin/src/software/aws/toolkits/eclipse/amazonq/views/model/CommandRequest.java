// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.views.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommandRequest(@JsonProperty("command") String commandString) {
    public Command getCommand() {
        return Command.fromString(commandString);
    }
}
