package com.wildbeeslabs.sensiblemetrics.supersolr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom resource exists exception
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Resource already exist")
public class ResourceAlreadyExistException extends RuntimeException {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 3180448258824085225L;

    public ResourceAlreadyExistException() {
    }

    public ResourceAlreadyExistException(String message) {
        super(message);
    }

    public ResourceAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public ResourceAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
