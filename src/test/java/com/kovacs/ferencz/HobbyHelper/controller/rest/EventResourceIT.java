package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.CreateEventVM;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EventResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_ATTENDANCE = 1;
    private static final Integer UPDATED_MAX_ATTENDANCE = 2;

    private static final Integer DEFAULT_MIN_ATTENDANCE = 1;
    private static final Integer UPDATED_MIN_ATTENDANCE = 2;

    private static final Integer DEFAULT_CURRENT_ATTENDANCE = 1;
    private static final Integer UPDATED_CURRENT_ATTENDANCE = 2;

    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final LocalDateTime UPDATED_CREATED_AT = LocalDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final LocalDateTime DEFAULT_STARTS_AT = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final LocalDateTime UPDATED_STARTS_AT = LocalDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

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
    private EventMapper eventMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private EventService eventService;

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

    private MockMvc restEventMockMvc;

    private Event event;

    @BeforeEach
    public void setup() {
        clearDatabase();
        event = initEvent();
        MockitoAnnotations.initMocks(this);
        final EventResource eventResource = new EventResource(eventService, locationService, messageSource);
        this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createEvent() throws Exception {
        //GIVEN
        Location location = event.getLocation();
        EventDTO eventDTO = eventMapper.toDto(event);
        LocationDTO locationDTO = locationMapper.toDto(location);
        locationDTO.setId(null);
        CreateEventVM createEventVM = new CreateEventVM();
        createEventVM.setLocation(locationDTO);
        createEventVM.setEvent(eventDTO);
        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createEventVM)))
            .andExpect(status().isCreated());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getMaxAttendance()).isEqualTo(DEFAULT_MAX_ATTENDANCE);
        assertThat(testEvent.getMinAttendance()).isEqualTo(DEFAULT_MIN_ATTENDANCE);
        assertThat(testEvent.getCurrentAttendance()).isEqualTo(DEFAULT_CURRENT_ATTENDANCE);
        assertThat(testEvent.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createEventWithExistingId() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        event.setId(1L);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkNameIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setName(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkDescriptionIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setDescription(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //wHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkMaxAttendanceIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setMaxAttendance(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkMinAttendanceIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setMinAttendance(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkCurrentAttendanceIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setCurrentAttendance(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkCreatedAtIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setCreatedAt(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkStartsAtIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setStartsAt(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkPriceIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        event.setPrice(null);
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEvents() throws Exception {
        //GIVEN
        Event savedEvent = eventRepository.saveAndFlush(event);
        //WHEN
        //THEN
        restEventMockMvc.perform(get("/api/events?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].maxAttendance").value(hasItem(DEFAULT_MAX_ATTENDANCE)))
            .andExpect(jsonPath("$.[*].minAttendance").value(hasItem(DEFAULT_MIN_ATTENDANCE)))
            .andExpect(jsonPath("$.[*].currentAttendance").value(hasItem(DEFAULT_CURRENT_ATTENDANCE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getEvent() throws Exception {
        //GIVEN
        Event savedEvent = eventRepository.saveAndFlush(event);
        //WHEN
        restEventMockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(savedEvent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.maxAttendance").value(DEFAULT_MAX_ATTENDANCE))
            .andExpect(jsonPath("$.minAttendance").value(DEFAULT_MIN_ATTENDANCE))
            .andExpect(jsonPath("$.currentAttendance").value(DEFAULT_CURRENT_ATTENDANCE))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingEvent() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateEvent() throws Exception {
        //GIVEN
        Event savedEvent = eventRepository.saveAndFlush(event);
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        Event updatedEvent = eventRepository.findById(savedEvent.getId()).get();
        em.detach(updatedEvent);
        updatedEvent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .maxAttendance(UPDATED_MAX_ATTENDANCE)
            .minAttendance(UPDATED_MIN_ATTENDANCE)
            .currentAttendance(UPDATED_CURRENT_ATTENDANCE)
            .createdAt(UPDATED_CREATED_AT)
            .startsAt(UPDATED_STARTS_AT)
            .price(UPDATED_PRICE);
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);
        //WHEN
        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isOk());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getMaxAttendance()).isEqualTo(UPDATED_MAX_ATTENDANCE);
        assertThat(testEvent.getMinAttendance()).isEqualTo(UPDATED_MIN_ATTENDANCE);
        assertThat(testEvent.getCurrentAttendance()).isEqualTo(UPDATED_CURRENT_ATTENDANCE);
        assertThat(testEvent.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateNonExistingEvent() throws Exception {
        //GIVEN
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        EventDTO eventDTO = eventMapper.toDto(event);
        //WHEN
        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteEvent() throws Exception {
        //GIVEN
        Event savedEvent = eventRepository.saveAndFlush(event);
        int databaseSizeBeforeDelete = eventRepository.findAll().size();
        //WHEN
        restEventMockMvc.perform(delete("/api/events/{id}", savedEvent.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());
        //THEN
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private void clearDatabase() {
        eventRepository.deleteAll();
        eventRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
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
        result.setId(1L);
        result.setUsername("user");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        result = userRepository.saveAndFlush(result);
        return result;
    }

    private Event initEvent() {
        Event result = new Event();
        result.setLocation(initLocation());
        result.setCurrentAttendance(DEFAULT_CURRENT_ATTENDANCE);
        result.setMinAttendance(DEFAULT_MIN_ATTENDANCE);
        result.setMaxAttendance(DEFAULT_MAX_ATTENDANCE);
        result.setCreatedAt(DEFAULT_CREATED_AT);
        result.setCreatedBy(initOwnerUser());
        result.setDescription(DEFAULT_DESCRIPTION);
        result.setStartsAt(DEFAULT_STARTS_AT);
        result.setPrice(DEFAULT_PRICE);
        EventType type = new EventType();
        type.setId(1L);
        type.setDescription("Sport");
        type.setName("Sport");
        type.setBannerUrl("/");
        type.setIconUrl("/");
        type = eventTypeRepository.saveAndFlush(type);
        result.setType(type);
        result.setName(DEFAULT_NAME);
        result.setId(null);
        return result;
    }

    private Location initLocation() {
        Location result = new Location();
        result.setId(1L);
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
