package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.CreateEventVM;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link  com.kovacs.ferencz.HobbyHelper.domain.Event}.
 */
@RestController
@RequestMapping("/api")
public class EventResource {

    public static class EventResourceException extends RuntimeException {
        private EventResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(EventResource.class);

    private static final String ENTITY_NAME = "event";

    @Value("${application.name}")
    private String applicationName;

    private final EventService eventService;

    private final LocationService locationService;

    private final MessageSource messageSource;

    public EventResource(EventService eventService, LocationService locationService, MessageSource messageSource) {
        this.eventService = eventService;
        this.locationService = locationService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /events} : Create a new event.
     *
     * @param createEventVM the viewmodel containing the event to create and its location.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventDTO, or with status {@code 400 (Bad Request)} if the event has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/events")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventVM createEventVM) throws URISyntaxException {
        log.debug("REST request to save Event : {}", createEventVM);
        if(!isAuthorizedForModification(createEventVM.getEvent())) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        if (createEventVM.getEvent().getId() != null) {
            throw new EventResourceException(messageSource.getMessage("event.idExists",
                    null, LocaleUtil.getUserLocale()));
        }
        if (createEventVM.getLocation().getId() != null) {
            throw new EventResourceException(messageSource.getMessage("location.idExists",
                    null, LocaleUtil.getUserLocale()));
        }
        LocationDTO location = locationService.save(createEventVM.getLocation());
        EventDTO event = createEventVM.getEvent();
        event.setLocationId(location.getId());
        EventDTO result = eventService.createEvent(event);
        return ResponseEntity.created(new URI("/api/events/" + result.getId()))
                .body(result);
    }

    /**
     * {@code PUT  /events} : Updates an existing event.
     *
     * @param eventDTO the eventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventDTO,
     * or with status {@code 400 (Bad Request)} if the eventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/events")
    public ResponseEntity<EventDTO> updateEvent(@Valid @RequestBody EventDTO eventDTO) throws URISyntaxException {
        log.debug("REST request to update Event : {}", eventDTO);
        if(!isAuthorizedForModification(eventDTO)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        if (eventDTO.getId() == null) {
            throw new EventResourceException(messageSource.getMessage("event.missingId",
                    null, LocaleUtil.getUserLocale()));
        }
        EventDTO result = eventService.save(eventDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    /**
     * {@code GET  /events} : get all the events.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of events in body.
     */
    @GetMapping("/events")
    public List<EventDTO> getAllEvents() {
        log.debug("REST request to get all Events");
        return eventService.findAll();
    }

    /**
     * {@code GET  /events/:id} : get the "id" event.
     *
     * @param id the id of the eventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        log.debug("REST request to get Event : {}", id);
        Optional<EventDTO> eventDTO = eventService.findOne(id);
        return eventDTO.isPresent() ? ResponseEntity.ok().body(eventDTO.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" event.
     *
     * @param id the id of the eventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.debug("REST request to delete Event : {}", id);
        Optional<EventDTO> toDelete = eventService.findOne(id);
        if(toDelete.isEmpty()) {
            throw new EventResourceException(messageSource.getMessage("event.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        if(!isAuthorizedForModification(toDelete.get())) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAuthorizedForModification(EventDTO eventDTO) {
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)
                || eventDTO.getCreatedByUsername().equals(SecurityUtils.getCurrentUserLogin().get());
    }
}
