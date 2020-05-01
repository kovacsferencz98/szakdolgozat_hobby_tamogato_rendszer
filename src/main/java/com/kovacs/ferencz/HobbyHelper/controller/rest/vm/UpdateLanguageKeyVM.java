package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import javax.validation.constraints.Size;

public class UpdateLanguageKeyVM {
    @Size(min = 2, max = 10)
    private String langKey;

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }
}
