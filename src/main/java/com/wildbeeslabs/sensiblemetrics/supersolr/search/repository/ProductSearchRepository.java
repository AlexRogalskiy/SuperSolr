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
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Shape;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.solr.core.query.Query.Operator;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Custom product search repository declaration {@link BaseDocumentSearchRepository}
 */
@Repository
@RepositoryRestResource(collectionResourceRel = "product-search-repo", itemResourceDescription = @Description(value = "search operations on product documents"))
public interface ProductSearchRepository extends BaseDocumentSearchRepository<Product, String> {

    @RestResource(rel = "by-name-in-collection", description = @Description(value = "find products by collection of names"))
    @Highlight(prefix = "<highlight>", postfix = "</highlight>")
    @Query(fields = {
        SearchableProduct.ID_FIELD_NAME,
        SearchableProduct.NAME_FIELD_NAME,
        SearchableProduct.ATTRIBUTES_FIELD_NAME,
        SearchableProduct.CATEGORIES_FIELD_NAME,
        SearchableProduct.CATALOG_NUMBER_FIELD_NAME,
        SearchableProduct.RATING_FIELD_NAME,
        SearchableProduct.LOCATION_FIELD_NAME
    }, defaultOperator = Operator.AND)
    HighlightPage<? extends Product> findByNameIn(final Collection<String> names, final Pageable page);

    @RestResource(rel = "by-short-description", description = @Description(value = "find products by short description"))
    @Query(fields = {
        SearchableProduct.ID_FIELD_NAME,
        SearchableProduct.NAME_FIELD_NAME,
        SearchableProduct.PAGE_TITLE_FIELD_NAME,
        SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME,
        SearchableProduct.LONG_DESCRIPTION_FIELD_NAME,
        SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME,
        SearchableProduct.RECOMMENDED_PRICE_FIELD_NAME,
    }, defaultOperator = Operator.OR)
    Page<? extends Product> findByShortDescription(@Boost(2) final String searchTerm, final Pageable pageable);

    @RestResource(rel = "by-name-like-in-collection", description = @Description(value = "find products like in collection of titles"))
    @Query(name = "Product.findByNameLike")
    List<? extends Product> findByNameLike(final Collection<String> names);

    @RestResource(rel = "by-id", description = @Description(value = "find products by ID"))
    @Query(name = "Product.findById")
    Page<? extends Product> findProductById(final String id, final Pageable page);

    @RestResource(rel = "by-name", description = @Description(value = "find products by name"))
    Page<? extends Product> findByName(final String name, final Pageable page);

    @RestResource(rel = "by-all", description = @Description(value = "find all products"))
    @Query(name = "Product.findAll")
    Page<? extends Product> findAllProducts(final Pageable page);

    @RestResource(rel = "by-description", description = @Description(value = "find products by description"))
    @Query(name = "Product.findByDescription")
    Page<? extends Product> findByDescription(final String description, final Pageable page);

    @RestResource(rel = "by-name-starting-with", description = @Description(value = "find products by name starting with"))
    @Query(name = "Product.findByTitleStartingWith")
    @Facet(fields = {SearchableProduct.NAME_FIELD_NAME})
    FacetPage<? extends Product> findByNameStartingWith(@Boost(1.5f) final String name, final Pageable page);

    @RestResource(rel = "by-name-or-description", description = @Description(value = "find products by name or description"))
    @Query(name = "Product.findByNameOrDescription")
    Page<? extends Product> findByNameOrDescription(@Boost(2) final String searchTerm, final Pageable page);

    @RestResource(rel = "by-name-category", description = @Description(value = "find products by category"))
    @Query(name = "Product.findByCategory")
    Page<? extends Product> findByCategory(final String category, final Pageable page);

    @RestResource(rel = "by-available", description = @Description(value = "find available products"))
    @Query(name = "Product.findAvailable")
    Page<? extends Product> findAvailableProducts(final Pageable page);

    @RestResource(rel = "by-availability", description = @Description(value = "find products by availability"))
    @Query(name = "Product.findByAvailability")
    Page<? extends Product> findByAvailableQuery(boolean inStock, final Pageable page);

    @RestResource(rel = "by-name-or-category", description = @Description(value = "find products by name or category"))
    @Query(name = "Product.findByNameOrCategory")
    Page<? extends Product> findByNameOrCategory(final String searchTerm, final Pageable page);

    @RestResource(rel = "by-rating", description = @Description(value = "find products by rating"))
    Page<? extends Product> findByRating(final Integer popularity, final Pageable pageable);

