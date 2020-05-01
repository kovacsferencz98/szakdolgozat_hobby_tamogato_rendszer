package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.controller.rest.LocationResource;
import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.repository.PictureRepository;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.PictureMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PictureServiceImplTest {

    @MockBean
    private PictureRepository pictureRepository;

    @MockBean
    private PictureMapper pictureMapper;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    PictureServiceImpl underTest;

    private PictureDTO pictureDTO;

    private Picture picture;

    @BeforeEach
    void setUp() {
        picture = initPicture();
        pictureDTO = initPictureDTO();
    }

    @Test
    void storeFileShouldThrowExceptionWhenInvalidFileName() {
        //GIVEN
        MultipartFile file = new MockMultipartFile("...!!@FREFpicture.....", "...!!@FREFpicture.....",  "image/jpeg", new byte[]{1});
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("Invalid file name");
        //WHEN
        Exception exception = assertThrows(PictureServiceImpl.PictureServiceException.class, () -> {
            underTest.storeFile(file);
        });
        //THEN
        assertEquals("Invalid file name", exception.getMessage());
    }

    @Test
    void storeFileShouldWrapIOException() throws Exception {
        //GIVEN
        MultipartFile file = Mockito.mock(MockMultipartFile.class);
        given(file.getBytes()).willThrow(IOException.class);
        given(file.getOriginalFilename()).willReturn("picture");
        given(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .willReturn("IO Exception");
        //WHEN
        Exception exception = assertThrows(PictureServiceImpl.PictureServiceException.class, () -> {
            underTest.storeFile(file);
        });
        //THEN
        assertEquals("IO Exception", exception.getMessage());
    }

    @Test
    void storeFileShouldSaveFile() throws Exception {
        //GIVEN
        Picture toSave = initPicture();
        toSave.setId(null);
        MultipartFile file = new MockMultipartFile("picture", "picture",  "image/jpeg", new byte[]{1});
        given(pictureRepository.save(any(Picture.class))).willReturn(picture);
        given(pictureMapper.toDto(any(Picture.class))).willReturn(pictureDTO);
        //WHEN
        PictureDTO result = underTest.storeFile(file);
        //THEN
        verify(pictureRepository).save(toSave);
        assertEquals(pictureDTO, result);
    }

    @Test
    void getFile() {
        //GIVEN
        given(pictureRepository.findById(anyLong())).willReturn(Optional.of(picture));
        given(pictureMapper.toDto(any(Picture.class))).willReturn(pictureDTO);
        //WHEN
        Optional<PictureDTO> result = underTest.getFile(1L);
        //THEN
        BDDMockito.verify(pictureRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(pictureDTO, result.get());
    }

    private PictureDTO initPictureDTO() {
        PictureDTO result = new PictureDTO();
        result.setData(new byte[]{1});
        result.setFileName("picture");
        result.setFileType("image/jpeg");
        result.setId(1L);
        return result;
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