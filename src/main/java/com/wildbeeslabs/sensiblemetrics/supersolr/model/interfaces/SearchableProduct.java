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
package com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces;

/**
 * Default searchable product model definition
 */
public interface SearchableProduct {

    /**
     * Default document ID
     */
    String DOCUMENT_ID = "Product";

    /**
     * Default field names
     */
    String ID_FIELD_NAME = "id";
    String NAME_FIELD_NAME = "name";
    String AVAILABLE_FIELD_NAME = "inStock";
    String LOCATION_FIELD_NAME = "store";
    String CATEGORY_FIELD_NAME = "category";
    String PRICE_FIELD_NAME = "price";
    String FEATURES_FIELD_NAME = "features";
    String RATING_FIELD_NAME = "rating";
    String ORDERS_FIELD_NAME = "orders";
}
