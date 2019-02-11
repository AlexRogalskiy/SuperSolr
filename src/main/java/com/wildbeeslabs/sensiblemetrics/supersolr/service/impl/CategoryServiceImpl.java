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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.CategoryRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.CategoryService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Custom category service implementation {@link Category}
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(CategoryService.SERVICE_ID)
public class CategoryServiceImpl extends BaseModelServiceImpl<Category, String> implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Category> findByTitle(final String title, final Pageable pageable) {
        return getRepository().findByTitle(title, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends Category> findByTitles(final String title, final Pageable pageable) {
        if (StringUtils.isBlank(title)) {
            return getRepository().findAll(pageable);
        }
        return getRepository().findByTitles(splitSearchTermAndRemoveIgnoredCharacters(title), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public FacetPage<? extends Category> autocompleteTitleFragment(final String fragment, final Pageable pageable) {
        if (StringUtils.isBlank(fragment)) {
            return new SolrResultPage<>(Collections.emptyList());
        }
        return getRepository().findByTitleStartsWith(splitSearchTermAndRemoveIgnoredCharacters(fragment), pageable);
    }

    private Collection<String> splitSearchTermAndRemoveIgnoredCharacters(final String searchTerm) {
        final String[] searchTerms = StringUtils.split(searchTerm, DEFAULT_SEARСH_TERM_DELIMITER);
        return Arrays.stream(searchTerms)
                .filter(StringUtils::isNotEmpty)
                .map(term -> DEFAULT_IGNORED_CHARS_PATTERN.matcher(term).replaceAll(DEFAULT_SEARСH_TERM_REPLACEMENT))
                .collect(Collectors.toList());
    }

    @Override
    protected CategoryRepository getRepository() {
        return this.categoryRepository;
    }
}
