package com.wildbeeslabs.sensiblemetrics.supersolr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom service operation exception
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Request cannot be served")
public class ServiceException extends RuntimeException {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -4524093023203617565L;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
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
