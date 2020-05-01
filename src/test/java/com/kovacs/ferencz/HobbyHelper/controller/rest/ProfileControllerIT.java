package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.ProfileDetailsVM;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserDetailsMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for the {@link ProfileController} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProfileControllerIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    private static final Boolean DEFAULT_PARTICIPATED = false;
    private static final Boolean UPDATED_PARTICIPATED = true;

    private static final Integer DEFAULT_RATING_OF_PARTICIPANT = 1;
    private static final Integer UPDATED_RATING_OF_PARTICIPANT = 2;

    private static final Integer DEFAULT_RATING_OF_EVENT = 3;
    private static final Integer UPDATED_RATING_OF_EVENT = 4;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventParticipantService eventParticipantService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private UserDetailsMapper userDetailsMapper;

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

    private MockMvc restProfileControllerMockMvc;

    private UserDetails userDetails;

    private EventParticipant eventParticipant;

    private User user;

    @BeforeEach
    public void setup() {
        clearDatabase();
        userDetails = initUserDetails();
        eventParticipant = initEventParticipant();
        MockitoAnnotations.initMocks(this);
        final ProfileController profileController = new ProfileController(eventService, eventTypeService, eventParticipantService, userService,
                locationService, userDetailsService, userMapper, messageSource);
        this.restProfileControllerMockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }


    @Test
    @Transactional
    @WithMockUser()
    public void getEventParticipantShouldNotCalculateScoreFromActiveEvents() throws Exception {
        //GIVEN
        LocalDateTime time = eventParticipant.getEvent().getStartsAt().truncatedTo(ChronoUnit.MINUTES);
        eventParticipant.getEvent().setStartsAt(time);
        userDetailsRepository.saveAndFlush(userDetails);
        eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        ResultActions resultActions = restProfileControllerMockMvc.perform(get("/api/profile/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        ProfileDetailsVM profile = objectMapper.readValue(contentAsString, ProfileDetailsVM.class);
        //THEN
        assertNotNull(profile);
        assertEquals(userMapper.userToUserDTO(user), profile.getUser());
        assertEquals(userDetailsMapper.toDto(userDetails), profile.getUserDetails());
        assertEquals(Arrays.asList(eventMapper.toDto(eventParticipant.getEvent())), profile.getOwnEvents());
        assertTrue(profile.getParticipateEvents().isEmpty());
        assertEquals(0, profile.getRatingAsParticipant());
        assertEquals(0, profile.getRatingAsOwner());
    }

    @Test
    @Transactional
    @WithMockUser()
    public void getEventParticipantShouldCalculateScoreFromFinishedEvents() throws Exception {
        //GIVEN
        LocalDateTime time = eventParticipant.getEvent().getStartsAt().minusMinutes(5).truncatedTo(ChronoUnit.MINUTES);
        eventParticipant.getEvent().setStartsAt(time);
        eventRepository.saveAndFlush(eventParticipant.getEvent());
        userDetailsRepository.saveAndFlush(userDetails);
        eventParticipantRepository.saveAndFlush(eventParticipant);
        //WHEN
        ResultActions resultActions = restProfileControllerMockMvc.perform(get("/api/profile/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        ProfileDetailsVM profile = objectMapper.readValue(contentAsString, ProfileDetailsVM.class);
        //THEN
        assertNotNull(profile);
        assertEquals(userMapper.userToUserDTO(user), profile.getUser());
        assertEquals(userDetailsMapper.toDto(userDetails), profile.getUserDetails());
        assertTrue(profile.getOwnEvents().isEmpty());
        assertTrue(profile.getParticipateEvents().isEmpty());
        assertEquals(0, profile.getRatingAsParticipant());
        assertEquals((double)DEFAULT_RATING_OF_EVENT, profile.getRatingAsOwner());
    }

    private void clearDatabase() {
        eventParticipantRepository.deleteAll();
        eventParticipantRepository.flush();
        eventRepository.deleteAll();
        eventRepository.flush();
        userDetailsRepository.deleteAll();
        userDetailsRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();
    }

    private User initUser() {
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
        user = result;
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

    public UserDetails initUserDetails() {
        UserDetails result = new UserDetails();
        result.setUser(initUser());
        result.setResidence(initLocation());
        result.setDescription(DEFAULT_DESCRIPTION);
        result.setId(null);
        return result;
    }

    private LocationDTO createLocationDTO() {
        LocationDTO result = new LocationDTO();
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
        return result;
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

    private Event initEvent() {
        Event result = new Event();
        result.setLocation(initLocation());
        result.setCurrentAttendance(1);
        result.setMinAttendance(1);
        result.setMaxAttendance(3);
        result.setCreatedAt(LocalDateTime.now());
        result.setCreatedBy(user);
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
}
