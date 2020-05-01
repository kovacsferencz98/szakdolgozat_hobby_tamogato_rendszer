package com.kovacs.ferencz.HobbyHelper.service.dto;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoleDTO.class);
        RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setName("USER");
        RoleDTO roleDTO2 = new RoleDTO();
        assertThat(roleDTO1).isNotEqualTo(roleDTO2);
        roleDTO2.setName(roleDTO1.getName());
        assertThat(roleDTO1).isEqualTo(roleDTO2);
        roleDTO2.setName("ADMIN");
        assertThat(roleDTO1).isNotEqualTo(roleDTO2);
        roleDTO1.setName(null);
        assertThat(roleDTO1).isNotEqualTo(roleDTO2);
    }
}
