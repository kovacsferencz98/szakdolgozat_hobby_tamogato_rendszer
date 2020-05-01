package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.RegistrationVM;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.repository.LocationRepository;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.LocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LocationResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LocationResourceIT {

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STREET = "AAAAAAAAAA";
    private static final String UPDATED_STREET = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER = 1;
    private static final Integer UPDATED_NUMBER = 2;

    private static final String DEFAULT_APARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_APARTMENT = "BBBBBBBBBB";

    private static final String DEFAULT_ZIP = "AAAAAAAAAA";
    private static final String UPDATED_ZIP = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Validator validator;

    private MockMvc restLocationMockMvc;

    private Location location;

    @BeforeEach
    public void setup() {
        clearDatabase();
        location = initLocation();
        MockitoAnnotations.initMocks(this);
        final LocationResource locationResource = new LocationResource(locationService, messageSource, userDetailsService, eventService);
        this.restLocationMockMvc = MockMvcBuilders.standaloneSetup(locationResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createLocation() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = locationRepository.findAll().size();
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isCreated());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeCreate + 1);
        Location testLocation = locationList.get(locationList.size() - 1);
        assertThat(testLocation.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testLocation.getRegion()).isEqualTo(DEFAULT_REGION);
        assertThat(testLocation.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testLocation.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testLocation.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testLocation.getApartment()).isEqualTo(DEFAULT_APARTMENT);
        assertThat(testLocation.getZip()).isEqualTo(DEFAULT_ZIP);
        assertThat(testLocation.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testLocation.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createLocationWithExistingId() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = locationRepository.findAll().size();
        location.setId(1L);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkCountryIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setCountry(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkCityIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setCity(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkStreetIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setStreet(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkNumberIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setNumber(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkZipIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setZip(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkLatitudeIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setLatitude(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkLongitudeIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = locationRepository.findAll().size();
        location.setLongitude(null);
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(post("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLocations() throws Exception {
        //GIVEN
        Location savedLocation = locationRepository.saveAndFlush(location);
        //WHEN
        //THEN
        restLocationMockMvc.perform(get("/api/locations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(savedLocation.getId().intValue())))
                .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
                .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
                .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET)))
                .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
                .andExpect(jsonPath("$.[*].apartment").value(hasItem(DEFAULT_APARTMENT)))
                .andExpect(jsonPath("$.[*].zip").value(hasItem(DEFAULT_ZIP)))
                .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
                .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }

    @Test
    @Transactional
    public void getLocation() throws Exception {
        //GIVEN
        Location savedLocation = locationRepository.saveAndFlush(location);
        //WHEN
        //THEN
        restLocationMockMvc.perform(get("/api/locations/{id}", location.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedLocation.getId().intValue()))
                .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
                .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
                .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
                .andExpect(jsonPath("$.street").value(DEFAULT_STREET))
                .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
                .andExpect(jsonPath("$.apartment").value(DEFAULT_APARTMENT))
                .andExpect(jsonPath("$.zip").value(DEFAULT_ZIP))
                .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
                .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingLocation() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restLocationMockMvc.perform(get("/api/locations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateLocation() throws Exception {
        //GIVEN
        Location savedLocation = locationRepository.saveAndFlush(location);
        int databaseSizeBeforeUpdate = locationRepository.findAll().size();
        Location updatedLocation = locationRepository.findById(location.getId()).get();
        em.detach(updatedLocation);
        updatedLocation
                .country(UPDATED_COUNTRY)
                .region(UPDATED_REGION)
                .city(UPDATED_CITY)
                .street(UPDATED_STREET)
                .number(UPDATED_NUMBER)
                .apartment(UPDATED_APARTMENT)
                .zip(UPDATED_ZIP)
                .latitude(UPDATED_LATITUDE)
                .longitude(UPDATED_LONGITUDE);
        LocationDTO locationDTO = locationMapper.toDto(updatedLocation);
        //WHEN
        restLocationMockMvc.perform(put("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isOk());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeUpdate);
        Location testLocation = locationList.get(locationList.size() - 1);
        assertThat(testLocation.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testLocation.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testLocation.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testLocation.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testLocation.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testLocation.getApartment()).isEqualTo(UPDATED_APARTMENT);
        assertThat(testLocation.getZip()).isEqualTo(UPDATED_ZIP);
        assertThat(testLocation.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testLocation.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateNonExistingLocation() throws Exception {
        //GIVEN
        int databaseSizeBeforeUpdate = locationRepository.findAll().size();
        LocationDTO locationDTO = locationMapper.toDto(location);
        //WHEN
        restLocationMockMvc.perform(put("/api/locations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locationDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteLocation() throws Exception {
        //GIVEN
        Location savedLocation = locationRepository.saveAndFlush(location);
        int databaseSizeBeforeDelete = locationRepository.findAll().size();
        //WHEN
        restLocationMockMvc.perform(delete("/api/locations/{id}", savedLocation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
        //THEN
        List<Location> locationList = locationRepository.findAll();
        assertThat(locationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private void clearDatabase() {
        locationRepository.deleteAll();
        locationRepository.flush();
    }

    private Location initLocation() {
        Location result = new Location();
        result.setId(null);
        result.setApartment(DEFAULT_APARTMENT);
        result.setCity(DEFAULT_CITY);
        result.setCountry(DEFAULT_COUNTRY);
        result.setRegion(DEFAULT_REGION);
        result.setStreet(DEFAULT_STREET);
        result.setNumber(DEFAULT_NUMBER);
        result.setLatitude(DEFAULT_LATITUDE);
        result.setLongitude(DEFAULT_LONGITUDE);
        result.setZip(DEFAULT_ZIP);
        return result;
    }

    private RegistrationVM createRegistrationVM() {
        RegistrationVM result = new RegistrationVM();
        result.setApartment(DEFAULT_APARTMENT);
        result.setCity(DEFAULT_CITY);
        result.setCountry(DEFAULT_COUNTRY);
        result.setRegion(DEFAULT_REGION);
        result.setStreet(DEFAULT_STREET);
        result.setNumber(DEFAULT_NUMBER);
        result.setLatitude(DEFAULT_LATITUDE);
        result.setLongitude(DEFAULT_LONGITUDE);
        result.setZip(DEFAULT_ZIP);
        result.setDescription("Description");
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
