package com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException() {
        super(null, "Entity couldn't be found", Status.NOT_FOUND);
    }
}
