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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableProduct;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.repository.ProductSearchRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.ProductSearchService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Custom product search service implementation {@link Product}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(ProductSearchService.SERVICE_ID)
@Transactional
public class ProductSearchServiceImpl extends BaseDocumentSearchServiceImpl<Product, String> implements ProductSearchService {

    @Autowired
    private ProductSearchRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByName(final String name, final Pageable pageable) {
        return getRepository().findByName(name, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByNames(final String names, final Pageable pageable) {
        if (StringUtils.isEmpty(names)) {
            return getRepository().findAll(pageable);
        }
        return getRepository().findByNames(tokenize(names), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByDescription(final String description, final Pageable pageable) {
        return getRepository().findByDescription(description, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public FacetPage<? extends Product> autoCompleteNameFragment(final String fragment, final Pageable pageable) {
        if (StringUtils.isEmpty(fragment)) {
            return new SolrResultPage<>(Collections.emptyList());
        }
        return getRepository().findByNameStartsWith(tokenize(fragment), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByCustomQuery(final String searchTerm, final PageRequest request) {
        return getRepository().findByCustomQuery(searchTerm, request);
    }

    @Override
    @Transactional(readOnly = true)
    public HighlightPage<? extends Product> find(final String searchTerm, final Pageable page) {
        final Criteria fileIdCriteria = new Criteria(SearchableProduct.ID_FIELD_NAME).boost(2).is(searchTerm);
        final Criteria pageTitleCriteria = new Criteria(SearchableProduct.PAGE_TITLE_FIELD_NAME).boost(2).is(searchTerm);
        final Criteria nameCriteria = new Criteria(SearchableProduct.NAME_FIELD_NAME).fuzzy(searchTerm);
        final SimpleHighlightQuery query = new SimpleHighlightQuery(fileIdCriteria.or(pageTitleCriteria).or(nameCriteria), page);
        query.setHighlightOptions(new HighlightOptions()
                .setSimplePrefix("<strong>")
                .setSimplePostfix("</strong>")
                .addField(SearchableProduct.ID_FIELD_NAME, SearchableProduct.PAGE_TITLE_FIELD_NAME, SearchableProduct.NAME_FIELD_NAME));
        return getSolrTemplate().queryForHighlightPage(query, Product.class);
    }

    protected Criteria getCriteria(final String searchTerm) {
        Criteria conditions = new Criteria();
        for (final String term : searchTerm.split(DEFAULT_SEARСH_TERM_DELIMITER)) {
            conditions = conditions
                    .or(new Criteria(SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME).contains(term))
                    .or(new Criteria(SearchableProduct.LONG_DESCRIPTION_FIELD_NAME).contains(term))
                    .or(new Criteria(SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME).contains(term))
                    .or(new Criteria(SearchableProduct.RECOMMENDED_PRICE_FIELD_NAME).contains(term));
        }
        return conditions;
    }

    protected ProductSearchRepository getRepository() {
        return this.productRepository;
    }
}
