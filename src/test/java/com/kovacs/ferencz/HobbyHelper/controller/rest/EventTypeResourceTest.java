package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.EventTypeService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
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
class EventTypeResourceTest {

    @MockBean
    private EventTypeService eventTypeService;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private EventService eventService;

    @Autowired
    EventTypeResource underTest;

    private EventTypeDTO eventType;

    @BeforeEach
    void setUp() {
        eventType = createExistingEventType();
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventTypeShouldThrowExceptionIfIdIsSet() {
        //GIVEN
        EventTypeDTO incomingType = createNewEventType();
        incomingType.setId(2L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id set");
        //WHEN
        Exception exception = assertThrows(EventTypeResource.EventTypeResourceException.class, () -> {
            underTest.createEventType(incomingType);
        });
        //THEN
        assertEquals("Id set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventTypeShouldThrowExceptionIfNameAlreadyExists() {
        //GIVEN
        EventTypeDTO incomingType = createNewEventType();
        given(eventTypeService.findByName(anyString())).willReturn(Optional.of(eventType));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Name used");
        //WHEN
        Exception exception = assertThrows(EventTypeResource.EventTypeResourceException.class, () -> {
            underTest.createEventType(incomingType);
        });
        //THEN
        assertEquals("Name used", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void createEventTypeShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        EventTypeDTO incomingType = createNewEventType();
        given(eventTypeService.findByName(anyString())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(Exception.class, () -> {
            underTest.createEventType(incomingType);
        });
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createEventTypeShouldCreateEntity() throws Exception {
        //GIVEN
        EventTypeDTO incomingType = createNewEventType();
        given(eventTypeService.findByName(anyString())).willReturn(Optional.empty());
        given(eventTypeService.save(any(EventTypeDTO.class))).willReturn(eventType);
        //WHEN
        ResponseEntity<EventTypeDTO> response = underTest.createEventType(incomingType);
        //THEN
        verify(eventTypeService).save(incomingType);
        assertEquals(eventType, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventTypeShouldThrowExceptionIfIdIsNotSet() {
        //GIVEN
        eventType.setId(null);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id not set");
        //WHEN
        Exception exception = assertThrows(EventTypeResource.EventTypeResourceException.class, () -> {
            underTest.updateEventType(eventType);
        });
        //THEN
        assertEquals("Id not set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateEventTypeShouldThrowExceptionIfNameAlreadyExists() {
        //GIVEN
        EventTypeDTO duplicate = createExistingEventType();
        duplicate.setId(2L);
        given(eventTypeService.findByName(anyString())).willReturn(Optional.of(duplicate));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Name used");
        //WHEN
        Exception exception = assertThrows(EventTypeResource.EventTypeResourceException.class, () -> {
            underTest.updateEventType(eventType);
        });
        //THEN
        assertEquals("Name used", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void updateEventTypeShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        EventTypeDTO incomingType = createExistingEventType();
        given(eventTypeService.findByName(anyString())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(Exception.class, () -> {
            underTest.updateEventType(incomingType);
        });
    }



    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void  updateEventTypeShouldUpdateEntity() throws Exception {
        //GIVEN
        EventTypeDTO incomingType = createExistingEventType();
        given(eventTypeService.findByName(anyString())).willReturn(Optional.empty());
        given(eventTypeService.save(any(EventTypeDTO.class))).willReturn(eventType);
        //WHEN
        ResponseEntity<EventTypeDTO> response = underTest.updateEventType(incomingType);
        //THEN
        verify(eventTypeService).save(incomingType);
        assertEquals(eventType, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllEventTypesShouldReturnEvents() {
        //GIVEN
        given(eventTypeService.findAll()).willReturn(Arrays.asList(eventType));
        //WHEN
        List<EventTypeDTO> result = underTest.getAllEventTypes();
        //THEN
        assertEquals(Arrays.asList(eventType), result);
    }

    @Test
    void getEventShouldReturnNotFoundResponseWhenNoParticipantIsFound() {
        //GIVEN
        given(eventTypeService.findOne(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<EventTypeDTO> result = underTest.getEventType(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getEventShouldReturnFoundParticipant() {
        //GIVEN
        given(eventTypeService.findOne(anyLong())).willReturn(Optional.of(eventType));
        //WHEN
        ResponseEntity<EventTypeDTO> result = underTest.getEventType(1L);
        //THEN
        assertEquals(eventType, result.getBody());
    }


    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void deleteEventTypeShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        given(eventService.findAllByType(anyLong())).willReturn(new ArrayList<>());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(Exception.class, () -> {
            underTest.deleteEventType(1L);
        });
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventTypeShouldThrowExceptionUsed() {
        //GIVEN
        given(eventService.findAllByType(anyLong())).willReturn(Arrays.asList(createEvent()));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("In use");
        //WHEN
        Exception exception = assertThrows(EventTypeResource.EventTypeResourceException.class, () -> {
            underTest.deleteEventType(1L);
        });
        //THEN
        assertEquals("In use", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteEventTypeShouldDeleteEntity() {
        //GIVEN
        given(eventService.findAllByType(anyLong())).willReturn(new ArrayList<>());
        //WHEN
        ResponseEntity<Void> response = underTest.deleteEventType(1L);
        //THEN
        verify(eventTypeService).delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
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

    private EventTypeDTO createNewEventType() {
        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        eventTypeDTO.setBannerUrl("/");
        eventTypeDTO.setDescription("Description");
        eventTypeDTO.setName("Cinema");
        eventTypeDTO.setIconUrl("/");
        return eventTypeDTO;
    }

    private EventDTO createEvent() {
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
}