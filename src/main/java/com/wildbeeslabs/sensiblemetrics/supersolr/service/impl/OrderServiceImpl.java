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
package com.wildbeeslabs.sensiblemetrics.supersolr.service.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableOrder;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.OrderRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseModelService;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.OrderService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Custom order service implementation {@link Order}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(OrderService.SERVICE_ID)
public class OrderServiceImpl extends BaseModelServiceImpl<Order, Long> implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByDescription(final String description, final PageRequest request) {
        return getRepository().findByDescription(description, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Order> findByCustomQuery(final String searchTerm, final PageRequest request) {
        return getRepository().findByCustomQuery(searchTerm, request);
    }

    public List<? extends Order> dynamicSearch(final String searchTerm) {
        final Criteria conditions = createConditions(searchTerm);
        final SimpleQuery search = new SimpleQuery(conditions);
        search.addSort(sortByIdDesc());
        final Page<? extends Order> results = this.solrTemplate.queryForPage(search, Order.class);
        return results.getContent();
    }

    private Criteria createConditions(final String searchTerm) {
        Criteria conditions = new Criteria();
        for (final String term : searchTerm.split(BaseModelService.DEFAULT_SEARСH_TERM_DELIMITER)) {
//            if (Objects.isNull(conditions)) {
//                conditions = new Criteria(SearchableOrder.ID_FIELD_NAME).contains(term)
//                        .or(new Criteria(SearchableOrder.DESCRIPTION_FIELD_NAME).contains(term));
//            } else {
            conditions = conditions.or(new Criteria(SearchableOrder.ID_FIELD_NAME).contains(term))
                    .or(new Criteria(SearchableOrder.DESCRIPTION_FIELD_NAME).contains(term));
            //}
        }
        return conditions;
    }

    private Sort sortByIdDesc() {
        return new Sort(Sort.Direction.DESC, SearchableOrder.ID_FIELD_NAME);
    }

    @Override
    protected OrderRepository getRepository() {
        return this.orderRepository;
    }
}
