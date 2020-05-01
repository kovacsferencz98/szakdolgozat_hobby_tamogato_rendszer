package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.EventDetailVM;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RatingVM;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventParticipantMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for the {@link ChatController} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventControllerIT {
    private static final LocalDateTime DEFAULT_JOINED_AT = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final LocalDateTime UPDATED_JOINED_AT = LocalDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    private static final Boolean DEFAULT_PARTICIPATED = false;
    private static final Boolean UPDATED_PARTICIPATED = true;

    private static final Integer DEFAULT_RATING_OF_PARTICIPANT = 1;
    private static final Integer UPDATED_RATING_OF_PARTICIPANT = 2;

    private static final Integer DEFAULT_RATING_OF_EVENT = 3;
    private static final Integer UPDATED_RATING_OF_EVENT = 4;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired UserDetailsRepository userDetailsRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private EventParticipantMapper eventParticipantMapper;

    @Autowired
    private EventParticipantService eventParticipantService;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc restEventControllerMockMvc;

    private EventParticipant eventParticipant;

    private User owningUser;

    private UserDetails owningUserDetails;

    private User participantUser;

    private UserDetails participantUserDetails;

    private Event event;

    @BeforeEach
    public void setup() {
        clearDatabase();
        eventParticipant = initEventParticipant();
        owningUserDetails = initUserDetails(owningUser);
        participantUserDetails = initUserDetails(participantUser);
        MockitoAnnotations.initMocks(this);
        final EventController eventController = new EventController(eventService, eventTypeService, userService, locationService,
                eventParticipantService, messageSource);
        restEventControllerMockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void getEventDetailShouldReturnProperData() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        ResultActions resultActions = restEventControllerMockMvc.perform(get("/api/event-detail/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        EventDetailVM eventDetail = objectMapper.readValue(contentAsString, EventDetailVM.class);
        //THEN
        assertEquals(1, eventDetail.getParticipants().size());
        assertEquals(savedParticipant.getId(), eventDetail.getParticipants().get(0).getId());
        assertEquals(event.getId(), eventDetail.getEventDetails().getId());
        assertEquals(locationMapper.toDto(event.getLocation()), eventDetail.getEventLocation());
        assertEquals(0, eventDetail.getRatingOfEventByUser());
        assertTrue(eventDetail.isOwner());
        assertFalse(eventDetail.isParticipant());
        assertFalse(eventDetail.isOver());
    }

    @Test
    @Transactional
    @WithMockUser(username="participant", roles = {"USER", "ADMIN"})
    public void leaveEventShouldDeleteEntity() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        int participantNumber = eventParticipantRepository.findAll().size();
        int attendance = event.getCurrentAttendance();
        //WHEN
        restEventControllerMockMvc.perform(delete("/api/leave-event/{id}", event.getId()))
                .andExpect(status().isNoContent());
        //THEN
        List<EventParticipant> participants = eventParticipantRepository.findAll();
        assertEquals(participantNumber - 1, participants.size());
        assertTrue(participants.isEmpty());
        Optional<Event> updatedEvent = eventRepository.findById(event.getId());
        assertEquals(attendance - 1, updatedEvent.get().getCurrentAttendance());
    }

    @Test
    @Transactional
    @WithMockUser(username="participant", roles = {"USER", "ADMIN"})
    public void joinEventShouldCreateEntity() throws Exception {
        //GIVEN
        int participantNumber = eventParticipantRepository.findAll().size();
        int attendance = event.getCurrentAttendance();
        //WHEN
        restEventControllerMockMvc.perform(post("/api/join-event/{id}", event.getId()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        //THEN
        List<EventParticipant> participants = eventParticipantRepository.findAll();
        assertEquals(participantNumber+1, participants.size());
        EventParticipant createdParticipant = participants.get(participants.size() - 1);
        assertEquals(event.getId(), createdParticipant.getEvent().getId());
        assertEquals(participantUser.getId() ,createdParticipant.getUser().getId());
        Optional<Event> updatedEvent = eventRepository.findById(event.getId());
        assertEquals(attendance + 1, updatedEvent.get().getCurrentAttendance());
    }

    @Test
    @Transactional
    @WithMockUser(username="participant", roles = {"USER", "ADMIN"})
    public void rateEventShouldUpdateEntity() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        int oldRating = savedParticipant.getRatingOfEvent();
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(UPDATED_RATING_OF_EVENT);
        //WHEN
        restEventControllerMockMvc.perform(post("/api/rate-event/{eventId}", event.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ratingVM)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        //THEN
        Optional<EventParticipant> updated = eventParticipantRepository.findById(savedParticipant.getId());
        assertTrue(updated.isPresent());
        EventParticipant updatedEntity = updated.get();
        assertEquals(UPDATED_RATING_OF_EVENT, updatedEntity.getRatingOfEvent());
        assertNotEquals(oldRating, updatedEntity.getRatingOfEvent());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void rateParticipantShouldUpdateEntity() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        int oldRating = savedParticipant.getRatingOfParticipant();
        RatingVM ratingVM = new RatingVM();
        ratingVM.setRating(UPDATED_RATING_OF_PARTICIPANT);
        //WHEN
        restEventControllerMockMvc.perform(post("/api/rate-participant/{eventParticipantId}", savedParticipant.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ratingVM)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        //THEN
        Optional<EventParticipant> updated = eventParticipantRepository.findById(savedParticipant.getId());
        assertTrue(updated.isPresent());
        EventParticipant updatedEntity = updated.get();
        assertEquals(UPDATED_RATING_OF_PARTICIPANT, updatedEntity.getRatingOfParticipant());
        assertNotEquals(oldRating, updatedEntity.getRatingOfParticipant());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void approveParticipantShouldUpdateEntity() throws Exception {
        //GIVEN
        eventParticipant.setApproved(false);
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        restEventControllerMockMvc.perform(post("/api/approve-participant/{participantId}", savedParticipant.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        //THEN
        Optional<EventParticipant> updated = eventParticipantRepository.findById(savedParticipant.getId());
        assertTrue(updated.isPresent());
        EventParticipant updatedEntity = updated.get();
        assertTrue(updatedEntity.getApproved());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void ownEventsShouldReturnEntityList() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        ResultActions resultActions = restEventControllerMockMvc.perform(get("/api/own-events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<List<EventDTO>>(){});
        //THEN
        assertEquals(1, events.size());
        EventDTO obtainedEvent = events.get(0);
        assertEquals(event.getId(), obtainedEvent.getId());
        assertEquals(event.getName(), obtainedEvent.getName());
        assertEquals(event.getType().getId(), obtainedEvent.getTypeId());
        assertEquals(event.getLocation().getId(), obtainedEvent.getLocationId());
        assertEquals(event.getCreatedBy().getId(), obtainedEvent.getCreatedById());
    }

    @Test
    @Transactional
    @WithMockUser(username="participant", roles = {"USER", "ADMIN"})
    public void participateEventsShouldReturnEntityList() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        ResultActions resultActions = restEventControllerMockMvc.perform(get("/api/participate-events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<List<EventDTO>>(){});
        //THEN
        assertEquals(1, events.size());
        EventDTO obtainedEvent = events.get(0);
        assertEquals(event.getId(), obtainedEvent.getId());
        assertEquals(event.getName(), obtainedEvent.getName());
        assertEquals(event.getType().getId(), obtainedEvent.getTypeId());
        assertEquals(event.getLocation().getId(), obtainedEvent.getLocationId());
        assertEquals(event.getCreatedBy().getId(), obtainedEvent.getCreatedById());
    }

    @Test
    @Transactional
    @WithMockUser(username="newUsernew", roles = {"USER"})
    public void activeEventsShouldReturnEntityList() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        ResultActions resultActions = restEventControllerMockMvc.perform(get("/api/active-events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<List<EventDTO>>(){});
        //THEN
        assertEquals(1, events.size());
        EventDTO obtainedEvent = events.get(0);
        assertEquals(event.getId(), obtainedEvent.getId());
        assertEquals(event.getName(), obtainedEvent.getName());
        assertEquals(event.getType().getId(), obtainedEvent.getTypeId());
        assertEquals(event.getLocation().getId(), obtainedEvent.getLocationId());
        assertEquals(event.getCreatedBy().getId(), obtainedEvent.getCreatedById());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteParticipantShouldDeleteEntity() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        int participantNumber = eventParticipantRepository.findAll().size();
        int attendance = event.getCurrentAttendance();
        //WHEN
        restEventControllerMockMvc.perform(delete("/api/delete-participant/{id}", savedParticipant.getId()))
                .andExpect(status().isNoContent());
        //THEN
        List<EventParticipant> participants = eventParticipantRepository.findAll();
        assertEquals(participantNumber - 1, participants.size());
        assertTrue(participants.isEmpty());
        Optional<Event> updatedEvent = eventRepository.findById(event.getId());
        assertEquals(attendance - 1, updatedEvent.get().getCurrentAttendance());
    }

    private void clearDatabase() {
        eventParticipantRepository.deleteAll();
        eventParticipantRepository.flush();
        eventRepository.deleteAll();
        eventRepository.flush();
        userDetailsRepository.deleteAll();
        userDetailsRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();
    }

    private EventParticipant initEventParticipant() {
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipant.setApproved(DEFAULT_APPROVED);
        eventParticipant.setUser(initParticipantUser());
        eventParticipant.setEvent(initEvent());
        eventParticipant.setId(1L);
        eventParticipant.setParticipated(DEFAULT_PARTICIPATED);
        eventParticipant.setRatingOfEvent(DEFAULT_RATING_OF_EVENT);
        eventParticipant.setRatingOfParticipant(DEFAULT_RATING_OF_PARTICIPANT);
        return eventParticipant;
    }

    private User initParticipantUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email2@email.com");
        Role role = new Role(AuthoritiesConstants.USER);
        roleRepository.saveAndFlush(role);
        result.setRoles(new HashSet(Arrays.asList(role)));
        result.setId(null);
        result.setUsername("participant");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        result = userRepository.saveAndFlush(result);
        participantUser = result;
        return result;
    }

    private User initOwnerUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        Role role = new Role(AuthoritiesConstants.USER);
        roleRepository.saveAndFlush(role);
        result.setRoles(new HashSet(Arrays.asList(role)));
        result.setId(null);
        result.setUsername("user");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        result = userRepository.saveAndFlush(result);
        owningUser = result;
        return result;
    }

    private Event initEvent() {
        Event result = new Event();
        result.setLocation(initLocation());
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedBy(initOwnerUser());
        result.setDescription("Description");
        result.setStartsAt(LocalDateTime.now().plusMinutes(5));
        result.setPrice(50.0);
        EventType type = new EventType();
        type.setId(1L);
        type.setDescription("Sport");
        type.setName("Sport");
        type.setBannerUrl("/");
        type.setIconUrl("/");
        type = eventTypeRepository.saveAndFlush(type);
        result.setType(type);
        result.setName("Event");
        result.setId(1L);
        result = eventRepository.saveAndFlush(result);
        event = result;
        return result;
    }

    public UserDetails initUserDetails(User user) {
        UserDetails result = new UserDetails();
        result.setUser(user);
        result.setResidence(initLocation());
        result.setDescription("Description");
        result.setId(null);
        result = userDetailsRepository.saveAndFlush(result);
        return result;
    }

    private Location initLocation() {
        Location result = new Location();
        result.setId(null);
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setStreet("Egyetem ter");
        result.setNumber(1);
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        result = locationRepository.saveAndFlush(result);
        return result;
    }
}
