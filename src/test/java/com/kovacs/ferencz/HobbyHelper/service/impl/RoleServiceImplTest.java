package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.service.dto.RoleDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.RoleDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.RoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleServiceImplTest {

    @MockBean
    private  RoleRepository roleRepository;

    @MockBean
    private RoleMapper roleMapper;

    @Autowired
    private RoleServiceImpl underTest;

    private Role role;

    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        role = initRole();
        roleDTO = initRoleDTO();
    }

    @Test
    void saveShouldSaveEntity() {
        //GIVEN
        given(roleMapper.toEntity(any(RoleDTO.class))).willReturn(role);
        given(roleMapper.toDto(any(Role.class))).willReturn(roleDTO);
        given(roleRepository.save(any(Role.class))).willReturn(role);
        //WHEN
        RoleDTO result = underTest.save(roleDTO);
        //THEN
        BDDMockito.verify(roleRepository).save(role);
        assertEquals(roleDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(roleRepository.findAll()).willReturn(Arrays.asList(role));
        given(roleMapper.toDto(any(Role.class))).willReturn(roleDTO);
        //WHEN
        List<RoleDTO> result = underTest.findAll();
        //THEN
        verify(roleRepository).findAll();
        assertEquals(Arrays.asList(roleDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(roleRepository.findById(anyString())).willReturn(Optional.of(role));
        given(roleMapper.toDto(any(Role.class))).willReturn(roleDTO);
        //WHEN
        Optional<RoleDTO> result = underTest.findOne("USER");
        //THEN
        verify(roleRepository).findById("USER");
        assertTrue(result.isPresent());
        assertEquals(roleDTO, result.get());
    }

    @Test
    void deleteShouldDeleteEntity() {
        //GIVEN - in setup
        //WHEN
        underTest.delete("USER");
        //THEN
        verify(roleRepository).deleteById("USER");
    }

    private RoleDTO initRoleDTO() {
        RoleDTO result = new RoleDTO();
        result.setName("USER");
        return result;
    }

    private Role initRole() {
        Role result = new Role();
        result.setName("USER");
        return result;
    }
}