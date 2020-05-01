package com.kovacs.ferencz.HobbyHelper.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kovacs.ferencz.HobbyHelper.domain.Event;
import com.kovacs.ferencz.HobbyHelper.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ChatMessageDTO {

    private Long id;

    @NotNull
    private String text;

    @NotNull
    private Long senderId;

    @NotNull
    private String senderUsername;

    @NotNull
    private Long eventId;

    @NotNull
    private String eventName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", senderId=" + senderId +
                ", senderUsername='" + senderUsername + '\'' +
                ", eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessageDTO that = (ChatMessageDTO) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getText() != null ? !getText().equals(that.getText()) : that.getText() != null) return false;
        if (getSenderId() != null ? !getSenderId().equals(that.getSenderId()) : that.getSenderId() != null)
            return false;
        if (getSenderUsername() != null ? !getSenderUsername().equals(that.getSenderUsername()) : that.getSenderUsername() != null)
            return false;
        if (getEventId() != null ? !getEventId().equals(that.getEventId()) : that.getEventId() != null) return false;
        return getEventName() != null ? getEventName().equals(that.getEventName()) : that.getEventName() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        result = 31 * result + (getSenderId() != null ? getSenderId().hashCode() : 0);
        result = 31 * result + (getSenderUsername() != null ? getSenderUsername().hashCode() : 0);
        result = 31 * result + (getEventId() != null ? getEventId().hashCode() : 0);
        result = 31 * result + (getEventName() != null ? getEventName().hashCode() : 0);
        return result;
    }
}
