package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.EventDetailVM;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RatingVM;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventControllerTest {

    @MockBean
    private EventService eventService;

    @MockBean
    private EventTypeService eventTypeService;

    @MockBean
    private EventParticipantService eventParticipantService;

    @MockBean
    private UserService userService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    SecurityContext securityContext;

    @MockBean
    Authentication authentication;

    @Autowired
    EventController underTest;

    private EventDTO event;

    private LocationDTO location;

    private User user;

    private List<EventParticipantDTO> participants;

    @BeforeEach
    void setUp() {
        event = initEvent();
        location = initLocation();
        user = initUser();
        participants = initParticipants();
    }

    @Test
    void getEventDetailShouldReturnNotFoundWhenEventNotFound() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event found");
        //WHEN
        ResponseEntity<EventDetailVM> response = underTest.getEventDetail(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getEventDetailShouldReturnNotFoundWhenNoLocationFound() {
        //GIVEN
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(locationService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No location found");
        //WHEN
        ResponseEntity<EventDetailVM> response = underTest.getEventDetail(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getEventDetailShouldReturnNotFoundWhenNoOwnerFound() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(locationService.findOne(anyLong())).willReturn(Optional.of(location));

        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No logged in user");
        //WHEN
        ResponseEntity<EventDetailVM> response = underTest.getEventDetail(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getEventDetailShouldReturnEventDetail() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(locationService.findOne(anyLong())).willReturn(Optional.of(location));
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        //WHEN
        ResponseEntity<EventDetailVM> response = underTest.getEventDetail(1L);
        //THEN
        EventDetailVM eventDetail = response.getBody();
        assertEquals(event, eventDetail.getEventDetails());
        assertEquals(location, eventDetail.getEventLocation());
        assertEquals(participants, eventDetail.getParticipants());
        assertEquals(0, eventDetail.getRatingOfEventByUser());
        assertFalse(eventDetail.isOver());
        assertTrue(eventDetail.isOwner());
        assertFalse(eventDetail.isParticipant());
    }

    @Test
    void joinEventShouldThrowExceptionWhenNoLogedInUser() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No logged in user");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.joinEvent(1L);
        });
        //THEN
        assertEquals("No logged in user", exception.getMessage());
    }

    @Test
    void joinEventShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.joinEvent(1L);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    void joinEventShouldThrowExceptionWhenUserIsAlreadyParticipant() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User already participates");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.joinEvent(1L);
        });
        //THEN
        assertEquals("User already participates", exception.getMessage());
    }

    @Test
    void joinEventShouldThrowExceptionWhenUserIsOwner() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User is owner");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.joinEvent(1L);
        });
        //THEN
        assertEquals("User is owner", exception.getMessage());
    }

    @Test
    void joinEventShouldThrowExceptionWhenEventIsFull() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user3");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createNewUser()));
        event.setMaxAttendance(1);
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Event is full");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.joinEvent(1L);
        });
        //THEN
        assertEquals("Event is full", exception.getMessage());
    }

    @Test
    void joinEventShouldThrowExceptionWhenEventIsOver() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user3");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createNewUser()));
        event.setStartsAt(LocalDateTime.now().minusSeconds(5));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Event is over");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.joinEvent(1L);
        });
        //THEN
        assertEquals("Event is over", exception.getMessage());
    }

    @Test
    void joinEventShouldIncrementAttendance() throws Exception {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user3");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createNewUser()));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(eventParticipantService.save(any(EventParticipantDTO.class))).willReturn(new EventParticipantDTO());
        //WHEN
        underTest.joinEvent(1L);
        //THEN
        ArgumentCaptor<EventDTO> eventArg = ArgumentCaptor.forClass(EventDTO.class);
        BDDMockito.verify(eventService).save(eventArg.capture());
        assertEquals(2, eventArg.getValue().getCurrentAttendance());
    }

    @Test
    void joinEventShouldSaveEventParticipant() throws Exception {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user3");
        SecurityContextHolder.setContext(securityContext);
        User newUser = createNewUser();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(newUser));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(eventParticipantService.save(any(EventParticipantDTO.class))).willReturn(new EventParticipantDTO());
        //WHEN
        underTest.joinEvent(1L);
        //THEN
        ArgumentCaptor<EventParticipantDTO> eventParticipantArg = ArgumentCaptor.forClass(EventParticipantDTO.class);
        BDDMockito.verify(eventParticipantService).save(eventParticipantArg.capture());
        assertEquals(event.getId(), eventParticipantArg.getValue().getEventId());
        assertEquals(event.getName(), eventParticipantArg.getValue().getEventName());
        assertEquals(newUser.getId(), eventParticipantArg.getValue().getUserId());
        assertEquals(newUser.getUsername(), eventParticipantArg.getValue().getUserUsername());
    }

    @Test
    void joinEventShouldReturnResponse() throws Exception {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user3");
        SecurityContextHolder.setContext(securityContext);
        User newUser = createNewUser();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(newUser));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        EventParticipantDTO eventParticipant = new EventParticipantDTO();
        given(eventParticipantService.save(any(EventParticipantDTO.class))).willReturn(eventParticipant);
        //WHEN
        ResponseEntity<EventParticipantDTO> response = underTest.joinEvent(1L);
        //THEN
        assertEquals(eventParticipant, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void leaveEventShouldThrowExceptionWhenNoLogedInUser() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No logged in user");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.leaveEvent(1L);
        });
        //THEN
        assertEquals("No logged in user", exception.getMessage());
    }

    @Test
    void leaveEventShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.leaveEvent(1L);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    void leaveEventShouldThrowExceptionWhenNoUserDoesNotParticipate() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User not participant");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.leaveEvent(1L);
        });
        //THEN
        assertEquals("User not participant", exception.getMessage());
    }

    @Test
    void leaveEventShouldThrowExceptionWhenEventIsOver() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        event.setStartsAt(LocalDateTime.now().minusSeconds(5));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Event is over");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.leaveEvent(1L);
        });
        //THEN
        assertEquals("Event is over", exception.getMessage());
    }

    @Test
    void leaveEventShouldThrowExceptionWhenUserIsOwner() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User is owner");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.leaveEvent(1L);
        });
        //THEN
        assertEquals("User is owner", exception.getMessage());
    }

    @Test
    void leaveEventShouldDecrementAttendance() throws Exception {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        //WHEN
        underTest.leaveEvent(1L);
        //THEN
        ArgumentCaptor<EventDTO> eventArg = ArgumentCaptor.forClass(EventDTO.class);
        verify(eventService).save(eventArg.capture());
        assertEquals(0, eventArg.getValue().getCurrentAttendance());
    }

    @Test
    void leaveEventShouldDeleteParticipant() throws Exception {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        //WHEN
        underTest.leaveEvent(1L);
        //THEN
        ArgumentCaptor<Long> eventParticipantIdArg = ArgumentCaptor.forClass(Long.class);
        verify(eventParticipantService).delete(eventParticipantIdArg.capture());
        assertEquals(participants.get(0).getId(), eventParticipantIdArg.getValue());
    }

    @Test
    void rateParticipantShouldThrowExceptionWhenNoParticipantFound() {
        //GIVEN

        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No participant");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.rateParticipant(1L, ratingVM);
        });
        //THEN
        assertEquals("No participant", exception.getMessage());
    }

    @Test
    void rateParticipantShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.rateParticipant(1L, ratingVM);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    void rateParticipantShouldThrowExceptionWhenNoLoggedInUser() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user logged in");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.rateParticipant(1L, ratingVM);
        });
        //THEN
        assertEquals("No user logged in", exception.getMessage());
    }

    @Test
    void rateParticipantShouldThrowExceptionWhenUserIsNotOwner() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User not owner");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.rateParticipant(1L, ratingVM);
        });
        //THEN
        assertEquals("User not owner", exception.getMessage());
    }

    @Test
    void rateParticipantShouldSaveParticipantWithNewRating() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        EventParticipantDTO participant = participants.get(0);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participant));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        //WHEN
        underTest.rateParticipant(1L, ratingVM);
        //THEN
        ArgumentCaptor<EventParticipantDTO> participantArg = ArgumentCaptor.forClass(EventParticipantDTO.class);
        verify(eventParticipantService).save(participantArg.capture());
        assertEquals(participant.getId(), participantArg.getValue().getId());
        assertEquals(5, participantArg.getValue().getRatingOfParticipant());
    }

    @Test
    void approveParticipantShouldThrowExceptionWhenNoParticipantFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No participant");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.approveParticipant(1L);
        });
        //THEN
        assertEquals("No participant", exception.getMessage());
    }

    @Test
    void approveParticipantShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.approveParticipant(1L);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    void approveParticipantShouldThrowExceptionWhenNoLoggedInUser() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user logged in");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.approveParticipant(1L);
        });
        //THEN
        assertEquals("No user logged in", exception.getMessage());
    }

    @Test
    void approveParticipantShouldThrowExceptionWhenUserIsNotOwner() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User not owner");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.approveParticipant(1L);
        });
        //THEN
        assertEquals("User not owner", exception.getMessage());
    }

    @Test
    void approveParticipantShouldApproveParticipant() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        EventParticipantDTO participant = participants.get(0);
        participant.setApproved(false);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participant));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        //WHEN
        underTest.approveParticipant(1L);
        //THEN
        ArgumentCaptor<EventParticipantDTO> participantArg = ArgumentCaptor.forClass(EventParticipantDTO.class);
        verify(eventParticipantService).save(participantArg.capture());
        assertEquals(participant.getId(), participantArg.getValue().getId());
        assertEquals(true, participantArg.getValue().isApproved());
    }

    @Test
    void rateEventShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.rateEvent(1L, ratingVM);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    void rateEventShouldThrowExceptionWhenNoLoggedInUser() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user logged in");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.rateEvent(1L, ratingVM);
        });
        //THEN
        assertEquals("No user logged in", exception.getMessage());
    }


    @Test
    void rateEventShouldThrowExceptionWhenUserIsNotParticipant() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("User not participant");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.rateEvent(1L, ratingVM);
        });
        //THEN
        assertEquals("User not participant", exception.getMessage());
    }

    @Test
    void rateEventShouldModifyEventRating() {
        //GIVEN
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(5);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByEvent(anyLong())).willReturn(participants);
        //WHEN
        underTest.rateEvent(1L, ratingVM);
        //THEN
        ArgumentCaptor<EventParticipantDTO> participantArg = ArgumentCaptor.forClass(EventParticipantDTO.class);
        verify(eventParticipantService).save(participantArg.capture());
        assertEquals(participants.get(0).getId(), participantArg.getValue().getId());
        assertEquals(5, participantArg.getValue().getRatingOfEvent());
    }

    @Test
    void getOwnEventsShouldThrowExceptionWhenNoLoggedInUser() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user logged in");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.getOwnEvents();
        });
        //THEN
        assertEquals("No user logged in", exception.getMessage());
    }

    @Test
    void getOwnEventsShouldReturnEvents() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(eventService.findByOwner(anyLong())).willReturn(Arrays.asList(event));
        //WHEN
        List<EventDTO> result = underTest.getOwnEvents();
        //THEN
        verify(eventService).findByOwner(user.getId());
        assertEquals(Arrays.asList(event), result);
    }

    @Test
    void getParticipateEventsShouldThrowExceptionWhenNoLoggedInUser() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user logged in");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.getParticipateEvents();
        });
        //THEN
        assertEquals("No user logged in", exception.getMessage());
    }

    @Test
    void getParticipateEventsShouldReturnEvents() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(eventParticipantService.findByUserId(anyLong())).willReturn(participants);
        //WHEN
        List<EventDTO> result = underTest.getParticipateEvents();
        //THEN
        verify(eventParticipantService).findByUserId(createParticipatingUser().getId());
        verify(eventService).findOne(event.getId());
        assertEquals(Arrays.asList(event), result);
    }

    @Test
    void getActiveEventsShouldReturnOnlyActive() {
        //GIVEN
        EventDTO activeEvent = initEvent();
        EventDTO inactiveEvent = initEvent();
        inactiveEvent.setStartsAt(LocalDateTime.now().minusSeconds(5));
        given(eventService.findAll()).willReturn(Arrays.asList(activeEvent, inactiveEvent));
        //WHEN
        List<EventDTO> result = underTest.getActiveEvents();
        //THEN
        assertEquals(Arrays.asList(activeEvent), result);
    }

    @Test
    void deleteParticipantShouldThrowExceptionWhenNoEventParticipantFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event participant");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.deleteParticipant(1L);
        });
        //THEN
        assertEquals("No event participant", exception.getMessage());
    }

    @Test
    void deleteParticipantShouldThrowExceptionWhenNoEventFound() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user2");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(createParticipatingUser()));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not owner");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.deleteParticipant(1L);
        });
        //THEN
        assertEquals("Not owner", exception.getMessage());
    }

    @Test
    void deleteParticipantShouldDeleteParticipant() {
        //GIVEN
        EventParticipantDTO eventParticipant = participants.get(0);
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventService.findOne(anyLong())).willReturn(Optional.of(event));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.deleteParticipant(eventParticipant.getId());
        //THEN
        verify(eventParticipantService).delete(eventParticipant.getId());
    }

    @Test
    void deleteParticipantShouldThrowExceptionWhenUserNotOwner() {
        //GIVEN
        given(eventParticipantService.findOne(anyLong())).willReturn(Optional.of(participants.get(0)));
        given(eventService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No event");
        //WHEN
        Exception exception = assertThrows(EventController.EventControllerException.class, () -> {
            underTest.deleteParticipant(1L);
        });
        //THEN
        assertEquals("No event", exception.getMessage());
    }

    @Test
    void deleteParticipant() {
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

    private List<EventParticipantDTO> initParticipants() {
        List<EventParticipantDTO> result = new ArrayList<>();
        EventParticipantDTO eventParticipant = new EventParticipantDTO();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(true);
        eventParticipant.setUserUsername("user2");
        eventParticipant.setUserId(2L);
        eventParticipant.setEventName("Event");
        eventParticipant.setEventId(1L);
        eventParticipant.setId(1L);
        result.add(eventParticipant);
        return result;
    }

    private User createParticipatingUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email2@email.com");
        result.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        result.setId(2L);
        result.setUsername("user2");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        return result;
    }

    private User createNewUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email3@email.com");
        result.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        result.setId(3L);
        result.setUsername("user3");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        return result;
    }
}