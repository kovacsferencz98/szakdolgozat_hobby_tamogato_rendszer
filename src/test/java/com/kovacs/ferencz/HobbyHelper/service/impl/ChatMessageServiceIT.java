package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.EventTypeResource;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.ChatMessageMapper;
import org.hibernate.loader.plan.build.internal.LoadGraphLoadPlanBuildingStrategy;
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

/**
 * Integration tests for {@link com.kovacs.ferencz.HobbyHelper.service.ChatMessageService}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatMessageServiceIT {

    private static Logger logger = LoggerFactory.getLogger(ChatMessageServiceIT.class);

    @Autowired
    private ChatMessageRepository chatMessageRepository;

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
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatMessageServiceImpl underTest;

    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        clearDatabase();
        logger.debug(chatMessageRepository.findAll().toString());
        chatMessage = initMessage();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    void assertThatMessageCanBeSaved() {
        //GIVEN
        ChatMessageDTO messageDTO = chatMessageMapper.toDto(chatMessage);
        //WHEN
        ChatMessageDTO response = underTest.save(messageDTO);
        //THEN
        Optional<ChatMessage> savedMessage = chatMessageRepository.findById(response.getId());
        assertTrue(savedMessage.isPresent());
        ChatMessage message = savedMessage.get();
        chatMessage.setId(message.getId());
        assertEquals(chatMessage, message);
    }

    @Test
    @Transactional
    void assertThatEveryMessageIsFound() {
        //GIVEN
        ChatMessage saved = chatMessageRepository.saveAndFlush(chatMessage);
        //WHEN
        List<ChatMessageDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(chatMessageMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void assertThatMessageIsFoundById() {
        //GIVEN
        ChatMessage saved = chatMessageRepository.saveAndFlush(chatMessage);
        //WHEN
        Optional<ChatMessageDTO> result = underTest.findOne(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(chatMessageMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void assertThatMessageIsFoundByEvent() {
        //GIVEN
        Long eventId = chatMessage.getEvent().getId();
        ChatMessage saved = chatMessageRepository.saveAndFlush(chatMessage);
        //WHEN
        List<ChatMessageDTO> result = underTest.findByEvent(eventId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(chatMessageMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void assertThatMessageIsDeletedById() {
        //GIVEN
        ChatMessage saved = chatMessageRepository.saveAndFlush(chatMessage);
        //WHEN
        underTest.delete(saved.getId());
        //THEN
        Optional<ChatMessage> entity = chatMessageRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    @Test
    @Transactional
    void assertThatMessageIsDeletedByEventId() {
        //GIVEN
        Long eventId = chatMessage.getEvent().getId();
        ChatMessage saved = chatMessageRepository.saveAndFlush(chatMessage);
        //WHEN
        underTest.deleteMessagesOfEvent(eventId);
        //THEN
        Optional<ChatMessage> entity = chatMessageRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    private void clearDatabase() {
        chatMessageRepository.deleteAll();
        chatMessageRepository.flush();
        eventRepository.deleteAll();
        eventRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
    }


    private ChatMessage initMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(1L);
        chatMessage.setText("Text");
        chatMessage.setId(1L);
        Event event = initEvent();
        chatMessage.setEvent(event);
        chatMessage.setSender(event.getCreatedBy());
        return chatMessage;
    }

    private ChatMessageDTO initMessageDTO() {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderUsername("user1");
        chatMessageDTO.setSenderId(1L);
        chatMessageDTO.setText("Text");
        chatMessageDTO.setEventName("Event");
        chatMessageDTO.setEventId(1L);
        chatMessageDTO.setId(1L);
        return chatMessageDTO;
    }

    private User initUser() {
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
        result.setCreatedBy(initUser());
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
