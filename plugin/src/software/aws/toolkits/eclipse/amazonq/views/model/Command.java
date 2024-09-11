// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.views.model;

public enum Command {
    LOGIN_BUILDER_ID("loginBuilderId"),
    CANCEL_LOGIN("cancelLogin"),
    UKNOWN("unknown");

    private final String commandString;

    Command(final String commandString) {
        this.commandString = commandString;
    }

    public static Command fromString(final String value) {
        for (Command command : Command.values()) {
            if (command.commandString.equals(value)) {
                return command;
            }
        }
        return Command.UKNOWN;
    }
}
