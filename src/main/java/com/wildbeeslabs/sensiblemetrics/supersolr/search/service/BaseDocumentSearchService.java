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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.BaseDocument;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Custom base document search service declaration
 *
 * @param <E>  type of base document
 * @param <ID> type of base document identifier
 */
public interface BaseDocumentSearchService<E extends BaseDocument<ID>, ID extends Serializable> extends AuditDocumentSearchService<E, ID> {

    /**
     * Default search term delimiter
     */
    String DEFAULT_SEARСH_TERM_DELIMITER = StringUtils.SPACE;
    /**
     * Default search term replacement
     */
    String DEFAULT_SEARСH_TERM_REPLACEMENT = StringUtils.EMPTY;
    /**
     * Default ignored characters pattern
     */
    Pattern DEFAULT_IGNORED_CHARS_PATTERN = Pattern.compile("\\p{Punct}");
    /**
     * Default document type
     */
    String DEFAULT_DOCTYPE = "doctype";
    /**
     * Default query rows size
     */
    int DEFAULT_QUERY_ROWS_SIZE = 1000;

    void saveOrUpdate(final E target, final Class<? extends E> clazz);

    HighlightPage<? extends E> find(final String searchTerm, final Pageable page);

    Page<? extends E> findBySimpleQuery(final Criteria criteria, final Pageable pageable, final Class<? extends E> clazz);

    Page<? extends E> findBySimpleQuery(final String queryString, final Criteria criteria, final Pageable pageable, final Class<? extends E> clazz);
}
