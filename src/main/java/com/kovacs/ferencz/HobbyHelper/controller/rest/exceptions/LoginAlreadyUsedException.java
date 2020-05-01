package com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions;

public class LoginAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException(String message) {
        super(message, "userManagement", "userexists");
    }
}
