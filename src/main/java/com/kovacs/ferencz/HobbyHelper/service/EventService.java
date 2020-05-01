package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.Event}.
 */
public interface EventService {

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    EventDTO save(EventDTO eventDTO);

    /**
     * Creates a new event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    EventDTO createEvent(EventDTO eventDTO);

    /**
     * Get all the events.
     *
     * @return the list of entities.
     */
    List<EventDTO> findAll();

    /**
     * Get all the events with the given location
     *
     * @param locationId the id of the given location
     * @return the list of entities.
     */
    List<EventDTO> findAllByLocation(Long locationId);

    /**
     * Get all the events with the given type
     *
     * @param typeId the id of the given type
     * @return the list of entities.
     */
    List<EventDTO> findAllByType(Long typeId);

    /**
     * Get the "id" event.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventDTO> findOne(Long id);

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Deletes the events created by the given user
     * @param userId the id of the user
     */
    void deleteEventsOfUser(Long userId);

    /**
     * Get every event created by the given user.
     * @param id the id of the owner
     * @return the list
     */
    List<EventDTO> findByOwner(Long id);


}
