package com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions;

public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException(String message) {
        super(message, "userManagement", "emailexists");
    }
}
