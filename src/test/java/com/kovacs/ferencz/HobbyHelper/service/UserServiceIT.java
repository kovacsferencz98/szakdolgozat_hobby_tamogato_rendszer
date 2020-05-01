package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.RandomUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link UserService}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceIT {

    private static Logger log = LoggerFactory.getLogger(UserServiceIT.class);

    private static final String DEFAULT_LOGIN = "johndoe";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";

    private static final String DEFAULT_LASTNAME = "doe";

    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";

    private static final String DEFAULT_LANGKEY = "dummy";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService underTest;

    @Mock
    private DateTimeProvider dateTimeProvider;

    private User user;

    @BeforeEach
    public void init() {
        userRepository.deleteAll();
        userRepository.flush();
        user = new User();
        user.setUsername(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setLangKey(DEFAULT_LANGKEY);
    }

    @Test
    @Transactional
    public void assertThatUserIsActivated() {
        //GIVEN
        user.setActivated(false);
        String key = "activationKey";
        user.setActivationKey(key);
        userRepository.saveAndFlush(user);
        //WHEN
        underTest.activateRegistration(key);
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertTrue(modifiedUser.isPresent());
        assertTrue(modifiedUser.get().getActivated());
        assertNull(modifiedUser.get().getActivationKey());
    }

    @Test
    @Transactional
    public void assertThatUserMustExistToResetPassword() {
        //GIVEn
        userRepository.saveAndFlush(user);
        Optional<User> maybeUser = underTest.requestPasswordReset("invalid.login@localhost");
        assertThat(maybeUser).isNotPresent();
        //WHEN
        maybeUser = underTest.requestPasswordReset(user.getEmail());
        //THEN
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getEmail()).isEqualTo(user.getEmail());
        assertThat(maybeUser.orElse(null).getResetDate()).isNotNull();
        assertThat(maybeUser.orElse(null).getResetKey()).isNotNull();
    }

    @Test
    @Transactional
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        //GIVEN
        user.setActivated(false);
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User> maybeUser = underTest.requestPasswordReset(user.getUsername());
        //THEN
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(user);
    }

    @Test
    @Transactional
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {
        //GIVEN
        Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User> maybeUser = underTest.completePasswordReset("johndoe2", user.getResetKey());
        //THEN
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(user);
    }

    @Test
    @Transactional
    public void assertThatResetKeyMustBeValid() {
        //GIVEN
        Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User> maybeUser = underTest.completePasswordReset("johndoe2", user.getResetKey());
        //THEN
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(user);
    }

    @Test
    @Transactional
    public void assertThatUserCanResetPassword() {
        //GIVEN
        String oldPassword = user.getPassword();
        Instant daysAgo = Instant.now().minus(2, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User> maybeUser = underTest.completePasswordReset("johndoe2", user.getResetKey());
        //THEN
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getResetDate()).isNull();
        assertThat(maybeUser.orElse(null).getResetKey()).isNull();
        assertThat(maybeUser.orElse(null).getPassword()).isNotEqualTo(oldPassword);
        userRepository.delete(user);
    }

    @Test
    @Transactional
    public void assertThatNotActivatedUsersWithNotNullActivationKeyAreDeleted() {
        //GIVEN
        Instant now = Instant.now();
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)));
        user.setActivated(false);
        user.setActivationKey(RandomStringUtils.random(20));
        User dbUser = userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user);
        List<User> users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNull();
        assertThat(users).isNotEmpty();
        //WHEN
        underTest.removeNotActivatedUsers();
        //THEN
        users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNull();
        assertThat(users).isEmpty();
    }

    @Test
    @Transactional
    public void assertThatNotActivatedUsersWithNullActivationKeyAreNotDeleted() {
        //GIVEN
        Instant now = Instant.now();
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)));
        user.setActivated(false);
        User dbUser = userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user);
        List<User> users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNull();
        assertThat(users).isEmpty();
        //WHEN
        underTest.removeNotActivatedUsers();
        //THEN
        Optional<User> maybeDbUser = userRepository.findById(dbUser.getId());
        assertThat(maybeDbUser).contains(dbUser);
    }

    @Test
    @Transactional
    public void assertThatAnonymousUserIsNotGet() {
        //GIVEN
        user.setUsername(Constants.ANONYMOUS_USER);
        if (!userRepository.findOneByUsername(Constants.ANONYMOUS_USER).isPresent()) {
            userRepository.saveAndFlush(user);
        }
        //WHEN
        final List<UserDTO> allManagedUsers = underTest.getAllManagedUsers();
        //THEN
        assertThat(allManagedUsers.stream()
                .noneMatch(user -> Constants.ANONYMOUS_USER.equals(user.getUsername())))
                .isTrue();
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatLanguageKeyIsUpdated() {
        //GIVEN
        String langKey = "hu";
        userRepository.saveAndFlush(user);
        //WHEN
        underTest.changeLanguageKey(langKey);
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertTrue(modifiedUser.isPresent());
        assertEquals(langKey, modifiedUser.get().getLangKey());
    }

    @Test
    @Transactional
    public void assertThatRegistrationCreatesEntity() {
        //GIVEN
        UserDTO userDTO = initUserDTO();
        //WHEN
        User registeredUser = underTest.registerUser(userDTO, "password");
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertTrue(modifiedUser.isPresent());
        assertEquals(registeredUser, modifiedUser.get());
        assertEquals(DEFAULT_EMAIL, modifiedUser.get().getEmail());
        assertEquals(DEFAULT_LOGIN, modifiedUser.get().getUsername());
        assertNotNull(modifiedUser.get().getPassword());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserEntityIsUpdated() {
        //GIVEN
        userRepository.saveAndFlush(user);
        String firstName = "First";
        String lastName = "Last";
        String email = "email@email.com";
        //WHEN
        underTest.updateUser(firstName, lastName, email);
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertTrue(modifiedUser.isPresent());
        assertEquals(firstName, modifiedUser.get().getFirstName());
        assertEquals(lastName, modifiedUser.get().getLastName());
        assertEquals(email, modifiedUser.get().getEmail());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserEntityIsUpdatedBasedOnDTO() {
        //GIVEN
        String firstName = "First";
        String lastName = "Last";
        String email = "email@email.com";
        userRepository.saveAndFlush(user);
        Optional<User> saved = userRepository.findOneByUsername(DEFAULT_LOGIN);
        UserDTO userDTO = initUserDTO();
        userDTO.setId(saved.get().getId());
        userDTO.setEmail(email);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        //WHEN
        underTest.updateUser(userDTO);
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertTrue(modifiedUser.isPresent());
        assertEquals(firstName, modifiedUser.get().getFirstName());
        assertEquals(lastName, modifiedUser.get().getLastName());
        assertEquals(email, modifiedUser.get().getEmail());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserCanBeDeleted() {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        underTest.deleteUser(DEFAULT_LOGIN);
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertFalse(modifiedUser.isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserPasswordCanBeChanged() {
        //GIVEN
        UserDTO userDTO = initUserDTO();
        String password = "password";
        String newPassword = "newPassword";
        User registered = underTest.registerUser(userDTO, password);
        String regPassword = registered.getPassword();
        //WHEN
        underTest.changePassword(password, newPassword);
        //THEN
        Optional<User> modifiedUser = userRepository.findOneByUsername(DEFAULT_LOGIN);
        assertTrue(modifiedUser.isPresent());
        assertNotEquals(regPassword, modifiedUser.get().getPassword());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserIsFoundByUsername() {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User>  result = underTest.getUserWithAuthoritiesByLogin(DEFAULT_LOGIN);
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserIsFoundByEmail() {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User>  result = underTest.getUserWithAuthoritiesByEmail(DEFAULT_EMAIL);
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserIsFoundById() {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User>  result = underTest.getUserWithAuthorities(user.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @Transactional
    @WithMockUser(username=DEFAULT_LOGIN, roles = {"USER"})
    public void assertThatUserIsFoundBySpringSecurityUsername() {
        //GIVEN
        userRepository.saveAndFlush(user);
        //WHEN
        Optional<User>  result = underTest.getUserWithAuthorities();
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    private UserDTO initUserDTO() {
        UserDTO result = new UserDTO();
        result.setLangKey(DEFAULT_LANGKEY);
        result.setLastName(DEFAULT_LASTNAME);
        result.setFirstName(DEFAULT_FIRSTNAME);
        result.setEmail(DEFAULT_EMAIL);
        result.setRoles(new HashSet(Arrays.asList(AuthoritiesConstants.USER)));
        result.setUsername(DEFAULT_LOGIN);
        result.setActivated(false);
        return result;
    }
}
