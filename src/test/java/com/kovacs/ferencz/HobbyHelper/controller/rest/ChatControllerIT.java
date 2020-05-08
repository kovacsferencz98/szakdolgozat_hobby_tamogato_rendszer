package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.ChatMessageVM;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.ChatMessageService;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.ChatMessageMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventParticipantMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Integration tests for the {@link ChatController} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ChatControllerIT {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventParticipantService eventParticipantService;

    @Autowired
    private MessageSource messageSource;

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
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventParticipantMapper eventParticipantMapper;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private ChatController underTest;

    private Event event;

    private User user;

    private List<EventParticipantDTO> participants;

    private ChatMessage message;

    private Principal principal;

    private EventParticipant eventParticipant;


    @BeforeEach
    void setUp() {
        clearDatabase();
        eventParticipant = eventParticipantRepository.saveAndFlush(initEventParticipant());
        principal = initPrincipal();
        message = initMessage();
        MockitoAnnotations.initMocks(this);
        underTest = new ChatController(messagingTemplate, chatMessageService, eventService, userService, eventParticipantService, messageSource);
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    void getMessagesShouldSendPreviousMessages() throws Exception {
        //GIVEN
        ChatMessage savedMessage = chatMessageRepository.saveAndFlush(message);
        ChatMessageVM expectedMessage = ChatMessageVM.createChatMessageVM(chatMessageMapper.toDto(savedMessage), ChatMessageVM.MessageType.CHAT);
        //WHEN
        List<ChatMessageVM> previousMessages = underTest.getMessages(principal, event.getId());
        //THEN
        assertEquals(1, previousMessages.size());
        assertEquals(expectedMessage, previousMessages.get(0));
    }

    @Test
    void sendMessageShouldSaveIncomingMessage() {
        //GIVEN
        ChatMessageDTO expectedMessage = chatMessageMapper.toDto(message);
        expectedMessage.setId(null);
        ChatMessageVM incomingMessage = ChatMessageVM.createChatMessageVM(expectedMessage, ChatMessageVM.MessageType.CHAT);
        int messagesCount = chatMessageRepository.findAll().size();
        //WHEN
        underTest.sendMessage(principal, event.getId(), incomingMessage);
        //THEN
        List<ChatMessage> messages = chatMessageRepository.findAll();
        int newMessageCount =  messages.size();
        assertEquals(messagesCount + 1, newMessageCount);
        ChatMessage obtainedMessage = messages.get(newMessageCount - 1);
        assertEquals(message.getEvent().getId(), obtainedMessage.getEvent().getId());
        assertEquals(message.getSender().getId(), obtainedMessage.getSender().getId());
        assertEquals(message.getText(), obtainedMessage.getText());
    }

    private void clearDatabase() {
        chatMessageRepository.deleteAll();
        chatMessageRepository.flush();
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

    private ChatMessage initMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setText("Text");
        chatMessage.setSender(user);
        chatMessage.setEvent(event);
        chatMessage.setId(null);
        return chatMessage;
    }

    private Principal initPrincipal() {
        return  new Principal() {
            @Override
            public String getName() {
                return "user";
            }
        };
    }

    private EventParticipant initEventParticipant() {
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(true);
        eventParticipant.setUser(initParticipantUser());
        eventParticipant.setEvent(initEvent());
        eventParticipant.setId(1L);
        eventParticipant.setParticipated(false);
        eventParticipant.setRatingOfEvent(2);
        eventParticipant.setRatingOfParticipant(3);
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
        user = result;
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
        event = result;
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
