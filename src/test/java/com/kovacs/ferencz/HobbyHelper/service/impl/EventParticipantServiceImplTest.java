package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.EventParticipantRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventParticipantMapper;
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
class EventParticipantServiceImplTest {

    @MockBean
    private EventParticipantRepository eventParticipantRepository;

    @MockBean
    private EventParticipantMapper eventParticipantMapper;

    @Autowired
    private EventParticipantServiceImpl underTest;

    private EventParticipant eventParticipant;

    private EventParticipantDTO eventParticipantDTO;

    @BeforeEach
    void setUp() {
        eventParticipant = initParticipant();
        eventParticipantDTO = initParticipantDTO();
    }

    @Test
    void createShouldCreateEntity() {
        //GIVEN
        given(eventParticipantMapper.toEntity(any(EventParticipantDTO.class))).willReturn(eventParticipant);
        given(eventParticipantMapper.toDto(any(EventParticipant.class))).willReturn(eventParticipantDTO);
        given(eventParticipantRepository.save(any(EventParticipant.class))).willReturn(eventParticipant);
        eventParticipantDTO.setJoinedAt(null);
        //WHEN
        EventParticipantDTO result = underTest.create(eventParticipantDTO);
        //THEN
        BDDMockito.verify(eventParticipantRepository).save(eventParticipant);
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), result.getJoinedAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void saveShouldSaveEntity() {
        //GIVEN
        given(eventParticipantMapper.toEntity(any(EventParticipantDTO.class))).willReturn(eventParticipant);
        given(eventParticipantMapper.toDto(any(EventParticipant.class))).willReturn(eventParticipantDTO);
        given(eventParticipantRepository.save(any(EventParticipant.class))).willReturn(eventParticipant);
        //WHEN
        EventParticipantDTO result = underTest.save(eventParticipantDTO);
        //THEN
        BDDMockito.verify(eventParticipantRepository).save(eventParticipant);
        assertEquals(eventParticipantDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(eventParticipantRepository.findAll()).willReturn(Arrays.asList(eventParticipant));
        given(eventParticipantMapper.toDto(any(EventParticipant.class))).willReturn(eventParticipantDTO);
        //WHEN
        List<EventParticipantDTO> result = underTest.findAll();
        //THEN
        verify(eventParticipantRepository).findAll();
        assertEquals(Arrays.asList(eventParticipantDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(eventParticipantRepository.findById(anyLong())).willReturn(Optional.of(eventParticipant));
        given(eventParticipantMapper.toDto(any(EventParticipant.class))).willReturn(eventParticipantDTO);
        //WHEN
        Optional<EventParticipantDTO> result = underTest.findOne(1L);
        //THEN
        verify(eventParticipantRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(eventParticipantDTO, result.get());
    }

    @Test
    void deleteShouldDeleteEntity() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(eventParticipantRepository).deleteById(1L);
    }
    

    @Test
    void deleteByUser() {
        //GIVEN
        given(eventParticipantRepository.findAllByUser_Id(anyLong())).willReturn(Arrays.asList(eventParticipant));
        //WHEN
        underTest.deleteByUser(1L);
        //THEN
        verify(eventParticipantRepository).findAllByUser_Id(1L);
        verify(eventParticipantRepository).deleteById(1L);
    }

    @Test
    void deleteByEventShouldDeleteEntity() {
        //GIVEN
        given(eventParticipantRepository.findAllByEvent_Id(anyLong())).willReturn(Arrays.asList(eventParticipant));
        //WHEN
        underTest.deleteByEvent(1L);
        //THEN
        verify(eventParticipantRepository).findAllByEvent_Id(1L);
        verify(eventParticipantRepository).deleteById(1L);
    }

    @Test
    void findByEvent() {
        //GIVEN
        given(eventParticipantRepository.findAllByEvent_Id(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(eventParticipantMapper.toDto(any(EventParticipant.class))).willReturn(eventParticipantDTO);
        //WHEN
        List<EventParticipantDTO> result = underTest.findByEvent(1L);
        //THEN
        verify(eventParticipantRepository).findAllByEvent_Id(1L);
        assertEquals(Arrays.asList(eventParticipantDTO), result);
    }

    @Test
    void findByUserId() {
        //GIVEN
        given(eventParticipantRepository.findAllByUser_Id(anyLong())).willReturn(Arrays.asList(eventParticipant));
        given(eventParticipantMapper.toDto(any(EventParticipant.class))).willReturn(eventParticipantDTO);
        //WHEN
        List<EventParticipantDTO> result = underTest.findByUserId(1L);
        //THEN
        verify(eventParticipantRepository).findAllByUser_Id(1L);
        assertEquals(Arrays.asList(eventParticipantDTO), result);
    }

    private EventParticipantDTO initParticipantDTO() {
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

    private EventParticipant initParticipant() {
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(true);
        eventParticipant.setUser(initUser());
        eventParticipant.setEvent(initEvent());
        eventParticipant.setId(1L);
        return eventParticipant;
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