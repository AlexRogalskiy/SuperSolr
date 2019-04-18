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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.iface;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.BaseDocument;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.entity.BaseDocumentView;

import java.io.Serializable;

/**
 * Base {@link BaseSearchController} declaration
 *
 * @param <E>  type of base document model {@link BaseDocument}
 * @param <T>  type of base document view model {@link BaseDocumentView}
 * @param <ID> type of base document identifier {@link Serializable}
 * @author Alex
 * @version 1.0.0
 */
public interface BaseDocumentSearchController<E extends BaseDocument<ID>, T extends BaseDocumentView<ID>, ID extends Serializable> extends BaseSearchController<E, T, ID> {

    /**
     * Default page offset value
     */
    String DEFAULT_PAGE_OFFSET_VALUE = "0";
    /**
     * Default page limit value
     */
    String DEFAULT_PAGE_LIMIT_VALUE = "10";
    /**
     * Default page size
     */
    int DEFAULT_PAGE_SIZE = 5;
}
