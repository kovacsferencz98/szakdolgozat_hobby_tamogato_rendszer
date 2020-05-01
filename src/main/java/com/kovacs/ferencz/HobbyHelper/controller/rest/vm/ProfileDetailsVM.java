package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;

import java.util.List;

public class ProfileDetailsVM {
    private UserDTO user;
    private UserDetailsDTO userDetails;
    private List<EventDTO> ownEvents;
    private List<EventDTO> participateEvents;
    private Double ratingAsOwner;
    private Double ratingAsParticipant;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDetailsDTO getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsDTO userDetails) {
        this.userDetails = userDetails;
    }

    public List<EventDTO> getOwnEvents() {
        return ownEvents;
    }

    public void setOwnEvents(List<EventDTO> ownEvents) {
        this.ownEvents = ownEvents;
    }

    public List<EventDTO> getParticipateEvents() {
        return participateEvents;
    }

    public void setParticipateEvents(List<EventDTO> participateEvents) {
        this.participateEvents = participateEvents;
    }

    public Double getRatingAsOwner() {
        return ratingAsOwner;
    }

    public void setRatingAsOwner(Double ratingAsOwner) {
        this.ratingAsOwner = ratingAsOwner;
    }

    public Double getRatingAsParticipant() {
        return ratingAsParticipant;
    }

    public void setRatingAsParticipant(Double ratingAsParticipant) {
        this.ratingAsParticipant = ratingAsParticipant;
    }
}
