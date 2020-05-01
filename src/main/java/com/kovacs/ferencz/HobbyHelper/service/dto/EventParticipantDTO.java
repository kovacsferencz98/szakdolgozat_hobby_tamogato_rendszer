package com.kovacs.ferencz.HobbyHelper.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.kovacs.ferencz.HobbyHelper.domain.EventParticipant} entity.
 */
public class EventParticipantDTO implements Serializable {

    private Long id;

    @DateTimeFormat(pattern= "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime joinedAt;

    @NotNull
    private Boolean approved;

    @NotNull
    private Boolean participated;

    private Integer ratingOfParticipant;

    private Integer ratingOfEvent;

    @NotNull
    private Long userId;

    @NotNull
    private String userUsername;

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

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean isParticipated() {
        return participated;
    }

    public void setParticipated(Boolean participated) {
        this.participated = participated;
    }

    public Integer getRatingOfParticipant() {
        return ratingOfParticipant;
    }

    public void setRatingOfParticipant(Integer ratingOfParticipant) {
        this.ratingOfParticipant = ratingOfParticipant;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
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

    public Boolean getApproved() {
        return approved;
    }

    public Boolean getParticipated() {
        return participated;
    }

    public Integer getRatingOfEvent() {
        return ratingOfEvent;
    }

    public void setRatingOfEvent(Integer ratingOfEvent) {
        this.ratingOfEvent = ratingOfEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventParticipantDTO that = (EventParticipantDTO) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getJoinedAt() != null ? !getJoinedAt().equals(that.getJoinedAt()) : that.getJoinedAt() != null)
            return false;
        if (getApproved() != null ? !getApproved().equals(that.getApproved()) : that.getApproved() != null)
            return false;
        if (getParticipated() != null ? !getParticipated().equals(that.getParticipated()) : that.getParticipated() != null)
            return false;
        if (getRatingOfParticipant() != null ? !getRatingOfParticipant().equals(that.getRatingOfParticipant()) : that.getRatingOfParticipant() != null)
            return false;
        if (getRatingOfEvent() != null ? !getRatingOfEvent().equals(that.getRatingOfEvent()) : that.getRatingOfEvent() != null)
            return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null) return false;
        if (getUserUsername() != null ? !getUserUsername().equals(that.getUserUsername()) : that.getUserUsername() != null)
            return false;
        if (getEventId() != null ? !getEventId().equals(that.getEventId()) : that.getEventId() != null) return false;
        return getEventName() != null ? getEventName().equals(that.getEventName()) : that.getEventName() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getJoinedAt() != null ? getJoinedAt().hashCode() : 0);
        result = 31 * result + (getApproved() != null ? getApproved().hashCode() : 0);
        result = 31 * result + (getParticipated() != null ? getParticipated().hashCode() : 0);
        result = 31 * result + (getRatingOfParticipant() != null ? getRatingOfParticipant().hashCode() : 0);
        result = 31 * result + (getRatingOfEvent() != null ? getRatingOfEvent().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getUserUsername() != null ? getUserUsername().hashCode() : 0);
        result = 31 * result + (getEventId() != null ? getEventId().hashCode() : 0);
        result = 31 * result + (getEventName() != null ? getEventName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventParticipantDTO{" +
            "id=" + getId() +
            ", joinedAt='" + getJoinedAt() + "'" +
            ", approved='" + isApproved() + "'" +
            ", participated='" + isParticipated() + "'" +
            ", ratingOfParticipant=" + getRatingOfParticipant() +
            ", user=" + getUserId() +
            ", user='" + getUserUsername() + "'" +
            ", event=" + getEventId() +
            ", event='" + getEventName() + "'" +
            "}";
    }
}
