package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
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
class EventParticipantResourceTest {

    @MockBean
    private EventParticipantService eventParticipantService;

    @MockBean
    private EventService eventService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private EventParticipantResource undertest;

    private EventDTO event;

    private  User user;

    private EventParticipantDTO eventParticipant;

    @BeforeEach
    void setUp() {
        event = initEvent();
        user = initUser();
        eventParticipant = initParticipant();
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldThrowExceptionWhenIdAlreadySet() {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        incomingParticipant.setId(3L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id set");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.createEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("Id set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldThrowExceptionWhenEventNotExists() {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.createEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldThrowExceptionWhenUserAlreadyParticipates() {
        //GIVEN
        EventParticipantDTO incomingParticipant = initParticipant();
        incomingParticipant.setId(null);
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User already participates");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.createEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("User already participates", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldThrowExceptionWhenEventIsFull() {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        event.setMaxAttendance(1);
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Event full");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.createEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("Event full", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldThrowExceptionWhenEventIsOver() {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        event.setStartsAt(LocalDateTime.now().minusSeconds(5));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Event over");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.createEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("Event over", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldIncrementAttendance() throws Exception {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(eventParticipantService.create(any(EventParticipantDTO.class))).willReturn(initParticipant());
        //WHEN
        undertest.createEventParticipant(incomingParticipant);
        //THEN
        ArgumentCaptor<EventDTO> eventArg = ArgumentCaptor.forClass(EventDTO.class);
        verify(eventService).save(eventArg.capture());
        assertEquals(2, eventArg.getValue().getCurrentAttendance());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createEventParticipantShouldCreateParticipant() throws Exception {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(eventParticipantService.create(any(EventParticipantDTO.class))).willReturn(initParticipant());
        //WHEN
        undertest.createEventParticipant(incomingParticipant);
        //THEN
        ArgumentCaptor<EventParticipantDTO> eventParticipantArg = ArgumentCaptor.forClass(EventParticipantDTO.class);
        verify(eventParticipantService).create(eventParticipantArg.capture());
        assertEquals(event.getId(), eventParticipantArg.getValue().getEventId());
        assertEquals(event.getName(), eventParticipantArg.getValue().getEventName());
        assertEquals(incomingParticipant.getUserId(), eventParticipantArg.getValue().getUserId());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventParticipantShouldThrowExceptionWhenIdNotSet() {
        //GIVEN
        EventParticipantDTO incomingParticipant = createNewParticipant();
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id not set");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.updateEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("Id not set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventParticipantShouldThrowExceptionWhenParticipantNotFound() {
        //GIVEN
        EventParticipantDTO incomingParticipant = initParticipant();
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("not found");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.updateEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("not found", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventParticipantShouldThrowExceptionWhenDuplicateParticipant() {
        //GIVEN
        EventParticipantDTO incomingParticipant = initParticipant();
        incomingParticipant.setEventId(2L);
        EventParticipantDTO duplicate = initParticipant();
        incomingParticipant.setEventId(2L);
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(duplicate));
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("duplicate");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.updateEventParticipant(incomingParticipant);
        });
        //THEN
        assertEquals("duplicate", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void updateEventParticipantShouldHandleEventChange() throws Exception {
        //GIVEN
        EventParticipantDTO incomingParticipant = initParticipant();
        incomingParticipant.setEventId(2L);
        EventDTO secondEvent = initEvent();
        secondEvent.setId(2L);
        given(eventService.findOne(1L)).willReturn(Optional.of(event));
        given(eventService.findOne(2L)).willReturn(Optional.of(secondEvent));
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(new ArrayList<>());
        given(eventParticipantService.save(any(EventParticipantDTO.class))).willReturn(initParticipant());
        //WHEN
        undertest.updateEventParticipant(incomingParticipant);
        //THEN
        InOrder inOrder = BDDMockito.inOrder(eventService);
        ArgumentCaptor<EventDTO> firstEventArg = ArgumentCaptor.forClass(EventDTO.class);
        inOrder.verify(eventService).save(firstEventArg.capture());
        ArgumentCaptor<EventDTO> secondEventArg = ArgumentCaptor.forClass(EventDTO.class);
        inOrder.verify(eventService).save(secondEventArg.capture());
        assertEquals(event.getId(), firstEventArg.getValue().getId());
        assertEquals(0, firstEventArg.getValue().getCurrentAttendance());
        assertEquals(secondEvent.getId(), secondEventArg.getValue().getId());
        assertEquals(2, secondEventArg.getValue().getCurrentAttendance());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void updateEventParticipantShouldUpdateParticipant() throws Exception {
        //GIVEN
        EventParticipantDTO incomingParticipant = initParticipant();
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(eventParticipantService.save(any(EventParticipantDTO.class))).willReturn(initParticipant());
        //WHEN
        undertest.updateEventParticipant(incomingParticipant);
        //THEN
        ArgumentCaptor<EventParticipantDTO> eventParticipantArg = ArgumentCaptor.forClass(EventParticipantDTO.class);
        verify(eventParticipantService).save(eventParticipantArg.capture());
        assertEquals(event.getId(), eventParticipantArg.getValue().getEventId());
        assertEquals(event.getName(), eventParticipantArg.getValue().getEventName());
        assertEquals(incomingParticipant.getUserId(), eventParticipantArg.getValue().getUserId());
    }

    @Test
    void getAllEventParticipantsShouldReturnParticipants() {
        //GIVEN
        given(eventParticipantService.findAll()).willReturn(Arrays.asList(eventParticipant));
        //WHEN
        List<EventParticipantDTO> result = undertest.getAllEventParticipants();
        //THEN
        assertEquals(Arrays.asList(eventParticipant), result);
    }

    @Test
    void getEventParticipantShouldReturnNotFoundResponseWhenNoParticipantIsFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<EventParticipantDTO> result = undertest.getEventParticipant(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getEventParticipantShouldReturnFoundParticipant() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        //WHEN
        ResponseEntity<EventParticipantDTO> result = undertest.getEventParticipant(1L);
        //THEN
        assertEquals(eventParticipant, result.getBody());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventParticipantShouldThrowExceptionIfNoParticipantFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No participant");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.deleteEventParticipant(1L);
        });
        //THEN
        assertEquals("No participant", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER"})
    void deleteEventParticipantShouldThrowExceptionWhenNotAuthorized() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            undertest.deleteEventParticipant(1L);
        });
        //THEN
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventParticipantShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventParticipantResource.EventParticipantResourceException.class, () -> {
            undertest.deleteEventParticipant(1L);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventParticipantShouldDecrementAttendance() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        //WHEN
        undertest.deleteEventParticipant(1L);
        //THEN
        ArgumentCaptor<EventDTO> eventArg = ArgumentCaptor.forClass(EventDTO.class);
        verify(eventService).save(eventArg.capture());
        assertEquals(event.getId(), eventArg.getValue().getId());
        assertEquals(0, eventArg.getValue().getCurrentAttendance());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventParticipantShouldDeleteParticipant() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        //WHEN
        undertest.deleteEventParticipant(1L);
        //THEN
        verify(eventParticipantService).delete(1L);
    }

    private EventParticipantDTO initParticipant() {
        EventParticipantDTO eventParticipant = new EventParticipantDTO();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(true);
        eventParticipant.setUserUsername("user2");
        eventParticipant.setUserId(2L);
        eventParticipant.setEventName("Event");
        eventParticipant.setEventId(1L);
        eventParticipant.setId(1L);
        return eventParticipant;
    }

    private EventParticipantDTO createNewParticipant() {
        EventParticipantDTO eventParticipant = new EventParticipantDTO();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(false);
        eventParticipant.setUserUsername("user3");
        eventParticipant.setUserId(3L);
        eventParticipant.setEventName("Event");
        eventParticipant.setEventId(1L);
        eventParticipant.setId(null);
        return eventParticipant;
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
}