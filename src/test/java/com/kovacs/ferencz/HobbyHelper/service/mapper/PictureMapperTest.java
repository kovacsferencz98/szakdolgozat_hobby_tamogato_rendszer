package com.kovacs.ferencz.HobbyHelper.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PictureMapperTest {

    @Autowired
    private PictureMapper pictureMapper;

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(pictureMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(pictureMapper.fromId(null)).isNull();
    }
}