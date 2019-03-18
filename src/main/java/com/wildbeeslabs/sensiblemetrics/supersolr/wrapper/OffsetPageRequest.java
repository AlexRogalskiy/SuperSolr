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
package com.wildbeeslabs.sensiblemetrics.supersolr.wrapper;

import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Custom offset page request implementation {@link Pageable}
 */
@Builder
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class OffsetPageRequest implements Pageable {

    /**
     * Default page offset
     */
    private long offset;
    /**
     * Default page limit
     */
    private int limit;
    /**
     * Default page sort
     */
    private Sort sort;

    /**
     * Default offset page request constructor with initial offset / limit input arguments
     *
     * @param offset - initial offset input argument
     * @param limit  - initial limit input argument
     */
    public OffsetPageRequest(long offset, int limit) {
        this(offset, limit, null);
    }

    public OffsetPageRequest(long offset, int limit, final Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return OffsetPageRequest
                .builder()
                .offset(getOffset() + getPageSize())
                .limit(getPageSize())
                .sort(getSort())
                .build();
    }

    @Override
    public Pageable previousOrFirst() {
        return OffsetPageRequest
                .builder()
                .offset(Math.max(0, getOffset() - getPageSize()))
                .limit(getPageSize())
                .sort(getSort())
                .build();
    }

    @Override
    public Pageable first() {
        return OffsetPageRequest
                .builder()
                .offset(0)
                .limit(getPageSize())
                .sort(getSort())
                .build();
    }

    @Override
    public boolean hasPrevious() {
        return getOffset() > 0;
    }
}
