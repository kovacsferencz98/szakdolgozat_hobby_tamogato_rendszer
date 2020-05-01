package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.service.RoleService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.RoleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleResourceTest {

    @MockBean
    private RoleService roleService;

    @Autowired
    private RoleResource underTest;

    private RoleDTO role;

    @BeforeEach
    void setUp() {
        role = initRole();
    }

    @Test
    void getAllRolesShouldReturnRoles() {
        //GIVEN
        given(roleService.findAll()).willReturn(Arrays.asList(role));
        //WHEN
        List<RoleDTO> result = underTest.getAllRoles();
        //THEN
        assertEquals(Arrays.asList(role), result);
    }

    @Test
    void getEventShouldReturnNotFoundResponseWhenNoLocationIsFound() {
        //GIVEN
        given(roleService.findOne(anyString())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<RoleDTO> result = underTest.getRole("USER");
        //THEN
        assertEquals(ResponseEntity.notFound().build(), result);
    }

    @Test
    void getEventShouldReturnFoundLocation() {
        //GIVEN
        given(roleService.findOne(anyString())).willReturn(Optional.of(role));
        //WHEN
        ResponseEntity<RoleDTO> result = underTest.getRole("USER");
        //THEN
        assertEquals(role, result.getBody());
    }

    private RoleDTO initRole() {
        RoleDTO result = new RoleDTO();
        result.setName("USER");
        return result;
    }
}