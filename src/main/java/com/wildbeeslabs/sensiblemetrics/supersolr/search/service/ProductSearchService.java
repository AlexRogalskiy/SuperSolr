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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.service;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Shape;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.Collection;
import java.util.List;

/**
 * Custom product document search service declaration
 */
public interface ProductSearchService extends BaseDocumentSearchService<Product, String> {

    /**
     * Default service ID
     */
    String SERVICE_ID = "ProductSearchService";
    /**
     * Default collection ID
     */
    String COLLECTION_ID = "product";

    Page<? extends Product> findByName(final String name, final Pageable pageable);

    Page<? extends Product> findByNames(final String names, final Pageable pageable);

    Page<? extends Product> findByDescription(final String description, final Pageable pageable);

    Page<? extends Product> findByNameOrDescription(final String searchTerm, final Pageable pageable);

    HighlightPage<? extends Product> findByHighlightedNameIn(final Collection<String> names, final Pageable pageable);

    Page<? extends Product> findByShortDescription(final String description, final Pageable pageable);

    FacetPage<? extends Product> findByAutoCompleteNameFragment(final String fragment, final Pageable pageable);

    Page<? extends Product> findByCustomQuery(final String searchTerm, final Pageable request);

    Page<? extends Product> findByCategory(final String category, final Pageable pageable);

    Page<? extends Product> findByRating(final Integer rating, final Pageable pageable);

    List<? extends Product> findByLockType(final Integer lockType, final Sort sort);

    Page<? extends Product> findByLocation(final Point location, final Pageable pageable);

    List<? extends Product> findByLocationWithin(final String location, int distanceRange);

    Page<? extends Product> findByNameOrCategory(final String searchTerm, final Pageable pageable);

    List<? extends Product> findByLocationNear(final Shape shape);

    Page<? extends Product> findAvailable(final Pageable pageable);

    Page<? extends Product> findUnavailable(final Pageable pageable);

    Page<? extends Product> findAllProducts(final Pageable pageable);

    void updatePartial(final Product product);
}