    @RestResource(rel = "by-rating-greater-than-equal", description = @Description(value = "find products by rating greater than or equal"))
    Page<? extends Product> findByRatingGreaterThanEqual(final Integer popularity, final Pageable page);

    @RestResource(rel = "by-lock-type", description = @Description(value = "find products by locktype"))
    List<? extends Product> findByLockType(final Integer lockType, final Sort sort);

    @RestResource(rel = "by-location", description = @Description(value = "find products by location"))
    Page<? extends Product> findByLocation(final Point location, final Distance distance, final Pageable page);

    @RestResource(rel = "by-location-within", description = @Description(value = "find products by location within"))
    List<? extends Product> findByLocationWithin(final Point location, final Distance distance);

    @RestResource(rel = "by-shape-location-near", description = @Description(value = "find products by shape location near"))
    List<? extends Product> findByGeoLocationWithin(final Shape shape);

    @RestResource(rel = "by-location-near", description = @Description(value = "find products by location near"))
    List<? extends Product> findByLocationNear(final Point location, final Distance distance);

    @RestResource(rel = "by-geolocation-near", description = @Description(value = "find products by geolocation near"))
    GeoResults<? extends Product> findByGeoLocationNear(final Point location, final Distance distance);

    @RestResource(rel = "by-location", description = @Description(value = "find products by location near"))
    @Query(name = "Product.findByLocation")
    List<? extends Product> findByLocationSomewhereNear(final Point location, final Distance distance);

    @RestResource(rel = "by-name-or-category-contains", description = @Description(value = "find products by name or category contains"))
    List<? extends Product> findByNameContainsOrCategoriesContains(final String name, final String category, final Sort sort);

    @RestResource(rel = "by-price-in-range", description = @Description(value = "find products by price in range"))
    @Query(name = "Product.findByPriceInRange")
    Page<? extends Product> findByPriceInRange(double lowerBound, double upperBound, final Pageable page);

    @RestResource(rel = "by-price-in-range-exclusive", description = @Description(value = "find products by price in range exclusive"))
    @Query(name = "Product.findByPriceInRangeExclusive")
    Page<? extends Product> findByPriceInRangeExclusive(double lowerBound, double upperBound, final Pageable page);

    @RestResource(rel = "by-available-and-name-starting-with", description = @Description(value = "find available products by name starting with"))
    Page<? extends Product> findByAvailableTrueAndNameStartingWith(final String name, final Pageable page);

    @RestResource(rel = "by-name-and-rating", description = @Description(value = "find products by name and rating"))
    @Query(name = "Product.findByNameAndRating")
    List<? extends Product> findByNameAndRating(final String name, final Integer rating);

    @RestResource(rel = "by-name-like-and-by-facet-category", description = @Description(value = "find products by name like"))
    @Query(name = "Product.findByNameLike")
    @Facet(fields = {
        SearchableProduct.ID_FIELD_NAME,
        SearchableProduct.NAME_FIELD_NAME,
        SearchableProduct.CATEGORIES_FIELD_NAME,
        SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME,
        SearchableProduct.RECOMMENDED_PRICE_FIELD_NAME
    })
    FacetPage<? extends Product> findByNameAndFacetOnCategory(final String name, Pageable page);

    @RestResource(rel = "by-age-restriction-less-than", description = @Description(value = "find products by age restriction less than"))
    Page<? extends Product> findByAgeRestrictionLessThan(final Integer ageRestriction, final Pageable page);

    @RestResource(rel = "by-rating-and-facet-on-name", description = @Description(value = "find products by rating and facet on name"))
    @Query(value = "Product.findByRating")
    @Facet(fields = {
        SearchableProduct.ID_FIELD_NAME,
        SearchableProduct.NAME_FIELD_NAME,
        SearchableProduct.PAGE_TITLE_FIELD_NAME,
        SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME,
        SearchableProduct.LONG_DESCRIPTION_FIELD_NAME,
        SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME,
        SearchableProduct.RECOMMENDED_PRICE_FIELD_NAME,
    }, prefix = "?1")
    FacetPage<Product> findByRatingFacetOnName(final Integer rating, final String prefix, final Pageable page);

    @RestResource(rel = "by-top10-name-or-short-description", description = @Description(value = "find top ten products by name or short description"))
    Page<? extends Product> findTop10ByNameOrShortDescription(final @Boost(2) String name, final String shortDescription, final Pageable page);
}
