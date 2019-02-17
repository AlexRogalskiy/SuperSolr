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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.document;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.*;

/**
 * Custom full-text search order document
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SolrDocument(solrCoreName = SearchableOrder.DOCUMENT_ID)
public class Order extends BaseDocument<Long> implements SearchableOrder {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -5055264765286046442L;

    @Indexed(name = TITLE_FIELD_NAME, type = "string")
    private String title;

    @Indexed(name = DESCRIPTION_FIELD_NAME, type = "text_general")
    private String description;

    @Indexed(name = CLIENT_NAME_FIELD_NAME, type = "string")
    private String clientName;

    @Indexed(name = CLIENT_MOBILE_FIELD_NAME, type = "string")
    private String clientMobile;

    @Indexed(name = PRODUCTS_FIELD_NAME)
    private Set<Product> products = new HashSet<>();

    public void setProducts(final Collection<? extends Product> products) {
        this.getProducts().clear();
        Optional.ofNullable(products)
                .orElseGet(Collections::emptyList)
                .forEach(product -> this.addProduct(product));
    }

    public void addProduct(final Product product) {
        if (Objects.nonNull(product)) {
            this.getProducts().add(product);
        }
    }
}
