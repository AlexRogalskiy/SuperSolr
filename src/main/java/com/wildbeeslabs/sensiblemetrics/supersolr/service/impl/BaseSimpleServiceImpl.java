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

import com.wildbeeslabs.sensiblemetrics.supersolr.exception.ResourceNotFoundException;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseSimpleService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

/**
 * Custom base simple service implementation
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(BaseSimpleService.SERVICE_ID)
public class BaseSimpleServiceImpl<E, ID extends Serializable> extends SimpleSolrRepository<E, ID> implements BaseSimpleService<E, ID> {

    /**
     * Default base simple service constructor with initial solr operations instance {@link SolrOperations} and class type instance {@link Class}
     *
     * @param solrOperations - initial input solr operations instance {@link SolrOperations}
     * @param entityClass    - initial input class type instance {@link Class}
     */
    public BaseSimpleServiceImpl(final SolrOperations solrOperations, final Class<E> entityClass) {
        super(solrOperations, entityClass);
    }

    @Override
    public long count(final String searchTerm) {
        final String[] words = searchTerm.split(StringUtils.SPACE);
        final Criteria conditions = createSearchConditions(words);
        final SimpleQuery countQuery = new SimpleQuery(conditions);
        return getSolrOperations().count(countQuery);
    }

    private Criteria createSearchConditions(final String[] words) {
        Criteria conditions = new Criteria();
        for (final String word : words) {
            //if (Objects.isNull(conditions)) {
            //    conditions = new Criteria("title").contains(word)
            //            .or(new Criteria("description").contains(word));
            //} else {
            conditions = conditions
                    .or(new Criteria("title").contains(word))
                    .or(new Criteria("description").contains(word));
        }
        return conditions;
    }

    @Override
    public <S extends E> Iterable<S> saveAll(final Iterable<S> targets) {
        return super.save(targets);
    }

    @Override
    public Optional<E> findById(final ID id) {
        return this.find(id);
    }

    @Override
    public boolean existsById(final ID id) {
        return this.exists(id);
    }

    @Override
    public Iterable<E> findAllById(final Iterable<ID> ids) {
        return super.findAll(ids);
    }

    @Override
    public void deleteById(final ID id) {
        final Optional<E> item = this.find(id);
        if (!item.isPresent()) {
            throw new ResourceNotFoundException(String.format("ERROR: cannot find resource with id={%s}", id));
        }
        this.delete(item.get());
    }

    @Override
    public Optional<E> find(final ID id) {
        return Optional.ofNullable(super.findOne(id));
    }

    @Override
    public void deleteAll(final Iterable<? extends E> entities) {
        super.delete(entities);
    }
}