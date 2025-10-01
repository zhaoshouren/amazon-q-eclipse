// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.chat.models;

public enum ChatUIInboundCommandName {
    ChatPrompt("aws/chat/sendChatPrompt"), // This is the odd one out, it follows the same message name as the request.
    InlineChatPrompt("aws/chat/sendInlineChatPrompt"),
    OpenTab("aws/chat/openTab"),

    SendToPrompt("sendToPrompt"),
    ErrorMessage("errorMessage"),
    InsertToCursorPosition("insertToCursorPosition"),
    AuthFollowUpClicked("authFollowUpClicked"),
    GenericCommand("genericCommand"),
    ChatOptionsUpdate("aws/chat/chatOptionsUpdate"),
    ListMcpServers("aws/chat/listMcpServers"),
    McpServerClick("aws/chat/mcpServerClick"),
    ListRules("aws/chat/listRules"),
    RuleClick("aws/chat/ruleClick"),
    ListAvailableModels("aws/chat/listAvailableModels"),
    SendPinnedContext("aws/chat/sendPinnedContext");

    private final String value;

    ChatUIInboundCommandName(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
