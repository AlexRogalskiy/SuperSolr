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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.repository;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Custom product search repository declaration
 */
//@Repository
public interface ProductSearchRepository extends BaseDocumentSearchRepository<Product, String> {

    @Highlight(prefix = "<highlight>", postfix = "</highlight>")
    @Query(fields = {
            SearchableProduct.ID_FIELD_NAME,
            SearchableProduct.NAME_FIELD_NAME,
            SearchableProduct.PRICE_FIELD_NAME,
            SearchableProduct.ATTRIBUTES_FIELD_NAME,
            SearchableProduct.AVAILABLE_FIELD_NAME,
            SearchableProduct.PAGE_TITLE_FIELD_NAME
    }, defaultOperator = org.springframework.data.solr.core.query.Query.Operator.AND)
    HighlightPage<? extends Product> findByHighlightedMultiQuery(final Collection<String> values, final Pageable pageable);

    @Highlight(prefix = "<highlight>", postfix = "</highlight>")
    @Query(fields = {
            SearchableProduct.ID_FIELD_NAME,
            SearchableProduct.NAME_FIELD_NAME,
            SearchableProduct.ATTRIBUTES_FIELD_NAME,
            SearchableProduct.CATEGORIES_FIELD_NAME,
            SearchableProduct.RATING_FIELD_NAME,
            SearchableProduct.LOCATION_FIELD_NAME
    }, defaultOperator = org.springframework.data.solr.core.query.Query.Operator.AND)
    HighlightPage<? extends Product> findByHighlightedNameIn(final Collection<String> names, final Pageable page);

    @Query(fields = {
            SearchableProduct.NAME_FIELD_NAME,
            SearchableProduct.PAGE_TITLE_FIELD_NAME,
            SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME,
            SearchableProduct.LONG_DESCRIPTION_FIELD_NAME,
            SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME
    }, defaultOperator = org.springframework.data.solr.core.query.Query.Operator.OR)
    Page<? extends Product> findByCustomQuery(@Boost(2) final String searchTerm, final Pageable pageable);

    @Query("name:*?0* AND doctype:product")
    Page<? extends Product> findByNameStartsWith(final String name, final Pageable pageable);

    Page<? extends Product> findByName(final String name, final Pageable pageable);

    Page<? extends Product> findByNames(final Collection<String> names, final Pageable pageable);

    @Query("doctype:product")
    Page<? extends Product> findAllProducts(final Pageable pageable);

    @Query(name = "Product.findByDescription")
    Page<? extends Product> findByDescription(final String description, final Pageable pageable);

    @Facet(fields = {SearchableProduct.NAME_FIELD_NAME})
    FacetPage<? extends Product> findByNameStartsWith(final Collection<String> fragments, final Pageable pageable);

    @Query(name = "Product.findByNameOrDescription")
    Page<? extends Product> findByNameOrDescription(@Boost(2) final String searchTerm, final Pageable pageable);

    @Query("categories:*?0* AND doctype:product")
    Page<? extends Product> findByCategory(final String category, final Pageable pageable);

    @Query("(name:*?0* OR categories:*?0*) AND doctype:product")
    List<? extends Product> findByAnnotatedQuery(final String searchTerm, final Sort sort);

    @Query("inStock:true AND doctype:product")
    List<? extends Product> findAvailableProducts();

    @Query(name = "Product.findByNameOrCategory")
    List<? extends Product> findByNameOrCategory(final String searchTerm, final Sort sort);

    Page<? extends Product> findByRating(final Integer popularity, final Pageable pageable);

    Page<? extends Product> findByRatingGreaterThanEqual(final Integer popularity, final Pageable page);

    Page<? extends Product> findByLockType(final Integer lockType, final Pageable pageable);

    Page<? extends Product> findByLocation(final Point location, final Pageable pageable);

    List<? extends Product> findByLocationWithin(final Point location, final Distance distance);

    List<? extends Product> findByLocationNear(final Point location, final Distance distance);

    List<? extends Product> findByAvailableTrue();

    @Query("{!geofilt pt=?0 sfield=store d=?1}")
    List<? extends Product> findByLocationSomewhereNear(final Point location, final Distance distance);

    List<? extends Product> findByNameContainsOrCategoriesContains(final String name, final String category, final Sort sort);
}