// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.views;


import org.eclipse.swt.browser.Browser;

import software.aws.toolkits.eclipse.amazonq.util.PluginLogger;
import software.aws.toolkits.eclipse.amazonq.views.model.ParsedCommand;

public class AmazonQChatViewActionHandler implements ViewActionHandler {
    @Override
    public final void handleCommand(ParsedCommand parsedCommand, final Browser browser) {
        switch (parsedCommand.getCommand()) {
            case CHAT_READY:
                PluginLogger.info("Chat_ready command received");
                break;
            case CHAT_TAB_ADD:
                PluginLogger.info("Chat_tab_add command received with params " + parsedCommand.getParams().toString());
                break;
            case TELEMETRY_EVENT:
            	PluginLogger.info("Telemetry command received with params " + parsedCommand.getParams().toString());
                break;
            default:
                PluginLogger.info("Unhandled command: " + parsedCommand.getCommand());
                break;
        }
    }
}
