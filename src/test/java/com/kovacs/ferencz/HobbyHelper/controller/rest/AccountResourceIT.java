package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.config.Constants;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.*;
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
import com.kovacs.ferencz.HobbyHelper.service.MailService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.PasswordChangeDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountResourceIT {

    private static Logger logger = LoggerFactory.getLogger(AccountResourceIT.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpMessageConverter<?>[] httpMessageConverters;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Mock
    private UserService mockUserService;

    @Mock
    private MessageSource mockMessageSource;

    @Mock
    private LocationService mockLocationService;

    @Mock
    private UserDetailsService mockUserDetailsService;

    @Mock
    private MailService mockMailService;

    private MockMvc restMvc;

    private MockMvc restUserMockMvc;

    private User user;

    @BeforeEach
    public void setup() {
        clearDatabase();

        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(any());
        AccountResource accountResource =
                new AccountResource(userRepository, userService, mockMailService, locationService, userDetailsService, messageSource);

        AccountResource accountUserMockResource =
                new AccountResource(userRepository, mockUserService, mockMailService, mockLocationService, mockUserDetailsService, mockMessageSource);
        this.restMvc = MockMvcBuilders.standaloneSetup(accountResource)
                .setMessageConverters(httpMessageConverters)
                .setControllerAdvice(exceptionTranslator)
                .build();
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountUserMockResource)
                .setControllerAdvice(exceptionTranslator)
                .build();

        user = initUser();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restUserMockMvc.perform(get("/api/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restUserMockMvc.perform(get("/api/authenticate")
                .with(request -> {
                    request.setRemoteUser("test");
                    return request;
                })
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    @WithMockUser("user")
    public void testGetExistingAccount() throws Exception {
        //GIVEN
        User savedUser = userRepository.saveAndFlush(user);
        UserDetails userDetails = initUserDetails();
        userDetails.setUser(savedUser);
        userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        //THEN
        restMvc.perform(get("/api/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.firstName").value("First"))
                .andExpect(jsonPath("$.lastName").value("Last"))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(jsonPath("$.langKey").value("en"))
                .andExpect(jsonPath("$.roles").value(AuthoritiesConstants.USER));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        //GIVEN
        when(mockUserService.getUserWithAuthorities()).thenReturn(Optional.empty());
        //WHEN
        //THEN
        restUserMockMvc.perform(get("/api/account")
                .accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testRegisterValid() throws Exception {
        //GIVEN
        RegistrationVM validUser = createRegistrationVM();
        assertThat(userRepository.findOneByUsername("user").isPresent()).isFalse();
        //WHEN
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(validUser)))
                .andExpect(status().isCreated());
        //THEN
        assertThat(userRepository.findOneByUsername("user").isPresent()).isTrue();
    }

    @Test
    @Transactional
    public void testRegisterInvalidLogin() throws Exception {
        //GIVEN
        RegistrationVM invalidUser = createRegistrationVM();
        invalidUser.setUsername("funky-log!n");// <-- invalid
        //WHEN
        restUserMockMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
                .andExpect(status().isBadRequest());
        //THEN
        Optional<User> user = userRepository.findOneByEmailIgnoreCase("email@email.com");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidEmail() throws Exception {
        //GIVEN
        RegistrationVM invalidUser = createRegistrationVM();
        invalidUser.setEmail("invalid");
        //WHEN
        restUserMockMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
                .andExpect(status().isBadRequest());
        //THEN
        Optional<User> user = userRepository.findOneByUsername("user");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidPassword() throws Exception {
        //GIVEN
        RegistrationVM invalidUser = createRegistrationVM();
        invalidUser.setPassword("123");// password with only 3 digits
        //WHEN
        restUserMockMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
                .andExpect(status().isBadRequest());
        //THEN
        Optional<User> user = userRepository.findOneByUsername("user");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterNullPassword() throws Exception {
        //GIVEN
        RegistrationVM invalidUser = createRegistrationVM();
        invalidUser.setPassword(null);// invalid null password
        //WHEN
        restUserMockMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
                .andExpect(status().isBadRequest());
        //THEN
        Optional<User> user = userRepository.findOneByUsername("user");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateLogin() throws Exception {
        //GIVEN
        // First registration
        RegistrationVM firstUser = createRegistrationVM();
        firstUser.setUsername("alice");
        firstUser.setPassword("password");
        firstUser.setFirstName("Alice");
        firstUser.setLastName("Something");
        firstUser.setEmail("alice@example.com");
        firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        firstUser.setRoles(Collections.singleton(AuthoritiesConstants.USER));

        // Duplicate login, different email
        RegistrationVM secondUser = createRegistrationVM();
        secondUser.setUsername(firstUser.getUsername());
        secondUser.setPassword(firstUser.getPassword());
        secondUser.setFirstName(firstUser.getFirstName());
        secondUser.setLastName(firstUser.getLastName());
        secondUser.setEmail("alice2@example.com");
        secondUser.setLangKey(firstUser.getLangKey());
        secondUser.setRoles(new HashSet<>(firstUser.getRoles()));
        //WHEN
        // First user
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(firstUser)))
                .andExpect(status().isCreated());
        //THEN
        // Second (non activated) user
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(secondUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void testRegisterDuplicateEmail() throws Exception {
        // First user
        RegistrationVM firstUser = createRegistrationVM();
        firstUser.setUsername("test-register-1");
        firstUser.setPassword("password");
        firstUser.setFirstName("Alice");
        firstUser.setLastName("Test");
        firstUser.setEmail("test-register-duplicate-email@example.com");
        firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        firstUser.setRoles(Collections.singleton(AuthoritiesConstants.USER));

        // Register first user
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(firstUser)))
                .andExpect(status().isCreated());

        Optional<User> testUser1 = userRepository.findOneByUsername("test-register-1");
        assertThat(testUser1.isPresent()).isTrue();

        // Duplicate email, different login
        RegistrationVM secondUser = createRegistrationVM();
        secondUser.setUsername("test-register-2");
        secondUser.setPassword(firstUser.getPassword());
        secondUser.setFirstName(firstUser.getFirstName());
        secondUser.setLastName(firstUser.getLastName());
        secondUser.setEmail(firstUser.getEmail());
        secondUser.setLangKey(firstUser.getLangKey());
        secondUser.setRoles(new HashSet<>(firstUser.getRoles()));

        // Register second (non activated) user
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(secondUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void testRegisterAdminIsIgnored() throws Exception {
        RegistrationVM registrationVM = createRegistrationVM();
        registrationVM.setActivated(true);
        registrationVM.setLangKey(Constants.DEFAULT_LANGUAGE);
        registrationVM.setRoles(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(registrationVM)))
                .andExpect(status().isCreated());

        Optional<User> userDup = userRepository.findOneByUsername("user");
        assertThat(userDup.isPresent()).isTrue();
        assertThat(userDup.get().getRoles()).hasSize(1)
                .containsExactly(roleRepository.findById(AuthoritiesConstants.USER).get());
    }

    @Test
    @Transactional
    public void testActivateAccount() throws Exception {
        final String activationKey = "some activation key";
        User user = new User();
        user.setUsername("activate-account");
        user.setEmail("activate-account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(false);
        user.setActivationKey(activationKey);

        userRepository.saveAndFlush(user);

        restMvc.perform(get("/api/activate?key={activationKey}", activationKey))
                .andExpect(status().isOk());

        user = userRepository.findOneByUsername(user.getUsername()).orElse(null);
        assertThat(user.getActivated()).isTrue();
    }

    @Test
    @Transactional
    public void testActivateAccountWithWrongKey() throws Exception {
        restMvc.perform(get("/api/activate?key=wrongActivationKey"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testSaveAccount() throws Exception {
        //GIVEN
        User saved = userRepository.saveAndFlush(user);
        UserDetails userDetails = initUserDetails();
        userDetails.setUser(saved);
        UserDetails savedDetails = userDetailsRepository.saveAndFlush(userDetails);
        AccountUpdateVM accountUpdateVM = initAccountUpdateVM();
        accountUpdateVM.setUserId(saved.getId());
        accountUpdateVM.setLocationId(savedDetails.getResidence().getId());
        accountUpdateVM.setDetailsId(savedDetails.getId());
        accountUpdateVM.setLastName("Not last");
        accountUpdateVM.setFirstName("Not first");
        accountUpdateVM.setEmail("newmail@email.com");
        //WHEN
        restMvc.perform(
                post("/api/account")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(accountUpdateVM)))
                .andExpect(status().isOk());

        //THEN
        User updatedUser = userRepository.findOneByUsername(user.getUsername()).orElse(null);
        assertThat(updatedUser.getFirstName()).isEqualTo(accountUpdateVM.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(accountUpdateVM.getLastName());
        assertThat(updatedUser.getEmail()).isEqualTo(accountUpdateVM.getEmail());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getActivated()).isEqualTo(true);
        assertThat(updatedUser.getRoles()).isNotEmpty();
    }

    @Test
    @Transactional
    @WithMockUser("save-invalid-email")
    public void testSaveInvalidEmail() throws Exception {
        User user = new User();
        user.setUsername("save-invalid-email");
        user.setEmail("save-invalid-email@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("invalid email");
        userDTO.setActivated(false);
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setRoles(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
                post("/api/account")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email")).isNotPresent();
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email")
    public void testSaveExistingEmail() throws Exception {
        User user = new User();
        user.setUsername("save-existing-email");
        user.setEmail("save-existing-email@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        User anotherUser = new User();
        anotherUser.setUsername("save-existing-email2");
        anotherUser.setEmail("save-existing-email2@example.com");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);

        userRepository.saveAndFlush(anotherUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-existing-email2@example.com");
        userDTO.setActivated(false);
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setRoles(Collections.singleton(AuthoritiesConstants.ADMIN));

        restMvc.perform(
                post("/api/account")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByUsername("save-existing-email").orElse(null);
        assertThat(updatedUser.getEmail()).isEqualTo("save-existing-email@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testSaveExistingEmailAndLogin() throws Exception {
        //GIVEN
        User saved = userRepository.saveAndFlush(user);

        AccountUpdateVM accountUpdateVM = initAccountUpdateVM();
        //WHEN
        restMvc.perform(
                post("/api/account")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(accountUpdateVM)))
                .andExpect(status().isOk());
        //THEN
        User updatedUser = userRepository.findOneByUsername("user").orElse(null);
        assertThat(updatedUser.getEmail()).isEqualTo("email@email.com");
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testChangePasswordWrongExistingPassword() throws Exception {
        //GIVEN
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        userRepository.saveAndFlush(user);
        //WHEN
        restMvc.perform(post("/api/account/change-password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO("1"+currentPassword, "new password"))))
                .andExpect(status().isBadRequest());
        //THEN
        User updatedUser = userRepository.findOneByUsername("user").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testChangePassword() throws Exception {
        //GIVEN
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        userRepository.saveAndFlush(user);
        //WHEN
        restMvc.perform(post("/api/account/change-password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "new password"))))
                .andExpect(status().isOk());
        //THEN
        User updatedUser = userRepository.findOneByUsername("user").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testChangePasswordTooSmall() throws Exception {
        //GIVEN
        String currentPassword = RandomStringUtils.random(60);
        userRepository.saveAndFlush(user);
        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1);
        //WHEN
        restMvc.perform(post("/api/account/change-password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
                .andExpect(status().isBadRequest());
        //THEN
        User updatedUser = userRepository.findOneByUsername("user").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testChangePasswordTooLong() throws Exception {
        //GIVEN
        userRepository.saveAndFlush(user);
        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1);
        //WHEN
        restMvc.perform(post("/api/account/change-password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(user.getPassword(), newPassword))))
                .andExpect(status().isBadRequest());
        //THEN
        User updatedUser = userRepository.findOneByUsername("user").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("user")
    public void testChangePasswordEmpty() throws Exception {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        restMvc.perform(post("/api/account/change-password")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(user.getPassword(), ""))))
                .andExpect(status().isBadRequest());
        //THEN
        User updatedUser = userRepository.findOneByUsername("user").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    public void testRequestPasswordReset() throws Exception {
        //GIVEN
        userRepository.saveAndFlush(user);
        ResetPasswordVM resetPasswordVM = new ResetPasswordVM();
        resetPasswordVM.setMail("email@email.com");
        //WHEN
        //THEN
        restMvc.perform(post("/api/account/reset-password/init")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resetPasswordVM)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testRequestPasswordResetUpperCaseEmail() throws Exception {
        //GIVEN
        userRepository.saveAndFlush(user);
        ResetPasswordVM resetPasswordVM = new ResetPasswordVM();
        resetPasswordVM.setMail("email@EMAIL.COM");
        //WHEN
        //THEN
        restMvc.perform(post("/api/account/reset-password/init")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resetPasswordVM)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRequestPasswordResetWrongEmail() throws Exception {
        //GIVEN
        ResetPasswordVM resetPasswordVM = new ResetPasswordVM();
        resetPasswordVM.setMail("password-reset-wrong-email@example.com");
        //WHEN
        //THEN
        restMvc.perform(
                post("/api/account/reset-password/init")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(resetPasswordVM)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testFinishPasswordReset() throws Exception {
        //GIVEN
        user.setResetKey("reset key");
        user.setResetDate(Instant.now().minusSeconds(3));
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("new password");
        //WHEN
        restMvc.perform(
                post("/api/account/reset-password/finish")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
                .andExpect(status().isOk());
        //THEN
        User updatedUser = userRepository.findOneByUsername(user.getUsername()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    public void testFinishPasswordResetTooSmall() throws Exception {
        //GIVEN
        user.setResetKey("reset key too small");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("foo");
        //WHEN
        restMvc.perform(
                post("/api/account/reset-password/finish")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
                .andExpect(status().isBadRequest());
        //THEN
        User updatedUser = userRepository.findOneByUsername(user.getUsername()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isFalse();
    }


    @Test
    @Transactional
    public void testFinishPasswordResetWrongKey() throws Exception {
        //GIVEN
        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey("wrong reset key");
        keyAndPassword.setNewPassword("new password");
        //WHEN
        //THEN
        restMvc.perform(
                post("/api/account/reset-password/finish")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
                .andExpect(status().isBadRequest());
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
        result.setActivated(true);
        result.setPassword("pwdpwdpwd");
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

    private AccountUpdateVM initAccountUpdateVM() {
        AccountUpdateVM result = new AccountUpdateVM();
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
        result.setFirstName("First");
        result.setLastName("Last");
        result.setUserId(1L);
        result.setLocationId(1L);
        result.setDetailsId(1L);
        return result;
    }
}
