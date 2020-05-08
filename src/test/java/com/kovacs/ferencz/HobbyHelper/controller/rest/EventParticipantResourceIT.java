package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.EventParticipantService;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventParticipantDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventParticipantMapper;
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
 * Integration tests for the {@link EventParticipantResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventParticipantResourceIT {

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

    @Autowired
    private EventParticipantMapper eventParticipantMapper;

    @Autowired
    private EventParticipantService eventParticipantService;

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

    private MockMvc restEventParticipantMockMvc;

    private EventParticipant eventParticipant;

    @BeforeEach
    public void setup() {
        clearDatabase();
        eventParticipant = initEventParticipant();

        MockitoAnnotations.initMocks(this);
        final EventParticipantResource eventParticipantResource = new EventParticipantResource(eventParticipantService, eventService, messageSource);
        this.restEventParticipantMockMvc = MockMvcBuilders.standaloneSetup(eventParticipantResource)
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
    public void createEventParticipant() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = eventParticipantRepository.findAll().size();
        eventParticipant.setId(null);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(post("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isCreated());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeCreate + 1);
        EventParticipant testEventParticipant = eventParticipantList.get(eventParticipantList.size() - 1);
        assertThat(testEventParticipant.isApproved()).isEqualTo(DEFAULT_APPROVED);
        assertThat(testEventParticipant.isParticipated()).isEqualTo(DEFAULT_PARTICIPATED);
        assertThat(testEventParticipant.getRatingOfParticipant()).isEqualTo(DEFAULT_RATING_OF_PARTICIPANT);
        assertThat(testEventParticipant.getRatingOfEvent()).isEqualTo(DEFAULT_RATING_OF_EVENT);
    }

    @Test
    @Transactional
    public void createEventParticipantWithExistingId() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = eventParticipantRepository.findAll().size();
        eventParticipant.setId(1L);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(post("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkJoinedAtIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventParticipantRepository.findAll().size();
        eventParticipant.setJoinedAt(null);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(post("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkApprovedIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventParticipantRepository.findAll().size();
        eventParticipant.setApproved(null);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(post("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkParticipatedIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventParticipantRepository.findAll().size();
        eventParticipant.setParticipated(null);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(post("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEventParticipants() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        //THEN
        restEventParticipantMockMvc.perform(get("/api/event-participants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(savedParticipant.getId().intValue())))
                .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED.booleanValue())))
                .andExpect(jsonPath("$.[*].participated").value(hasItem(DEFAULT_PARTICIPATED.booleanValue())))
                .andExpect(jsonPath("$.[*].ratingOfParticipant").value(hasItem(DEFAULT_RATING_OF_PARTICIPANT)))
                .andExpect(jsonPath("$.[*].ratingOfEvent").value(DEFAULT_RATING_OF_EVENT));
    }

    @Test
    @Transactional
    @WithMockUser()
    public void getEventParticipant() throws Exception {
        //GIVEN
        EventParticipant savedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        //THEN
        restEventParticipantMockMvc.perform(get("/api/event-participants/{id}", savedEventParticipant.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedEventParticipant.getId().intValue()))
                .andExpect(jsonPath("$.approved").value(DEFAULT_APPROVED.booleanValue()))
                .andExpect(jsonPath("$.participated").value(DEFAULT_PARTICIPATED.booleanValue()))
                .andExpect(jsonPath("$.ratingOfParticipant").value(DEFAULT_RATING_OF_PARTICIPANT))
                .andExpect(jsonPath("$.ratingOfEvent").value(DEFAULT_RATING_OF_EVENT));
    }

    @Test
    @Transactional
    public void getNonExistingEventParticipant() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restEventParticipantMockMvc.perform(get("/api/event-participants/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateEventParticipant() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        int databaseSizeBeforeUpdate = eventParticipantRepository.findAll().size();
        EventParticipant updatedEventParticipant = eventParticipantRepository.findById(savedParticipant.getId()).get();

        em.detach(updatedEventParticipant);
        updatedEventParticipant
                .joinedAt(UPDATED_JOINED_AT)
                .approved(UPDATED_APPROVED)
                .participated(UPDATED_PARTICIPATED)
                .ratingOfParticipant(UPDATED_RATING_OF_PARTICIPANT)
                .setRatingOfEvent(UPDATED_RATING_OF_EVENT);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(updatedEventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(put("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isOk());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeUpdate);
        EventParticipant testEventParticipant = eventParticipantList.get(eventParticipantList.size() - 1);
        assertThat(testEventParticipant.isApproved()).isEqualTo(UPDATED_APPROVED);
        assertThat(testEventParticipant.isParticipated()).isEqualTo(UPDATED_PARTICIPATED);
        assertThat(testEventParticipant.getRatingOfParticipant()).isEqualTo(UPDATED_RATING_OF_PARTICIPANT);
        assertThat(testEventParticipant.getRatingOfEvent()).isEqualTo(UPDATED_RATING_OF_EVENT);
    }

    @Test
    @Transactional
    public void updateNonExistingEventParticipant() throws Exception {
        //GIVEN
        int databaseSizeBeforeUpdate = eventParticipantRepository.findAll().size();
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        //WHEN
        restEventParticipantMockMvc.perform(put("/api/event-participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventParticipantDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteEventParticipant() throws Exception {
        //GIVEN
        EventParticipant savedParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);
        int databaseSizeBeforeDelete = eventParticipantRepository.findAll().size();
        //WHEN
        restEventParticipantMockMvc.perform(delete("/api/event-participants/{id}", savedParticipant.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
        //THEN
        List<EventParticipant> eventParticipantList = eventParticipantRepository.findAll();
        assertThat(eventParticipantList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private void clearDatabase() {
        eventParticipantRepository.deleteAll();
        eventParticipantRepository.flush();
        eventRepository.deleteAll();
        eventRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
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
        result.setId(2L);
        result.setUsername("participant");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        result = userRepository.saveAndFlush(result);
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
