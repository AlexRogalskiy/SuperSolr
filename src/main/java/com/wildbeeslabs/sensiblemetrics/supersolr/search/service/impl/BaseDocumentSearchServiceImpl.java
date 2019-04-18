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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.BaseDocument;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.repository.BaseDocumentSearchRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.iface.BaseDocumentSearchService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @link BaseDocument} search service implementation
 *
 * @param <E>  type of base document {@link BaseDocument}
 * @param <ID> type of base document identifier {@link Serializable}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Transactional
public abstract class BaseDocumentSearchServiceImpl<E extends BaseDocument<ID>, ID extends Serializable> extends AuditDocumentSearchServiceImpl<E, ID> implements BaseDocumentSearchService<E, ID> {

    @Override
    public void saveOrUpdate(final E target, final Class<? extends E> clazz) {
        log.info("Saving or updating target entity: {}", target);
        if (target.isNew()) {
            getEntityManager().persist(target);
        } else {
            final E targetEntity = getEntityManager().find(clazz, target.getId());
            if (Objects.isNull(targetEntity)) {
                getEntityManager().persist(target);
            } else {
                getEntityManager().merge(target);
            }
        }
    }

    @Transactional(readOnly = true)
    protected long count() {
        return getRepository().count();
    }

    protected Page<? extends E> findByCriteria(final String collection, final Criteria criteria, final Pageable pageable, final Class<? extends E> clazz) {
        final Query query = new SimpleQuery(criteria, pageable);
        query.setRows(DEFAULT_QUERY_ROWS_SIZE);
        return getSolrTemplate().queryForPage(collection, query, clazz);
    }

    protected Page<? extends E> findByQueryAndCriteria(final String collection, final String queryString, final Criteria criteria, final Pageable pageable, final Class<? extends E> clazz) {
        final Query query = new SimpleQuery(queryString);
        query.addFilterQuery(new SimpleQuery(criteria));
        query.setPageRequest(pageable);
        query.setRows(DEFAULT_QUERY_ROWS_SIZE);
        return getSolrTemplate().queryForPage(collection, query, clazz);
    }

    protected Collection<String> tokenize(final String searchTerm) {
        final String[] searchTerms = StringUtils.split(searchTerm, DEFAULT_SEARСH_TERM_DELIMITER);
        return Arrays.stream(searchTerms)
            .filter(StringUtils::isNotEmpty)
            .map(term -> DEFAULT_IGNORED_CHARS_PATTERN.matcher(term).replaceAll(DEFAULT_SEARСH_TERM_REPLACEMENT))
            .collect(Collectors.toList());
    }

    protected List<? extends E> search(final String collection, final Query query, final Class<? extends E> clazz) {
        return getSolrTemplate().queryForPage(collection, query, clazz).getContent();
    }

    protected Page<? extends E> findByQuery(final String collection, final Query query, final Class<? extends E> clazz) {
        return getSolrTemplate().queryForPage(collection, query, clazz);
    }

    protected FacetPage<? extends E> findByFacetQuery(final String collection, final FacetQuery facetQuery, final Class<? extends E> clazz) {
        return getSolrTemplate().queryForFacetPage(collection, facetQuery, clazz);
    }

    /**
     * Returns {@link BaseDocumentSearchRepository} repository
     *
     * @return {@link BaseDocumentSearchRepository} repository
     */
    protected abstract BaseDocumentSearchRepository<E, ID> getRepository();
}
