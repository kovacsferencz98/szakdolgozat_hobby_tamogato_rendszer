package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.service.dto.ChatMessageDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.ChatMessage}.
 */
public interface ChatMessageService {

    /**
     * Save a chatMessage.
     *
     * @param chatMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ChatMessageDTO save(ChatMessageDTO chatMessageDTO);

    /**
     * Get all the chatMessages.
     *
     * @return the list of entities.
     */
    List<ChatMessageDTO> findAll();


    /**
     * Get the "id" chatMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChatMessageDTO> findOne(Long id);

    /**
     * Delete the "id" chatMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Deletes the chatMessage entities connected to the given event
     * @param eventId the id of the event
     */
    void deleteMessagesOfEvent(Long eventId);

    /**
     * Finds every ChatMessage of a given event.
     * @param id The id of the event
     * @return the List of ChatMessageDTO representing the messages sent by the participants of the given event
     */
    List<ChatMessageDTO> findByEvent(Long id);
}
