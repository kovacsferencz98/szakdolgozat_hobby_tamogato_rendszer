package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


class BasicLocaleServiceTest {

    private BasicLocaleService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BasicLocaleService();
    }

    @Test
    void getBasicLocaleShouldReturnDefaultIfNotSet() {
        //GIVEN
        //WHEN
        Locale result = underTest.getBasicLocale();
        //THEN
        assertEquals(Locale.forLanguageTag(Constants.DEFAULT_LANGUAGE), result);
    }

    @Test
    void setBasicLocaleShouldSetLocaleField() {
        //GIVEN
        Locale locale = Locale.forLanguageTag("hu");
        //WHEN
        underTest.setBasicLocale("hu");
        //THEN
        assertEquals(locale, underTest.getBasicLocale());
    }
}