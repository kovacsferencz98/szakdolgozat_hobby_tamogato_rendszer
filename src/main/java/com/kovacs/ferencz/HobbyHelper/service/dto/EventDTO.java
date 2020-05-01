package com.kovacs.ferencz.HobbyHelper.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A DTO for the {@link com.kovacs.ferencz.HobbyHelper.domain.Event} entity.
 */
public class EventDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Integer maxAttendance;

    @NotNull
    private Integer minAttendance;

    @NotNull
    private Integer currentAttendance;

    private LocalDateTime createdAt;

    @NotNull
    @DateTimeFormat(pattern= "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startsAt;

    @NotNull
    private Double price;

    @NotNull
    private Long typeId;

    @NotNull
    private String typeName;

    @NotNull
    private Long locationId;

    @NotNull
    private Long createdById;

    @NotNull
    private String createdByUsername;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxAttendance() {
        return maxAttendance;
    }

    public void setMaxAttendance(Integer maxAttendance) {
        this.maxAttendance = maxAttendance;
    }

    public Integer getMinAttendance() {
        return minAttendance;
    }

    public void setMinAttendance(Integer minAttendance) {
        this.minAttendance = minAttendance;
    }

    public Integer getCurrentAttendance() {
        return currentAttendance;
    }

    public void setCurrentAttendance(Integer currentAttendance) {
        this.currentAttendance = currentAttendance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long eventTypeId) {
        this.typeId = eventTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long userId) {
        this.createdById = userId;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventDTO eventDTO = (EventDTO) o;

        if (getId() != null ? !getId().equals(eventDTO.getId()) : eventDTO.getId() != null) return false;
        if (getName() != null ? !getName().equals(eventDTO.getName()) : eventDTO.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(eventDTO.getDescription()) : eventDTO.getDescription() != null)
            return false;
        if (getMaxAttendance() != null ? !getMaxAttendance().equals(eventDTO.getMaxAttendance()) : eventDTO.getMaxAttendance() != null)
            return false;
        if (getMinAttendance() != null ? !getMinAttendance().equals(eventDTO.getMinAttendance()) : eventDTO.getMinAttendance() != null)
            return false;
        if (getCurrentAttendance() != null ? !getCurrentAttendance().equals(eventDTO.getCurrentAttendance()) : eventDTO.getCurrentAttendance() != null)
            return false;
        if (getCreatedAt() != null ? !getCreatedAt().equals(eventDTO.getCreatedAt()) : eventDTO.getCreatedAt() != null)
            return false;
        if (getStartsAt() != null ? !getStartsAt().equals(eventDTO.getStartsAt()) : eventDTO.getStartsAt() != null)
            return false;
        if (getPrice() != null ? !getPrice().equals(eventDTO.getPrice()) : eventDTO.getPrice() != null) return false;
        if (getTypeId() != null ? !getTypeId().equals(eventDTO.getTypeId()) : eventDTO.getTypeId() != null)
            return false;
        if (getTypeName() != null ? !getTypeName().equals(eventDTO.getTypeName()) : eventDTO.getTypeName() != null)
            return false;
        if (getLocationId() != null ? !getLocationId().equals(eventDTO.getLocationId()) : eventDTO.getLocationId() != null)
            return false;
        if (getCreatedById() != null ? !getCreatedById().equals(eventDTO.getCreatedById()) : eventDTO.getCreatedById() != null)
            return false;
        return getCreatedByUsername() != null ? getCreatedByUsername().equals(eventDTO.getCreatedByUsername()) : eventDTO.getCreatedByUsername() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getMaxAttendance() != null ? getMaxAttendance().hashCode() : 0);
        result = 31 * result + (getMinAttendance() != null ? getMinAttendance().hashCode() : 0);
        result = 31 * result + (getCurrentAttendance() != null ? getCurrentAttendance().hashCode() : 0);
        result = 31 * result + (getCreatedAt() != null ? getCreatedAt().hashCode() : 0);
        result = 31 * result + (getStartsAt() != null ? getStartsAt().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        result = 31 * result + (getTypeId() != null ? getTypeId().hashCode() : 0);
        result = 31 * result + (getTypeName() != null ? getTypeName().hashCode() : 0);
        result = 31 * result + (getLocationId() != null ? getLocationId().hashCode() : 0);
        result = 31 * result + (getCreatedById() != null ? getCreatedById().hashCode() : 0);
        result = 31 * result + (getCreatedByUsername() != null ? getCreatedByUsername().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", maxAttendance=" + getMaxAttendance() +
                ", minAttendance=" + getMinAttendance() +
                ", currentAttendance=" + getCurrentAttendance() +
                ", createdAt='" + getCreatedAt() + "'" +
                ", startsAt='" + getStartsAt() + "'" +
                ", price=" + getPrice() +
                ", type=" + getTypeId() +
                ", location=" + getLocationId() +
                ", createdBy=" + getCreatedById() +
                "}";
    }
}
