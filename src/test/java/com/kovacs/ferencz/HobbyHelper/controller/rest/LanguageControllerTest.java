package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.UpdateLanguageKeyVM;
import com.kovacs.ferencz.HobbyHelper.service.BasicLocaleService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.EventTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LanguageControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private BasicLocaleService basicLocaleService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private LanguageController underTest;

    private UpdateLanguageKeyVM languageKeyVM;

    @BeforeEach
    void setUp() {
        languageKeyVM = new UpdateLanguageKeyVM();
        languageKeyVM.setLangKey(Constants.DEFAULT_LANGUAGE);
    }

    @Test
    void updateLanguageKeyShouldThrowExceptionIfInvalidLanguageKey() {
        //GIVEN
        languageKeyVM.setLangKey("INVALID");
        //WHEN
        //THEN
        assertThrows(LanguageController.LanguageKeyException.class, () -> {
            underTest.updateLanguageKey(languageKeyVM);
        });
    }

    @Test
    @WithAnonymousUser
    void updateLanguageKeyShouldSetBasicKeyIfNotLoggedIn() {
        //GIVEN - in setup
        //WHEN
        underTest.updateLanguageKey(languageKeyVM);
        //THEN
        verify(basicLocaleService).setBasicLocale(languageKeyVM.getLangKey());
    }

    @Test
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    void updateLanguageKeyShouldSetUserKeyIfLoggedIn() {
        //GIVEN - in setup
        //WHEN
        underTest.updateLanguageKey(languageKeyVM);
        //THEN
        verify(userService).changeLanguageKey(languageKeyVM.getLangKey());
    }
}