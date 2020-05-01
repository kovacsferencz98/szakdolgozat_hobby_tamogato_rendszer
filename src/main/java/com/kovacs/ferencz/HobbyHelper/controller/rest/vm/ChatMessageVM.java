package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;

import javax.validation.constraints.NotNull;

public class ChatMessageVM {
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    @NotNull
    private MessageType messageType;

    @NotNull
    private String content;

    @NotNull
    private String sender;

    public MessageType getType() {
        return messageType;
    }

    public void setType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public static ChatMessageVM createChatMessageVM(ChatMessageDTO chatMessageDTO, MessageType type) {
        ChatMessageVM result = new ChatMessageVM();
        result.setContent(chatMessageDTO.getText());
        result.setSender(chatMessageDTO.getSenderUsername());
        result.setType(type);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessageVM that = (ChatMessageVM) o;

        if (messageType != that.messageType) return false;
        if (!content.equals(that.content)) return false;
        return sender.equals(that.sender);
    }

    @Override
    public int hashCode() {
        int result = messageType.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + sender.hashCode();
        return result;
    }
}
