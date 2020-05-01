package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.ChatMessageRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.ChatMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatMessageServiceImplTest {

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    ChatMessageServiceImpl underTest;

    private ChatMessageDTO chatMessageDTO;

    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatMessageDTO = initMessageDTO();
        chatMessage = initMessage();
    }

    @Test
    void saveShouldReturnSavedEntity() {
        //GIVEN
        given(chatMessageMapper.toEntity(any(ChatMessageDTO.class))).willReturn(chatMessage);
        given(chatMessageMapper.toDto(any(ChatMessage.class))).willReturn(chatMessageDTO);
        given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(chatMessage);
        //WHEN
        ChatMessageDTO result = underTest.save(chatMessageDTO);
        //THEN
        verify(chatMessageRepository).save(chatMessage);
        assertEquals(chatMessageDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(chatMessageRepository.findAll()).willReturn(Arrays.asList(chatMessage));
        given(chatMessageMapper.toDto(any(ChatMessage.class))).willReturn(chatMessageDTO);
        //WHEN
        List<ChatMessageDTO> result = underTest.findAll();
        //THEN
        verify(chatMessageRepository).findAll();
        assertEquals(Arrays.asList(chatMessageDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(chatMessageRepository.findById(anyLong())).willReturn(Optional.of(chatMessage));
        given(chatMessageMapper.toDto(any(ChatMessage.class))).willReturn(chatMessageDTO);
        //WHEN
        Optional<ChatMessageDTO> result = underTest.findOne(1L);
        //THEN
        verify(chatMessageRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(chatMessageDTO, result.get());
    }

    @Test
    void deleteShouldDeleteEntity() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(chatMessageRepository).deleteById(1L);
    }

    @Test
    void deleteMessagesOfEvent() {
        //GIVEN
        given(chatMessageRepository.findAllByEvent_Id(anyLong())).willReturn(Arrays.asList(chatMessage));
        //WHEN
        underTest.deleteMessagesOfEvent(1L);
        //THEN
        verify(chatMessageRepository).findAllByEvent_Id(1L);
        verify(chatMessageRepository).deleteById(1L);
    }

    @Test
    void findByEventShouldReturnEntities() {
        //GIVEN
        given(chatMessageRepository.findAllByEvent_Id(anyLong())).willReturn(Arrays.asList(chatMessage));
        given(chatMessageMapper.toDto(any(ChatMessage.class))).willReturn(chatMessageDTO);
        //WHEN
        List<ChatMessageDTO> result = underTest.findByEvent(1L);
        //THEN
        verify(chatMessageRepository).findAllByEvent_Id(1L);
        assertEquals(Arrays.asList(chatMessageDTO), result);
    }

    private ChatMessage initMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(1L);
        chatMessage.setText("Text");
        chatMessage.setId(1L);
        chatMessage.setEvent(initEvent());
        chatMessage.setSender(initUser());
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