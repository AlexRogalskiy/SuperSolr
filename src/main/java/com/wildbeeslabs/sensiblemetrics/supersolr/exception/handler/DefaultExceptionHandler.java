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
package com.wildbeeslabs.sensiblemetrics.supersolr.exception.handler;

import com.wildbeeslabs.sensiblemetrics.supersolr.exception.*;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.ExceptionView;
import lombok.extern.slf4j.Slf4j;
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
import org.hibernate.exception.ConstraintViolationException;

/**
 * Custom default exception handler implementation
 */
@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {//extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceAlreadyExistException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final ResourceAlreadyExistException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final BadRequestException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmptyContentException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final EmptyContentException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.NO_CONTENT.value())
                .message(ex.getMessage())
                .build(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final TypeMismatchException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message(ex.getMessage())
                .build(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, DataIntegrityViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({javax.validation.ConstraintViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final javax.validation.ConstraintViolationException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingPathVariableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final MissingPathVariableException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message(ex.getMessage())
                .build(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final HttpMessageNotReadableException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final HttpMediaTypeNotSupportedException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message(ex.getMessage())
                .build(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({ServiceException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final ServiceException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final AccessDeniedException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ResponseEntity<?> handle(final HttpServletRequest req, final HttpMediaTypeNotAcceptableException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionView
                .builder()
                .path(req.getRequestURI().substring(req.getContextPath().length()))
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message(ex.getMessage())
                .build(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
