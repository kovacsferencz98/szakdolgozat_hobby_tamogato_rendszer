package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.repository.PictureRepository;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.PictureMapper;
import com.kovacs.ferencz.HobbyHelper.service.mapper.RoleMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PictureServiceIT {
    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    PictureMapper pictureMapper;

    @Autowired
    private PictureServiceImpl underTest;

    private Picture picture;

    @BeforeEach
    void setUp() {
        clearDatabase();
        picture =  initPicture();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Test
    @Transactional
    void storeFileShouldCreateDatabaseEntity() {
        //GIVEN
        MultipartFile file = new MockMultipartFile("picture", "picture",  "image/jpeg", new byte[]{1});
        //WHEN
        PictureDTO response = underTest.storeFile(file);
        //THEN
        Optional<Picture> saved = pictureRepository.findById(response.getId());
        assertTrue(saved.isPresent());
        Picture pictureObtained = saved.get();
        assertArrayEquals(picture.getData(), pictureObtained.getData());
        assertEquals(picture.getFileName(), pictureObtained.getFileName());
        assertEquals(picture.getFileType(), pictureObtained.getFileType());
    }

    @Test
    @Transactional
    void getFileShouldReturnEntity() {
        //GIVEN
        Picture saved = pictureRepository.saveAndFlush(picture);
        //WHEN
        Optional<PictureDTO> result = underTest.getFile(saved.getId());
        //THEN
        assertTrue(result.isPresent());
        assertEquals(pictureMapper.toDto(saved), result.get());
    }


    private void clearDatabase() {
        pictureRepository.deleteAll();
        pictureRepository.flush();
    }

    private Picture initPicture() {
        Picture result = new Picture();
        result.setData(new byte[]{1});
        result.setFileName("picture");
        result.setFileType("image/jpeg");
        result.setId(1L);
        return result;
    }
}
