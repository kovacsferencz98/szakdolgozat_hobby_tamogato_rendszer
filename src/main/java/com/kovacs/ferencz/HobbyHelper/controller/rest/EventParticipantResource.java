package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import  com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import  com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link  com.kovacs.ferencz.HobbyHelper.domain.EventParticipant}.
 */
@RestController
@RequestMapping("/api")
public class EventParticipantResource {

    public static class EventParticipantResourceException extends RuntimeException {
        private EventParticipantResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(EventParticipantResource.class);

    private static final String ENTITY_NAME = "eventParticipant";

    @Value("${application.name}")
    private String applicationName;

    private final EventParticipantService eventParticipantService;

    private final EventService eventService;

    private  final MessageSource messageSource;


    public EventParticipantResource(EventParticipantService eventParticipantService, EventService eventService, MessageSource messageSource) {
        this.eventParticipantService = eventParticipantService;
        this.eventService = eventService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /event-participants} : Create a new eventParticipant.
     *
     * @param eventParticipantDTO the eventParticipantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventParticipantDTO, or with status {@code 400 (Bad Request)} if the eventParticipant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-participants")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<EventParticipantDTO> createEventParticipant(@Valid @RequestBody EventParticipantDTO eventParticipantDTO) throws URISyntaxException {
        log.debug("REST request to save EventParticipant : {}", eventParticipantDTO);
        if (eventParticipantDTO.getId() != null) {
            throw new EventParticipantResourceException(messageSource.getMessage("eventParticipants.idExists",
                    null, LocaleUtil.getUserLocale()));
        }
        if(userAlreadyParticipates(eventParticipantDTO)) {
            throw new EventParticipantResourceException(messageSource.getMessage("user.already.participates",
                    null, LocaleUtil.getUserLocale()));
        }
        if(isEventOver(eventParticipantDTO.getEventId())) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.isOver",
                    null, LocaleUtil.getUserLocale()));
        }
        incrementCurrentAttendance(eventParticipantDTO.getEventId());
        EventParticipantDTO result = eventParticipantService.create(eventParticipantDTO);
        return ResponseEntity.created(new URI("/api/event-participants/" + result.getId()))
                .body(result);
    }

    /**
     * {@code PUT  /event-participants} : Updates an existing eventParticipant.
     *
     * @param eventParticipantDTO the eventParticipantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventParticipantDTO,
     * or with status {@code 400 (Bad Request)} if the eventParticipantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventParticipantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-participants")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<EventParticipantDTO> updateEventParticipant(@Valid @RequestBody EventParticipantDTO eventParticipantDTO) throws URISyntaxException {
        log.debug("REST request to update EventParticipant : {}", eventParticipantDTO);
        if (eventParticipantDTO.getId() == null) {
            throw new EventParticipantResourceException(messageSource.getMessage("eventParticipants.missingId",
                    null, LocaleUtil.getUserLocale()));
        }
        if(didEventChangeOccur(eventParticipantDTO)) {
            if(isDuplicate(eventParticipantDTO)) {
                throw new EventParticipantResourceException(messageSource.getMessage("user.already.participates",
                        null, LocaleUtil.getUserLocale()));
            }
            handleChangeOfEvents(eventParticipantDTO);
        }
        EventParticipantDTO result = eventParticipantService.save(eventParticipantDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    /**
     * {@code GET  /event-participants} : get all the eventParticipants.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventParticipants in body.
     */
    @GetMapping("/event-participants")
    public List<EventParticipantDTO> getAllEventParticipants() {
        log.debug("REST request to get all EventParticipants");
        return eventParticipantService.findAll();
    }

    /**
     * {@code GET  /event-participants/:id} : get the "id" eventParticipant.
     *
     * @param id the id of the eventParticipantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventParticipantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-participants/{id}")
    public ResponseEntity<EventParticipantDTO> getEventParticipant(@PathVariable Long id) {
        log.debug("REST request to get EventParticipant : {}", id);
        Optional<EventParticipantDTO> eventParticipantDTO = eventParticipantService.findOne(id);
        return eventParticipantDTO.isPresent() ? ResponseEntity.ok().body(eventParticipantDTO.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code DELETE  /event-participants/:id} : delete the "id" eventParticipant.
     *
     * @param id the id of the eventParticipantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-participants/{id}")
    public ResponseEntity<Void> deleteEventParticipant(@PathVariable Long id) {
        log.debug("REST request to delete EventParticipant : {}", id);
        EventParticipantDTO eventParticipantDTO = findEventParticipant(id);
        if(!isAuthorized(id)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        decrementCurrentAttendance(eventParticipantDTO.getEventId());
        eventParticipantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean userAlreadyParticipates(EventParticipantDTO eventParticipantDTO) {
        Optional<EventDTO> event = eventService.findOne(eventParticipantDTO.getEventId());
        if(event.isEmpty()) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        return eventParticipantService.findByEvent(eventParticipantDTO.getEventId()).stream()
                .anyMatch(eventParticipant ->  eventParticipant.getUserId().equals(eventParticipantDTO.getUserId()));
    }

    private void handleChangeOfEvents(EventParticipantDTO eventParticipantDTO) {
        EventParticipantDTO oldEntry = findEventParticipant(eventParticipantDTO.getId());
        decrementCurrentAttendance(oldEntry.getEventId());
        incrementCurrentAttendance(eventParticipantDTO.getEventId());
    }

    private boolean didEventChangeOccur(EventParticipantDTO eventParticipantDTO) {
        EventParticipantDTO oldEntry = findEventParticipant(eventParticipantDTO.getId());
        return !oldEntry.getEventId().equals(eventParticipantDTO.getEventId());
    }

    private boolean isDuplicate(EventParticipantDTO eventParticipantDTO) {
        return eventParticipantService.findByEvent(eventParticipantDTO.getEventId()).stream()
                .anyMatch(eventParticipant -> eventParticipant.getUserId().equals(eventParticipantDTO.getUserId()));
    }

    private boolean isUserOwner(EventParticipantDTO eventParticipantDTO) {
        Optional<EventDTO> event = eventService.findOne(eventParticipantDTO.getEventId());
        if(event.isEmpty()) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        return  event.get().getCreatedById().equals(eventParticipantDTO.getUserId());

    }

    private void incrementCurrentAttendance(Long eventId) {
        EventDTO eventDTO = eventService.findOne(eventId).get();
        if(eventDTO.getMaxAttendance().equals(eventDTO.getCurrentAttendance())) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.full",
                    null, LocaleUtil.getUserLocale()));
        }
        eventDTO.setCurrentAttendance(eventDTO.getCurrentAttendance() + 1);
        eventService.save(eventDTO);
    }

    private void decrementCurrentAttendance(Long eventId) {
        EventDTO eventDTO = eventService.findOne(eventId).get();
        if(eventDTO.getMaxAttendance().equals(eventDTO.getCurrentAttendance())) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.full",
                    null, LocaleUtil.getUserLocale()));
        }
        eventDTO.setCurrentAttendance(eventDTO.getCurrentAttendance() - 1);
        eventService.save(eventDTO);
    }


    private boolean isAuthorized(Long id) {
        String username = SecurityUtils.getCurrentUserLogin().get();
        boolean isAdmin = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);
        EventParticipantDTO eventParticipantDTO = findEventParticipant(id);
        Optional<EventDTO> event = eventService.findOne(eventParticipantDTO.getEventId());
        if(event.isEmpty()) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        boolean isUserOwner = event.get().getCreatedByUsername().equals(username);
        return isAdmin || isUserOwner;
    }

    private EventParticipantDTO findEventParticipant(Long id) {
        Optional<EventParticipantDTO> participant = eventParticipantService.findOne(id);
        if (participant.isEmpty()) {
            throw new EventParticipantResourceException(messageSource.getMessage("eventParticipants.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        return participant.get();
    }

    private boolean isEventOver(Long id) {
        Optional<EventDTO> event = eventService.findOne(id);
        if(event.isEmpty()) {
            throw new EventParticipantResourceException(messageSource.getMessage("event.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        return event.get().getStartsAt().isBefore(LocalDateTime.now());
    }

}
