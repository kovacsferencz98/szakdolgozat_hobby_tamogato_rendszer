package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.ChatMessage;
import com.kovacs.ferencz.HobbyHelper.domain.EventParticipant;
import com.kovacs.ferencz.HobbyHelper.repository.ChatMessageRepository;
import com.kovacs.ferencz.HobbyHelper.service.ChatMessageService;
import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.ChatMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.kovacs.ferencz.HobbyHelper.domain.ChatMessage}.
 */
@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    private final Logger log = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
    }


    /**
     * Save a chatMessage.
     *
     * @param chatMessageDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ChatMessageDTO save(ChatMessageDTO chatMessageDTO) {
        log.debug("Request to save EventParticipant : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    /**
     * Get all the chatMessages.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> findAll() {
        log.debug("Request to get all ChatMessages");
        return chatMessageRepository.findAll().stream()
                .map(chatMessageMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get the "id" chatMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ChatMessageDTO> findOne(Long id) {
        log.debug("Request to get ChatMessage : {}", id);
        return chatMessageRepository.findById(id)
                .map(chatMessageMapper::toDto);
    }

    /**
     * Delete the "id" chatMessage.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ChatMessage : {}", id);
        chatMessageRepository.deleteById(id);
    }

    /**
     * Deletes the chatMessage entities connected to the given event
     * @param eventId the id of the event
     */
    @Override
    public void deleteMessagesOfEvent(Long eventId) {
        chatMessageRepository.findAllByEvent_Id(eventId).stream()
                .forEach(chatMessage -> delete(chatMessage.getId()));
    }

    /**
     * Finds every ChatMessage of a given event.
     * @param id The id of the event
     * @return the List of ChatMessageDTO representing the messages sent by the participants of the given event
     */
    @Override
    public List<ChatMessageDTO> findByEvent(Long id) {
        log.debug("Request to get all ChatMessages of Event: " + id);
        return chatMessageRepository.findAllByEvent_Id(id).stream()
                .map(chatMessageMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
