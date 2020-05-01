package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class BasicLocaleService {
    private Locale basicLocale = Locale.forLanguageTag(Constants.DEFAULT_LANGUAGE);

    public Locale getBasicLocale() {
        return basicLocale;
    }

    public void setBasicLocale(String basicLocale) {
        this.basicLocale = Locale.forLanguageTag(basicLocale);
    }
}
