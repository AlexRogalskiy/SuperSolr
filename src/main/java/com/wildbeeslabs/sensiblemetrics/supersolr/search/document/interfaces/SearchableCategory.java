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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces;

import java.io.Serializable;

/**
 * Searchable category document definition
 */
public interface SearchableCategory extends Serializable {

    /**
     * Default document ID
     */
    String CORE_ID = "category";
    /**
     * Default collection ID
     */
    String COLLECTION_ID = "category";

    /**
     * Default field names
     */
    String ID_FIELD_NAME = "id";
    String INDEX_FIELD_NAME = "index";
    String TITLE_FIELD_NAME = "title";
    String DESCRIPTION_FIELD_NAME = "description";
    String CATEGORY_FIELD_NAME = "category";
    String PRODUCTS_FIELD_NAME = "products";
    String MAIN_PRODUCTS_FIELD_NAME = "mainProducts";
}
