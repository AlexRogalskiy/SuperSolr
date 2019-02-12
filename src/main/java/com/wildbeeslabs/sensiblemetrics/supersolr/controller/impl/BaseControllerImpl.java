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

import com.google.common.collect.Lists;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.BaseController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.ResourceNotFoundException;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseService;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.formatMessage;

/**
 * Base controller implementation
 *
 * @param <E>  type of entity model
 * @param <T>  type of entity view model
 * @param <ID> type of entity identifier
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class BaseControllerImpl<E, T, ID extends Serializable> implements BaseController<E, T, ID> {

    @Autowired
    private MessageSource messageSource;

    protected List<? extends E> getAllItems() throws EmptyContentException {
        log.debug("Fetching all items");
        final List<? extends E> items = Lists.newArrayList(getService().findAll());
        if (items.isEmpty()) {
            throw new EmptyContentException(formatMessage(getMessageSource(), "error.no.content"));
        }
        return items;
    }

    protected E getItem(final ID id) {
        log.debug("Fetching item by ID: {}", id);
        final Optional<? extends E> item = getService().find(id);
        if (!item.isPresent()) {
            throw new ResourceNotFoundException(formatMessage(getMessageSource(), "error.no.item.id", id));
        }
        return item.get();
    }

    protected E createItem(final T itemDto,
                           final Class<? extends E> entityClass) {
        log.debug("Creating new item: {}", itemDto);
        final E itemEntity = MapperUtils.map(itemDto, entityClass);
//        if (getService().exists(itemEntity)) {
//            throw new ResourceAlreadyExistException(formatMessage(getMessageSource(), "error.already.exist.item"));
//        }
        getService().save(itemEntity);
        return itemEntity;
    }

    protected E updateItem(final ID id,
                           final T itemDto,
                           final Class<? extends E> entityClass) {
        log.info("Updating item by ID: {}, itemDto: {}", id, itemDto);
        final Optional<? extends E> currentItem = getService().find(id);
        if (!currentItem.isPresent()) {
            throw new ResourceNotFoundException(formatMessage(getMessageSource(), "error.no.item.id", id));
        }
        final E itemEntity = MapperUtils.map(itemDto, entityClass);
        getService().save(itemEntity);
        return currentItem.get();
    }

    protected E deleteItem(final ID id) {
        log.info("Deleting item by ID: {}", id);
        final Optional<? extends E> item = getService().find(id);
        if (!item.isPresent()) {
            throw new ResourceNotFoundException(formatMessage(getMessageSource(), "error.no.item.id", id));
        }
        getService().delete(item.get());
        return item.get();
    }

    protected void deleteItems(final List<? extends T> itemDtos,
                               final Class<? extends E> entityClass) {
        log.debug("Deleting items: {}", StringUtils.join(itemDtos, ", "));
        final List<? extends E> items = MapperUtils.mapAll(itemDtos, entityClass);
        getService().deleteAll(items);
    }

    protected void deleteAllItems() {
        log.debug("Deleting all items");
        getService().deleteAll();
    }

    /**
     * Custom base enum converter implementation {@link PropertyEditorSupport}
     *
     * @param <U> type of enum value
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    protected static class BaseEnumConverter<U extends Enum<U>> extends PropertyEditorSupport {

        private final Class<U> type;

        /**
         * Default enum converter constructor
         *
         * @param type - initial input class instance (@link Class)
         */
        public BaseEnumConverter(final Class<U> type) {
            this.type = type;
        }

        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            final U item = Enum.valueOf(getType(), text.toUpperCase());
            setValue(item);
        }
    }

    protected MessageSource getMessageSource() {
        return this.messageSource;
    }

    protected abstract BaseService<E, ID> getService();
}
