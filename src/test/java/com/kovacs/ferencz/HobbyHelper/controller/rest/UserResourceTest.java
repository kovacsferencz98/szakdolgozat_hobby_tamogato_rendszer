package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.BadRequestAlertException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.EmailAlreadyUsedException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.LoginAlreadyUsedException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.MailService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserResourceTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailService mailService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    UserResource underTest;

    private UserDTO userDTO;

    private User user;

    @BeforeEach
    void setUp() {
        userDTO = initUserDTO();
        user = initUser();
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createUserShouldThrowExceptionIfIdISSet() {
        //GIVEN
        UserDTO incomingUser = createNewUser();
        incomingUser.setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id set");
        //WHEN
        Exception exception = assertThrows(BadRequestAlertException.class, () -> {
            underTest.createUser(incomingUser);
        });
        //THEN
        assertEquals("Id set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createUserShouldThrowExceptionIfUsernameIsUsed() {
        //GIVEN
        UserDTO incomingUser = createNewUser();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Username used");
        //WHEN
        Exception exception = assertThrows(LoginAlreadyUsedException.class, () -> {
            underTest.createUser(incomingUser);
        });
        //THEN
        assertEquals("Username used", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createUserShouldThrowExceptionIfEmailIsUsed() {
        //GIVEN
        UserDTO incomingUser = createNewUser();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.empty());
        given(userService.getUserWithAuthoritiesByEmail(anyString())).willReturn(Optional.of(user));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Email used");
        //WHEN
        Exception exception = assertThrows(EmailAlreadyUsedException.class, () -> {
            underTest.createUser(incomingUser);
        });
        //THEN
        assertEquals("Email used", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER", "ADMIN"})
    void createUserShouldCreateNewEntity() throws Exception {
        //GIVEN
        UserDTO incomingUser = createNewUser();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.empty());
        given(userService.getUserWithAuthoritiesByEmail(anyString())).willReturn(Optional.empty());
        given(userService.createUser(any(UserDTO.class))).willReturn(user);
        //WHEN
        ResponseEntity<User> response = underTest.createUser(incomingUser);
        //THEN
        verify(userService).createUser(incomingUser);
        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


    @Test
    @WithMockUser(username="user2", roles = {"USER"})
    void updateUserShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.updateUser(userDTO);
        });
        //THEN
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateUserShouldThrowExceptionIfUsernameIsUsed() {
        //GIVEN
        UserDTO incomingUser = initUserDTO();
        incomingUser.setId(2L);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Username used");
        //WHEN
        Exception exception = assertThrows(LoginAlreadyUsedException.class, () -> {
            underTest.updateUser(incomingUser);
        });
        //THEN
        assertEquals("Username used", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateUserShouldThrowExceptionIfEmailIsUsed() {
        //GIVEN
        UserDTO incomingUser = initUserDTO();
        incomingUser.setId(2L);
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.empty());
        given(userService.getUserWithAuthoritiesByEmail(anyString())).willReturn(Optional.of(user));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Email used");
        //WHEN
        Exception exception = assertThrows(EmailAlreadyUsedException.class, () -> {
            underTest.updateUser(incomingUser);
        });
        //THEN
        assertEquals("Email used", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateUserShouldUpdateEntity() {
        //GIVEN
        UserDTO incomingUser = initUserDTO();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.empty());
        given(userService.getUserWithAuthoritiesByEmail(anyString())).willReturn(Optional.empty());
        given(userService.updateUser(any(UserDTO.class))).willReturn(Optional.of(userDTO));
        //WHEN
        ResponseEntity<UserDTO> response = underTest.updateUser(incomingUser);
        //THEN
        verify(userService).updateUser(incomingUser);
        assertEquals(userDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateUserShouldReturnNotFoundResponseWhenBadID() {
        //GIVEN
        UserDTO incomingUser = initUserDTO();
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.empty());
        given(userService.getUserWithAuthoritiesByEmail(anyString())).willReturn(Optional.empty());
        given(userService.updateUser(any(UserDTO.class))).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<UserDTO> response = underTest.updateUser(incomingUser);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void getAllUsersShouldReturnLocations() {
        //GIVEN
        given(userService.getAllManagedUsers()).willReturn(Arrays.asList(userDTO));
        //WHEN
        List<UserDTO> result = underTest.getAllUsers();
        //THEN
        assertEquals(Arrays.asList(userDTO), result);
    }

    @Test
    void getUserDetailsShouldReturnNotFoundResponseWhenNoLocationIsFound() {
        //GIVEN
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<UserDTO> result = underTest.getUser("user");
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getEventShouldReturnFoundEvent() {
        //GIVEN
        given(userService.getUserWithAuthoritiesByLogin(anyString())).willReturn(Optional.of(user));
        //WHEN
        ResponseEntity<UserDTO> result = underTest.getUser("user");
        //THEN
        assertEquals(userDTO, result.getBody());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void getAuthoritiesShouldReturnAuthorities() {
        //GIVEN
        given(userService.getAuthorities()).willReturn(Arrays.asList(AuthoritiesConstants.USER));
        //WHEN
        List<String> result = underTest.getAuthorities();
        //THEN
        assertEquals(Arrays.asList(AuthoritiesConstants.USER), result);
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER"})
    void deleteUserShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.deleteUser("user");
        });
        //THEN
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteUserShouldDeleteEntity() {
        //GIVEN - in setUp
        //WHEN
        ResponseEntity<Void> response = underTest.deleteUser("user");
        //THEN
        verify(userService).deleteUser("user");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

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

    private UserDTO createNewUser() {
        UserDTO result = new UserDTO();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        result.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        result.setId(null);
        result.setUsername("user");
        result.setActivated(false);
        return result;
    }
}