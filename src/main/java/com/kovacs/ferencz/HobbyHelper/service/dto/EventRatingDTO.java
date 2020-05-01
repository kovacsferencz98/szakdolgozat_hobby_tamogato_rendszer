package com.kovacs.ferencz.HobbyHelper.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kovacs.ferencz.HobbyHelper.domain.EventRating} entity.
 */
public class EventRatingDTO implements Serializable {

    private Long id;

    private Integer ratingOfEvent;


    private Long userId;

    private String userUsername;

    private Long eventId;

    private String eventName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRatingOfEvent() {
        return ratingOfEvent;
    }

    public void setRatingOfEvent(Integer ratingOfEvent) {
        this.ratingOfEvent = ratingOfEvent;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userAId) {
        this.userId = userAId;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userAUsername) {
        this.userUsername = userAUsername;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventRatingDTO that = (EventRatingDTO) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
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
        result = 31 * result + (getRatingOfEvent() != null ? getRatingOfEvent().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getUserUsername() != null ? getUserUsername().hashCode() : 0);
        result = 31 * result + (getEventId() != null ? getEventId().hashCode() : 0);
        result = 31 * result + (getEventName() != null ? getEventName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventRatingDTO{" +
            "id=" + getId() +
            ", ratingOfEvent=" + getRatingOfEvent() +
            ", user=" + getUserId() +
            ", user='" + getUserUsername() + "'" +
            ", event=" + getEventId() +
            ", event='" + getEventName() + "'" +
            "}";
    }
}
