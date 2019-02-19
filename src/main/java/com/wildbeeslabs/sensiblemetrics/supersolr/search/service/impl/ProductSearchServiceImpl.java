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
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Shape;
import org.springframework.data.solr.UncategorizedSolrException;
import org.springframework.data.solr.core.geo.GeoConverters;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Custom product search service implementation {@link ProductSearchService}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(ProductSearchService.SERVICE_ID)
@Transactional
public class ProductSearchServiceImpl extends BaseDocumentSearchServiceImpl<Product, String> implements ProductSearchService {

    @Autowired
    private ProductSearchRepository productSearchRepository;

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
        return getRepository().findByNameIn(tokenize(names), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByDescription(final String description, final Pageable pageable) {
        return getRepository().findByDescription(description, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByNameOrDescription(final String searchTerm, final Pageable pageable) {
        return getRepository().findByNameOrDescription(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public HighlightPage<? extends Product> findByHighlightedNameIn(final Collection<String> names, final Pageable pageable) {
        if (CollectionUtils.isEmpty(names)) {
            return new SolrResultPage<>(Collections.emptyList());
        }
        return getRepository().findByNameIn(names, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public FacetPage<? extends Product> findByAutoCompleteNameFragment(final String fragment, final Pageable pageable) {
        if (StringUtils.isEmpty(fragment)) {
            return new SolrResultPage<>(Collections.emptyList());
        }
        return getRepository().findByNameStartingWith(fragment, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByShortDescription(final String searchTerm, final Pageable pageable) {
        return getRepository().findByShortDescription(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByCategory(final String category, final Pageable pageable) {
        return getRepository().findByCategory(category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByRating(final Integer popularity, final Pageable pageable) {
        return getRepository().findByRating(popularity, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<? extends Product> findByLockType(final Integer lockType, final Sort sort) {
        return getRepository().findByLockType(lockType, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByLocation(final Point location, final Distance distance, final Pageable pageable) {
        return getRepository().findByLocation(location, distance, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<? extends Product> findByLocationNear(final Point location, final Distance distance) {
        return getRepository().findByLocationNear(location, distance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<? extends Product> findByLocationWithin(final String location, final Distance distance) {
        final org.springframework.data.geo.Point point = GeoConverters.StringToPointConverter.INSTANCE.convert(location);
        return getRepository().findByLocationWithin(new Point(point.getX(), point.getY()), distance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByNameOrCategory(final String searchTerm, final Pageable pageable) {
        return getRepository().findByNameOrCategory(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<? extends Product> findByLocationNear(final Shape shape) {
        return getRepository().findByLocationNear(shape);
    }

    @Override
    @Transactional(readOnly = true)
    public GeoResults<? extends Product> findByGeoLocationNear(final Point location, final Distance distance) {
        return getRepository().findByGeoLocationNear(location, distance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findAvailable(final Pageable pageable) {
        return getRepository().findByAvailableTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findUnavailable(final Pageable pageable) {
        return getRepository().findByAvailableFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findAllProducts(final Pageable pageable) {
        return getRepository().findAllProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findAvailableProductsByName(final String name, final Pageable pageable) {
        return getRepository().findByAvailableTrueAndNameStartingWith(name, pageable);
    }

    @Override
    @Transactional(rollbackFor = {UncategorizedSolrException.class})
    public void updatePartial(final Product product) {
        final PartialUpdate productUpdate = new PartialUpdate(SearchableProduct.ID_FIELD_NAME, product.getId());
        productUpdate.add(SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME, product.getShortDescription());
        productUpdate.add(SearchableProduct.LONG_DESCRIPTION_FIELD_NAME, product.getLongDescription());
        productUpdate.add(SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME, product.getPriceDescription());
        productUpdate.add(SearchableProduct.CATALOG_NUMBER_FIELD_NAME, product.getCatalogNumber());
        productUpdate.add(SearchableProduct.PAGE_TITLE_FIELD_NAME, product.getPageTitle());
        productUpdate.add(SearchableProduct.AVAILABLE_FIELD_NAME, product.isAvailable());
        productUpdate.add(SearchableProduct.LOCATION_FIELD_NAME, product.getLocation());
        productUpdate.add(SearchableProduct.CATEGORIES_FIELD_NAME, product.getCategories());
        productUpdate.add(SearchableProduct.MAIN_CATEGORIES_FIELD_NAME, product.getMainCategories());
        productUpdate.add(SearchableProduct.PRICE_FIELD_NAME, product.getPrice());
        productUpdate.add(SearchableProduct.RECOMMENDED_PRICE_FIELD_NAME, product.getRecommendedPrice());
        productUpdate.add(SearchableProduct.ATTRIBUTES_FIELD_NAME, product.getAttributes());
        productUpdate.add(SearchableProduct.RATING_FIELD_NAME, product.getRating());
        getSolrTemplate().saveBean(COLLECTION_ID, productUpdate);
        getSolrTemplate().commit(COLLECTION_ID);
    }

    @Override
    @Transactional(readOnly = true)
    @ApiModelProperty(name = "internal", access = "limited")
    public HighlightPage<? extends Product> find(final String searchTerm, final Pageable page) {
        final Criteria fileIdCriteria = new Criteria(SearchableProduct.ID_FIELD_NAME).boost(2).is(searchTerm);
        final Criteria pageTitleCriteria = new Criteria(SearchableProduct.PAGE_TITLE_FIELD_NAME).boost(2).is(searchTerm);
        final Criteria nameCriteria = new Criteria(SearchableProduct.NAME_FIELD_NAME).fuzzy(searchTerm);
        final SimpleHighlightQuery query = new SimpleHighlightQuery(fileIdCriteria.or(pageTitleCriteria).or(nameCriteria), page);
        query.setHighlightOptions(new HighlightOptions()
            .setSimplePrefix("<highlight>")
            .setSimplePostfix("</highlight>")
            .addField(SearchableProduct.ID_FIELD_NAME, SearchableProduct.PAGE_TITLE_FIELD_NAME, SearchableProduct.NAME_FIELD_NAME));
        return getSolrTemplate().queryForHighlightPage(COLLECTION_ID, query, Product.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByQuery(final String collection, final Query query) {
        return this.findByQuery(collection, query, Product.class);
    }

    @Override
    @Transactional(readOnly = true)
    public FacetPage<? extends Product> findByFacetQuery(final String collection, final FacetQuery facetQuery) {
        return this.findByFacetQuery(collection, facetQuery, Product.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByQueryAndCriteria(final String collection, final Criteria criteria, final Pageable pageable) {
        return this.findByQueryAndCriteria(collection, criteria, pageable, Product.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Product> findByQueryAndCriteria(final String collection, final String queryString, final Criteria criteria, final Pageable pageable) {
        return this.findByQueryAndCriteria(collection, queryString, criteria, pageable, Product.class);
    }

    protected Criteria nameOrDescriptionCriteria(final String searchTerm) {
        final String[] searchTerms = StringUtils.split(searchTerm, DEFAULT_SEARСH_TERM_DELIMITER);
        Criteria criteria = new Criteria();
        for (final String term : searchTerms) {
            criteria = criteria
                .and(new Criteria(SearchableProduct.NAME_FIELD_NAME).contains(term))
                .or(new Criteria(SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME).contains(term))
                .or(new Criteria(SearchableProduct.LONG_DESCRIPTION_FIELD_NAME).contains(term))
                .or(new Criteria(SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME).contains(term))
                .or(new Criteria(SearchableProduct.RECOMMENDED_PRICE_FIELD_NAME).contains(term));
        }
        return criteria.and(new Criteria(DEFAULT_DOCTYPE).is(SearchableProduct.CORE_ID));
    }

    protected ProductSearchRepository getRepository() {
        return this.productSearchRepository;
    }
}
