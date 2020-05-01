package com.kovacs.ferencz.HobbyHelper.service.dto;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PasswordChangeDTOTest {
    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PasswordChangeDTO.class);
        PasswordChangeDTO passwordChangeDTO1 = new PasswordChangeDTO();
        passwordChangeDTO1.setNewPassword("newnew");
        passwordChangeDTO1.setCurrentPassword("current");
        PasswordChangeDTO passwordChangeDTO2 = new PasswordChangeDTO();
        assertThat(passwordChangeDTO1).isNotEqualTo(passwordChangeDTO2);
        passwordChangeDTO2.setNewPassword(passwordChangeDTO1.getNewPassword());
        passwordChangeDTO2.setCurrentPassword(passwordChangeDTO1.getCurrentPassword());
        assertThat(passwordChangeDTO1).isEqualTo(passwordChangeDTO2);
        passwordChangeDTO2.setCurrentPassword("current2");
        assertThat(passwordChangeDTO1).isNotEqualTo(passwordChangeDTO2);
        passwordChangeDTO1.setNewPassword(null);
        assertThat(passwordChangeDTO1).isNotEqualTo(passwordChangeDTO2);
    }
}