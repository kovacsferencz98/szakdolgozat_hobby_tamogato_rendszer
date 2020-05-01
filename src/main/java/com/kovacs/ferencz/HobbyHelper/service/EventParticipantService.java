package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.EventParticipant}.
 */
public interface EventParticipantService {

    /**
     * Save a eventParticipant.
     *
     * @param eventParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    EventParticipantDTO save(EventParticipantDTO eventParticipantDTO);

    /**
     * Creates a eventParticipant.
     *
     * @param eventParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    EventParticipantDTO create(EventParticipantDTO eventParticipantDTO);

    /**
     * Get all the eventParticipants.
     *
     * @return the list of entities.
     */
    List<EventParticipantDTO> findAll();


    /**
     * Get the "id" eventParticipant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventParticipantDTO> findOne(Long id);

    /**
     * Delete the "id" eventParticipant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete the eventParticipant entities connected to the given user.
     *
     * @param userId the id of the user.
     */
    void deleteByUser(Long userId);

    /**
     * Delete the eventParticipant entities connected to the given event.
     *
     * @param eventId the id of the event.
     */
    void deleteByEvent(Long eventId);

    /**
     * Finds every EventParticipant of a given event.
     * @param id The id of the event
     * @return the List of EventParticipantDTO representing the participants of the given event
     */
    List<EventParticipantDTO> findByEvent(Long id);

    /**
     * Finds every EventParticipant with the given user.
     * @param id The id of the user
     * @return the List of EventParticipantDTO representing the events the user participates in.
     */
    List<EventParticipantDTO> findByUserId(Long id);
}
