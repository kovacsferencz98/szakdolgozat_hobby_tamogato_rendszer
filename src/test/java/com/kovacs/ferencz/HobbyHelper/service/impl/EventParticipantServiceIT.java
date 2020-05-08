package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.EventParticipantResource;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventParticipantMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventParticipantServiceIT {

    private static Logger logger = LoggerFactory.getLogger(EventParticipantServiceIT.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    EventParticipantRepository eventParticipantRepository;

    @Autowired
    EventParticipantMapper eventParticipantMapper;

    @Autowired
    private EventParticipantServiceImpl underTest;

    private EventParticipant eventParticipant;


    @BeforeEach
    void setUp() {
        clearDatabase();
        eventParticipant = initEventParticipant();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    void createShouldCreateDatabaseEntity() {
        //GIVEN
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        EventParticipantDTO response = underTest.create(eventParticipantDTO);
        //THEN
        assertNotNull(response.getJoinedAt());
        eventParticipant.setJoinedAt(response.getJoinedAt());
        Optional<EventParticipant> saved = eventParticipantRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        EventParticipant participant = saved.get();
        eventParticipant.setId(participant.getId());
        assertEquals(eventParticipant, participant);
    }

    @Test
    @Transactional
    void saveShouldCreateDatabaseEntity() {
        //GIVEN
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        EventParticipantDTO response = underTest.save(eventParticipantDTO);
        //THEN
        Optional<EventParticipant> saved = eventParticipantRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        EventParticipant participant = saved.get();
        eventParticipant.setId(participant.getId());
        assertEquals(eventParticipant, participant);
    }

    @Test
    @Transactional
    void findAllShouldReturnEntities() {
        //GIVEN
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        List<EventParticipantDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventParticipantMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findOneShouldReturnEntity() {
        //GIVEN
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        Optional<EventParticipantDTO> result = underTest.findOne(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(eventParticipantMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void findByEventShouldReturnEntity() {
        //GIVEN
        Long eventId = eventParticipant.getEvent().getId();
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        List<EventParticipantDTO> result = underTest.findByEvent(eventId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventParticipantMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findByUserShouldReturnEntity() {
        //GIVEN
        Long userId = eventParticipant.getUser().getId();
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        List<EventParticipantDTO> result = underTest.findByUserId(userId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventParticipantMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void deleteShouldDeleteEntity() {
        //GIVEN
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        underTest.delete(saved.getId());
        //THEN
        Optional<EventParticipant> entity = eventParticipantRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    @Test
    @Transactional
    void deleteByEventShouldDeleteEntity() {
        //GIVEN
        Long eventId = eventParticipant.getEvent().getId();
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        underTest.deleteByEvent(eventId);
        //THEN
        Optional<EventParticipant> entity = eventParticipantRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    @Test
    @Transactional
    void deleteByUserShouldDeleteEntity() {
        //GIVEN
        Long userId = eventParticipant.getUser().getId();
        EventParticipant saved = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        underTest.deleteByUser(userId);
        //THEN
        Optional<EventParticipant> entity = eventParticipantRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }


    private void clearDatabase() {
        eventParticipantRepository.deleteAll();
        eventParticipantRepository.flush();
        eventRepository.deleteAll();
        eventRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
    }

    private EventParticipant initEventParticipant() {
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(true);
        eventParticipant.setUser(initParticipantUser());
        eventParticipant.setEvent(initEvent());
        eventParticipant.setId(1L);
        eventParticipant.setParticipated(false);
        return eventParticipant;
    }

    private User initParticipantUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email2@email.com");
        Role role = new Role(AuthoritiesConstants.USER);
        roleRepository.saveAndFlush(role);
        result.setRoles(new HashSet(Arrays.asList(role)));
        result.setId(2L);
        result.setUsername("participant");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        result = userRepository.saveAndFlush(result);
        return result;
    }

    private User initOwnerUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        Role role = new Role(AuthoritiesConstants.USER);
        roleRepository.saveAndFlush(role);
        result.setRoles(new HashSet(Arrays.asList(role)));
        result.setId(1L);
        result.setUsername("user");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        result = userRepository.saveAndFlush(result);
        return result;
    }

    private Event initEvent() {
        Event result = new Event();
        result.setLocation(initLocation());
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedBy(initOwnerUser());
        result.setDescription("Description");
        result.setStartsAt(LocalDateTime.now().plusMinutes(5));
        result.setPrice(50.0);
        EventType type = new EventType();
        type.setId(1L);
        type.setDescription("Sport");
        type.setName("Sport");
        type.setBannerUrl("/");
        type.setIconUrl("/");
        type = eventTypeRepository.saveAndFlush(type);
        result.setType(type);
        result.setName("Event");
        result.setId(1L);
        result = eventRepository.saveAndFlush(result);
        return result;
    }

    private Location initLocation() {
        Location result = new Location();
        result.setId(1L);
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setStreet("Egyetem ter");
        result.setNumber(1);
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        result = locationRepository.saveAndFlush(result);
        return result;
    }
}
