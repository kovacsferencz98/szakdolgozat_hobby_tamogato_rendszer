package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventServiceIT {
    
    private static Logger logger = LoggerFactory.getLogger(EventServiceIT.class);

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
    EventMapper eventMapper;

    @Autowired
    private EventServiceImpl underTest;

    private Event event;

    @BeforeEach
    void setUp() {
        clearDatabase();
        event = initEvent();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    void createShouldCreateDatabaseEntity() {
        //GIVEN
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        EventDTO response = underTest.createEvent(eventDTO);
        //THEN
        assertNotNull(response.getCreatedAt());
        event.setCreatedAt(response.getCreatedAt());
        Optional<Event> saved = eventRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        Event participant = saved.get();
        event.setId(participant.getId());
        assertEquals(event, participant);
    }

    @Test
    @Transactional
    void saveShouldCreateDatabaseEntity() {
        //GIVEN
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        EventDTO response = underTest.save(eventDTO);
        //THEN
        Optional<Event> saved = eventRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        Event participant = saved.get();
        event.setId(participant.getId());
        assertEquals(event, participant);
    }

    @Test
    @Transactional
    void findAllShouldReturnEntities() {
        //GIVEN
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        List<EventDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findAllByLocationShouldReturnEntities() {
        //GIVEN
        Long locationId = event.getLocation().getId();
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        List<EventDTO> result = underTest.findAllByLocation(locationId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findAllByTypeShouldReturnEntities() {
        //GIVEN
        Long typeID = event.getType().getId();
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        List<EventDTO> result = underTest.findAllByType(typeID);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findOneShouldReturnEntity() {
        //GIVEN
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        Optional<EventDTO> result = underTest.findOne(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(eventMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void findByOwnerShouldReturnEntity() {
        //GIVEN
        Long userId = event.getCreatedBy().getId();
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        List<EventDTO> result = underTest.findByOwner(userId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(eventMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void deleteShouldDeleteEntity() {
        //GIVEN
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        underTest.delete(saved.getId());
        //THEN
        Optional<Event> entity = eventRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    @Test
    @Transactional
    void deleteEventsOfUserShouldDeleteEntities() {
        //GIVEN
        Long userId = event.getCreatedBy().getId();
        Event saved = eventRepository.saveAndFlush(event);
        //WHEN
        underTest.deleteEventsOfUser(userId);
        //THEN
        Optional<Event> entity = eventRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    private void clearDatabase() {
        eventRepository.deleteAll();
        eventRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
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
