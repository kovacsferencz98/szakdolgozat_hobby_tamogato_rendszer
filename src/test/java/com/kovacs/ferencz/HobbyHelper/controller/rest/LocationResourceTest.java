package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LocationResourceTest {

    @MockBean
    private LocationService locationService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private EventService eventService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private LocationResource underTest;

    private LocationDTO location;

    @BeforeEach
    void setUp() {
        location = initLocation();
    }


    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createLocationShouldThrowExceptionIfIdIsSet() {
        //GIVEN
        LocationDTO incomingLocation = createNewLocation();
        incomingLocation.setId(2L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id set");
        //WHEN
        Exception exception = assertThrows(LocationResource.LocationResourceException.class, () -> {
            underTest.createLocation(incomingLocation);
        });
        //THEN
        assertEquals("Id set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void createLocationShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        LocationDTO incomingLocation = createNewLocation();
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(Exception.class, () -> {
            underTest.createLocation(incomingLocation);
        });
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createLocationShouldSaveEntity() throws Exception {
        //GIVEN
        LocationDTO incomingLocation = createNewLocation();
        given(locationService.save(any(LocationDTO.class))).willReturn(location);
        //WHEN
        ResponseEntity<LocationDTO> repsonse = underTest.createLocation(incomingLocation);
        //THEN
        verify(locationService).save(incomingLocation);
        assertEquals(location, repsonse.getBody());
        assertEquals(HttpStatus.CREATED, repsonse.getStatusCode());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateLocationShouldThrowExceptionIfIdIsNotSet() {
        //GIVEN
        LocationDTO incomingLocation = createNewLocation();
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id not set");
        //WHEN
        Exception exception = assertThrows(LocationResource.LocationResourceException.class, () -> {
            underTest.updateLocation(incomingLocation);
        });
        //THEN
        assertEquals("Id not set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void updateLocationShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        given(userDetailsService.findAllByResidence(anyLong())).willReturn(new ArrayList<>());
        given(eventService.findAllByLocation(anyLong())).willReturn(new ArrayList<>());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.updateLocation(location);
        });
        //THEN
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateLocationShouldUpdateEntity() throws Exception {
        //GIVEN
        given(locationService.save(any(LocationDTO.class))).willReturn(location);
        //WHEN
        ResponseEntity<LocationDTO> repsonse = underTest.updateLocation(location);
        //THEN
        verify(locationService).save(location);
        assertEquals(location, repsonse.getBody());
        assertEquals(HttpStatus.OK, repsonse.getStatusCode());
    }

    @Test
    void getAllLocationsShouldReturnLocations() {
        //GIVEN
        given(locationService.findAll()).willReturn(Arrays.asList(location));
        //WHEN
        List<LocationDTO> result = underTest.getAllLocations();
        //THEN
        assertEquals(Arrays.asList(location), result);
    }

    @Test
    void getLocationShouldReturnNotFoundResponseWhenNoLocationIsFound() {
        //GIVEN
        given(locationService.findOne(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<LocationDTO> result = underTest.getLocation(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getLocationShouldReturnFoundLocation() {
        //GIVEN
        given(locationService.findOne(anyLong())).willReturn(Optional.of(location));
        //WHEN
        ResponseEntity<LocationDTO> result = underTest.getLocation(1L);
        //THEN
        assertEquals(location, result.getBody());
    }


    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteLocationShouldThrowExceptionIfStillInUse() {
        //GIVEN
        given(eventService.findAllByLocation(anyLong())).willReturn(Arrays.asList(initEvent()));
        given(userDetailsService.findAllByResidence(anyLong())).willReturn(Arrays.asList(initUserDetail()));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Still in use");
        //WHEN
        Exception exception = assertThrows(LocationResource.LocationResourceException.class, () -> {
            underTest.deleteLocation(1L);
        });
        //THEN
        assertEquals("Still in use", exception.getMessage());
    }

    @Test
    @WithMockUser(username="notUser", roles = {"USER"})
    void deleteLocationShouldThrowExceptionIfNotAuthorized() {
        //GIVEN
        given(locationService.findOne(anyLong())).willReturn(Optional.of(location));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(Exception.class, () -> {
            underTest.deleteLocation(1L);
        });
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteLocationShouldDeleteEntity() {
        //GIVEN
        given(eventService.findAllByLocation(anyLong())).willReturn(new ArrayList<>());
        given(userDetailsService.findAllByResidence(anyLong())).willReturn(new ArrayList<>());
        given(locationService.findOne(anyLong())).willReturn(Optional.of(location));
        //WHEN
        ResponseEntity<Void> response = underTest.deleteLocation(1L);
        //THEN
        verify(locationService).delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private LocationDTO initLocation() {
        LocationDTO result = new LocationDTO();
        result.setId(1L);
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        return result;
    }

    private LocationDTO createNewLocation() {
        LocationDTO result = new LocationDTO();
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        return result;
    }

    private EventDTO initEvent() {
        EventDTO result = new EventDTO();
        result.setLocationId(1L);
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedById(1L);
        result.setCreatedByUsername("user1");
        result.setDescription("Description");
        result.setStartsAt(LocalDateTime.now().plusMinutes(5));
        result.setPrice(50.0);
        result.setTypeId(1L);
        result.setTypeName("Sport");
        result.setName("Event");
        result.setId(1L);
        return result;
    }

    public UserDetailsDTO initUserDetail() {
        UserDetailsDTO result = new UserDetailsDTO();
        result.setProfilePicId(1L);
        result.setUserUsername("user");
        result.setUserId(1L);
        result.setResidenceId(1L);
        result.setDescription("Description");
        return result;
    }
}