package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.AccountUpdateVM;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.ProfileDetailsVM;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.*;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProfileControllerTest {

    @MockBean
    private EventService eventService;

    @MockBean
    private EventTypeService eventTypeService;

    @MockBean
    private EventParticipantService eventParticipantService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfileController underTest;

    private EventDTO eventDTO;

    private EventDTO participatedEvent;

    private EventTypeDTO eventTypeDTO;

    private EventParticipantDTO eventParticipantDTO;

    private User user;

    private UserDetailsDTO userDetailsDTO;

    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        eventDTO = initEvent();
        participatedEvent = initParticipatedEvent();
        eventTypeDTO = createExistingEventType();
        eventParticipantDTO = initParticipant();
        user = initUser();
        userDetailsDTO = initUserDetail();
        locationDTO = createLocationDTO();
    }

    @Test
    void getProfileShouldReturnNotFoundResponseIfNoUserIsFound() {
        //GIVEN
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<ProfileDetailsVM> response = underTest.getProfile(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getProfileShouldReturnNotFoundResponseIfNoUserDetailsIsFound() {
        //GIVEN
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<ProfileDetailsVM> response = underTest.getProfile(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getProfileShouldReturnProperResponse() {
        //GIVEN
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(eventService.findByOwner(anyLong())).willReturn(Arrays.asList(eventDTO));
        given(eventParticipantService.findByUserId(anyLong())).willReturn(Arrays.asList(eventParticipantDTO));
        given(eventService.findOne(2L)).willReturn(Optional.of(participatedEvent));
        EventParticipantDTO participant = initParticipant();
        participant.setEventId(1L);
        participant.setRatingOfEvent(5);
        given(eventParticipantService.findByEvent(1L)).willReturn(Arrays.asList(participant));
        eventParticipantDTO.setRatingOfParticipant(4);
        given(eventParticipantService.findByEvent(1L)).willReturn(Arrays.asList(eventParticipantDTO));
        //WHEN
        ResponseEntity<ProfileDetailsVM> response = underTest.getProfile(1L);
        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getProfileShouldFilterActiveEvents() {
        //GIVEN
        eventDTO.setStartsAt(LocalDateTime.now().plusMinutes(5));
        participatedEvent.setStartsAt(LocalDateTime.now().minusSeconds(5));
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(eventService.findByOwner(anyLong())).willReturn(Arrays.asList(eventDTO));
        given(eventParticipantService.findByUserId(anyLong())).willReturn(Arrays.asList(eventParticipantDTO));
        given(eventService.findOne(2L)).willReturn(Optional.of(participatedEvent));
        EventParticipantDTO participant = initParticipant();
        participant.setEventId(1L);
        participant.setRatingOfEvent(5);
        given(eventParticipantService.findByEvent(1L)).willReturn(Arrays.asList(participant));
        eventParticipantDTO.setRatingOfParticipant(4);
        given(eventParticipantService.findByEvent(1L)).willReturn(Arrays.asList(eventParticipantDTO));
        //WHEN
        ResponseEntity<ProfileDetailsVM> response = underTest.getProfile(1L);
        //THEN
        ProfileDetailsVM profile = response.getBody();
        assertEquals(Arrays.asList(eventDTO), profile.getOwnEvents());
        assertTrue(profile.getParticipateEvents().isEmpty());
    }

    @Test
    void getProfileShouldCalculateScoreForCompletedEvents() {
        //GIVEN
        eventDTO.setStartsAt(LocalDateTime.now().minusSeconds(5));
        participatedEvent.setStartsAt(LocalDateTime.now().minusSeconds(5));
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(eventService.findByOwner(anyLong())).willReturn(Arrays.asList(eventDTO));
        given(eventParticipantService.findByUserId(anyLong())).willReturn(Arrays.asList(eventParticipantDTO));
        given(eventService.findOne(2L)).willReturn(Optional.of(participatedEvent));
        EventParticipantDTO participant = initParticipant();
        participant.setEventId(1L);
        participant.setRatingOfEvent(5);
        given(eventParticipantService.findByEvent(1L)).willReturn(Arrays.asList(participant));
        eventParticipantDTO.setRatingOfParticipant(4);
        given(eventParticipantService.findByEvent(2L)).willReturn(Arrays.asList(eventParticipantDTO));
        //WHEN
        ResponseEntity<ProfileDetailsVM> response = underTest.getProfile(1L);
        //THEN
        ProfileDetailsVM profile = response.getBody();
        assertEquals(5, profile.getRatingAsOwner());
        assertEquals(4, profile.getRatingAsParticipant());
    }

    @Test
    void getProfileShouldNotCalculateScoreForActiveEvents() {
        //GIVEN
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(eventService.findByOwner(anyLong())).willReturn(Arrays.asList(eventDTO));
        given(eventParticipantService.findByUserId(anyLong())).willReturn(Arrays.asList(eventParticipantDTO));
        given(eventService.findOne(2L)).willReturn(Optional.of(participatedEvent));
        EventParticipantDTO participant = initParticipant();
        participant.setEventId(1L);
        participant.setRatingOfEvent(5);
        given(eventParticipantService.findByEvent(1L)).willReturn(Arrays.asList(participant));
        eventParticipantDTO.setRatingOfParticipant(4);
        given(eventParticipantService.findByEvent(2L)).willReturn(Arrays.asList(eventParticipantDTO));
        //WHEN
        ResponseEntity<ProfileDetailsVM> response = underTest.getProfile(1L);
        //THEN
        ProfileDetailsVM profile = response.getBody();
        assertEquals(0, profile.getRatingAsOwner());
        assertEquals(0, profile.getRatingAsParticipant());
    }

    private User initUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        result.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        result.setId(1L);
        result.setUsername("user");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        return result;
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

    private LocationDTO createLocationDTO() {
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

    public UserDetailsDTO initUserDetail() {
        UserDetailsDTO result = new UserDetailsDTO();
        result.setProfilePicId(1L);
        result.setUserUsername("user");
        result.setUserId(1L);
        result.setResidenceId(1L);
        result.setDescription("Description");
        return result;
    }

    private EventParticipantDTO initParticipant() {
        EventParticipantDTO eventParticipant = new EventParticipantDTO();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(true);
        eventParticipant.setUserUsername("user2");
        eventParticipant.setUserId(2L);
        eventParticipant.setEventName("Part Event");
        eventParticipant.setEventId(2L);
        eventParticipant.setId(1L);
        return eventParticipant;
    }

    private EventTypeDTO createExistingEventType() {
        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        eventTypeDTO.setBannerUrl("/");
        eventTypeDTO.setDescription("Description");
        eventTypeDTO.setId(1L);
        eventTypeDTO.setName("Sport");
        eventTypeDTO.setIconUrl("/");
        return eventTypeDTO;
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

    private EventDTO  initParticipatedEvent() {
        EventDTO result = new EventDTO();
        result.setLocationId(1L);
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedById(2L);
        result.setCreatedByUsername("user2");
        result.setDescription("Description");
        result.setStartsAt(LocalDateTime.now().plusMinutes(5));
        result.setPrice(50.0);
        result.setTypeId(1L);
        result.setTypeName("Sport");
        result.setName("Part Event");
        result.setId(2L);
        return result;
    }
}