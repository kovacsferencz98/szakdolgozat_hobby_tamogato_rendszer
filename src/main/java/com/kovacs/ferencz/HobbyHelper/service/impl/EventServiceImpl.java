package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Event;
import com.kovacs.ferencz.HobbyHelper.repository.EventRepository;
import com.kovacs.ferencz.HobbyHelper.service.ChatMessageService;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final ChatMessageService chatMessageService;

    private final EventParticipantService eventParticipantService;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper,
                            ChatMessageService chatMessageService, EventParticipantService eventParticipantService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.chatMessageService = chatMessageService;
        this.eventParticipantService = eventParticipantService;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventDTO save(EventDTO eventDTO) {
        log.debug("Request to save Event : {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    /**
     * Creates a new event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        eventDTO.setCreatedAt(LocalDateTime.now());
        return save(eventDTO);
    }

    /**
     * Get all the events.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> findAll() {
        log.debug("Request to get all Events");
        return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the events with the given location
     *
     * @param locationId the id of the given location
     * @return the list of entities.
     */
    public List<EventDTO> findAllByLocation(Long locationId){
        log.debug("Request to get all Events for location: " + locationId);
        return eventRepository.findAllByLocation_Id(locationId).stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the events with the given type
     *
     * @param typeId the id of the given type
     * @return the list of entities.
     */
    public List<EventDTO> findAllByType(Long typeId) {
        log.debug("Request to get all Events for type: " + typeId);
        return eventRepository.findAllByType_Id(typeId).stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findById(id)
                .map(eventMapper::toDto);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        chatMessageService.deleteMessagesOfEvent(id);
        eventParticipantService.deleteByEvent(id);
        eventRepository.deleteById(id);
    }

    /**
     * Deletes the events created by the given user
     * @param userId
     */
    @Override
    public void deleteEventsOfUser(Long userId) {
        eventRepository.findAllByCreatedBy_Id(userId).stream()
                .forEach(event -> delete(event.getId()));
    }

    /**
     * Get every event created by the given user.
     * @param id the id of the owner
     * @return the list
     */
    public List<EventDTO> findByOwner(Long id) {
        log.debug("Find event by owner id: {}", id);
        return eventRepository.findAllByCreatedBy_Id(id).stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
