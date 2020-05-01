package com.kovacs.ferencz.HobbyHelper.domain;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role1 = new Role();
        role1.setName("USER");
        Role role2 = new Role();
        role2.setName(role1.getName());
        assertThat(role1).isEqualTo(role2);
        role2.setName("ADMIN");
        assertThat(role1).isNotEqualTo(role2);
        role1.setName(null);
        assertThat(role1).isNotEqualTo(role2);
    }
}
