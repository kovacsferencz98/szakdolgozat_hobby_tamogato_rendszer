package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventParticipantService eventParticipantService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService underTest;

    private User user;

    private UserDTO userDTO;

    private Role role;

    @BeforeEach
    void setUp() {
        user = initUser();
        userDTO = initUserDTO();
        role = initRole();
    }

    @Test
    void activateRegistrationShouldActivateTheEntityWithActivationKey() {
        //GIVEN
        String key = "key";
        user.setActivated(false);
        user.setActivationKey(key);
        given(userRepository.findOneByActivationKey(anyString())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.activateRegistration(key);
        //THEN
        verify(userRepository).save(user);
        assertTrue(result.isPresent());
        assertTrue(user.getActivated());
        assertNull(user.getActivationKey());
        assertEquals(user, result.get());
    }

    @Test
    void completePasswordResetShouldNotChangeEntityIfResetDateIsTooOld() {
        //GIVEN
        String oldPassword = "oldPassword";
        String password = "pwdpwdpwd";
        String key = "key";
        String encrypted = "verySecure";
        user.setPassword(oldPassword);
        user.setResetKey(key);
        Instant resetTime = Instant.now().minusSeconds(86401);
        user.setResetDate(resetTime);
        given(userRepository.findOneByResetKey(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.encode(anyString())).willReturn(encrypted);
        //WHEN
        Optional<User> result = underTest.completePasswordReset(password, key);
        //THEN
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        assertFalse(result.isPresent());
        assertEquals(resetTime, user.getResetDate());
        assertEquals(key, user.getResetKey());
        assertEquals(oldPassword, user.getPassword());
    }

    @Test
    void completePasswordResetShouldUpdateUserEntity() {
        //GIVEN
        String password = "pwdpwdpwd";
        String key = "key";
        String encrypted = "verySecure";
        user.setResetKey(key);
        user.setResetDate(Instant.now());
        given(userRepository.findOneByResetKey(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.encode(anyString())).willReturn(encrypted);
        //WHEN
        Optional<User> result = underTest.completePasswordReset(password, key);
        //THEN
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(password);
        assertTrue(result.isPresent());
        User changedUser = result.get();
        assertNull(changedUser.getResetDate());
        assertNull(changedUser.getResetKey());
        assertEquals(encrypted, changedUser.getPassword());
    }

    @Test
    void requestPasswordResetShouldUpdateEntity() {
        //GIVEN
        user.setActivated(true);
        user.setResetKey(null);
        user.setResetDate(null);
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.requestPasswordReset(user.getEmail());
        //THEN
        verify(userRepository).save(user);
        assertTrue(result.isPresent());
        User updatedUser = result.get();
        assertNotNull(updatedUser.getResetKey());
        assertNotNull(updatedUser.getResetDate());
    }

    @Test
    void requestPasswordResetShouldNotUpdateEntityIfNotActivated() {
        //GIVEN
        user.setActivated(false);
        user.setResetKey(null);
        user.setResetDate(null);
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.requestPasswordReset(user.getEmail());
        //THEN
        verify(userRepository, never()).save(user);
        assertTrue(result.isEmpty());
        assertNull(user.getResetKey());
        assertNull(user.getResetDate());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void changeLanguageKeyShouldUpdateEntity() {
        //GIVEN
        String key = "hu";
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.changeLanguageKey(key);
        //THEN
        verify(userRepository).save(user);
        assertEquals(key, user.getLangKey());
    }

    @Test
    void registerUserShouldThrowExceptionWhenUsernameUsed() {
        //GIVEN
        String password = "pwdpwdpwd";
        String encrypted = "verySecure";
        user.setActivated(true);
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        //WHEN
        //THEN
        assertThrows(UsernameAlreadyUsedException.class, () -> {
            underTest.registerUser(userDTO, password);
        });
    }

    @Test
    void registerUserShouldThrowExceptionWhenEmailUsed() {
        //GIVEN
        String password = "pwdpwdpwd";
        String encrypted = "verySecure";
        user.setActivated(true);
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.empty());
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.of(user));
        //WHEN
        //THEN
        assertThrows(EmailAlreadyUsedException.class, () -> {
            underTest.registerUser(userDTO, password);
        });
    }

    @Test
    void registerUserShouldCreateAndSaveUserEntity() {
        //GIVEN
        String password = "pwdpwdpwd";
        String encrypted = "verySecure";
        user.setPassword(encrypted);
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.empty());
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.empty());
        given(roleRepository.findById(anyString())).willReturn(Optional.of(role));
        given(passwordEncoder.encode(anyString())).willReturn(encrypted);
        //WHEN
        User result = underTest.registerUser(userDTO, password);
        //THEN
        ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArg.capture());
        User toSave = userArg.getValue();
        assertEquals(toSave, result);
        assertEquals(encrypted, result.getPassword());
        assertNotNull(result.getActivationKey());
        assertFalse(result.getActivated());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getEmail(), result.getEmail());
    }

    @Test
    void createUserShouldSaveEntity() {
        //GIVEN
        user.setActivated(true);
        given(roleRepository.findById(anyString())).willReturn(Optional.of(role));
        //WHEN
        User result = underTest.createUser(userDTO);
        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captured = userArgumentCaptor.getValue();
        assertEquals(user.getEmail(), captured.getEmail());
        assertEquals(user.getUsername(), captured.getUsername());
        assertEquals(user.getRoles(), captured.getRoles());
        assertEquals(user.getActivated(), captured.getActivated());

    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void updateUserShouldUpdateEntity() {
        //GIVEN
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@doe.com";
        //WHEN
        underTest.updateUser(firstName, lastName, email);
        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captured = userArgumentCaptor.getValue();
        assertEquals(firstName, captured.getFirstName());
        assertEquals(lastName, captured.getLastName());
        assertEquals(email, captured.getEmail());
    }

    @Test
    void updateUserShouldUpdateEntityBasedOnDTO() {
        //GIVEN
        UserMapper userMapper = new UserMapper();
        User blankUser = new User();
        blankUser.setId(userDTO.getId());
        given(userRepository.findById(anyLong())).willReturn(Optional.of(blankUser));
        given(roleRepository.findById(anyString())).willReturn(Optional.of(role));
        //WHEN
        Optional<UserDTO> result = underTest.updateUser(userDTO);
        //THEN
        verify(userRepository).save(userMapper.userDTOToUser(userDTO));
        assertTrue(result.isPresent());
        UserDTO updatedUserDTO = result.get();
        assertEquals(userDTO, updatedUserDTO);
    }

    @Test
    void deleteUserShouldDeleteEveryUsedEntity() {
        //GIVEN
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.deleteUser("user");
        //THEN
        verify(userDetailsService).deleteDetailOfUser(user.getId());
        verify(eventService).deleteEventsOfUser(user.getId());
        verify(eventParticipantService).deleteByUser(user.getId());
        verify(userRepository).delete(user);

    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void changePasswordShouldThrowExceptionWhenPasswordNotChanged() {
        //GIVEN
        String newPassword = "newPassword";
        String oldPassword = "incorrect";
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);
        //WHEN
        //THEN
        assertThrows(InvalidPasswordException.class, () -> {
            underTest.changePassword(oldPassword, newPassword);
        });

    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void changePasswordShouldUpdateEntity() {
        //GIVEN
        String newPassword = "newPassword";
        String oldPassword = user.getPassword();
        String encrypted = "encryptedVerySafe";
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn(encrypted);
        //WHEN
        underTest.changePassword(oldPassword, newPassword);
        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captured = userArgumentCaptor.getValue();
        assertEquals(encrypted, captured.getPassword());
    }

    @Test
    void getAllManagedUsersShouldReturnEntities() {
        //GIVEN
        given(userRepository.findAllByUsernameNot(anyString())).willReturn(Arrays.asList(user));
        //WHEN
        List<UserDTO> result = underTest.getAllManagedUsers();
        //THEN
        assertEquals(Arrays.asList(userDTO), result);

    }

    @Test
    void getUserWithAuthoritiesByLoginShouldReturnEntity() {
        //GIVEN
        given(userRepository.findOneWithRolesByUsername(anyString())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.getUserWithAuthoritiesByLogin("user");
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserWithAuthoritiesByEmailShouldReturnEntity() {
        //GIVEN
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.getUserWithAuthoritiesByEmail("email@email.com");
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserWithAuthoritiesShouldReturnEntityById() {
        //GIVEN
        given(userRepository.findOneWithRolesById(anyLong())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.getUserWithAuthorities(1L);
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void getUserWithAuthoritiesShouldReturnEntity() {
        //GIVEN
        given(userRepository.findOneWithRolesByUsername(anyString())).willReturn(Optional.of(user));
        //WHEN
        Optional<User> result = underTest.getUserWithAuthorities();
        //THEN
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void removeNotActivatedUsers() {
        //GIVEN
        User inactive = initUser();
        inactive.setActivated(false);
        user.setActivationKey("Key");
        inactive.setActivationKey("Key");
        List<User> users = Arrays.asList(inactive);
        given(userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNull()).willReturn(users);
        given(userRepository.findOneByUsername(anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.removeNotActivatedUsers();
        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).delete(userArgumentCaptor.capture());
        User captured = userArgumentCaptor.getValue();
        assertEquals(inactive, captured);
    }

    @Test
    void getAuthoritiesShouldReturnEveryAuthority() {
        //GIVEN
        Role adminRole = new Role(AuthoritiesConstants.ADMIN);
        Role userRole = new Role(AuthoritiesConstants.USER);
        given(roleRepository.findAll()).willReturn(Arrays.asList(userRole, adminRole));
        //WHEN
        List<String> result = underTest.getAuthorities();
        //THEN
        assertEquals(Arrays.asList(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN), result);
    }

    private User initUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        result.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        result.setId(1L);
        result.setUsername("user");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        return result;
    }

    private UserDTO initUserDTO() {
        UserDTO result = new UserDTO();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        result.setRoles(new HashSet(Arrays.asList(AuthoritiesConstants.USER)));
        result.setId(1L);
        result.setUsername("user");
        result.setActivated(false);
        return result;
    }

    private Role initRole() {
        Role result = new Role();
        result.setName(AuthoritiesConstants.USER);
        return result;
    }
}