package com.kovacs.ferencz.HobbyHelper.domain;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTypeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventType.class);
        EventType eventType1 = new EventType();
        eventType1.setId(1L);
        EventType eventType2 = new EventType();
        eventType2.setId(eventType1.getId());
        assertThat(eventType1).isEqualTo(eventType2);
        eventType2.setId(2L);
        assertThat(eventType1).isNotEqualTo(eventType2);
        eventType1.setId(null);
        assertThat(eventType1).isNotEqualTo(eventType2);
    }
}
