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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableOrder;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.repository.OrderSearchRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.OrderSearchService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom order search service implementation {@link OrderSearchService}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(OrderSearchService.SERVICE_ID)
@Transactional
public class OrderSearchServiceImpl extends BaseDocumentSearchServiceImpl<Order, String> implements OrderSearchService {

    @Autowired
    private OrderSearchRepository orderSearchRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByDescription(final String searchTerm, final Pageable page) {
        return getRepository().findByDescription(searchTerm, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByTitle(final String searchTerm, final Pageable page) {
        return getRepository().findByTitle(searchTerm, page);
    }

    @Override
    @Transactional(readOnly = true)
    public HighlightPage<? extends Order> find(final String collection, final String searchTerm, final Pageable page) {
        final Criteria fileIdCriteria = new Criteria(SearchableOrder.ID_FIELD_NAME).boost(2).is(searchTerm);
        final Criteria descriptionCriteria = new Criteria(SearchableOrder.DESCRIPTION_FIELD_NAME).fuzzy(searchTerm);
        final SimpleHighlightQuery query = new SimpleHighlightQuery(fileIdCriteria.or(descriptionCriteria), page);
        query.setHighlightOptions(new HighlightOptions()
            .setSimplePrefix("<highlight>")
            .setSimplePostfix("</highlight>")
            .addField(SearchableOrder.ID_FIELD_NAME, SearchableOrder.DESCRIPTION_FIELD_NAME));
        return getSolrTemplate().queryForHighlightPage(collection, query, Order.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByQuery(final String collection, final Query query) {
        return this.findByQuery(collection, query, Order.class);
    }

    @Override
    @Transactional(readOnly = true)
    public FacetPage<? extends Order> findByFacetQuery(final String collection, final FacetQuery facetQuery) {
        return this.findByFacetQuery(collection, facetQuery, Order.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByQueryAndCriteria(final String collection, final Criteria criteria, final Pageable pageable) {
        return this.findByQueryAndCriteria(collection, criteria, pageable, Order.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByQueryAndCriteria(final String collection, final String queryString, final Criteria criteria, final Pageable pageable) {
        return this.findByQueryAndCriteria(collection, queryString, criteria, pageable, Order.class);
    }

    protected Criteria titleOrDescriptionCriteria(final String searchTerm) {
        final String[] searchTerms = StringUtils.split(searchTerm, DEFAULT_SEARСH_TERM_DELIMITER);
        Criteria criteria = new Criteria();
        for (final String term : searchTerms) {
            criteria = criteria
                .and(new Criteria(SearchableOrder.TITLE_FIELD_NAME).contains(term))
                .or(new Criteria(SearchableOrder.DESCRIPTION_FIELD_NAME).contains(term));
        }
        return criteria.and(new Criteria(DEFAULT_DOCTYPE).is(SearchableOrder.CORE_ID));
    }

    @Override
    protected OrderSearchRepository getRepository() {
        return this.orderSearchRepository;
    }
}
