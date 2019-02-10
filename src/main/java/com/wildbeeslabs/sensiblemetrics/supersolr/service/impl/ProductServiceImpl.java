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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.ProductRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.ProductService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Custom product service implementation {@link Product}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(ProductService.SERVICE_ID)
public class ProductServiceImpl extends BaseModelServiceImpl<Product, String> implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<? extends Product> findByName(final String name, final Pageable pageable) {
        if (StringUtils.isBlank(name)) {
            return getRepository().findAll(pageable);
        }
        return getRepository().findByNames(splitSearchTermAndRemoveIgnoredCharacters(name), pageable);
    }

    @Override
    public FacetPage<? extends Product> autocompleteNameFragment(final String fragment, final Pageable pageable) {
        if (StringUtils.isBlank(fragment)) {
            return new SolrResultPage<>(Collections.emptyList());
        }
        return getRepository().findByNameStartsWith(splitSearchTermAndRemoveIgnoredCharacters(fragment), pageable);
    }

    private Collection<String> splitSearchTermAndRemoveIgnoredCharacters(final String searchTerm) {
        final String[] searchTerms = StringUtils.split(searchTerm, DEFAULT_SEARСH_TERM_DELIMITER);
        return Arrays.stream(searchTerms)
                .filter(StringUtils::isNotEmpty)
                .map(term -> DEFAULT_IGNORED_CHARS_PATTERN.matcher(term).replaceAll(DEFAULT_SEARСH_TERM_REPLACEMENT))
                .collect(Collectors.toList());
    }

    protected ProductRepository getRepository() {
        return this.productRepository;
    }
}
