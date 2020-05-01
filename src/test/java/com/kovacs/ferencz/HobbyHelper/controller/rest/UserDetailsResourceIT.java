package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.CreateUserDetailVM;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.domain.UserDetails;
import com.kovacs.ferencz.HobbyHelper.repository.LocationRepository;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.repository.UserDetailsRepository;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserDetailsMapper;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserDetailsResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserDetailsResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restUserDetailsMockMvc;

    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        clearDatabase();
        userDetails = initUserDetails();
        MockitoAnnotations.initMocks(this);
        final UserDetailsResource userDetailsResource = new UserDetailsResource(userDetailsService, locationService, messageSource, userService);
        this.restUserDetailsMockMvc = MockMvcBuilders.standaloneSetup(userDetailsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createUserDetails() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = userDetailsRepository.findAll().size();
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);
        userDetailsDTO.setId(null);
        CreateUserDetailVM createUserDetailVM = createUserDetailVM();
        createUserDetailVM.setUserDetails(userDetailsDTO);
        //WHEN
        restUserDetailsMockMvc.perform(post("/api/user-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createUserDetailVM)))
            .andExpect(status().isCreated());
        //THEN
        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        assertThat(userDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        UserDetails testUserDetails = userDetailsList.get(userDetailsList.size() - 1);
        assertThat(testUserDetails.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createUserDetailsWithExistingId() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = userDetailsRepository.findAll().size();
        userDetails.setId(1L);
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);
        //WHEN
        restUserDetailsMockMvc.perform(post("/api/user-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDetailsDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        assertThat(userDetailsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkDescriptionIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = userDetailsRepository.findAll().size();
        userDetails.setDescription(null);
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);
        //WHEN
        restUserDetailsMockMvc.perform(post("/api/user-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDetailsDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        assertThat(userDetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUserDetails() throws Exception {
        //GIVEN
        UserDetails savedDetails = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        //THEN
        restUserDetailsMockMvc.perform(get("/api/user-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getUserDetails() throws Exception {
        //GIVEN
        UserDetails savedDetails = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        //THEN
        restUserDetailsMockMvc.perform(get("/api/user-details/{id}", savedDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(userDetails.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    public void getNonExistingUserDetails() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restUserDetailsMockMvc.perform(get("/api/user-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateUserDetails() throws Exception {
        //GIVEN
        UserDetails savedDetail = userDetailsRepository.saveAndFlush(userDetails);
        int databaseSizeBeforeUpdate = userDetailsRepository.findAll().size();
        UserDetails updatedUserDetails = userDetailsRepository.findById(savedDetail.getId()).get();
        em.detach(updatedUserDetails);
        updatedUserDetails
            .description(UPDATED_DESCRIPTION);
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(updatedUserDetails);
        //WHEN
        restUserDetailsMockMvc.perform(put("/api/user-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDetailsDTO)))
            .andExpect(status().isOk());
        //THEN
        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        assertThat(userDetailsList).hasSize(databaseSizeBeforeUpdate);
        UserDetails testUserDetails = userDetailsList.get(userDetailsList.size() - 1);
        assertThat(testUserDetails.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateNonExistingUserDetails() throws Exception {
        //GIVEN
        int databaseSizeBeforeUpdate = userDetailsRepository.findAll().size();
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);
        //WHEN
        restUserDetailsMockMvc.perform(put("/api/user-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDetailsDTO)))
            .andExpect(status().isBadRequest());
        //THEN
        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        assertThat(userDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteUserDetails() throws Exception {
        //GIVEN
        userDetails.setUser(null);
        UserDetails savedDetail = userDetailsRepository.saveAndFlush(userDetails);
        int databaseSizeBeforeDelete = userDetailsRepository.findAll().size();
        //WHEN
        restUserDetailsMockMvc.perform(delete("/api/user-details/{id}", savedDetail.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());
        //THEN
        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        assertThat(userDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private void clearDatabase() {
        userDetailsRepository.deleteAll();
        userDetailsRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
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

    private RegistrationVM createRegistrationVM() {
        RegistrationVM result = new RegistrationVM();
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setStreet("Egyetem ter");
        result.setNumber(1);
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setDescription(DEFAULT_DESCRIPTION);
        result.setZip("4031");
        result.setEmail("email@email.com");
        result.setUsername("User");
        result.setPassword("password");
        result.setLangKey("en");
        result.setFirstName("First");
        result.setLastName("Last");
        result.setActivated(false);
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

    private CreateUserDetailVM createUserDetailVM() {
        CreateUserDetailVM result = new CreateUserDetailVM();
        LocationDTO location = createLocationDTO();
        result.setLocation(location);
        return result;
    }
}
