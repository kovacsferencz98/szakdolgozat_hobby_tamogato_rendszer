package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.CreateUserDetailVM;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserDetailsResourceTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private UserDetailsResource underTest;

    private UserDetailsDTO userDetailsDTO;

    private User user;

    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        userDetailsDTO = initUserDetail();
        user = initUser();
        locationDTO = createLocationDTO();
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createUserDetailsShouldThrowExceptionIfUserDetailsIdIsSet() {
        //GIVEN
        CreateUserDetailVM createUserDetailVM = createUserDetailVM();
        createUserDetailVM.getUserDetails().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id set");
        //WHEN
        Exception exception = assertThrows(UserDetailsResource.UserDetailsResourceException.class, () -> {
            underTest.createUserDetails(createUserDetailVM);
        });
        //THEN
        assertEquals("Id set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createUserDetailsShouldThrowExceptionIfLocationIdIsSet() {
        //GIVEN
        CreateUserDetailVM createUserDetailVM = createUserDetailVM();
        createUserDetailVM.getLocation().setId(1L);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id set");
        //WHEN
        Exception exception = assertThrows(UserDetailsResource.UserDetailsResourceException.class, () -> {
            underTest.createUserDetails(createUserDetailVM);
        });
        //THEN
        assertEquals("Id set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER"})
    void createUserDetailsShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        CreateUserDetailVM createUserDetailVM = createUserDetailVM();
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(Exception.class, () -> {
            underTest.createUserDetails(createUserDetailVM);
        });
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void createUserDetailsShouldSaveEntity() throws Exception {
        //GIVEN
        CreateUserDetailVM createUserDetailVM = createUserDetailVM();
        given(userDetailsService.save(any(UserDetailsDTO.class))).willReturn(userDetailsDTO);
        given(locationService.save(any(LocationDTO.class))).willReturn(locationDTO);
        UserDetailsDTO details = initUserDetail();
        details.setId(null);
        LocationDTO location = createLocationDTO();
        location.setId(null);
        //WHEN
        ResponseEntity<UserDetailsDTO> repsonse = underTest.createUserDetails(createUserDetailVM);
        //THEN
        verify(userDetailsService).save(details);
        verify(locationService).save(location);
        assertEquals(userDetailsDTO, repsonse.getBody());
        assertEquals(HttpStatus.CREATED, repsonse.getStatusCode());
    }

    @Test
    @WithMockUser(username="user2", roles = {"USER"})
    void updateUserDetailsShouldThrowExceptionIfUnauthorized() {
        //GIVEN
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        Exception exception = assertThrows(UnauthorizedRequest.class, () -> {
            underTest.updateUserDetails(userDetailsDTO);
        });
        //THEN
        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateUserDetailsShouldThrowExceptionIfIdIsNotSet() {
        //GIVEN
        userDetailsDTO.setId(null);
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Id not set");
        //WHEN
        Exception exception = assertThrows(UserDetailsResource.UserDetailsResourceException.class, () -> {
            underTest.updateUserDetails(userDetailsDTO);
        });
        //THEN
        assertEquals("Id not set", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateUserDetailsShouldUpdateEntity() throws Exception {
        //GIVEN
        given(userDetailsService.save(any(UserDetailsDTO.class))).willReturn(userDetailsDTO);
        //WHEN
        ResponseEntity<UserDetailsDTO> repsonse = underTest.updateUserDetails(userDetailsDTO);
        //THEN
        verify(userDetailsService).save(userDetailsDTO);
        assertEquals(userDetailsDTO, repsonse.getBody());
        assertEquals(HttpStatus.OK, repsonse.getStatusCode());
    }

    @Test
    void getAllUserDetailsShouldReturnLocations() {
        //GIVEN
        given(userDetailsService.findAll()).willReturn(Arrays.asList(userDetailsDTO));
        //WHEN
        List<UserDetailsDTO> result = underTest.getAllUserDetails();
        //THEN
        assertEquals(Arrays.asList(userDetailsDTO), result);
    }

    @Test
    void getUserDetailsShouldReturnNotFoundResponseWhenNoLocationIsFound() {
        //GIVEN
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<UserDetailsDTO> result = underTest.getUserDetails(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getEventShouldReturnFoundLocation() {
        //GIVEN
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.of(userDetailsDTO));
        //WHEN
        ResponseEntity<UserDetailsDTO> result = underTest.getUserDetails(1L);
        //THEN
        assertEquals(userDetailsDTO, result.getBody());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteUserDetailsShouldThrowExceptionIfNotFound() {
        //GIVEN
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not found");
        //WHEN
        Exception exception = assertThrows(UserDetailsResource.UserDetailsResourceException.class, () -> {
            underTest.deleteUserDetails(1L);
        });
        //THEN
        assertEquals("Not found", exception.getMessage());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteUserDetailsShouldThrowExceptionIfStillInUse() {
        //GIVEN
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.of(user));
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Still in use");
        //WHEN
        Exception exception = assertThrows(UserDetailsResource.UserDetailsResourceException.class, () -> {
            underTest.deleteUserDetails(1L);
        });
        //THEN
        assertEquals("Still in use", exception.getMessage());
    }

    @Test
    @WithMockUser(username="notUser", roles = {"USER"})
    void deleteUserDetailsShouldThrowExceptionIfNotAuthorized() {
        //GIVEN
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.empty());
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Not authorized");
        //WHEN
        //THEN
        assertThrows(UnauthorizedRequest.class, () -> {
            underTest.deleteUserDetails(1L);
        });
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void deleteLocationShouldDeleteEntity() {
        //GIVEN
        given(userDetailsService.findOne(anyLong())).willReturn(Optional.of(userDetailsDTO));
        given(userService.getUserWithAuthorities(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<Void> response = underTest.deleteUserDetails(1L);
        //THEN
        verify(userDetailsService).delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
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
        result.setId(1L);
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

    private CreateUserDetailVM createUserDetailVM() {
        CreateUserDetailVM result = new CreateUserDetailVM();
        LocationDTO location = createLocationDTO();
        location.setId(null);
        UserDetailsDTO userDetails = initUserDetail();
        userDetails.setId(null);
        result.setLocation(location);
        result.setUserDetails(userDetails);
        return result;
    }
}