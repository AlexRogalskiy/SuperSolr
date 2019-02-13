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
package com.wildbeeslabs.sensiblemetrics.supersolr.model;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableBaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Custom full-text search order document model
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "order", catalog = "market_data")
@AttributeOverrides({
        @AttributeOverride(name = SearchableBaseModel.ID_FIELD_NAME, column = @Column(name = SearchableOrder.ID_FIELD_NAME))
})
@Inheritance(strategy = InheritanceType.JOINED)
@SolrDocument(solrCoreName = SearchableOrder.MODEL_ID)
public class Order extends BaseModel<Long> implements SearchableOrder {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -5055264765286046442L;

    @Indexed(name = TITLE_FIELD_NAME, type = "string")
    private String title;

    @Lob
    @Indexed(name = DESCRIPTION_FIELD_NAME, type = "string")
    private String description;

    @Indexed(name = "productName", type = "string")
    private String productName;

    @Indexed(name = CLIENT_NAME_FIELD_NAME, type = "string")
    private String clientName;

    @Indexed(name = CLIENT_MOBILE_FIELD_NAME, type = "string")
    private String clientMobile;

    @ManyToMany(mappedBy = PRODUCTS_FIELD_NAME)
    @Indexed(name = PRODUCTS_FIELD_NAME)
    private final Set<Product> products = new HashSet<>();

    public void setProducts(final Set<Product> products) {
        this.products.clear();
        if (Objects.nonNull(products)) {
            this.products.addAll(products);
        }
    }

    public void addProduct(final Product product) {
        if (Objects.nonNull(product)) {
            this.products.add(product);
        }
    }
}
