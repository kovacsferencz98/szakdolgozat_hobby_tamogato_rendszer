package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.EmailAlreadyUsedException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.EmailNotFoundException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.InvalidPasswordException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.*;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.MailService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.PasswordChangeDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountResourceTest {

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private MailService mailService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    SecurityContext securityContext;

    @MockBean
    Authentication authentication;

    @Autowired
    private AccountResource underTest;

    private User user;

    private Location location;

    private UserDetailsDTO userDetails;

    @BeforeEach
    public void setUp() {
        user = initUser();
        location = initLocation();
        userDetails = initUserDetail();
    }

    @Test
    void registerAccountShouldThrowExceptionWhenInvalidPassword() {
        //GIVEN
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Bad password");
        RegistrationVM incomingRegistration = createRegistrationVM();
        incomingRegistration.setPassword("short");
        //WHEN
        Exception exception = assertThrows(InvalidPasswordException.class, () -> {
            underTest.registerAccount(incomingRegistration);
        });
        //THEN
        assertEquals("Bad password", exception.getMessage());
    }

    @Test
    void registerAccountShouldCreateUser() {
        //GIVEN
        given(userService.registerUser(any(UserDTO.class), anyString())).willReturn(user);
        RegistrationVM incomingRegistration = createRegistrationVM();
        //WHEN
        underTest.registerAccount(incomingRegistration);
        //THEN
        ArgumentCaptor<UserDTO> userArgument = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<String> stringArgument = ArgumentCaptor.forClass(String.class);
        verify(userService).registerUser(userArgument.capture(), stringArgument.capture());
        assertEquals(incomingRegistration.getPassword(), stringArgument.getValue());
        assertEquals(incomingRegistration.getUsername(), userArgument.getValue().getUsername());
        assertEquals(incomingRegistration.getEmail(), userArgument.getValue().getEmail());
    }

    @Test
    void registerAccountShouldSendActivationEmail() {
        //GIVEN
        given(userService.registerUser(any(UserDTO.class), anyString())).willReturn(user);
        RegistrationVM incomingRegistration = createRegistrationVM();
        //WHEN
        underTest.registerAccount(incomingRegistration);
        //THEN
        ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        verify(mailService).sendActivationEmail(userArgument.capture());
        assertEquals(user, userArgument.getValue());
    }

    @Test
    void registerAccountShouldCreateLocation() {
        //GIVEN
        given(userService.registerUser(any(UserDTO.class), anyString())).willReturn(user);
        given(locationService.registerLocation(any(RegistrationVM.class))).willReturn(location);
        RegistrationVM incomingRegistration = createRegistrationVM();
        //WHEN
        underTest.registerAccount(incomingRegistration);
        //THEN
        ArgumentCaptor<RegistrationVM> registrationArgument = ArgumentCaptor.forClass(RegistrationVM.class);
        verify(locationService).registerLocation(registrationArgument.capture());
        assertEquals(incomingRegistration, registrationArgument.getValue());
    }

    @Test
    void registerAccountShouldCreateUserDetails() {
        //GIVEN
        given(userService.registerUser(any(UserDTO.class), anyString())).willReturn(user);
        given(locationService.registerLocation(any(RegistrationVM.class))).willReturn(location);
        RegistrationVM incomingRegistration = createRegistrationVM();
        //WHEN
        underTest.registerAccount(incomingRegistration);
        //THEN
        ArgumentCaptor<RegistrationVM> registrationArgument = ArgumentCaptor.forClass(RegistrationVM.class);
        ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Location> locationArgument = ArgumentCaptor.forClass(Location.class);
        verify(userDetailsService).registerUserDetails(registrationArgument.capture(), userArgument.capture(), locationArgument.capture());
        assertEquals(incomingRegistration, registrationArgument.getValue());
        assertEquals(user, userArgument.getValue());
        assertEquals(location, locationArgument.getValue());
    }


    @Test
    void activateAccountShouldActivateUser() {
        //GIVEN
        String key = "key";
        given(userService.activateRegistration(anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.activateAccount(key);
        //THEN
        ArgumentCaptor<String> stringArgument = ArgumentCaptor.forClass(String.class);
        verify(userService).activateRegistration(stringArgument.capture());
        assertEquals(key, stringArgument.getValue());
    }

    @Test
    void activateAccountShouldThrowExceptionWhenNoUserIsFoundForKey() {
        //GIVEN
        String key = "key";
        given(userService.activateRegistration(anyString())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user for key");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.activateAccount(key);
        });
        //THEN
        assertEquals("No user for key", exception.getMessage());
    }

    @Test
    void getAccountShouldThrowExceptionWhenNoLoggedInUserIsFound() {
        //GIVEN
        given(userService.getUserWithAuthorities()).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user found");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.getAccount();
        });
        //THEN
        assertEquals("No user found", exception.getMessage());
    }

    @Test
    void getAccountShouldThrowExceptionWhenNoUserDetailsIsFound() {
        //GIVEN
        given(userService.getUserWithAuthorities()).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user detail found");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.getAccount();
        });
        //THEN
        assertEquals("No user detail found", exception.getMessage());
    }

    @Test
    void getAccountShouldThrowExceptionWhenNoLocationIsFound() {
        //GIVEN
        given(userService.getUserWithAuthorities()).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.of(userDetails));
        given(locationService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No location found");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.getAccount();
        });
        //THEN
        assertEquals("No location found", exception.getMessage());
    }

    @Test
    void getAccountShouldReturnAccount() {
        //GIVEN
        LocationDTO locationDTO= createLocationDTO();
        given(userService.getUserWithAuthorities()).willReturn(Optional.of(user));
        given(userDetailsService.findByUserId(anyLong())).willReturn(Optional.of(userDetails));
        given(locationService.findOne(anyLong())).willReturn(Optional.of(locationDTO));
        AccountVM account = AccountVM.createAccountVM(new UserDTO(user), locationDTO, userDetails);
        //WHEN
        AccountVM result = underTest.getAccount();
        //THEN
        assertEquals(account, result);
    }

    @Test
    void saveAccountShouldThrowExceptionWhenNoUserIsLoggedIn() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);
        SecurityContextHolder.setContext(securityContext);
        AccountUpdateVM incomingAccountUpdate = createAccountUpdate();
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No current user");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.saveAccount(incomingAccountUpdate);
        });
        //THEN
        assertEquals("No current user", exception.getMessage());
    }

    @Test
    void saveAccountShouldThrowExceptionWhenEmailIsDuplicate() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        AccountUpdateVM incomingAccountUpdate = createAccountUpdate();
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        User duplicate = initUser();
        duplicate.setUsername("duplicate");
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.of(duplicate));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Duplicate email");
        //WHEN
        Exception exception = assertThrows(EmailAlreadyUsedException.class, () -> {
            underTest.saveAccount(incomingAccountUpdate);
        });
        //THEN
        assertEquals("Duplicate email", exception.getMessage());
    }

    @Test
    void saveAccountShouldThrowExceptionWhenLocationNotFound() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        AccountUpdateVM incomingAccountUpdate = createAccountUpdate();
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.empty());
        given(locationService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No location");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.saveAccount(incomingAccountUpdate);
        });
        //THEN
        assertEquals("No location", exception.getMessage());
    }

    @Test
    void saveAccountShouldThrowExceptionWhenUserDetailNotFound() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        AccountUpdateVM incomingAccountUpdate = createAccountUpdate();
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.empty());
        given(locationService.findOne(anyLong())).willReturn(Optional.of(createLocationDTO()));
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user detail");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.saveAccount(incomingAccountUpdate);
        });
        //THEN
        assertEquals("No user detail", exception.getMessage());
    }

    @Test
    void saveAccountShouldUpdateUser() {
        //GIVEN
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("user");
        SecurityContextHolder.setContext(securityContext);
        AccountUpdateVM incomingAccountUpdate = createAccountUpdate();
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(userRepository.findOneByEmailIgnoreCase(anyString())).willReturn(Optional.empty());
        given(locationService.findOne(anyLong())).willReturn(Optional.of(createLocationDTO()));
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.of(userDetails));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("No user detail");
        //WHEN
        underTest.saveAccount(incomingAccountUpdate);
        //THEN
        verify(userService).updateUser(incomingAccountUpdate.getFirstName(), incomingAccountUpdate.getLastName(), incomingAccountUpdate.getEmail());
    }


    @Test
    void changePasswordShouldThrowExceptionWhenInvalidPassword() {
        //GIVEN
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("password");
        passwordChangeDTO.setNewPassword("short");
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Invalid Password");
        //WHEN
        Exception exception = assertThrows(InvalidPasswordException.class, () -> {
            underTest.changePassword(passwordChangeDTO);
        });
        //THEN
        assertEquals("Invalid Password", exception.getMessage());
    }

    @Test
    void changePasswordShouldChangeUserPassword() {
        //GIVEN
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("password");
        passwordChangeDTO.setNewPassword("password");
        //WHEN
        underTest.changePassword(passwordChangeDTO);
        //THEN
        ArgumentCaptor<String> oldPassword = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPassword = ArgumentCaptor.forClass(String.class);
        verify(userService).changePassword(oldPassword.capture(), newPassword.capture());
        assertEquals(passwordChangeDTO.getCurrentPassword(), oldPassword.getValue());
        assertEquals(passwordChangeDTO.getNewPassword(), newPassword.getValue());
    }

    @Test
    void requestPasswordResetShouldThrowExceptionWhenEmailNotFound() {
        //GIVEN
        ResetPasswordVM resetPasswordVM = new ResetPasswordVM();
        resetPasswordVM.setMail("email@email.com");
        given(userService.requestPasswordReset(anyString())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Email not found");
        //WHEN
        Exception exception = assertThrows(EmailNotFoundException.class, () -> {
            underTest.requestPasswordReset(resetPasswordVM);
        });
        //THEN
        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void requestPasswordResetShouldSendResetMail() {
        //GIVEN
        ResetPasswordVM resetPasswordVM = new ResetPasswordVM();
        resetPasswordVM.setMail("email@email.com");
        given(userService.requestPasswordReset(anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.requestPasswordReset(resetPasswordVM);
        //THEN
        verify(mailService).sendPasswordResetMail(user);
    }

    @Test
    void finishPasswordResetShouldThrowExceptionWhenInvalidPassword() {
        //GIVEN
        KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
        keyAndPasswordVM.setKey("key");
        keyAndPasswordVM.setNewPassword("short");
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Invalid Password");
        //WHEN
        Exception exception = assertThrows(InvalidPasswordException.class, () -> {
            underTest.finishPasswordReset(keyAndPasswordVM);
        });
        //THEN
        assertEquals("Invalid Password", exception.getMessage());
    }

    @Test
    void finishPasswordResetShouldThrowExceptionWhenInvalidResetKey() {
        //GIVEN
        KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
        keyAndPasswordVM.setKey("key");
        keyAndPasswordVM.setNewPassword("password");
        given(userService.completePasswordReset(anyString(), anyString())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Invalid reset key");
        //WHEN
        Exception exception = assertThrows(AccountResource.AccountResourceException.class, () -> {
            underTest.finishPasswordReset(keyAndPasswordVM);
        });
        //THEN
        assertEquals("Invalid reset key", exception.getMessage());
    }

    @Test
    void finishPasswordResetShouldResetPassword() {
        //GIVEN
        KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
        keyAndPasswordVM.setKey("key");
        keyAndPasswordVM.setNewPassword("password");
        given(userService.completePasswordReset(anyString(), anyString())).willReturn(Optional.of(user));
        //WHEN
        underTest.finishPasswordReset(keyAndPasswordVM);
        //THEN
        verify(userService).completePasswordReset(keyAndPasswordVM.getNewPassword(), keyAndPasswordVM.getKey());
    }

    private RegistrationVM createRegistrationVM() {
        RegistrationVM result = new RegistrationVM();
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
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

    private Location initLocation() {
        Location result = new Location();
        result.setId(1L);
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        return result;
    }

    private LocationDTO createLocationDTO() {
        LocationDTO result = new LocationDTO();
        result.setId(1L);
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        return result;
    }

    public UserDetailsDTO initUserDetail() {
        UserDetailsDTO result = new UserDetailsDTO();
        result.setProfilePicId(1L);
        result.setUserUsername("user");
        result.setUserId(1L);
        result.setResidenceId(1L);
        result.setDescription("Description");
        return result;
    }

    AccountUpdateVM createAccountUpdate() {
        AccountUpdateVM result = new AccountUpdateVM();
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
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