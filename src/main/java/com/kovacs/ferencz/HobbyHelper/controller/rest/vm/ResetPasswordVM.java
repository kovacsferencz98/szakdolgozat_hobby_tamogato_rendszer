package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import javax.validation.constraints.NotNull;

public class ResetPasswordVM {
    @NotNull
    private String mail;

    public ResetPasswordVM() {
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
