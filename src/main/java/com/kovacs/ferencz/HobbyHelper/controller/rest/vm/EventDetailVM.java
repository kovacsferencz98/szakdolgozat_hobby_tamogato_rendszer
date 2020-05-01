package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;

import java.util.List;

public class EventDetailVM {
    private EventDTO eventDetails;
    private LocationDTO eventLocation;
    private List<EventParticipantDTO> participants;
    private Integer ratingOfEventByUser;
    private boolean isOwner;
    private boolean isParticipant;
    private boolean isOver;

    public EventDTO getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDTO eventDetails) {
        this.eventDetails = eventDetails;
    }

    public LocationDTO getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(LocationDTO eventLocation) {
        this.eventLocation = eventLocation;
    }

    public List<EventParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<EventParticipantDTO> participants) {
        this.participants = participants;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public boolean isParticipant() {
        return isParticipant;
    }

    public void setParticipant(boolean participant) {
        isParticipant = participant;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    public Integer getRatingOfEventByUser() {
        return ratingOfEventByUser;
    }

    public void setRatingOfEventByUser(Integer ratingOfEventByUser) {
        this.ratingOfEventByUser = ratingOfEventByUser;
    }
}
