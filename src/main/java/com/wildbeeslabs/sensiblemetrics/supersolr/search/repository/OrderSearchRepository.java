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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Query.Operator;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Custom order search repository declaration {@link BaseDocumentSearchRepository}
 */
@Repository
public interface OrderSearchRepository extends BaseDocumentSearchRepository<Order, String> {

    @Query(name = "Order.findByDescription")
    Page<? extends Order> findByDescription(final String description, final Pageable pageable);

    @Query(fields = {
        SearchableOrder.ID_FIELD_NAME,
        SearchableOrder.DESCRIPTION_FIELD_NAME,
        SearchableOrder.TITLE_FIELD_NAME
    }, defaultOperator = Operator.OR)
    Page<? extends Order> findByTitle(@Boost(2) final String searchTerm, final Pageable pageable);

    List<? extends Order> findByTitleLike(final Collection<String> titles);

    @Query(name = "Order.findByTitleStartingWith")
    Page<? extends Order> findByTitleStartingWith(final String title, final Pageable pageable);

    @Query(name = "Order.findByText")
    Page<? extends Order> findByNamedQuery(@Boost(2) final String searchTerm, final Pageable pageable);
}
