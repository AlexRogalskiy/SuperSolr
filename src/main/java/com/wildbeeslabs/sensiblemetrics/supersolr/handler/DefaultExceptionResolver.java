/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.sensiblemetrics.supersolr.handler;

import com.wildbeeslabs.sensiblemetrics.supersolr.annotation.ApiVersion;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.*;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.entity.ExceptionView;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Default {@link ResponseEntityExceptionHandler} implementation
 */
@RestControllerAdvice(annotations = ApiVersion.class)
public class DefaultExceptionResolver {

    @ExceptionHandler({ResourceAlreadyExistException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handleResourceAlreadyExistException(final HttpServletRequest req, final ResourceAlreadyExistException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleBadRequestException(final HttpServletRequest req, final BadRequestException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<?> handleResourceNotFoundException(final HttpServletRequest req, final ResourceNotFoundException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmptyContentException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected ResponseEntity<?> handleEmptyContentException(final HttpServletRequest req, final EmptyContentException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ResponseEntity<?> handleTypeMismatchException(final HttpServletRequest req, final TypeMismatchException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, DataIntegrityViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleMethodArgumentException(final HttpServletRequest req, final MethodArgumentNotValidException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({javax.validation.ConstraintViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleConstraintViolationException(final HttpServletRequest req, final javax.validation.ConstraintViolationException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingPathVariableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleMissingPathVariableException(final HttpServletRequest req, final MissingPathVariableException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    protected ResponseEntity<?> handleHttpRequestMethodNotSupportedException(final HttpServletRequest req, final HttpRequestMethodNotSupportedException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleHttpMessageNotReadableException(final HttpServletRequest req, final HttpMessageNotReadableException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ResponseEntity<?> handleHttpMediaTypeNotSupportedException(final HttpServletRequest req, final HttpMediaTypeNotSupportedException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({ServiceException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<?> handleServiceException(final HttpServletRequest req, final ServiceException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<?> handleAccessDeniedException(final HttpServletRequest req, final AccessDeniedException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ResponseEntity<?> handleHttpMediaTypeNotAcceptableException(final HttpServletRequest req, final HttpMediaTypeNotAcceptableException ex) {
        return errorResponse(req.getContextPath(), ex.getLocalizedMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    private ResponseEntity<?> errorResponse(final String path, final String message, final HttpStatus status) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionView.builder()
                .path(path)
                .message(message)
                .code(status.value())
                .build());
    }
}
