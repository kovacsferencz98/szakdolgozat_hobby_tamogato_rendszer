package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.EventParticipant;
import com.kovacs.ferencz.HobbyHelper.repository.EventParticipantRepository;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventParticipantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link EventParticipant}.
 */
@Service
@Transactional
public class EventParticipantServiceImpl implements EventParticipantService {

    private final Logger log = LoggerFactory.getLogger(EventParticipantServiceImpl.class);

    private final EventParticipantRepository eventParticipantRepository;

    private final EventParticipantMapper eventParticipantMapper;

    public EventParticipantServiceImpl(EventParticipantRepository eventParticipantRepository, EventParticipantMapper eventParticipantMapper) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventParticipantMapper = eventParticipantMapper;
    }

    /**
     * Create a eventParticipant.
     *
     * @param eventParticipantDTO the entity to create.
     * @return the persisted entity.
     */
    @Override
    public EventParticipantDTO create(EventParticipantDTO eventParticipantDTO) {
        log.debug("Request to create EventParticipant : {}", eventParticipantDTO);
        eventParticipantDTO.setJoinedAt(LocalDateTime.now());
        return save(eventParticipantDTO);
    }

    /**
     * Save a eventParticipant.
     *
     * @param eventParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventParticipantDTO save(EventParticipantDTO eventParticipantDTO) {
        log.debug("Request to save EventParticipant : {}", eventParticipantDTO);
        EventParticipant eventParticipant = eventParticipantMapper.toEntity(eventParticipantDTO);
        eventParticipant = eventParticipantRepository.save(eventParticipant);
        return eventParticipantMapper.toDto(eventParticipant);
    }

    /**
     * Get all the eventParticipants.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventParticipantDTO> findAll() {
        log.debug("Request to get all EventParticipants");
        return eventParticipantRepository.findAll().stream()
                .map(eventParticipantMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one eventParticipant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventParticipantDTO> findOne(Long id) {
        log.debug("Request to get EventParticipant : {}", id);
        return eventParticipantRepository.findById(id)
                .map(eventParticipantMapper::toDto);
    }

    /**
     * Delete the eventParticipant by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventParticipant : {}", id);
        eventParticipantRepository.deleteById(id);
    }

    /**
     * Delete the eventParticipant entities connected to the given user.
     *
     * @param userId the id of the user.
     */
    @Override
    public void deleteByUser(Long userId) {
        eventParticipantRepository.findAllByUser_Id(userId).stream()
                .forEach(eventParticipant -> delete(eventParticipant.getId()));
    }

    /**
     * Delete the eventParticipant entities connected to the given event.
     *
     * @param eventId the id of the event.
     */
    @Override
    public void deleteByEvent(Long eventId) {
        eventParticipantRepository.findAllByEvent_Id(eventId).stream()
                .forEach(eventParticipant -> delete(eventParticipant.getId()));
    }

    /**
     * Finds every EventParticipant of a given event.
     * @param id The id of the event
     * @return the List of EventParticipantDTO representing the participants of the given event
     */
    @Override
    public List<EventParticipantDTO> findByEvent(Long id) {
        log.debug("Request to get all EventParticipants of Event: " + id);
        return eventParticipantRepository.findAllByEvent_Id(id).stream()
                .map(eventParticipantMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Finds every EventParticipant with the given user.
     * @param id The id of the user
     * @return the List of EventParticipantDTO representing the events the user participates in.
     */
    @Override
    public List<EventParticipantDTO> findByUserId(Long id) {
        log.debug("Request to get all EventParticipants with the User: " + id);
        return eventParticipantRepository.findAllByUser_Id(id).stream()
                .map(eventParticipantMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
