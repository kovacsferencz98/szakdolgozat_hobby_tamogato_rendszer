package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
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
import com.kovacs.ferencz.HobbyHelper.service.MailService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import javax.persistence.EntityManager;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserResourceIT {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final String UPDATED_LOGIN = "johnNewMan";

    private static final Long DEFAULT_ID = 1L;

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passpassverypass";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "newmail@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "FirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "LastName";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

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

    private MockMvc restUserMockMvc;

    private User user;

    @BeforeEach
    public void setup() {
        clearDatabase();
        user = initUser();
        UserResource userResource = new UserResource(userService, mailService, messageSource);

        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter)
                .build();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createUser() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        UserDTO userDTO = userMapper.userToUserDTO(user);
        //WHEN
        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isCreated());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createUserWithExistingId() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        UserDTO userDTO = userMapper.userToUserDTO(user);
        userDTO.setId(1L);
        //WHEN
        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createUserWithExistingLogin() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        UserDTO userDTO = userMapper.userToUserDTO(user);
        userDTO.setEmail("anothermail@localhost");
        //WHEN
        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createUserWithExistingEmail() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        UserDTO userDTO = userMapper.userToUserDTO(user);
        userDTO.setUsername("anotherlogin");
        //WHEN
        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        //THEN
        restUserMockMvc.perform(get("/api/users?sort=id,desc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_LOGIN)))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANGKEY)));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        //WHEN
        //THEN
        restUserMockMvc.perform(get("/api/users/{username}", savedUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
                .andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.langKey").value(DEFAULT_LANGKEY));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restUserMockMvc.perform(get("/api/users/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateUser() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        User updatedUser = userRepository.findById(user.getId()).get();
        UserDTO userDTO = userMapper.userToUserDTO(updatedUser);
        userDTO.setId(updatedUser.getId());
        userDTO.setFirstName(UPDATED_FIRSTNAME);
        userDTO.setLastName(UPDATED_LASTNAME);
        userDTO.setEmail(UPDATED_EMAIL);
        userDTO.setLangKey(UPDATED_LANGKEY);
        //WHEN
        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isOk());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getLangKey()).isEqualTo(UPDATED_LANGKEY);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateUserLogin() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        User updatedUser = userRepository.findById(user.getId()).get();
        UserDTO userDTO = userMapper.userToUserDTO(updatedUser);
        userDTO.setId(updatedUser.getId());
        userDTO.setUsername(UPDATED_LOGIN);
        userDTO.setFirstName(UPDATED_FIRSTNAME);
        userDTO.setLastName(UPDATED_LASTNAME);
        userDTO.setEmail(UPDATED_EMAIL);
        userDTO.setLangKey(UPDATED_LANGKEY);
        //WHEN
        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isOk());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(UPDATED_LOGIN.toLowerCase());
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getLangKey()).isEqualTo(UPDATED_LANGKEY);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateUserExistingEmail() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        User anotherUser =initUser();
        anotherUser.setUsername("not-user");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("mailmail@localhost");
        User anotherSaved = userRepository.saveAndFlush(anotherUser);
        User updatedUser = userRepository.findById(user.getId()).get();
        UserDTO userDTO = userMapper.userToUserDTO(updatedUser);
        userDTO.setId(updatedUser.getId());
        userDTO.setEmail("mailmail@localhost");
        //WHEN
        //THEN
        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateUserExistingLogin() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        User anotherUser =initUser();
        anotherUser.setUsername("not-user");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("mailmail@localhost");
        User anotherSaved = userRepository.saveAndFlush(anotherUser);
        User updatedUser = userRepository.findById(user.getId()).get();
        UserDTO userDTO = userMapper.userToUserDTO(updatedUser);
        userDTO.setId(updatedUser.getId());
        userDTO.setUsername("not-user");
        //WHEN
        //THEN
        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteUser() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();
        //WHEN
        restUserMockMvc.perform(delete("/api/users/{login}", savedUser.getUsername())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
        //THEN
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void getAllAuthorities() throws Exception {
        //GIVEN - in setup
        restUserMockMvc.perform(get("/api/users/authorities")
                .accept(TestUtil.APPLICATION_JSON_UTF8)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasItems(AuthoritiesConstants.USER)));
    }

    @Test
    @Transactional
    public void testUserEquals() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId(2L);
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    public void testUserDTOtoUser() {
        //GIVEN
        UserDTO userDTO = new UserDTO();
        userDTO.setId(DEFAULT_ID);
        userDTO.setUsername(DEFAULT_LOGIN);
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setRoles(Collections.singleton(AuthoritiesConstants.USER));
        //WHEN
        User convertedUser = userMapper.userDTOToUser(userDTO);
        //THEN
        assertThat(convertedUser.getId()).isEqualTo(DEFAULT_ID);
        assertThat(convertedUser.getUsername()).isEqualTo(DEFAULT_LOGIN);
        assertThat(convertedUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(convertedUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(convertedUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(convertedUser.getActivated()).isEqualTo(true);
        assertThat(convertedUser.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(convertedUser.getRoles()).extracting("name").containsExactly(AuthoritiesConstants.USER);
    }

    @Test
    public void testUserToUserDTO() {
        //GIVEN
        user.setId(DEFAULT_ID);
        //WHEN
        UserDTO userDTO = userMapper.userToUserDTO(user);
        //THEN
        assertThat(userDTO.getId()).isEqualTo(DEFAULT_ID);
        assertThat(userDTO.getUsername()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(userDTO.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(userDTO.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDTO.isActivated()).isEqualTo(false);
        assertThat(userDTO.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(userDTO.getRoles()).containsExactly(AuthoritiesConstants.USER);
        assertThat(userDTO.toString()).isNotNull();
    }

    @Test
    public void testRoleEquals() {
        Role authorityA = new Role();
        assertThat(authorityA).isEqualTo(authorityA);
        assertThat(authorityA).isNotEqualTo(null);
        assertThat(authorityA).isNotEqualTo(new Object());
        assertThat(authorityA.hashCode()).isEqualTo(0);
        assertThat(authorityA.toString()).isNotNull();

        Role authorityB = new Role();
        assertThat(authorityA).isEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.ADMIN);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityA.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isEqualTo(authorityB);
        assertThat(authorityA.hashCode()).isEqualTo(authorityB.hashCode());
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
        result.setLangKey(DEFAULT_LANGKEY);
        result.setLastName(DEFAULT_LASTNAME);
        result.setFirstName(DEFAULT_FIRSTNAME);
        result.setEmail(DEFAULT_EMAIL);
        Role role = new Role(AuthoritiesConstants.USER);
        roleRepository.saveAndFlush(role);
        result.setRoles(new HashSet(Arrays.asList(role)));
        result.setId(null);
        result.setUsername(DEFAULT_LOGIN);
        result.setActivated(false);
        result.setPassword(DEFAULT_PASSWORD);
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
        result.setDescription("Description");
        result.setId(1L);
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
        result.setDescription("Description");
        result.setLatitude(42.0);
        result.setLongitude(27.0);
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
}
