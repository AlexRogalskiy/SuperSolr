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
package com.wildbeeslabs.sensiblemetrics.supersolr.repository;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProductRepository extends BaseModelRepository<Product, String> {

    @Query("id:*?0* OR name:*?0*")
    Page<Product> findByCustomQuery(final String searchTerm, final Pageable pageable);

    @Query(name = "Product.findByNamedQuery")
    Page<Product> findByNamedQuery(final String searchTerm, final Pageable pageable);

    @Highlight(prefix = "<b>", postfix = "</b>")
    @Query(fields = {
            SearchableProduct.ID_FIELD_NAME,
            SearchableProduct.NAME_FIELD_NAME,
            SearchableProduct.PRICE_FIELD_NAME,
            SearchableProduct.FEATURES_FIELD_NAME,
            SearchableProduct.AVAILABLE_FIELD_NAME
    }, defaultOperator = org.springframework.data.solr.core.query.Query.Operator.AND)
    HighlightPage<? extends Product> findByNames(final Collection<String> names, final Pageable pageable);

    @Facet(fields = {SearchableProduct.NAME_FIELD_NAME})
    FacetPage<? extends Product> findByNameStartsWith(final Collection<String> fragments, final Pageable pageable);
}