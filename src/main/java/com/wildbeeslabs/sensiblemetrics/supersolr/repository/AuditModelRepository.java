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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.AuditModel;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * {@link AuditModel} repository declaration {@link BaseJpaRepository}
 *
 * @param <E>  type of audit model
 * @param <ID> type of audit model identifier
 */
@NoRepositoryBean
public interface AuditModelRepository<E extends AuditModel, ID extends Serializable> extends BaseJpaRepository<E, ID> {

    //@Query("SELECT e FROM #{#entityName} e WHERE e.created <= ?1")
    @Async
    @RestResource(rel = "fetch-by-created-less-than-equal", description = @Description(value = "find models by created less than equal"))
    CompletableFuture<Iterable<? extends E>> findByCreatedLessThanEqual(final Date date);

    //@Query("SELECT e FROM #{#entityName} e WHERE e.created > ?1")
    @Async
    @RestResource(rel = "fetch-by-created-greater-than", description = @Description(value = "find models by created greater than equal"))
    CompletableFuture<Iterable<? extends E>> findByCreatedGreaterThan(final Date date);

    //@Query("SELECT e FROM #{#entityName} e WHERE e.created > ?1 AND e.created <= ?2")
    @Async
    @RestResource(rel = "fetch-by-created-between", description = @Description(value = "find models by created between"))
    CompletableFuture<Iterable<? extends E>> findByCreatedBetween(final Date dateFrom, final Date dateTo);

    //@Query("SELECT e FROM #{#entityName} e WHERE e.createdBy = ?1")
    @Async
    @RestResource(rel = "fetch-by-createdBy", description = @Description(value = "find models by createdBy"))
    CompletableFuture<Iterable<? extends E>> findByCreatedByIgnoreCase(final String createdBy);

    //@Query("SELECT e FROM #{#entityName} e WHERE e.changedBy = ?1")
    @Async
    @RestResource(rel = "fetch-by-changedBy", description = @Description(value = "find models by changedBy"))
    CompletableFuture<Iterable<? extends E>> findByChangedByIgnoreCase(final String changedBy);

    //@Query("SELECT e FROM #{#entityName} e WHERE e.changed > ?1 AND e.changed <= ?2")
    @Async
    @RestResource(rel = "fetch-by-changed-between", description = @Description(value = "find models by changed between"))
    CompletableFuture<Iterable<? extends E>> findByChangedBetween(final Date dateFrom, final Date dateTo);
}
