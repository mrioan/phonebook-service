package com.centralway.assessment.phonebook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Allow the controllers to return a 403 if an operation is forbidden.
 * The @ResponseStatus causes Spring MVC to return 403 instead of the usual 500.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class OperationNotAllowedException extends RuntimeException {

    public OperationNotAllowedException(String message) {
        super(message);
    }

}