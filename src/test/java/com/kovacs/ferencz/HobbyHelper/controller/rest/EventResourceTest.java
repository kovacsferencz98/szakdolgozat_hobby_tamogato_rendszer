package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.CreateEventVM;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventResourceTest {

    @MockBean
    private EventService eventService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    EventResource underTest;

    private EventDTO event;

    private User user;

    private LocationDTO location;

    @BeforeEach
    void setUp() {
        event = initEvent();
        user = initUser();
        location = initLocation();
    }

    @Test
    @WithMockUser(username="notUser", roles = {"USER", })
    void createEventShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getEvent().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Unauthorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.createEvent(createEventVM);
        });
        //THEN
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventShouldThrowExceptionIfEventIdIsSet() {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getEvent().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("ID set");
        //WHEN
        Exception exception = assertThrows(EventResource.EventResourceException.class, () -> {
            underTest.createEvent(createEventVM);
        });
        //THEN
        assertEquals("ID set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventShouldThrowExceptionIfLocationIdIsSet() {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getLocation().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("ID set");
        //WHEN
        Exception exception = assertThrows(EventResource.EventResourceException.class, () -> {
            underTest.createEvent(createEventVM);
        });
        //THEN
        assertEquals("ID set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventShouldCreateLocation() throws Exception {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        given(locationService.save(any(LocationDTO.class))).willReturn(location);
        given(eventService.createEvent(any(EventDTO.class))).willReturn(event);
        //WHEN
        underTest.createEvent(createEventVM);
        //THEN
        ArgumentCaptor<LocationDTO> locationArg = ArgumentCaptor.forClass(LocationDTO.class);
        verify(locationService).save(locationArg.capture());
        assertEquals(createEventVM.getLocation(), locationArg.getValue());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventShouldCreateEvent() throws Exception {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        given(locationService.save(any(LocationDTO.class))).willReturn(location);
        given(eventService.createEvent(any(EventDTO.class))).willReturn(event);
        //WHEN
        underTest.createEvent(createEventVM);
        //THEN
        ArgumentCaptor<EventDTO> eventArg = ArgumentCaptor.forClass(EventDTO.class);
        verify(eventService).createEvent(eventArg.capture());
        assertEquals(location.getId(), eventArg.getValue().getLocationId());
        assertEquals(event.getCreatedById(), eventArg.getValue().getCreatedById());
        assertEquals(event.getName(), eventArg.getValue().getName());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventShouldReturnCreatedEvent() throws Exception {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        given(locationService.save(any(LocationDTO.class))).willReturn(location);
        given(eventService.createEvent(any(EventDTO.class))).willReturn(event);
        //WHEN
        ResponseEntity<EventDTO> response = underTest.createEvent(createEventVM);
        //THEN
        assertEquals(event, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @WithMockUser(username="notUser", roles = {"USER", })
    void updateEventShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getEvent().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Unauthorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.updateEvent(event);
        });
        //THEN
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventShouldThrowExceptionIfNoId() {
        //GIVEN
        event.setId(null);
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getEvent().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No id");
        //WHEN
        Exception exception = assertThrows(EventResource.EventResourceException.class, () -> {
            underTest.updateEvent(event);
        });
        //THEN
        assertEquals("No id", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventShouldUpdateEventEntity() throws Exception {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getEvent().setId(1L);
        given(eventService.save(any(EventDTO.class))).willReturn(event);
        //WHEN
        underTest.updateEvent(event);
        //THEN
        ArgumentCaptor<EventDTO> eventArg = ArgumentCaptor.forClass(EventDTO.class);
        verify(eventService).save(eventArg.capture());
        assertEquals(event, eventArg.getValue());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventShouldReturnUpdatedEventEntity() throws Exception {
        //GIVEN
        CreateEventVM createEventVM = createEventVM();
        createEventVM.getEvent().setId(1L);
        given(eventService.save(any(EventDTO.class))).willReturn(event);
        //WHEN
        ResponseEntity<EventDTO> response = underTest.updateEvent(event);
        //THEN
        assertEquals(event, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllEventsShouldReturnEvents() {
        //GIVEN
        given(eventService.findAll()).willReturn(Arrays.asList(event));
        //WHEN
        List<EventDTO> result = underTest.getAllEvents();
        //THEN
        assertEquals(Arrays.asList(event), result);
    }

    @Test
    void getEventShouldReturnNotFoundResponseWhenNoParticipantIsFound() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<EventDTO> result = underTest.getEvent(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getEventShouldReturnFoundParticipant() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        //WHEN
        ResponseEntity<EventDTO> result = underTest.getEvent(1L);
        //THEN
        assertEquals(event, result.getBody());
    }


    @Test
    void deleteEventShouldThrowExceptionIfNoEventFound() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventResource.EventResourceException.class, () -> {
            underTest.deleteEvent(1L);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    @WithMockUser(username="notUser", roles = {"USER"})
    void deleteEventShouldThrowExceptionIfNotAuthorized() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Unauthorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.deleteEvent(1L);
        });
        //THEN
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventShouldDeleteEntity() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        //WHEN
           ResponseEntity<Void> response = underTest.deleteEvent(1L);
        //THEN
        verify(eventService).delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
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

    private CreateEventVM createEventVM() {
        CreateEventVM createEventVM = new CreateEventVM();
        EventDTO eventDTO = initEvent();
        eventDTO.setId(null);
        createEventVM.setEvent(eventDTO);
        LocationDTO locationDTO = initLocation();
        locationDTO.setId(null);
        createEventVM.setLocation(locationDTO);
        return createEventVM;
    }
}