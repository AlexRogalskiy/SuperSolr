/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.BaseController;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BaseService<E, ID> baseService;

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
    public ResponseEntity<?> update(final ID id, final E itemDto) {
        getService().saveOrUpdate(itemDto, null);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    protected ResponseEntity<?> update(final ID id, final E itemDto, final Class<? extends E> clazz) {
        getService().saveOrUpdate(itemDto, clazz);
        return new ResponseEntity<>(null, HttpStatus.OK);
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
    @EqualsAndHashCode
    @ToString
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

    protected BaseService<E, ID> getService() {
        return this.baseService;
    }
}
