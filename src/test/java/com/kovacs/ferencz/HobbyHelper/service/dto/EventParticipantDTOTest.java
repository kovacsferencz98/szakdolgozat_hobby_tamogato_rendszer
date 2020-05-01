package com.kovacs.ferencz.HobbyHelper.service.dto;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventParticipantDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventParticipantDTO.class);
        EventParticipantDTO eventParticipantDTO1 = new EventParticipantDTO();
        eventParticipantDTO1.setId(1L);
        EventParticipantDTO eventParticipantDTO2 = new EventParticipantDTO();
        assertThat(eventParticipantDTO1).isNotEqualTo(eventParticipantDTO2);
        eventParticipantDTO2.setId(eventParticipantDTO1.getId());
        assertThat(eventParticipantDTO1).isEqualTo(eventParticipantDTO2);
        eventParticipantDTO2.setId(2L);
        assertThat(eventParticipantDTO1).isNotEqualTo(eventParticipantDTO2);
        eventParticipantDTO1.setId(null);
        assertThat(eventParticipantDTO1).isNotEqualTo(eventParticipantDTO2);
    }
}
