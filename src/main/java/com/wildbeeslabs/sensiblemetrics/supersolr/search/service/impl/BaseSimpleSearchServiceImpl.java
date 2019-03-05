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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.service.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.exception.ResourceNotFoundException;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.BaseSimpleSearchService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * Base simple {@link BaseSimpleSearchService} implementation
 *
 * @param <E>  type of document model
 * @param <ID> type of document identifier {@link Serializable}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
//@Service(BaseSimpleSearchService.SERVICE_ID)
@Transactional
public class BaseSimpleSearchServiceImpl<E, ID extends Serializable> extends SimpleSolrRepository<E, ID> implements BaseSimpleSearchService<E, ID> {

    /**
     * Default base simple service constructor with initial solr operations instance {@link SolrOperations} and class type instance {@link Class}
     *
     * @param solrOperations - initial input solr operations instance {@link SolrOperations}
     * @param entityClass    - initial input class type instance {@link Class}
     */
    public BaseSimpleSearchServiceImpl(final SolrOperations solrOperations, final Class<E> entityClass) {
        super(solrOperations, entityClass);
    }

    /**
     * Return number of matched results by search term {@link String}
     *
     * @param searchTerm - initial input search term {@link String}
     * @return number of matched results by search term
     */
    @Override
    @Transactional(readOnly = true)
    public long count(final String collection, final String searchTerm, final Criteria criteria) {
        final SimpleQuery countQuery = new SimpleQuery(criteria);
        return getSolrOperations().count(collection, countQuery);
    }

    /**
     * Returns search criteria {@link Criteria} by input array of words / matched fields
     *
     * @param words  - initial input array of words to be matched with
     * @param fields - initial input array of fields to be matched by
     * @return search criteria {@link Criteria}
     */
    protected Criteria getSearchConditions(final String[] words, final String[] fields) {
        Criteria criteria = new Criteria();
        for (final String word : words) {
            for (final String field : fields) {
                criteria = criteria.or(new Criteria(field).contains(word));
            }
        }
        return criteria;
    }

    /**
     * Returns iterable collection of saved entities {@link Iterable} by input collection of entities to be saved {@link Iterable}
     *
     * @param target - initial input iterable collection of entities to be saved {@link Iterable}
     * @param <S>
     * @return iterable collection of saved entities {@link Iterable}
     */
    @Override
    public <S extends E> Iterable<S> saveAll(final Iterable<S> target) {
        return super.saveAll(target);
    }

    /**
     * Returns optional of matched wrapped entity {@link Optional} by input ID
     *
     * @param id - initial input entity ID to be searched by
     * @return optional of matched entity {@link Optional}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<E> findById(final ID id) {
        return this.find(id);
    }

    /**
     * Returns binary flag based on entities existence by input entity ID
     *
     * @param id - initial input entity ID to be searched by
     * @return true - if entity exists, false - otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(final ID id) {
        return this.exists(id);
    }

    /**
     * Returns iterable collection of searchable entities {@link Iterable} by input collection of entity IDs {@link Iterable}
     *
     * @param ids - initial input collection of entity IDs {@link Iterable}
     * @return iterable collection of searchable entities {@link Iterable}
     */
    @Override
    @Transactional(readOnly = true)
    public Iterable<E> findAllById(final Iterable<ID> ids) {
        return super.findAllById(ids);
    }

    /**
     * Removes entity by input ID
     *
     * @param id - initial input entity ID to be removed by
     */
    @Override
    public void deleteById(final ID id) {
        final Optional<E> entity = this.find(id);
        if (!entity.isPresent()) {
            throw new ResourceNotFoundException(String.format("ERROR: cannot find resource with id={%s}", id));
        }
        this.delete(entity.get());
    }

    @Override
    public Iterable<? extends E> findAll(Iterable<ID> ids) {
        return null;
    }

    /**
     * Returns optional of matched wrapped entity by input ID
     *
     * @param id - initial input entity ID to be removed by
     * @return optional of matched entity {@link Optional}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<E> find(final ID id) {
        return super.findById(id);
    }

    @Override
    public boolean exists(ID id) {
        return false;
    }

    @Override
    public <S extends E> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    /**
     * Removes iterable collection of entities {@link Iterable} by input collection {@link Iterable}
     *
     * @param entities - initial input collection of entities {@link Iterable}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteAll(final Iterable<? extends E> entities) {
        super.delete((E) entities);
    }
}
