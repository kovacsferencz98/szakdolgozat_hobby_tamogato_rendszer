package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.UpdateLanguageKeyVM;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.BasicLocaleService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LanguageController {

    public static class LanguageKeyException extends RuntimeException {
        private LanguageKeyException(String message) {
            super(message);
        }
    }

    @Value("${app.allowed.languages}")
    private String[] allowedLanguages;

    private final Logger log = LoggerFactory.getLogger(LanguageController.class);

    private final UserService userService;

    private final BasicLocaleService basicLocaleService;

    private final MessageSource messageSource;

    public LanguageController(UserService userService, BasicLocaleService basicLocaleService, MessageSource messageSource) {
        this.userService = userService;
        this.basicLocaleService = basicLocaleService;
        this.messageSource = messageSource;
    }

    @PostMapping(path = "/language")
    public void updateLanguageKey(@RequestBody UpdateLanguageKeyVM updateLanguageKeyVM) {
        log.debug("Change language to: " + updateLanguageKeyVM.getLangKey());
        String langKey = updateLanguageKeyVM.getLangKey();
        if(!checkLanguageKey(langKey)) {
            throw new LanguageKeyException(messageSource.getMessage("language.invalid.key", null, LocaleUtil.getUserLocale()));
        }
        if(SecurityUtils.isAuthenticated()) {
            userService.changeLanguageKey(langKey);
        } else {
            basicLocaleService.setBasicLocale(langKey);
        }
    }

    private boolean checkLanguageKey(String key) {
        Optional<String> matchedKey = Arrays.stream(allowedLanguages).filter(lang -> lang.equals(key)).findFirst();
        return matchedKey.isPresent();
    }
}
