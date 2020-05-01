package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.EventDetailVM;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RatingVM;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EventController {
    public static class EventControllerException extends RuntimeException {
        private EventControllerException(String message) {
            super(message);
        }
    }

    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    private EventService eventService;

    private EventTypeService eventTypeService;

    private EventParticipantService eventParticipantService;

    private UserService userService;

    private LocationService locationService;

    private MessageSource messageSource;

    public EventController(EventService eventService, EventTypeService eventTypeService, UserService userService, LocationService locationService,
                           EventParticipantService eventParticipantService, MessageSource messageSource) {
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.userService = userService;
        this.locationService = locationService;
        this.eventParticipantService = eventParticipantService;
        this.messageSource = messageSource;
    }

    /**
     * {@code GET /event-detail/:id} : Get Event Details
     *
     * @param id the id of the event the details of which should be obtained.
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the obtained EventDetailVM, or with status {@code 404 (Not Found)} if the event was not found, or the detail viewmodel couldn't be created.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @GetMapping("/event-detail/{id}")
    public ResponseEntity<EventDetailVM> getEventDetail(@PathVariable Long id) {
        EventDetailVM eventDetail = null;
        try {
            eventDetail = createEventDetail(id);
        } catch(Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok().body(eventDetail);
    }

    /**
     * {@code Post /join-event/:id} : Join an event
     *
     * @param id the id of the event to join.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new EventParticipantDTO, or with status {@code 400 (Bad Request)} if the logged in user already joined the given event or is the owner of the event.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/join-event/{id}")
    public ResponseEntity<EventParticipantDTO> joinEvent(@PathVariable Long id) throws URISyntaxException {
        User user = obtainCurrentUser();
        EventDTO eventDTO = findEvent(id);
        List<EventParticipantDTO> participants = findParticipants(eventDTO);
        if(isEventOver(eventDTO)) {
            throw new EventControllerException(messageSource.getMessage("event.isOver", null, LocaleUtil.getUserLocale()));
        }
        if(isUserParticipant(participants)) {
            throw new EventControllerException(messageSource.getMessage("user.already.participates", null, LocaleUtil.getUserLocale()));
        }
        if(isUserOwner(eventDTO)) {
            throw new EventControllerException(messageSource.getMessage("owner.notAllowed", null, LocaleUtil.getUserLocale()));
        }
        incrementCurrentAttendance(eventDTO);
        EventParticipantDTO eventParticipantDTO = createEventParticipant(user, eventDTO);
        EventParticipantDTO result = eventParticipantService.save(eventParticipantDTO);
        return ResponseEntity.created(new URI("/api/join-event/" + result.getId()))
                .body(result);
    }

    /**
     * {@code Post /leave-event/:id} : Leave an event
     *
     * @param id the id of the event to leave.
     * @return the {@link ResponseEntity} with status {@code 2204 (NO_CONTENT)}, or with status {@code 400 (Bad Request)} if the logged in user isn't a participant of the given event or is the owner of the event.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @DeleteMapping("/leave-event/{id}")
    public ResponseEntity<Void> leaveEvent(@PathVariable Long id) throws URISyntaxException {
        User user = obtainCurrentUser();
        EventDTO eventDTO = findEvent(id);
        List<EventParticipantDTO> participants = findParticipants(eventDTO);
        EventParticipantDTO participantToRemove = findEventParticipantOfUser(participants);
        if(isEventOver(eventDTO)) {
            throw new EventControllerException(messageSource.getMessage("event.isOver", null, LocaleUtil.getUserLocale()));
        }
        if(isUserOwner(eventDTO)) {
            throw new EventControllerException(messageSource.getMessage("owner.notAllowed", null, LocaleUtil.getUserLocale()));
        }
        eventParticipantService.delete(participantToRemove.getId());
        decrementCurrentAttendance(eventDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /rate-participant/:eventParticipantId} : rate a participant
     *
     * @param eventParticipantId the id of the eventParticipant to rate.
     * @param ratingVM the rating of the participant.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}  and with body the updated EventParticipantDTO, or with status {@code 400 (Bad Request)} if the event participant to rate was not found or the logged in user isn't the owner of the event.
     */
    @PostMapping("/rate-participant/{eventParticipantId}")
    public ResponseEntity<EventParticipantDTO> rateParticipant(@PathVariable Long eventParticipantId,
                                                               @RequestBody @Valid RatingVM ratingVM) {
        EventParticipantDTO eventParticipantDTO = findEventParticipant(eventParticipantId);
        EventDTO event = findEvent(eventParticipantDTO.getEventId());
        if(!isUserOwner(event)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request", null, LocaleUtil.getUserLocale()));
        }
        eventParticipantDTO.setRatingOfParticipant(ratingVM.getRating());
        EventParticipantDTO result = eventParticipantService.save(eventParticipantDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST rate-event/:eventId} : rate an event
     *
     * @param eventId the id of the event to rate.
     * @param ratingVM the rating of the event.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}  and with body the updated EventParticipantDTO, or with status {@code 400 (Bad Request)} if the logged in user isn't a participant of the event.
     */
    @PostMapping("rate-event/{eventId}")
    public ResponseEntity<EventParticipantDTO> rateEvent(@PathVariable Long eventId,
                                                         @RequestBody @Valid RatingVM ratingVM) {
        EventDTO eventDTO = findEvent(eventId);
        List<EventParticipantDTO> participants = findParticipants(eventDTO);
        if(!isUserParticipant(participants)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request", null, LocaleUtil.getUserLocale()));
        }
        EventParticipantDTO eventParticipantDTO = findEventParticipantOfUser(participants);
        eventParticipantDTO.setRatingOfEvent(ratingVM.getRating());
        EventParticipantDTO result = eventParticipantService.save(eventParticipantDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST approve-participant/:participantId} : approve a participant.
     *
     * @param participantId the id of the event participant to approve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}  and with body the updated EventParticipantDTO, or with status {@code 400 (Bad Request)} if the logged in user isn't the owner of the event.
     */
    @PostMapping("approve-participant/{participantId}")
    public ResponseEntity<EventParticipantDTO> approveParticipant(@PathVariable Long participantId) {
        EventParticipantDTO eventParticipantDTO = findEventParticipant(participantId);
        EventDTO eventDTO = findEvent(eventParticipantDTO.getEventId());
        if(!isUserOwner(eventDTO)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request", null, LocaleUtil.getUserLocale()));
        }
        eventParticipantDTO.setApproved(true);
        EventParticipantDTO result = eventParticipantService.save(eventParticipantDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET own-events} : get the logged in user's own events.
     * @return the events created by the logged in user.
     */
    @GetMapping("/own-events")
    public List<EventDTO> getOwnEvents(){
        List<EventDTO> ownEvents = new ArrayList<>();
        User user = obtainCurrentUser();
        ownEvents = findOwnEvents(user);
        return ownEvents;
    }

    /**
     * {@code GET participate-events} : get the events the logged in user participates in.
     * @return the events joined by the logged in user.
     */
    @GetMapping("/participate-events")
    public List<EventDTO> getParticipateEvents(){
        List<EventDTO> participateEvents = new ArrayList<>();
        User user = obtainCurrentUser();
        participateEvents = findParticipatedEvents(user);
        return participateEvents;
    }

    /**
     * {@code GET active-events} : get the every active event.
     * @return the active events.
     */
    @GetMapping("/active-events")
    public List<EventDTO> getActiveEvents(){
        List<EventDTO> events = eventService.findAll();
        List<EventDTO> activeEvents = filterActiveEvents(events);
        return activeEvents;
    }

    /**
     * {@code DELETE  /delete-participant/:id} : delete the given EventParticipant
     *
     * @param id the id of the eventParticipant to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}, or with status {@code 400 (Bad Request)} if the logged in user isn't the owner of the event.
     */
    @DeleteMapping("/delete-participant/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        logger.debug("REST request to delete Event Participant: {}", id);
        EventParticipantDTO eventParticipantDTO = findEventParticipant(id);
        EventDTO eventDTO = findEvent(eventParticipantDTO.getEventId());
        if(!isUserOwner(eventDTO)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request", null, LocaleUtil.getUserLocale()));
        }
        decrementCurrentAttendance(eventDTO);
        eventParticipantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void incrementCurrentAttendance(EventDTO eventDTO) {
        if(eventDTO.getMaxAttendance().equals(eventDTO.getCurrentAttendance())) {
            throw new EventControllerException(messageSource.getMessage("event.full",
                    null, LocaleUtil.getUserLocale()));
        }
        eventDTO.setCurrentAttendance(eventDTO.getCurrentAttendance() + 1);
        eventService.save(eventDTO);
    }

    private void decrementCurrentAttendance(EventDTO eventDTO) {
        eventDTO.setCurrentAttendance(eventDTO.getCurrentAttendance() - 1);
        eventService.save(eventDTO);
    }

    private EventParticipantDTO findEventParticipant(@PathVariable Long id) {
        Optional<EventParticipantDTO> participant = eventParticipantService.findOne(id);
        if(participant.isPresent()) {
            return participant.get();
        } else {
            throw new EventControllerException(messageSource.getMessage("participant.notExist", null, LocaleUtil.getUserLocale()));
        }
    }

    private List<EventDTO> filterActiveEvents(List<EventDTO> events) {
        return events.stream().filter(event -> event.getStartsAt().isAfter(LocalDateTime.now())).collect(Collectors.toList());
    }

    private EventDetailVM createEventDetail(Long id) {
        EventDetailVM eventDetail = new EventDetailVM();
        EventDTO eventDTO = findEvent(id) ;
        eventDetail.setEventDetails(eventDTO);
        eventDetail.setEventLocation(findEventLocation(eventDTO));
        eventDetail.setParticipants(findParticipants(eventDTO));
        eventDetail.setOwner(isUserOwner(eventDTO));
        eventDetail.setParticipant(isUserParticipant(eventDetail.getParticipants()));
        eventDetail.setOver(isEventOver(eventDTO));
        eventDetail.setRatingOfEventByUser(findRatingOfEventByUser(eventDetail.getParticipants()));
        return eventDetail;
    }

    private Integer findRatingOfEventByUser(List<EventParticipantDTO> participants) {
        User user = obtainCurrentUser();
        Optional<EventParticipantDTO> userParticipant = participants.stream().filter(participant -> participant.getUserId().equals(user.getId())).findFirst();
        if(userParticipant.isPresent()) {
            return userParticipant.get().getRatingOfEvent();
        } else {
            return 0;
        }
    }

    private EventParticipantDTO createEventParticipant(User user, EventDTO eventDTO) {
        EventParticipantDTO eventParticipantDTO = new EventParticipantDTO();
        eventParticipantDTO.setApproved(false);
        eventParticipantDTO.setEventId(eventDTO.getId());
        eventParticipantDTO.setEventName(eventDTO.getName());
        eventParticipantDTO.setJoinedAt(LocalDateTime.now());
        eventParticipantDTO.setParticipated(false);
        eventParticipantDTO.setUserId(user.getId());
        eventParticipantDTO.setUserUsername(user.getUsername());
        return eventParticipantDTO;
    }

    private EventDTO findEvent(Long id) {
        Optional<EventDTO> eventDTO = eventService.findOne(id);
        if(eventDTO.isPresent()) {
            return eventDTO.get();
        } else {
            throw new EventControllerException(messageSource.getMessage("event.notFound", null, LocaleUtil.getUserLocale()));
        }
    }

    private LocationDTO findEventLocation(EventDTO eventDTO) {
        Optional<LocationDTO> locationDTO = locationService.findOne(eventDTO.getLocationId());
        if(locationDTO.isPresent()) {
            return locationDTO.get();
        } else {
            throw new EventControllerException(messageSource.getMessage("location.notFound", null, LocaleUtil.getUserLocale()));
        }
    }

    private List<EventParticipantDTO> findParticipants(EventDTO eventDTO) {
        List<EventParticipantDTO> participants = eventParticipantService.findByEvent(eventDTO.getId());
        return participants;
    }

    private boolean isUserOwner(EventDTO eventDTO) {
        User user = obtainCurrentUser();
        logger.debug("Current user: " + user.getUsername() + "  " + user.getId());
        logger.debug("Event: " + eventDTO.getName() + "  " + eventDTO.getId());
        return  user.getId().equals(eventDTO.getCreatedById());
    }

    private User obtainCurrentUser() {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EventControllerException(
                messageSource.getMessage("user.notFound", null, LocaleUtil.getUserLocale())));
        logger.debug("Current username: " + username);
        return userService.getUserWithAuthoritiesByLogin(username).get();
    }

    private boolean isUserParticipant(List<EventParticipantDTO> participants) {
        User user = obtainCurrentUser();
        logger.debug("Current user: " + user.getUsername() + "  " + user.getId());
        return participants.stream()
                .filter(participant -> participant.getUserId().equals(user.getId()))
                .findFirst().isPresent();
    }

    private EventParticipantDTO findEventParticipantOfUser(List<EventParticipantDTO> participants) {
        User user = obtainCurrentUser();
        Optional<EventParticipantDTO> userParticipant =  participants.stream()
                .filter(participant -> participant.getUserId().equals(user.getId()))
                .findFirst();
        if(userParticipant.isPresent()) {
            return userParticipant.get();
        } else {
            throw new EventControllerException(messageSource.getMessage("eventParticipants.notFound", null, LocaleUtil.getUserLocale()));
        }
    }

    private boolean isEventOver(EventDTO eventDTO) {
        return eventDTO.getStartsAt().isBefore(LocalDateTime.now());
    }

    private List<EventDTO> findOwnEvents(User user) {
        List<EventDTO> result =  eventService.findByOwner(user.getId());
        if(result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    private List<EventDTO> findParticipatedEvents(User user) {
        List<EventDTO> result = eventParticipantService.findByUserId(user.getId()).stream()
                .map(eventParticipantDTO -> eventService.findOne(eventParticipantDTO.getEventId()).get())
                .collect(Collectors.toList());
        if(result == null) {
            result = new ArrayList<>();
        }
        return result;
    }
}
