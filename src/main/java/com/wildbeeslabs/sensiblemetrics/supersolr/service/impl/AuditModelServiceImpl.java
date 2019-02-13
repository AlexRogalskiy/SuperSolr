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
package com.wildbeeslabs.sensiblemetrics.supersolr.service.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.AuditModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.AuditModelRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.AuditModelService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * Custom audit model service implementation
 *
 * @param <E>  type of entity model
 * @param <ID> type of entity identifier
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Transactional
public abstract class AuditModelServiceImpl<E extends AuditModel, ID extends Serializable> extends BaseServiceImpl<E, ID> implements AuditModelService<E, ID> {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    @Transactional(readOnly = true)
    public long count(final String searchTerm) {
        return getRepository().count(searchTerm);
    }

    protected SolrTemplate getSolrTemplate() {
        return this.solrTemplate;
    }

    protected abstract AuditModelRepository<E, ID> getRepository();
}