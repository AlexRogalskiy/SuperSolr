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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * Base controller declaration
 *
 * @param <E>  type of entity model
 * @param <T>  type of entity view model
 * @param <ID> type of entity identifier
 * @author Alex
 * @version 1.0.0
 * @since 2017-08-08
 */
public interface BaseController<E, T, ID extends Serializable> {

    /**
     * Default not allowed response
     */
    ResponseEntity<?> DEFAULT_NOT_ALLOWED_RESPONSE = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);

    /**
     * Returns response entity body {@link ResponseEntity}
     *
     * @return response body {@link ResponseEntity}
     */
    default ResponseEntity<?> getAll() {
        return DEFAULT_NOT_ALLOWED_RESPONSE;
    }

    /**
     * Returns response entity body {@link ResponseEntity} by ID
     *
     * @param id - initial input entity identifier
     * @return response body {@link ResponseEntity}
     */
    default ResponseEntity<?> getById(final ID id) {
        return DEFAULT_NOT_ALLOWED_RESPONSE;
    }

    /**
     * Returns created response entity body {@link ResponseEntity}
     *
     * @param itemDto - initial input entity dto
     * @return response body {@link ResponseEntity}
     */
    default ResponseEntity<?> create(final T itemDto) {
        return DEFAULT_NOT_ALLOWED_RESPONSE;
    }

    /**
     * Returns updated response entity body {@link ResponseEntity}
     *
     * @param id      - initial input entity identifier
     * @param itemDto - initial input entity dto
     * @return response body {@link ResponseEntity}
     */
    default ResponseEntity<?> update(final ID id, final T itemDto) {
        return DEFAULT_NOT_ALLOWED_RESPONSE;
    }

    /**
     * Returns deleted response entity body {@link ResponseEntity}
     *
     * @param id - initial input entity identifier
     * @return response body {@link ResponseEntity}
     */
    default ResponseEntity<?> delete(final ID id) {
        return DEFAULT_NOT_ALLOWED_RESPONSE;
    }

    /**
     * Returns deleted response entity body {@link ResponseEntity}
     *
     * @return response body {@link ResponseEntity}
     */
    default ResponseEntity<?> deleteAll() {
        return DEFAULT_NOT_ALLOWED_RESPONSE;
    }
}