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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.product.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.wrapper.SearchResult;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.ProductView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Product {@link SearchResult} entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "products")
public class ProductSearchResult extends SearchResult<ProductView> {

    /**
     * Default {@link ProductSearchResult} constructor with input {@link ProductView} key
     *
     * @param key - initial input {@link ProductView} key
     */
    public ProductSearchResult(final ProductView key) {
        super(key);
    }

    /**
     * Default {@link ProductSearchResult} constructor with input {@link ProductView} key and error message {@link String}
     *
     * @param key          - initial input {@link ProductView} key
     * @param errorMessage - initial input error message {@link String}
     */
    public ProductSearchResult(final ProductView key, final String errorMessage) {
        super(key, errorMessage);
    }
}
