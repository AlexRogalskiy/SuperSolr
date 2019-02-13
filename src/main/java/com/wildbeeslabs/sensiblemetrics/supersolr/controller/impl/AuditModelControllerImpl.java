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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.AuditModelController;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.AuditModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.AuditModelView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.AuditModelService;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

/**
 * Audit model controller implementation
 *
 * @param <E>  type of entity model
 * @param <T>  type of entity view model
 * @param <ID> type of entity identifier
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AuditModelControllerImpl<E extends AuditModel, T extends AuditModelView, ID extends Serializable> extends BaseControllerImpl<E, T, ID> implements AuditModelController<E, T, ID> {

    protected HttpHeaders getHeaders(final Page<?> page) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Elements", Long.toString(page.getTotalElements()));
        return headers;
    }

    protected abstract AuditModelService<E, ID> getService();
}