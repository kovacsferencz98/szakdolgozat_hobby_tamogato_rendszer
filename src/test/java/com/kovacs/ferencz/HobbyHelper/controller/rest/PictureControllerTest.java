package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.SavedPictureVM;
import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.service.PictureService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PictureControllerTest {

    @MockBean
    private PictureService pictureService;

    @Autowired
    private PictureController underTest;

    private PictureDTO pictureDTO;

    private SavedPictureVM savedPictureVM;

    @BeforeEach
    void setUp() {
        pictureDTO = initPictureDTO();
        savedPictureVM = createSavedPictureVM();
    }

    @Test
    void uploadPictureShouldSavePicture() throws Exception {
        //GIVEN
        MultipartFile file = new MockMultipartFile("picture", new byte[]{1});
        given(pictureService.storeFile(any(MultipartFile.class))).willReturn(pictureDTO);
        //WHEN
        ResponseEntity<SavedPictureVM> response = underTest.uploadPicture(file);
        //THEN
        verify(pictureService).storeFile(file);
        assertEquals(savedPictureVM, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    void downloadPictureShouldReturnNotFoundResponseIfPictureNotPresent() throws Exception {
        //GIVEN
        MultipartFile file = new MockMultipartFile("picture", new byte[]{1});
        given(pictureService.getFile(anyLong())).willReturn(Optional.empty());
        //WHEN
        ResponseEntity<Resource> response = underTest.downloadPicture(1L);
        //THEN
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void downloadPictureShouldReturnFoundPicture() throws Exception {
        //GIVEN
        MultipartFile file = new MockMultipartFile("picture", new byte[]{1});
        given(pictureService.getFile(anyLong())).willReturn(Optional.of(pictureDTO));
        //WHEN
        ResponseEntity<Resource> response = underTest.downloadPicture(1L);
        //THEN
        assertEquals(new ByteArrayResource(file.getBytes()), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private PictureDTO initPictureDTO() {
        PictureDTO result = new PictureDTO();
        result.setData(new byte[]{1});
        result.setFileName("picture");
        result.setFileType("image/jpeg");
        result.setId(1L);
        return result;
    }

    private SavedPictureVM createSavedPictureVM() {
        SavedPictureVM result = new SavedPictureVM();
        result.setId(1L);
        result.setFileName("picture");
        result.setFileType("image/jpeg");
        return result;
    }
}