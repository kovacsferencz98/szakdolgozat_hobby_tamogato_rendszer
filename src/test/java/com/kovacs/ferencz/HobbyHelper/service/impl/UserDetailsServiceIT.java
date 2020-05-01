package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.*;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserDetailsMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserDetailsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserDetailsServiceIT {

    private static Logger logger = LoggerFactory.getLogger(UserDetailsServiceIT.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    UserDetailsMapper userDetailsMapper;

    @Autowired
    private UserDetailsServiceImpl underTest;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        clearDatabase();
        userDetails = initUserDetails();
    }

    @Test
    @Transactional
    void registerUserDetailsShouldCreateDatabaseEntity() {
        //GIVEN
        RegistrationVM registrationVM = createRegistrationVM();
        //WHEN
        UserDetailsDTO response = underTest.registerUserDetails(registrationVM, userDetails.getUser(), userDetails.getResidence());
        //THEN
        Optional<UserDetails> saved = userDetailsRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        UserDetails participant = saved.get();
        participant.setId(null);
        userDetails.setId(null);
        assertEquals(userDetails, participant);
    }

    @Test
    @Transactional
    void saveShouldCreateDatabaseEntity() {
        //GIVEN
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);
        //WHEN
        UserDetailsDTO response = underTest.save(userDetailsDTO);
        //THEN
        Optional<UserDetails> saved = userDetailsRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        UserDetails obtainedUserDetails = saved.get();
        obtainedUserDetails.setId(null);
        this.userDetails.setId(null);
        assertEquals(this.userDetails, obtainedUserDetails);
    }

    @Test
    @Transactional
    void findAllShouldReturnEntities() {
        //GIVEN
        UserDetails saved = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        List<UserDetailsDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(userDetailsMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findAllByResidenceShouldReturnEntities() {
        //GIVEN
        Long locationId = userDetails.getResidence().getId();
        UserDetails saved = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        List<UserDetailsDTO> result = underTest.findAllByResidence(locationId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(userDetailsMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findOneShouldReturnEntity() {
        //GIVEN
        UserDetails saved = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        Optional<UserDetailsDTO> result = underTest.findOne(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(userDetailsMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void findByUserIdShouldReturnEntity() {
        //GIVEN
        Long userId = userDetails.getUser().getId();
        UserDetails saved = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        Optional<UserDetailsDTO> result = underTest.findByUserId(userId);
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(userDetailsMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void deleteShouldDeleteEntity() {
        //GIVEN
        UserDetails saved = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        underTest.delete(saved.getId());
        //THEN
        Optional<UserDetails> entity = userDetailsRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    @Test
    @Transactional
    void deleteDetailOfUserShouldDeleteEntities() {
        //GIVEN
        Long userId = userDetails.getUser().getId();
        UserDetails saved = userDetailsRepository.saveAndFlush(userDetails);
        //WHEN
        underTest.deleteDetailOfUser(userId);
        //THEN
        Optional<UserDetails> entity = userDetailsRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
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
