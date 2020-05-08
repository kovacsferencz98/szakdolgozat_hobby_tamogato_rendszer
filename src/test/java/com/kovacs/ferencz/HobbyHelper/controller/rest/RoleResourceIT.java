package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.service.RoleService;
import com.kovacs.ferencz.HobbyHelper.service.mapper.RoleMapper;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RoleResource} REST controller.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RoleResourceIT {

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restRoleMockMvc;

    private Role role;

    @BeforeEach
    public void setup() {
        clearDatabase();
        role =  new Role(DEFAULT_ROLE);
        MockitoAnnotations.initMocks(this);
        final RoleResource roleResource = new RoleResource(roleService);
        this.restRoleMockMvc = MockMvcBuilders.standaloneSetup(roleResource)
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
    public void getAllRoles() throws Exception {
        //GIVEN
        Role savedRole = roleRepository.saveAndFlush(role);
        //WHEN
        //THEN
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_ROLE)));
    }
    
    @Test
    @Transactional
    public void getRole() throws Exception {
        //GIVEN
        Role savedRole = roleRepository.saveAndFlush(role);
        //WHEN
        //THEN
        restRoleMockMvc.perform(get("/api/roles/{name}", role.getName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(DEFAULT_ROLE));
    }

    @Test
    @Transactional
    public void getNonExistingRole() throws Exception {
        //GIVEN - in setup
        //WHEN
        //THEN
        restRoleMockMvc.perform(get("/api/roles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    private void clearDatabase() {
        roleRepository.deleteAll();
        roleRepository.flush();
    }
}
