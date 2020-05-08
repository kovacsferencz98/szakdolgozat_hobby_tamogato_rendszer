package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Event;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.RoleDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.EventMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.RoleMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RoleServiceIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    private RoleServiceImpl underTest;

    private Role role;

    @BeforeEach
    void setUp() {
        clearDatabase();
        role =  new Role(AuthoritiesConstants.USER);
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    void saveShouldCreateDatabaseEntity() {
        //GIVEN
        RoleDTO roleDTO = roleMapper.toDto(role);
        //WHEN
        RoleDTO response = underTest.save(roleDTO);
        //THEN
        Optional<Role> saved = roleRepository.findById(response.getName());
        assertTrue(saved.isPresent());
        Role roleObtained = saved.get();
        assertEquals(this.role, roleObtained);
    }

    @Test
    @Transactional
    void findAllShouldReturnEntities() {
        //GIVEN
        Role saved = roleRepository.saveAndFlush(role);
        //WHEN
        List<RoleDTO> result = underTest.findAll();
        //THEN
        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(roleMapper.toDto(saved)), result);
    }

    @Test
    @Transactional
    void findOneShouldReturnEntity() {
        //GIVEN
        Role saved = roleRepository.saveAndFlush(role);
        //WHEN
        Optional<RoleDTO> result = underTest.findOne(saved.getName());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(roleMapper.toDto(saved), result.get());
    }

    @Test
    @Transactional
    void deleteShouldDeleteEntity() {
        //GIVEN
        Role saved = roleRepository.saveAndFlush(role);
        //WHEN
        underTest.delete(saved.getName());
        //THEN
        Optional<Role> entity = roleRepository.findById(saved.getName());
        assertFalse(entity.isPresent());
    }

    private void clearDatabase() {
        roleRepository.deleteAll();
        roleRepository.flush();
    }

}
