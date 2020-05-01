package com.kovacs.ferencz.HobbyHelper.service.util;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.BasicLocaleService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

/**
 * Utility class for obtaining locale
 */
@Component
public class LocaleUtil {

    private static UserService userService;
    private static BasicLocaleService basicLocaleService;

    @Autowired
    public LocaleUtil(UserService userService, BasicLocaleService basicLocaleService){
        LocaleUtil.userService = userService;
        LocaleUtil.basicLocaleService = basicLocaleService;
    }

    /**
     * Returns the user's selected language or the default language if the user isn't loged in.
     * @return the locale to be used for i18n
     */
    public static Locale getUserLocale() {
        Optional<String> username = SecurityUtils.getCurrentUserLogin();
        Locale locale = basicLocaleService.getBasicLocale();
        if(username.isPresent()) {
            Optional<User> user = userService.getUserWithAuthoritiesByLogin(username.get());
            if(user.isPresent()) {
                locale = Locale.forLanguageTag(user.get().getLangKey());
            }
        }
        return locale;
    }
}
