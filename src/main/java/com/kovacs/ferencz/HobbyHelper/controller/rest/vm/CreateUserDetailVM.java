package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;

import javax.validation.constraints.NotNull;

public class CreateUserDetailVM {
    @NotNull
    private UserDetailsDTO userDetails;
    @NotNull
    private LocationDTO location;

    public UserDetailsDTO getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsDTO userDetails) {
        this.userDetails = userDetails;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }
}
