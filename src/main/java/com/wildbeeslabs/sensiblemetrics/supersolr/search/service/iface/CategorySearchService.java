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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.service.iface;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.Collection;

/**
 * {@link Category} document search service declaration {@link BaseDocumentSearchService}
 */
public interface CategorySearchService extends BaseDocumentSearchService<Category, String> {

    /**
     * Default service ID
     */
    String SERVICE_ID = "CategorySearchService";
    /**
     * Default collection ID
     */
    String COLLECTION_ID = "category";

    Page<? extends Category> findByTitleLike(final String title, final Pageable pageable);

    Page<? extends Category> findByTitle(final String title, final Pageable pageable);

    Page<? extends Category> findByTitles(final String titles, final Pageable pageable);

    Page<? extends Category> findByDescription(final String description, final Pageable pageable);

    HighlightPage<? extends Category> findByTitleIn(final Collection<String> values, final Pageable pageable);

    FacetPage<? extends Category> findByAutoCompleteTitleFragment(final String fragment, final Pageable pageable);
}
