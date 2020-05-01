package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.ProfileDetailsVM;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.service.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private EventService eventService;

    private EventTypeService eventTypeService;

    private EventParticipantService eventParticipantService;

    private UserService userService;

    private UserDetailsService userDetailsService;

    private LocationService locationService;

    private UserMapper userMapper;

    private MessageSource messageSource;

    public ProfileController(EventService eventService, EventTypeService eventTypeService,
                             EventParticipantService eventParticipantService, UserService userService,
                             LocationService locationService, UserDetailsService userDetailsService,
                             UserMapper userMapper, MessageSource messageSource) {
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.eventParticipantService = eventParticipantService;
        this.userService = userService;
        this.locationService = locationService;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.messageSource = messageSource;
    }

    /**
     * {@code GET profile/:id} : Get Profile Details
     *
     * @param id the id of the user whose profile details are to be obtained.
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the obtained ProfileDetailsVM, or with status {@code 404 (Not Found)} if the user was not found, or the profile viewmodel couldn't be created.
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDetailsVM> getProfile(@PathVariable Long id) {
        ProfileDetailsVM profile = createProfileVM(id);
        return profile != null ? ResponseEntity.ok().body(profile) : ResponseEntity.notFound().build();
    }

    private ProfileDetailsVM createProfileVM(Long id) {
        ProfileDetailsVM result = null;
        try {
            result = new ProfileDetailsVM();
            User user = findUser(id);
            result.setUser(userMapper.userToUserDTO(user));
            UserDetailsDTO userDetailsDTO = findUserDetails(id);
            result.setUserDetails(userDetailsDTO);
            List<EventDTO> ownEvents = findOwnEvents(user);
            List<EventDTO> activeOwnEvents = filterActiveEvents(ownEvents);
            result.setOwnEvents(activeOwnEvents);
            List<EventDTO> participatedEvents = findParticipatedEvents(user);
            List<EventDTO> activeParticipatedEvents = filterActiveEvents(participatedEvents);
            result.setParticipateEvents(activeParticipatedEvents);
            Double scoreAsOwner = calculateOwnerScore(ownEvents);
            result.setRatingAsOwner(scoreAsOwner);
            Double scoreAsParticipant = calculateParticipantScore(participatedEvents);
            result.setRatingAsParticipant(scoreAsParticipant);
       } catch (ProfileControllerException e) {
            logger.error(e.getMessage());
            result = null;
        }
        return result;
    }

    private Double calculateOwnerScore(List<EventDTO> ownEvents) {
        Double scoreAsOwner = 0.0;
        List<EventDTO> completedOwnEvents = filterCompletedEvents(ownEvents);
        logger.debug("Owner score events: " + ownEvents);
        logger.debug("Owner score completed events: " + completedOwnEvents);
        List<EventParticipantDTO> ownEventParticipantRatings = findRatingsForEvents(completedOwnEvents);
        Integer scoreSum = summarizeEventScore(ownEventParticipantRatings);
        scoreAsOwner = calculateAverage(ownEventParticipantRatings, scoreSum);
        return scoreAsOwner;
    }

    private Double calculateParticipantScore(List<EventDTO> participatedEvents) {
        Double scoreAsParticipant = 0.0;
        List<EventDTO> completedParticipatedEvents = filterCompletedEvents(participatedEvents);
        logger.debug("Participan score events: " + participatedEvents);
        logger.debug("Participan score completed events: " + completedParticipatedEvents);
        List<EventParticipantDTO> ratedParticipations = findRatingsForParticipation(completedParticipatedEvents);
        Integer scoreSum = summarizeParticipantScore(ratedParticipations);
        scoreAsParticipant = calculateAverage(ratedParticipations, scoreSum);
        return scoreAsParticipant;
    }

    private double calculateAverage(List<EventParticipantDTO> eventParticipants, double scoreSum) {
        return eventParticipants.isEmpty() ? 0 : scoreSum / eventParticipants.size();
    }

    private Integer summarizeEventScore(List<EventParticipantDTO> eventParticipants) {
        return eventParticipants.stream()
                .reduce(0,
                        (subtotal, eventParticipant) -> subtotal + eventParticipant.getRatingOfEvent(),
                        Integer::sum);
    }

    private Integer summarizeParticipantScore(List<EventParticipantDTO> ratedParticipations) {
        return ratedParticipations.stream()
                .reduce(0,
                        (subtotal, eventParticipant) -> subtotal + eventParticipant.getRatingOfParticipant(),
                        Integer::sum);
    }

    private List<EventParticipantDTO> findRatingsForEvents(List<EventDTO> events) {
        return events.stream()
                .flatMap(event -> eventParticipantService.findByEvent(event.getId()).stream())
                .filter(eventParticipant -> eventParticipant.getRatingOfEvent() != null)
                .collect(Collectors.toList());
    }

    private UserDetailsDTO findUserDetails(Long id) {
        Optional<UserDetailsDTO> result = userDetailsService.findByUserId(id);
        if(result.isEmpty()) {
            throw new ProfileControllerException(messageSource.getMessage("user.noDetails", null, LocaleUtil.getUserLocale()));
        }
        return result.get();
    }

    private List<EventParticipantDTO> findRatingsForParticipation(List<EventDTO> events) {
        return events.stream()
                .flatMap(event -> eventParticipantService.findByEvent(event.getId()).stream())
                .filter(eventParticipant -> eventParticipant.getRatingOfParticipant() != null)
                .collect(Collectors.toList());
    }

    private List<EventDTO> filterCompletedEvents(List<EventDTO> events) {
        return events.stream().filter(event -> event.getStartsAt().isBefore(LocalDateTime.now())).collect(Collectors.toList());
    }

    private List<EventDTO> filterActiveEvents(List<EventDTO> events) {
        return events.stream().filter(event -> event.getStartsAt().isAfter(LocalDateTime.now())).collect(Collectors.toList());
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

    private User findUser(Long id) {
        Optional<User> user = userService.getUserWithAuthorities(id);
        if(user.isEmpty()) {
            throw new ProfileControllerException(messageSource.getMessage("user.notFound", null, LocaleUtil.getUserLocale()));
        }
        return user.get();
    }

    public static class ProfileControllerException extends RuntimeException {
        private ProfileControllerException(String message) {
            super(message);
        }
    }
}
