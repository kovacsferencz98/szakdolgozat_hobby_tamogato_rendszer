package com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String DEFAULT_TYPE = "message.problem";
    public static final String CONSTRAINT_VIOLATION_TYPE = "constraint.violation";
    public static final String ENTITY_NOT_FOUND_TYPE = "entity.not.found";
    public static final String INVALID_PASSWORD_TYPE = "invalid.password";
    public static final String EMAIL_ALREADY_USED_TYPE = "email.already.used";
    public static final String LOGIN_ALREADY_USED_TYPE = "login.already.used";
    public static final String EMAIL_NOT_FOUND_TYPE ="email.not.found";

    private ErrorConstants() {
    }
}
