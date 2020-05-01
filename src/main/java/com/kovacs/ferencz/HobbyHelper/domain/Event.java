package com.kovacs.ferencz.HobbyHelper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "max_attendance", nullable = false)
    private Integer maxAttendance;

    @NotNull
    @Column(name = "min_attendance", nullable = false)
    private Integer minAttendance;

    @NotNull
    @Column(name = "current_attendance", nullable = false)
    private Integer currentAttendance;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE")
    @JsonIgnoreProperties("events")
    private EventType type;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "LOCATION")
    @JsonIgnoreProperties("event")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties("events")
    private User createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Event name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Event description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxAttendance() {
        return maxAttendance;
    }

    public Event maxAttendance(Integer maxAttendance) {
        this.maxAttendance = maxAttendance;
        return this;
    }

    public void setMaxAttendance(Integer maxAttendance) {
        this.maxAttendance = maxAttendance;
    }

    public Integer getMinAttendance() {
        return minAttendance;
    }

    public Event minAttendance(Integer minAttendance) {
        this.minAttendance = minAttendance;
        return this;
    }

    public void setMinAttendance(Integer minAttendance) {
        this.minAttendance = minAttendance;
    }

    public Integer getCurrentAttendance() {
        return currentAttendance;
    }

    public Event currentAttendance(Integer currentAttendance) {
        this.currentAttendance = currentAttendance;
        return this;
    }

    public void setCurrentAttendance(Integer currentAttendance) {
        this.currentAttendance = currentAttendance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Event createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public Event startsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
        return this;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public Double getPrice() {
        return price;
    }

    public Event price(Double price) {
        this.price = price;
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public EventType getType() {
        return type;
    }

    public Event type(EventType eventType) {
        this.type = eventType;
        return this;
    }

    public void setType(EventType eventType) {
        this.type = eventType;
    }

    public Location getLocation() {
        return location;
    }

    public Event location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Event createdBy(User userA) {
        this.createdBy = userA;
        return this;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (maxAttendance != null ? !maxAttendance.equals(event.maxAttendance) : event.maxAttendance != null)
            return false;
        if (minAttendance != null ? !minAttendance.equals(event.minAttendance) : event.minAttendance != null)
            return false;
        if (currentAttendance != null ? !currentAttendance.equals(event.currentAttendance) : event.currentAttendance != null)
            return false;
        if (createdAt != null ? !createdAt.equals(event.createdAt) : event.createdAt != null) return false;
        if (startsAt != null ? !startsAt.equals(event.startsAt) : event.startsAt != null) return false;
        if (price != null ? !price.equals(event.price) : event.price != null) return false;
        if (type != null ? !type.getId().equals(event.type.getId()) : event.type != null) return false;
        if (location != null ? !location.getId().equals(event.location.getId()) : event.location != null) return false;
        return createdBy != null ? createdBy.getId().equals(event.createdBy.getId()) : event.createdBy == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (maxAttendance != null ? maxAttendance.hashCode() : 0);
        result = 31 * result + (minAttendance != null ? minAttendance.hashCode() : 0);
        result = 31 * result + (currentAttendance != null ? currentAttendance.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (startsAt != null ? startsAt.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (type != null ? type.getId().hashCode() : 0);
        result = 31 * result + (location != null ? location.getId().hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxAttendance=" + maxAttendance +
                ", minAttendance=" + minAttendance +
                ", currentAttendance=" + currentAttendance +
                ", createdAt=" + createdAt +
                ", startsAt=" + startsAt +
                ", price=" + price +
                ", type=" + (type!=null ? type.getId() : "-") +
                ", location=" + (location!=null ? location.getId() : "-") +
                ", createdBy=" + (createdBy!=null ? createdBy.getId() : "-") +
                '}';
    }
}
