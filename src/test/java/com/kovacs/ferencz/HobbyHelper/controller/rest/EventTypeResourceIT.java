package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.domain.EventType;
import com.kovacs.ferencz.HobbyHelper.repository.EventTypeRepository;
import com.kovacs.ferencz.HobbyHelper.service.EventService;
import com.kovacs.ferencz.HobbyHelper.service.EventTypeService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventTypeMapper;
import org.junit.jupiter.api.AfterEach;
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
 * Integration tests for the {@link EventTypeResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ICON = "AAAAAAAAAA";
    private static final String UPDATED_ICON = "BBBBBBBBBB";

    private static final String DEFAULT_BANNER = "AAAAAAAAAA";
    private static final String UPDATED_BANNER = "BBBBBBBBBB";

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeMapper eventTypeMapper;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EventService eventService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEventTypeMockMvc;

    private EventType eventType;

    @BeforeEach
    public void setup() {
        clearDatabase();
        eventType = initEventType();
        MockitoAnnotations.initMocks(this);
        final EventTypeResource eventTypeResource = new EventTypeResource(eventTypeService, messageSource, eventService);
        this.restEventTypeMockMvc = MockMvcBuilders.standaloneSetup(eventTypeResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createEventType() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = eventTypeRepository.findAll().size();
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(post("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isCreated());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeCreate + 1);
        EventType testEventType = eventTypeList.get(eventTypeList.size() - 1);
        assertThat(testEventType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEventType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEventType.getIconUrl()).isEqualTo(DEFAULT_ICON);
        assertThat(testEventType.getBannerUrl()).isEqualTo(DEFAULT_BANNER);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void createEventTypeWithExistingId() throws Exception {
        //GIVEN
        int databaseSizeBeforeCreate = eventTypeRepository.findAll().size();
        eventType.setId(1L);
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(post("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkNameIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventTypeRepository.findAll().size();
        eventType.setName(null);
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(post("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkDescriptionIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventTypeRepository.findAll().size();
        eventType.setDescription(null);
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(post("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkIconIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventTypeRepository.findAll().size();
        eventType.setIconUrl(null);
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(post("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void checkBannerIsRequired() throws Exception {
        //GIVEN
        int databaseSizeBeforeTest = eventTypeRepository.findAll().size();
        eventType.setBannerUrl(null);
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(post("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEventTypes() throws Exception {
        //GIVEN
        EventType savedType = eventTypeRepository.saveAndFlush(eventType);
        //WHEN
        //THEN
        restEventTypeMockMvc.perform(get("/api/event-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(savedType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                .andExpect(jsonPath("$.[*].iconUrl").value(hasItem(DEFAULT_ICON)))
                .andExpect(jsonPath("$.[*].bannerUrl").value(hasItem(DEFAULT_BANNER)));
    }

    @Test
    @Transactional
    public void getEventType() throws Exception {
        //GIVEN
        EventType savedType = eventTypeRepository.saveAndFlush(eventType);
        //WHEN
        //THEN
        restEventTypeMockMvc.perform(get("/api/event-types/{id}", savedType.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedType.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                .andExpect(jsonPath("$.iconUrl").value(DEFAULT_ICON))
                .andExpect(jsonPath("$.bannerUrl").value(DEFAULT_BANNER));
    }

    @Test
    @Transactional
    public void getNonExistingEventType() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restEventTypeMockMvc.perform(get("/api/event-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateEventType() throws Exception {
        //GIVEN
        EventType savedType = eventTypeRepository.saveAndFlush(eventType);
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        EventType updatedEventType = eventTypeRepository.findById(eventType.getId()).get();
        em.detach(updatedEventType);
        updatedEventType
                .name(UPDATED_NAME)
                .description(UPDATED_DESCRIPTION)
                .iconUrl(UPDATED_ICON)
                .bannerUrl(UPDATED_BANNER);
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(updatedEventType);
        //WHEN
        restEventTypeMockMvc.perform(put("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isOk());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        EventType testEventType = eventTypeList.get(eventTypeList.size() - 1);
        assertThat(testEventType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEventType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEventType.getIconUrl()).isEqualTo(UPDATED_ICON);
        assertThat(testEventType.getBannerUrl()).isEqualTo(UPDATED_BANNER);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void updateNonExistingEventType() throws Exception {
        //GIVEN
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        //WHEN
        restEventTypeMockMvc.perform(put("/api/event-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
                .andExpect(status().isBadRequest());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void deleteEventType() throws Exception {
        //GIVEN
        eventTypeRepository.saveAndFlush(eventType);
        int databaseSizeBeforeDelete = eventTypeRepository.findAll().size();
        //WHEN
        restEventTypeMockMvc.perform(delete("/api/event-types/{id}", eventType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
        //THEN
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private void clearDatabase() {
        eventTypeRepository.deleteAll();
        eventTypeRepository.flush();
    }

    private EventType initEventType() {
        EventType type = new EventType();
        type.setId(null);
        type.setDescription(DEFAULT_DESCRIPTION);
        type.setName(DEFAULT_NAME);
        type.setBannerUrl(DEFAULT_BANNER);
        type.setIconUrl(DEFAULT_ICON);
        return type;
    }
}
