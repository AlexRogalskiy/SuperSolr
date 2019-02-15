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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    @Column(name = NAME_FIELD_NAME)
    @Indexed(name = NAME_FIELD_NAME, type = "string")
    private String name;

    @Column(name = SHORT_DESCRIPTION_FIELD_NAME, columnDefinition = "text")
    @Indexed(name = SHORT_DESCRIPTION_FIELD_NAME, type = "string")
    private String shortDescription;

    @Lob
    @Column(name = LONG_DESCRIPTION_FIELD_NAME, columnDefinition = "text")
    @Indexed(name = LONG_DESCRIPTION_FIELD_NAME, type = "string")
    private String longDescription;

    @Column(name = PRICE_DESCRIPTION_FIELD_NAME, columnDefinition = "text")
    @Indexed(name = PRICE_DESCRIPTION_FIELD_NAME, type = "string")
    private String priceDescription;

    @Column(name = CATALOG_NUMBER_FIELD_NAME)
    @Indexed(name = CATALOG_NUMBER_FIELD_NAME, type = "string")
    private String catalogNumber;

    @Column(name = PAGE_TITLE_FIELD_NAME)
    @Indexed(name = PAGE_TITLE_FIELD_NAME, type = "string")
    private String pageTitle;

    @Column(name = AVAILABLE_FIELD_NAME)
    @Indexed(name = AVAILABLE_FIELD_NAME)
    private boolean available;

    @Column(name = PRICE_FIELD_NAME)
    @Indexed(name = PRICE_FIELD_NAME)
    private double price;

    @Column(name = RECOMMENDED_PRICE_FIELD_NAME)
    @Indexed(name = RECOMMENDED_PRICE_FIELD_NAME)
    private double recommendedPrice;

    @Column(name = RATING_FIELD_NAME)
    @Indexed(name = RATING_FIELD_NAME, type = "int")
    private Integer rating;

    @Column(name = AGE_RESTRICTION_FIELD_NAME)
    @Indexed(name = AGE_RESTRICTION_FIELD_NAME, type = "int")
    private Integer ageRestriction;

    @Column(name = LOCK_TYPE_FIELD_NAME)
    @Indexed(name = LOCK_TYPE_FIELD_NAME, type = "int")
    private Integer lockType;

    @Column(name = LOCATION_FIELD_NAME)
    @Indexed(name = LOCATION_FIELD_NAME)
    private Point location;

//    @Indexed(name = CATEGORY_FIELD_NAME)
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
//    @Fetch(FetchMode.SELECT)
//    @JoinColumn(name = CATEGORY_FIELD_NAME, nullable = false)
//    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Product_Category",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableCategory.ID_FIELD_NAME, referencedColumnName = SearchableCategory.ID_FIELD_NAME)}
    )
    @Indexed(name = CATEGORIES_FIELD_NAME)
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private final Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Product_Main_Category",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableCategory.ID_FIELD_NAME, referencedColumnName = SearchableCategory.ID_FIELD_NAME)}
    )
    @Indexed(name = MAIN_CATEGORIES_FIELD_NAME)
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private final Set<Category> mainCategories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Product_Attribute",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableAttribute.ID_FIELD_NAME, referencedColumnName = SearchableAttribute.ID_FIELD_NAME)}
    )
    @Fetch(FetchMode.SELECT)
    @Indexed(name = ATTRIBUTES_FIELD_NAME)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private final List<Attribute> attributes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Product_Order",
            joinColumns = {@JoinColumn(name = SearchableProduct.ID_FIELD_NAME, referencedColumnName = SearchableProduct.ID_FIELD_NAME)},
            inverseJoinColumns = {@JoinColumn(name = SearchableOrder.ID_FIELD_NAME, referencedColumnName = SearchableOrder.ID_FIELD_NAME)}
    )
    @Indexed(name = ORDERS_FIELD_NAME)
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private final Set<Order> orders = new HashSet<>();

    public void setCategories(final Collection<? extends Category> categories) {
        this.categories.clear();
        Optional.ofNullable(categories)
                .orElseGet(Collections::emptyList)
                .forEach(category -> addCategory(category));
    }

    public void addCategory(final Category category) {
        if (Objects.nonNull(category)) {
            this.categories.add(category);
        }
    }

    public void setMainCategories(final Collection<? extends Category> mainCategories) {
        this.mainCategories.clear();
        Optional.ofNullable(mainCategories)
                .orElseGet(Collections::emptyList)
                .forEach(mainCategory -> addMainCategory(mainCategory));
    }

    public void addMainCategory(final Category mainCategory) {
        if (Objects.nonNull(mainCategory)) {
            this.mainCategories.add(mainCategory);
        }
    }

    public void setAttributes(final Collection<? extends Attribute> attributes) {
        this.attributes.clear();
        Optional.ofNullable(attributes)
                .orElseGet(Collections::emptyList)
                .forEach(attribute -> addAttribute(attribute));
    }

    public void addAttribute(final Attribute attribute) {
        if (Objects.nonNull(attribute)) {
            this.attributes.add(attribute);
        }
    }

    public void setOrders(final Collection<? extends Order> orders) {
        this.orders.clear();
        Optional.ofNullable(orders)
                .orElseGet(Collections::emptyList)
                .forEach(order -> addOrder(order));
    }

    public void addOrder(final Order order) {
        if (Objects.nonNull(order)) {
            this.orders.add(order);
        }
    }
}