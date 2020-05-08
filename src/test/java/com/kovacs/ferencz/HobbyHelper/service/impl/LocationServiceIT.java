package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.LocationRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import org.junit.jupiter.api.AfterEach;
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
public class LocationServiceIT {

    private static Logger logger = LoggerFactory.getLogger(LocationServiceIT.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    LocationMapper locationMapper;

    @Autowired
    private LocationServiceImpl underTest;

    private Location location;


    @BeforeEach
    void setUp() {
        clearDatabase();
        location = initLocation();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    void registerLocationShouldCreateDatabaseEntity() {
        //GIVEN
        RegistrationVM registrationVM = createRegistrationVM();
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        Location response = underTest.registerLocation(registrationVM);
        //THEN
        Optional<Location> saved = locationRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        Location locationObtained = saved.get();
        this.location.setId(locationObtained.getId());
        assertEquals(this.location, locationObtained);
    }

    @Test
    @Transactional
    void saveShouldCreateDatabaseEntity() {
        //GIVEN
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        LocationDTO response = underTest.save(locationDTO);
        //THEN
        Optional<Location> saved = locationRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        Location locationObtained = saved.get();
        this.location.setId(locationObtained.getId());
        assertEquals(this.location, locationObtained);
    }

    @Test
    @Transactional
    void findAllShouldReturnEntities() {
        //GIVEN
        Location saved = locationRepository.saveAndFlush(location);
        //WHEN
        List<LocationDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(locationMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findOneShouldReturnEntity() {
        //GIVEN
        Location saved = locationRepository.saveAndFlush(location);
        //WHEN
        Optional<LocationDTO> result = underTest.findOne(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(locationMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void deleteShouldDeleteEntity() {
        //GIVEN
        Location saved = locationRepository.saveAndFlush(location);
        //WHEN
        underTest.delete(saved.getId());
        //THEN
        Optional<Location> entity = locationRepository.findById(saved.getId());
        assertFalse(entity.isPresent());
    }

    private void clearDatabase() {
        locationRepository.deleteAll();
        locationRepository.flush();
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

