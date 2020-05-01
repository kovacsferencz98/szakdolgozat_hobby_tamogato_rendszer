package com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UnauthorizedRequest extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public UnauthorizedRequest(String message) {
        super(null, message, Status.BAD_REQUEST);
    }
}

