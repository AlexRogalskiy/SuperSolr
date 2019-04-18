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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.AuditDocument;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Custom audit document search repository declaration {@link BaseSearchRepository}
 *
 * @param <E>  type of audit document {@link AuditDocument}
 * @param <ID> type of audit document identifier {@link Serializable}
 */
@NoRepositoryBean
public interface AuditDocumentSearchRepository<E extends AuditDocument, ID extends Serializable> extends BaseSearchRepository<E, ID> {

    @Async
    @RestResource(rel = "fetch-by-created-date-less-than-equal", description = @Description(value = "find documents by created date less than or equal"))
    CompletableFuture<Iterable<? extends E>> findByCreatedLessThanEqual(final Date createdDate);

    @Async
    @RestResource(rel = "fetch--by-created-date-greater-than", description = @Description(value = "find documents by created date greater than"))
    CompletableFuture<Iterable<? extends E>> findByCreatedGreaterThan(final Date createdDate);

    @Async
    @RestResource(rel = "fetch-by-created-date-between", description = @Description(value = "find documents by created date between"))
    CompletableFuture<Iterable<? extends E>> findByCreatedBetween(final Date createdDateFrom, final Date createdDateTo);

    @Async
    @RestResource(rel = "fetch-by-changed-date-between", description = @Description(value = "find documents by changed date between"))
    CompletableFuture<Iterable<? extends E>> findByChangedBetween(final Date changedDateFrom, final Date changedDateTo);
}
