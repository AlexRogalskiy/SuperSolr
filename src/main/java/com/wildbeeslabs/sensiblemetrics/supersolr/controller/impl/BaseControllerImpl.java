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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.BaseController;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;

/**
 * Base controller implementation
 *
 * @param <E>
 * @param <ID>
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class BaseControllerImpl<E, ID extends Serializable> implements BaseController<E, ID> {

    @Override
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(getService().findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getById(final ID id) {
        return new ResponseEntity<>(getService().find(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> create(final E itemDto) {
        getService().save(itemDto);
        UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(ucBuilder.path(request.getRequestURI() + "/{id}").buildAndExpand(itemEntity.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> delete(final ID id) {
        //E itemEntity = getService().deleteItem(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteAll() {
        //getService().deleteAllItems();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Custom base enum converter implementation
     *
     * @param <U>
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    protected static class BaseEnumConverter<U extends Enum<U>> extends PropertyEditorSupport {

        private final Class<U> type;

        /**
         * Default enum converter constructor
         *
         * @param type
         */
        public BaseEnumConverter(final Class<U> type) {
            this.type = type;
        }

        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            final U item = Enum.valueOf(this.type, text.toUpperCase());
            setValue(item);
        }
    }

    protected abstract BaseService<E, ID> getService();
}
