package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.EventType;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.repository.LocationRepository;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LocationServiceImplTest {

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private LocationMapper locationMapper;

    @Autowired
    LocationServiceImpl underTest;

    private Location location;

    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        location = initLocation();
        locationDTO = initLocationDTO();
    }

    @Test
    void saveShouldSaveEntity() {
        //GIVEN
        given(locationMapper.toEntity(any(LocationDTO.class))).willReturn(location);
        given(locationMapper.toDto(any(Location.class))).willReturn(locationDTO);
        given(locationRepository.save(any(Location.class))).willReturn(location);
        //WHEN
        LocationDTO result = underTest.save(locationDTO);
        //THEN
        BDDMockito.verify(locationRepository).save(location);
        assertEquals(locationDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(locationRepository.findAll()).willReturn(Arrays.asList(location));
        given(locationMapper.toDto(any(Location.class))).willReturn(locationDTO);
        //WHEN
        List<LocationDTO> result = underTest.findAll();
        //THEN
        verify(locationRepository).findAll();
        assertEquals(Arrays.asList(locationDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(locationRepository.findById(anyLong())).willReturn(Optional.of(location));
        given(locationMapper.toDto(any(Location.class))).willReturn(locationDTO);
        //WHEN
        Optional<LocationDTO> result = underTest.findOne(1L);
        //THEN
        verify(locationRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(locationDTO, result.get());
    }

    @Test
    void delete() {
        //GIVEN - in setup
        //WHEN
        underTest.delete(1L);
        //THEN
        verify(locationRepository).deleteById(1L);
    }

    @Test
    void registerLocationShouldSaveEntity() {
        //GIVEN
        RegistrationVM registrationVM = createRegistrationVM();
        locationDTO.setId(null);
        given(locationMapper.toEntity(any(LocationDTO.class))).willReturn(location);
        given(locationRepository.save(any(Location.class))).willReturn(location);
        //WHEN
        Location result = underTest.registerLocation(registrationVM);
        //THEN
        ArgumentCaptor<LocationDTO> locationDTOArgumentCaptor = ArgumentCaptor.forClass(LocationDTO.class);
        BDDMockito.verify(locationMapper).toEntity(locationDTOArgumentCaptor.capture());
        BDDMockito.verify(locationRepository).save(location);
        assertEquals(locationDTO, locationDTOArgumentCaptor.getValue());
        assertEquals(location, result);
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

    private LocationDTO initLocationDTO() {
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
}