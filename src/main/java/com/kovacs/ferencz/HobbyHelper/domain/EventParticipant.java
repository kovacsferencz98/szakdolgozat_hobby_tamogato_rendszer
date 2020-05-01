package com.kovacs.ferencz.HobbyHelper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * A EventParticipant.
 */
@Entity
@Table(name = "event_participant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventParticipant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @NotNull
    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @NotNull
    @Column(name = "participated", nullable = false)
    private Boolean participated;

    @Column(name = "rating_of_participant")
    private Integer ratingOfParticipant;

    @Column(name = "rating_of_event")
    private Integer ratingOfEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("eventParticipants")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties("eventParticipants")
    private Event event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public EventParticipant joinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
        return this;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Boolean isApproved() {
        return approved;
    }

    public EventParticipant approved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean isParticipated() {
        return participated;
    }

    public EventParticipant participated(Boolean participated) {
        this.participated = participated;
        return this;
    }

    public void setParticipated(Boolean participated) {
        this.participated = participated;
    }

    public Integer getRatingOfParticipant() {
        return ratingOfParticipant;
    }

    public EventParticipant ratingOfParticipant(Integer ratingOfParticipant) {
        this.ratingOfParticipant = ratingOfParticipant;
        return this;
    }

    public void setRatingOfParticipant(Integer ratingOfParticipant) {
        this.ratingOfParticipant = ratingOfParticipant;
    }

    public User getUser() {
        return user;
    }

    public EventParticipant user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
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

    public EventParticipant event(Event event) {
        this.event = event;
        return this;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventParticipant that = (EventParticipant) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (joinedAt != null ? !joinedAt.equals(that.joinedAt) : that.joinedAt != null) return false;
        if (approved != null ? !approved.equals(that.approved) : that.approved != null) return false;
        if (participated != null ? !participated.equals(that.participated) : that.participated != null) return false;
        if (ratingOfParticipant != null ? !ratingOfParticipant.equals(that.ratingOfParticipant) : that.ratingOfParticipant != null)
            return false;
        if (ratingOfEvent != null ? !ratingOfEvent.equals(that.ratingOfEvent) : that.ratingOfEvent != null)
            return false;
        if (user != null ? !user.getId().equals(that.user.getId()) : that.user != null) return false;
        return event != null ? event.getId().equals(that.event.getId()) : that.event == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (joinedAt != null ? joinedAt.hashCode() : 0);
        result = 31 * result + (approved != null ? approved.hashCode() : 0);
        result = 31 * result + (participated != null ? participated.hashCode() : 0);
        result = 31 * result + (ratingOfParticipant != null ? ratingOfParticipant.hashCode() : 0);
        result = 31 * result + (ratingOfEvent != null ? ratingOfEvent.hashCode() : 0);
        result = 31 * result + (user != null ? user.getId().hashCode() : 0);
        result = 31 * result + (event != null ? event.getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventParticipant{" +
                "id=" + id +
                ", joinedAt=" + joinedAt +
                ", approved=" + approved +
                ", participated=" + participated +
                ", ratingOfParticipant=" + ratingOfParticipant +
                ", ratingOfEvent=" + ratingOfEvent +
                ", user=" + (user!=null ? user.getId() : "-") +
                ", event=" + (event!=null ? event.getId() : "-") +
                '}';
    }
}
