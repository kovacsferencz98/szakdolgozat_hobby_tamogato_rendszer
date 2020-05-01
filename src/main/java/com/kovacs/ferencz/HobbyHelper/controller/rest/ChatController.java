package com.kovacs.ferencz.HobbyHelper.controller.rest;

import static java.lang.String.format;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.ChatMessageVM;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.service.ChatMessageService;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private SimpMessageSendingOperations messagingTemplate;

    private ChatMessageService chatMessageService;

    private EventService eventService;

    private UserService userService;

    private EventParticipantService eventParticipantService;

    private MessageSource messageSource;

    public ChatController(SimpMessageSendingOperations messagingTemplate, ChatMessageService chatMessageService,
                          EventService eventService, UserService userService, EventParticipantService eventParticipantService,
                          MessageSource messageSource) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.eventService = eventService;
        this.userService = userService;
        this.eventParticipantService = eventParticipantService;
        this.messageSource = messageSource;
    }

    /**
     * Handles new subscriptions to a given event's chat room
     * @param principal contains the logged in user info
     * @param eventId the id of the event the chatroom of which the user wants to join
     * @return previous chat messages of the event's chat room
     */
    @SubscribeMapping("/oldermessages/{eventId}")
    public List<ChatMessageVM> getMessages(Principal principal, @DestinationVariable Long eventId) {
        if(isUserAllowedToChat(principal.getName(), eventId)) {
            logger.debug("Return previous messages on subscribe for " + principal.getName() + "in room: " + eventId);
            sendJoinMessage(principal, eventId);
            List<ChatMessageVM> chatMessageVMS = findPreviousMessages(eventId);
            return chatMessageVMS;
        } else {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request", null, LocaleUtil.getUserLocale()));
        }
    }

    /**
     * Handles message being sent by the user
     * @param principal user sending the message
     * @param eventId id of the chat room
     * @param chatMessageVM the message
     */
    @MessageMapping("/sendMessage/{eventId}")
    public void sendMessage(Principal principal, @DestinationVariable Long eventId, @Payload ChatMessageVM chatMessageVM) {
        if(isUserAllowedToChat(principal.getName(), eventId)) {
            ChatMessageDTO chatMessageDTO = createMessageDTO(eventId, chatMessageVM, principal.getName());
            chatMessageService.save(chatMessageDTO);
            messagingTemplate.convertAndSend(format("/chat/%s", eventId), chatMessageVM);
        } else {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request", null, LocaleUtil.getUserLocale()));
        }
    }

    /**
     * Handles exceptions of the controller
     * @param exception
     * @return
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }


    private List<ChatMessageVM> findPreviousMessages(@DestinationVariable Long eventId) {
        List<ChatMessageDTO> chatMessages = chatMessageService.findByEvent(eventId);
        return chatMessages.stream()
                .map(message -> ChatMessageVM.createChatMessageVM(message, ChatMessageVM.MessageType.CHAT))
                .collect(Collectors.toList());
    }

    private void sendJoinMessage(Principal principal, @DestinationVariable Long eventId) {
        ChatMessageVM joinMessage = createJoinMessage(principal.getName());
        messagingTemplate.convertAndSend(format("/chat/%s", eventId), joinMessage);
    }

    private ChatMessageVM createJoinMessage(String name) {
        ChatMessageVM chatMessageVM = new ChatMessageVM();
        chatMessageVM.setType(ChatMessageVM.MessageType.JOIN);
        chatMessageVM.setSender(name);
        chatMessageVM.setContent(name + " joined the chat.");
        return chatMessageVM;
    }

    private ChatMessageDTO createMessageDTO(Long eventId, ChatMessageVM chatMessageVM, String username) {
        ChatMessageDTO result = new ChatMessageDTO();
        EventDTO eventDTO = obtainEvent(eventId);
        result.setEventId(eventDTO.getId());
        result.setEventName(eventDTO.getName());
        result.setText(chatMessageVM.getContent());
        User user = obtainUser(username);
        result.setSenderId(user.getId());
        result.setSenderUsername(user.getUsername());
        return result;
    }

    private boolean isUserAllowedToChat(String username, long eventId) {
        User user = obtainUser(username);
        EventDTO eventDTO = obtainEvent(eventId);
        List<EventParticipantDTO> participants = obtainEventParticipants(eventId);
        return isUserOwner(user, eventDTO) || isUserParticipant(user, participants);
    }

    private boolean isUserParticipant(User user, List<EventParticipantDTO> participants) {
        Optional<EventParticipantDTO> eventParticipantDTO = participants.stream()
                .filter(participant -> participant.getUserUsername().equals(user.getUsername()))
                .findFirst();
        return eventParticipantDTO.isPresent();
    }

    private boolean isUserOwner(User user, EventDTO event) {
        return event.getCreatedByUsername().equals(user.getUsername());
    }

    private User obtainUser(String username) {
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(username);
        if(user.isEmpty()) {
            throw new RuntimeException(messageSource.getMessage("message.noUser", null, LocaleUtil.getUserLocale()));
        }

        return user.get();
    }

    private EventDTO obtainEvent(Long eventId) {
        Optional<EventDTO> eventDTO = eventService.findOne(eventId);
        if(eventDTO.isEmpty()) {
            throw new RuntimeException(messageSource.getMessage("message.noEvent", null, LocaleUtil.getUserLocale()));
        }

        return eventDTO.get();
    }

    private List<EventParticipantDTO> obtainEventParticipants(Long eventId)  {
        return eventParticipantService.findByEvent(eventId);
    }
}
