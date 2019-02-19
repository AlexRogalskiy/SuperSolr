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
package com.wildbeeslabs.sensiblemetrics.supersolr;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Attribute;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.BaseDocument;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.io.Serializable;
import java.util.*;

/**
 * Base document test implementation
 */
@Slf4j
public abstract class BaseDocumentTest {

    @Autowired
    private SolrTemplate solrTemplate;

    protected Category createCategory(
        final String id,
        final Integer index,
        final String title,
        final String description,
        final Product... products) {
        final Category category = new Category();
        category.setId(id);
        category.setIndex(index);
        category.setTitle(title);
        category.setDescription(description);
        category.setProducts(Arrays.asList(Optional.ofNullable(products).orElse(new Product[0])));
        return category;
    }

    protected Product createProduct(
        final String id,
        final String name,
        final String shortDescription,
        final String longDescription,
        final String priceDescription,
        final String catalogNumber,
        final String pageTitle,
        final Integer rating,
        double price,
        double recommendedPrice,
        boolean available,
        final Attribute... attributes) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setShortDescription(shortDescription);
        product.setLongDescription(longDescription);
        product.setPriceDescription(priceDescription);
        product.setCatalogNumber(catalogNumber);
        product.setPageTitle(pageTitle);
        product.setRating(rating);
        product.setPrice(price);
        product.setRecommendedPrice(recommendedPrice);
        product.setAvailable(available);
        product.setAttributes(Arrays.asList(Optional.ofNullable(attributes).orElse(new Attribute[0])));
        return product;
    }

    protected <E extends BaseDocument<ID>, ID extends Serializable> Map<String, Long> getFacetCounts(final FacetPage<? extends E> facetPage) {
        final Map<String, Long> facetCounts = new HashMap<>();
        for (final Page<? extends FacetEntry> page : facetPage.getAllFacets()) {
            for (final FacetEntry facetEntry : page.getContent()) {
                facetCounts.put(facetEntry.getValue(), facetEntry.getValueCount());
            }
        }
        return facetCounts;
    }

    protected <E extends BaseDocument<ID>, ID extends Serializable> boolean containsIds(final List<? extends E> models, final String... idsToCheck) {
        final String[] categoryIds = models.stream().map(category -> category.getId()).toArray(String[]::new);
        Arrays.sort(categoryIds);
        Arrays.sort(idsToCheck);
        return Arrays.equals(categoryIds, idsToCheck);
    }

    protected <E extends BaseDocument<ID>, ID extends Serializable> boolean containsSnipplet(final HighlightPage<? extends E> categoryHighlightPage, final String snippletToCheck) {
        for (final HighlightEntry<? extends E> he : categoryHighlightPage.getHighlighted()) {
            for (final HighlightEntry.Highlight highlight : he.getHighlights()) {
                for (final String snipplet : highlight.getSnipplets()) {
                    if (snipplet.equals(snippletToCheck)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void clear(final String collection) {
        getSolrTemplate().delete(collection, getQuery("*:*"));
    }

    protected Query getQuery(final String queryString) {
        return new SimpleQuery(new SimpleStringCriteria(queryString));
    }

    protected Query getQuery(final String queryString, final Pageable page) {
        return new SimpleQuery(new SimpleStringCriteria(queryString))
            .setPageRequest(page);
    }

    protected SolrTemplate getSolrTemplate() {
        return this.solrTemplate;
    }
}
