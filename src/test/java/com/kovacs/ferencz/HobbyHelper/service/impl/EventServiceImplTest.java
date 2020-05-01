package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.EventRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.ChatMessageService;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventServiceImplTest {

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EventMapper eventMapper;

    @MockBean
    private ChatMessageService chatMessageService;

    @MockBean
    private EventParticipantService eventParticipantService;

    @Autowired
    private  EventServiceImpl underTest;

    private Event event;

    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        event = initEvent();
        eventDTO = initEventDTO();
    }

    @Test
    void createShouldCreateEntity() {
        //GIVEN
        given(eventMapper.toEntity(any(EventDTO.class))).willReturn(event);
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        given(eventRepository.save(any(Event.class))).willReturn(event);
        eventDTO.setCreatedAt(null);
        //WHEN
        EventDTO result = underTest.createEvent(eventDTO);
        //THEN
        BDDMockito.verify(eventRepository).save(event);
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void saveShouldSaveEntity() {
        //GIVEN
        given(eventMapper.toEntity(any(EventDTO.class))).willReturn(event);
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        given(eventRepository.save(any(Event.class))).willReturn(event);
        //WHEN
        EventDTO result = underTest.createEvent(eventDTO);
        //THEN
        BDDMockito.verify(eventRepository).save(event);
        assertEquals(eventDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(eventRepository.findAll()).willReturn(Arrays.asList(event));
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        //WHEN
        List<EventDTO> result = underTest.findAll();
        //THEN
        verify(eventRepository).findAll();
        assertEquals(Arrays.asList(eventDTO), result);
    }

    @Test
    void findAllByLocation() {
        //GIVEN
        given(eventRepository.findAllByLocation_Id(anyLong())).willReturn(Arrays.asList(event));
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        //WHEN
        List<EventDTO> result = underTest.findAllByLocation(1L);
        //THEN
        verify(eventRepository).findAllByLocation_Id(1L);
        assertEquals(Arrays.asList(eventDTO), result);
    }

    @Test
    void findAllByType() {
        //GIVEN
        given(eventRepository.findAllByType_Id(anyLong())).willReturn(Arrays.asList(event));
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        //WHEN
        List<EventDTO> result = underTest.findAllByType(1L);
        //THEN
        verify(eventRepository).findAllByType_Id(1L);
        assertEquals(Arrays.asList(eventDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(eventRepository.findById(anyLong())).willReturn(Optional.of(event));
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        //WHEN
        Optional<EventDTO> result = underTest.findOne(1L);
        //THEN
        verify(eventRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(eventDTO, result.get());
    }

    @Test
    void deleteShouldDeleteEntity() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void deleteShouldDeleteParticipants() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(eventParticipantService).deleteByEvent(1L);
    }

    @Test
    void deleteShouldDeleteChatMessagess() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(chatMessageService).deleteMessagesOfEvent(1L);
    }

    @Test
    void deleteEventsOfUser() {
        //GIVEN
        given(eventRepository.findAllByCreatedBy_Id(anyLong())).willReturn(Arrays.asList(event));
        //WHEN
        underTest.deleteEventsOfUser(1L);
        //THEN
        verify(eventRepository).findAllByCreatedBy_Id(1L);
        verify(eventRepository).deleteById(1L);
        verify(chatMessageService).deleteMessagesOfEvent(1L);
        verify(eventParticipantService).deleteByEvent(1L);
    }

    @Test
    void findByOwner() {
        //GIVEN
        given(eventRepository.findAllByCreatedBy_Id(anyLong())).willReturn(Arrays.asList(event));
        given(eventMapper.toDto(any(Event.class))).willReturn(eventDTO);
        //WHEN
        List<EventDTO> result = underTest.findByOwner(1L);
        //THEN
        verify(eventRepository).findAllByCreatedBy_Id(1L);
        assertEquals(Arrays.asList(eventDTO), result);
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

    private EventDTO initEventDTO() {
        EventDTO result = new EventDTO();
        result.setLocationId(1L);
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedByUsername("user");
        result.setCreatedById(1L);
        result.setDescription("Description");
        result.setStartsAt(LocalDateTime.now().plusMinutes(5));
        result.setPrice(50.0);
        result.setTypeName("Sport");
        result.setTypeId(1L);
        result.setName("Event");
        result.setId(1L);
        return result;
    }

    private Event initEvent() {
        Event result = new Event();
        result.setLocation(initLocation());
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedBy(initUser());
        result.setDescription("Description");
        result.setStartsAt(LocalDateTime.now().plusMinutes(5));
        result.setPrice(50.0);
        EventType type = new EventType();
        type.setId(1L);
        type.setDescription("Sport");
        type.setName("Sport");
        result.setType(type);
        result.setName("Event");
        result.setId(1L);
        return result;
    }

    private Location initLocation() {
        Location result = new Location();
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
}