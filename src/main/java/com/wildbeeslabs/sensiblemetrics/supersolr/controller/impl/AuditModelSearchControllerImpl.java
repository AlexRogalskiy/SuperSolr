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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.AuditDocumentSearchController;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.AuditDocument;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.AuditDocumentSearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.AuditDocumentView;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Audit document search controller implementation
 *
 * @param <E>  type of audit document {@link AuditDocument}
 * @param <T>  type of audit document view {@link AuditDocumentView}
 * @param <ID> type of audit document identifier {@link Serializable}
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AuditModelSearchControllerImpl<E extends AuditDocument, T extends AuditDocumentView, ID extends Serializable> extends BaseSearchControllerImpl<E, T, ID> implements AuditDocumentSearchController<E, T, ID> {

    protected HttpHeaders getHeaders(final Page<?> page) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(DEFAULT_TOTAL_ELEMENTS_HEADER, Long.toString(page.getTotalElements()));
        headers.add(DEFAULT_EXPIRES_AFTER_HEADER, String.valueOf(LocalDateTime.from(DateUtils.now().toInstant()).plusDays(DEFAULT_TOKEN_EXPIRE_PERIOD).toEpochSecond(ZoneOffset.UTC)));
        headers.add(DEFAULT_RATE_LIMIT_HEADER, String.valueOf(DEFAULT_RATE_LIMIT));
        return headers;
    }

//    protected HttpHeaders getLimitHeaders() {
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(DEFAULT_EXPIRES_AFTER_HEADER, String.valueOf(LocalDateTime.from(DateUtils.now().toInstant()).plusDays(DEFAULT_TOKEN_EXPIRE_PERIOD).toEpochSecond(ZoneOffset.UTC)));
//        headers.add(DEFAULT_RATE_LIMIT_HEADER, String.valueOf(DEFAULT_RATE_LIMIT));
//        return headers;
//    }

    protected abstract AuditDocumentSearchService<E, ID> getSearchService();
}
