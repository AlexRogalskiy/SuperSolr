package com.wildbeeslabs.sensiblemetrics.supersolr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom empty content exception
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Empty content")
public class EmptyContentException extends Exception {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -4928833819624966509L;

    public EmptyContentException() {
    }

    public EmptyContentException(String message) {
        super(message);
    }

    public EmptyContentException(Throwable cause) {
        super(cause);
    }

    public EmptyContentException(String message, Throwable cause) {
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
