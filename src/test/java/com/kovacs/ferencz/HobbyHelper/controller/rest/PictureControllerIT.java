package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.SavedPictureVM;
import com.kovacs.ferencz.HobbyHelper.domain.Picture;
import com.kovacs.ferencz.HobbyHelper.repository.PictureRepository;
import com.kovacs.ferencz.HobbyHelper.service.PictureService;
import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.PictureMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;

import java.util.List;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PictureControllerIT {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private Validator validator;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc restLocationMockMvc;

    private Picture picture;

    @BeforeEach
    public void setup() {
        clearDatabase();
        picture = initPicure();
        MockitoAnnotations.initMocks(this);
        final PictureController pictureController = new PictureController(pictureService);
        this.restLocationMockMvc = MockMvcBuilders.standaloneSetup(pictureController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    @Transactional
    @Test
    public void testUploadPicture() throws Exception {
        //GIVEN
        MockMultipartFile file = new MockMultipartFile("picture", "picture.jpg", "image/jpeg", new byte[]{1});
        int databaseSizeBeforeCreate = pictureRepository.findAll().size();
        //WHEN
        restLocationMockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadPicture")
                .file("file", file.getBytes())
        ).andExpect(status().isCreated());
        //THEN
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeCreate + 1);
        Picture testPicture = pictureList.get(pictureList.size() - 1);
        assertArrayEquals(picture.getData(), testPicture.getData());
    }

    private void clearDatabase() {
        pictureRepository.deleteAll();
        pictureRepository.flush();
    }

    private PictureDTO initPictureDTO() {
        PictureDTO result = new PictureDTO();
        result.setData(new byte[]{1});
        result.setFileName("picture");
        result.setFileType("image/jpeg");
        result.setId(null);
        return result;
    }

    private Picture initPicure() {
        Picture result = new Picture();
        result.setId(null);
        result.setData(new byte[]{1});
        result.setFileName("picture.png");
        result.setFileType("image/png");
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
