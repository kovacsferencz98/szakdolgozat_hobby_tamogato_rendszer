package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.EventTypeService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
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
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link  com.kovacs.ferencz.HobbyHelper.domain.EventType}.
 */
@RestController
@RequestMapping("/api")
public class EventTypeResource {

    public static class EventTypeResourceException extends RuntimeException {
        private EventTypeResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(EventTypeResource.class);

    private static final String ENTITY_NAME = "eventType";

    @Value("${application.name}")
    private String applicationName;

    private final EventTypeService eventTypeService;

    private final MessageSource messageSource;

    private final EventService eventService;

    public EventTypeResource(EventTypeService eventTypeService, MessageSource messageSource, EventService eventService) {
        this.eventTypeService = eventTypeService;
        this.messageSource = messageSource;
        this.eventService = eventService;
    }

    /**
     * {@code POST  /event-types} : Create a new eventType.
     *
     * @param eventTypeDTO the eventTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventTypeDTO, or with status {@code 400 (Bad Request)} if the eventType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-types")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") or hasRole(\"" + AuthoritiesConstants.MODERATOR + "\")")
    public ResponseEntity<EventTypeDTO> createEventType(@Valid @RequestBody EventTypeDTO eventTypeDTO) throws URISyntaxException {
        log.debug("REST request to save EventType : {}", eventTypeDTO);
        if (eventTypeDTO.getId() != null) {
            throw new EventTypeResourceException(messageSource.getMessage("eventType.idExists",
                    null, LocaleUtil.getUserLocale()));
        }
        Optional<EventTypeDTO> existingType = eventTypeService.findByName(eventTypeDTO.getName());
        if(existingType.isPresent()) {
            throw new EventTypeResourceException(messageSource.getMessage("eventType.nameUsed",
                    null, LocaleUtil.getUserLocale()));
        }
        EventTypeDTO result = eventTypeService.save(eventTypeDTO);
        return ResponseEntity.created(new URI("/api/event-types/" + result.getId()))
                .body(result);
    }

    /**
     * {@code PUT  /event-types} : Updates an existing eventType.
     *
     * @param eventTypeDTO the eventTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventTypeDTO,
     * or with status {@code 400 (Bad Request)} if the eventTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-types")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") or hasRole(\"" + AuthoritiesConstants.MODERATOR + "\")")
    public ResponseEntity<EventTypeDTO> updateEventType(@Valid @RequestBody EventTypeDTO eventTypeDTO) throws URISyntaxException {
        log.debug("REST request to update EventType : {}", eventTypeDTO);
        if (eventTypeDTO.getId() == null) {
            throw new EventTypeResourceException(messageSource.getMessage("eventType.missingId",
                    null, LocaleUtil.getUserLocale()));
        }
        Optional<EventTypeDTO> existingType = eventTypeService.findByName(eventTypeDTO.getName());
        if(existingType.isPresent() && !idEquals(eventTypeDTO, existingType)) {
            throw new EventTypeResourceException(messageSource.getMessage("eventType.nameUsed",
                    null, LocaleUtil.getUserLocale()));
        }
        EventTypeDTO result = eventTypeService.save(eventTypeDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    private boolean idEquals(@RequestBody @Valid EventTypeDTO eventTypeDTO, Optional<EventTypeDTO> existingType) {
        return existingType.get().getId().equals(eventTypeDTO.getId());
    }

    /**
     * {@code GET  /event-types} : get all the eventTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventTypes in body.
     */
    @GetMapping("/event-types")
    public List<EventTypeDTO> getAllEventTypes() {
        log.debug("REST request to get all EventTypes");
        return eventTypeService.findAll();
    }

    /**
     * {@code GET  /event-types/:id} : get the "id" eventType.
     *
     * @param id the id of the eventTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-types/{id}")
    public ResponseEntity<EventTypeDTO> getEventType(@PathVariable Long id) {
        log.debug("REST request to get EventType : {}", id);
        Optional<EventTypeDTO> eventTypeDTO = eventTypeService.findOne(id);
        return eventTypeDTO.isPresent() ? ResponseEntity.ok().body(eventTypeDTO.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code DELETE  /event-types/:id} : delete the "id" eventType.
     *
     * @param id the id of the eventTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-types/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\") or hasRole(\"" + AuthoritiesConstants.MODERATOR + "\")")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        log.debug("REST request to delete EventType : {}", id);
        if(isStillInUse(id)) {
            throw new EventTypeResourceException(messageSource.getMessage("eventType.inUse",
                    null, LocaleUtil.getUserLocale()));
        }
        eventTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isStillInUse(Long id) {
        return  !eventService.findAllByType(id).isEmpty();
    }
}
