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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.*;
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
        indexes = {@Index(name = "product_catalog_number_idx", columnList = SearchableProduct.ID_FIELD_NAME + ", " + SearchableProduct.CATALOG_NUMBER_FIELD_NAME)}
)
@AttributeOverrides({
        @AttributeOverride(name = SearchableBaseModel.ID_FIELD_NAME, column = @Column(name = SearchableProduct.ID_FIELD_NAME))
})
@Inheritance(strategy = InheritanceType.JOINED)
@SolrDocument(solrCoreName = SearchableProduct.MODEL_ID)
public class Product extends BaseModel<String> implements SearchableProduct {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 6034172782528641104L;

    @Indexed(name = NAME_FIELD_NAME, type = "string")
    private String name;

    @Indexed(name = SHORT_DESCRIPTION_FIELD_NAME, type = "string")
    private String shortDescription;

    @Lob
    @Indexed(name = LONG_DESCRIPTION_FIELD_NAME, type = "string")
    private String longDescription;

    @Indexed(name = PRICE_DESCRIPTION_FIELD_NAME, type = "string")
    private String priceDescription;

    @Indexed(name = CATALOG_NUMBER_FIELD_NAME, type = "string")
    private String catalogNumber;

    @Indexed(name = PAGE_TITLE_FIELD_NAME, type = "string")
    private String pageTitle;

    @Indexed(name = AVAILABLE_FIELD_NAME)
    private boolean available;

    @Indexed(name = PRICE_FIELD_NAME)
    private double price;

//    @Indexed(name = CATEGORY_FIELD_NAME)
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
//    @Fetch(FetchMode.SELECT)
//    @JoinColumn(name = CATEGORY_FIELD_NAME, nullable = false)
//    private Category category;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Product_Category",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableCategory.ID_FIELD_NAME, referencedColumnName = SearchableCategory.ID_FIELD_NAME)}
    )
    @Indexed(name = CATEGORIES_FIELD_NAME)
    @Fetch(FetchMode.SELECT)
    private final Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Product_Main_Category",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableCategory.ID_FIELD_NAME, referencedColumnName = SearchableCategory.ID_FIELD_NAME)}
    )
    @Indexed(name = MAiN_CATEGORIES_FIELD_NAME)
    @Fetch(FetchMode.SELECT)
    private final Set<Category> mainCategories = new HashSet<>();

    @Indexed(name = RATING_FIELD_NAME, type = "integer")
    private Integer rating;

    @Indexed(name = AGE_RESTRICTION_FIELD_NAME, type = "integer")
    private Integer ageRestriction;

    @Indexed(name = LOCK_TYPE_FIELD_NAME, type = "integer")
    private Integer lockType;

    @Indexed(name = LOCATION_FIELD_NAME)
    private Point location;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Product_Attribute",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableAttribute.ID_FIELD_NAME, referencedColumnName = SearchableAttribute.ID_FIELD_NAME)}
    )
    @Fetch(FetchMode.SELECT)
    @Indexed(name = ATTRIBUTES_FIELD_NAME)
    private final List<Attribute> attributes = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Product_Order",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableOrder.ID_FIELD_NAME, referencedColumnName = SearchableOrder.ID_FIELD_NAME)}
    )
    @Indexed(name = ORDERS_FIELD_NAME)
    @Fetch(FetchMode.SELECT)
    private final Set<Order> orders = new HashSet<>();

    public void setCategories(final List<? extends Category> categories) {
        this.categories.clear();
        if (Objects.nonNull(categories)) {
            this.categories.addAll(categories);
        }
    }

    public void addCategory(final Category category) {
        if (Objects.nonNull(category)) {
            this.categories.add(category);
        }
    }

    public void setMainCategories(final List<? extends Category> mainCategories) {
        this.mainCategories.clear();
        if (Objects.nonNull(mainCategories)) {
            this.mainCategories.addAll(mainCategories);
        }
    }

    public void addMainCategory(final Category mainCategory) {
        if (Objects.nonNull(mainCategory)) {
            this.mainCategories.add(mainCategory);
        }
    }

    public void setAttributes(final List<? extends Attribute> attributes) {
        this.attributes.clear();
        if (Objects.nonNull(attributes)) {
            this.attributes.addAll(attributes);
        }
    }

    public void addAttribute(final Attribute attribute) {
        if (Objects.nonNull(attribute)) {
            this.attributes.add(attribute);
        }
    }

    public void setOrders(final List<Order> orders) {
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