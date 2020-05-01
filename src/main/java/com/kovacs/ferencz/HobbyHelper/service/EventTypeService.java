package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kovacs.ferencz.HobbyHelper.domain.EventType}.
 */
public interface EventTypeService {

    /**
     * Save a eventType.
     *
     * @param eventTypeDTO the entity to save.
     * @return the persisted entity.
     */
    EventTypeDTO save(EventTypeDTO eventTypeDTO);

    /**
     * Get all the eventTypes.
     *
     * @return the list of entities.
     */
    List<EventTypeDTO> findAll();


    /**
     * Get the "id" eventType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventTypeDTO> findOne(Long id);

    /**
     * Get the eventType with the given name
     *
     * @param name the name of the entity.
     * @return the entity.
     */
    Optional<EventTypeDTO> findByName(String name);

    /**
     * Delete the "id" eventType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
