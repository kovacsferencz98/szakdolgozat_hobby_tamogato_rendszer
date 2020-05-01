package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.ChatMessageVM;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.ChatMessageService;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ChatControllerTest.class);

    @MockBean
    private ChatMessageService chatMessageService;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserService userService;

    @MockBean
    private EventParticipantService eventParticipantService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private ChatController underTest;

    private EventDTO eventDTO;

    private List<User> users;

    private List<EventParticipantDTO> participants;

    private List<ChatMessageDTO> messages;

    private Principal principal;

    @BeforeEach
    void initUseCase() {
        eventDTO = initEvent();
        users = initUsers();
        principal = initPrincipal();
        participants = initParticipants();
        messages = initMessages();
    }

    @Test
    void getMessagesShouldThrowExceptionWhenNoUserFound() throws Exception {
        //GIVEN
        BDDMockito.given(userService.getUserWithAuthoritiesByLogin(BDDMockito.anyString())).willReturn(Optional.empty());
        BDDMockito.given(messageSource.getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class)))
                .willReturn("Missing user");
        //WHEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            underTest.getMessages(principal, 1L);
        });
        //THEN
        BDDMockito.verify(messageSource).getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class));
        assertEquals("Missing user", exception.getMessage());
    }

    @Test
    void getMessagesShouldThrowExceptionWhenNoEventFound() throws Exception {
        //GIVEN
        BDDMockito.given(userService.getUserWithAuthoritiesByLogin(BDDMockito.anyString())).willReturn(Optional.of(users.get(0)));
        BDDMockito.given(eventService.findOne(BDDMockito.anyLong())).willReturn(Optional.empty());
        BDDMockito.given(messageSource.getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class)))
                .willReturn("Missing event");
        //WHEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            underTest.getMessages(principal, 1L);
        });
        //THEN
        BDDMockito.verify(messageSource).getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class));
        assertEquals("Missing event", exception.getMessage());
    }

    @Test
    void getMessagesShouldThrowExceptionWhenUserNotAuthorized() throws Exception {
        //GIVEN
        BDDMockito.given(userService.getUserWithAuthoritiesByLogin(BDDMockito.anyString())).willReturn(Optional.of(users.get(1)));
        BDDMockito.given(eventService.findOne(BDDMockito.anyLong())).willReturn(Optional.of(eventDTO));
        BDDMockito.given(eventParticipantService.findByEvent(BDDMockito.anyLong())).willReturn(new ArrayList<>());
        BDDMockito.given(messageSource.getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.getMessages(principal, 1L);
        });
        //THEN
        BDDMockito.verify(messageSource).getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class));
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    void getMessagesShouldSendPreviousMessages() throws Exception {
        //GIVEN
        BDDMockito.given(userService.getUserWithAuthoritiesByLogin(BDDMockito.anyString())).willReturn(Optional.of(users.get(0)));
        BDDMockito.given(eventService.findOne(BDDMockito.anyLong())).willReturn(Optional.of(eventDTO));
        BDDMockito.given(eventParticipantService.findByEvent(BDDMockito.anyLong())).willReturn(participants);
        BDDMockito.given(chatMessageService.findByEvent(BDDMockito.anyLong())).willReturn(messages);
        ChatMessageVM expectedMessage = ChatMessageVM.createChatMessageVM(messages.get(0), ChatMessageVM.MessageType.CHAT);
        //WHEN
        List<ChatMessageVM> previousMessages = underTest.getMessages(principal, 1L);
        //THEN
        assertEquals(1, previousMessages.size());
        assertEquals(expectedMessage, previousMessages.get(0));
    }

    @Test
    void sendMessageShouldSaveIncomingMessage() {
        //GIVEN
        BDDMockito.given(userService.getUserWithAuthoritiesByLogin(BDDMockito.anyString())).willReturn(Optional.of(users.get(0)));
        BDDMockito.given(eventService.findOne(BDDMockito.anyLong())).willReturn(Optional.of(eventDTO));
        BDDMockito.given(eventParticipantService.findByEvent(BDDMockito.anyLong())).willReturn(participants);
        ChatMessageDTO expectedMessage = messages.get(0);
        expectedMessage.setId(null);
        ChatMessageVM incomingMessage = ChatMessageVM.createChatMessageVM(expectedMessage, ChatMessageVM.MessageType.CHAT);
        //WHEN
        underTest.sendMessage(principal, 1L, incomingMessage);
        //THEN
        ArgumentCaptor<ChatMessageDTO> argument = ArgumentCaptor.forClass(ChatMessageDTO.class);
        BDDMockito.verify(chatMessageService).save(argument.capture());
        assertEquals(expectedMessage, argument.getValue());
    }

    @Test
    void sendMessageShouldThrowExceptionWhenUserNotAuthorized() {
        //GIVEN
        BDDMockito.given(userService.getUserWithAuthoritiesByLogin(BDDMockito.anyString())).willReturn(Optional.of(users.get(1)));
        BDDMockito.given(eventService.findOne(BDDMockito.anyLong())).willReturn(Optional.of(eventDTO));
        BDDMockito.given(eventParticipantService.findByEvent(BDDMockito.anyLong())).willReturn(new ArrayList<>());
        BDDMockito.given(messageSource.getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class)))
                .willReturn("Not authorized");
        ChatMessageVM incomingMessage = ChatMessageVM.createChatMessageVM(messages.get(0), ChatMessageVM.MessageType.CHAT);
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.sendMessage(principal, 1L, incomingMessage);
        });
        //THEN
        BDDMockito.verify(messageSource).getMessage(BDDMockito.any(String.class), BDDMockito.any(), BDDMockito.any(Locale.class));
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    void handleExceptionShouldReturnExceptionMessage() {
        //GIVEN
        String message = "message";
        //WHEN
        String result = underTest.handleException(new Exception(message));
        //THEN
        assertEquals(message, result);
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

    private List<User> initUsers() {
        List<User> result = new ArrayList<>();
        User user1 = new User();
        user1.setLangKey("en");
        user1.setLastName("Last");
        user1.setFirstName("First");
        user1.setEmail("email@email.com");
        user1.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        user1.setId(1L);
        user1.setUsername("user1");
        result.add(user1);
        User user2 = new User();
        user2.setLangKey("en");
        user2.setLastName("Last");
        user2.setFirstName("First");
        user2.setEmail("email2@email.com");
        user2.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        user2.setId(2L);
        user2.setUsername("user2");
        result.add(user2);
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

    private List<ChatMessageDTO> initMessages() {
        List<ChatMessageDTO> chatMessages = new ArrayList<>();
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderUsername("user1");
        chatMessageDTO.setSenderId(1L);
        chatMessageDTO.setText("Text");
        chatMessageDTO.setEventName("Event");
        chatMessageDTO.setEventId(1L);
        chatMessageDTO.setId(1L);
        chatMessages.add(chatMessageDTO);
        return chatMessages;
    }

    private Principal initPrincipal() {
        return  new Principal() {
            @Override
            public String getName() {
                return "user1";
            }
        };
    }
}