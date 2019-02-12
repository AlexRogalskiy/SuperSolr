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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableProduct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.*;
import java.util.*;

/**
 * Custom full-text search product document model
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "products", catalog = "market_data",
        indexes = {@Index(name = "product_category_idx", columnList = "product_id, category")}
)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = SearchableProduct.ID_FIELD_NAME))
})
@Inheritance(strategy = InheritanceType.JOINED)
@SolrDocument(solrCoreName = SearchableProduct.MODEL_ID)
public class Product extends BaseModel<String> implements SearchableProduct {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 6034172782528641104L;

    @Indexed(name = TITLE_FIELD_NAME, type = "string")
    private String title;

    @Lob
    @Indexed(name = DESCRIPTION_FIELD_NAME, type = "string")
    private String description;

    @Indexed(name = AVAILABLE_FIELD_NAME)
    private boolean available;

    @Indexed(name = FEATURES_FIELD_NAME)
    private final List<String> features = new ArrayList<>();

    @Indexed(name = PRICE_FIELD_NAME)
    private double price;

    @Indexed(name = CATEGORY_FIELD_NAME)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = CATEGORY_FIELD_NAME, nullable = false)
    private Category category;

    @Indexed(name = RATING_FIELD_NAME, type = "integer")
    private Integer rating;

    @Indexed(name = LOCATION_FIELD_NAME)
    private Point location;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Product_Order",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")}
    )
    @Indexed(name = SearchableProduct.ORDERS_FIELD_NAME)
    private final Set<Order> orders = new HashSet<>();

    public void setFeatures(final List<String> features) {
        this.features.clear();
        if (Objects.nonNull(features)) {
            this.features.addAll(features);
        }
    }

    public void addFeature(final String feature) {
        if (Objects.nonNull(feature)) {
            this.features.add(feature);
        }
    }

    public void setOrders(final Set<Order> orders) {
        this.orders.clear();
        if (Objects.nonNull(orders)) {
            this.orders.addAll(orders);
        }
    }

    public void addOrder(final Order order) {
        if (Objects.nonNull(order)) {
            this.orders.add(order);
        }
    }
}