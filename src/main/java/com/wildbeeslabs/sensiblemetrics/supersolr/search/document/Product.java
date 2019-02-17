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

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableProduct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.*;

/**
 * Custom full-text search product document
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SolrDocument(solrCoreName = SearchableProduct.DOCUMENT_ID)
public class Product extends BaseDocument<String> implements SearchableProduct {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 6034172782528641104L;

    @Indexed(name = NAME_FIELD_NAME, type = "text_general")
    private String name;

    @Indexed(name = SHORT_DESCRIPTION_FIELD_NAME, type = "text_general")
    private String shortDescription;

    @Indexed(name = LONG_DESCRIPTION_FIELD_NAME, type = "text_general")
    private String longDescription;

    @Indexed(name = PRICE_DESCRIPTION_FIELD_NAME, type = "text_general")
    private String priceDescription;

    @Indexed(name = CATALOG_NUMBER_FIELD_NAME, type = "string")
    private String catalogNumber;

    @Indexed(name = PAGE_TITLE_FIELD_NAME, type = "text_general")
    private String pageTitle;

    @Indexed(name = AVAILABLE_FIELD_NAME)
    private boolean available;

    @Indexed(name = PRICE_FIELD_NAME)
    private double price;

    @Indexed(name = RECOMMENDED_PRICE_FIELD_NAME)
    private double recommendedPrice;

    @Indexed(name = RATING_FIELD_NAME, type = "int")
    private Integer rating;

    @Indexed(name = AGE_RESTRICTION_FIELD_NAME, type = "int")
    private Integer ageRestriction;

    @Indexed(name = LOCK_TYPE_FIELD_NAME, type = "int")
    private Integer lockType;

    @Indexed(name = LOCATION_FIELD_NAME)
    private Point location;

    @Indexed(name = CATEGORIES_FIELD_NAME)
    private final Set<Category> categories = new HashSet<>();

    @Indexed(name = MAIN_CATEGORIES_FIELD_NAME)
    private final Set<Category> mainCategories = new HashSet<>();

    @Indexed(name = ATTRIBUTES_FIELD_NAME)
    private final List<Attribute> attributes = new ArrayList<>();

    @Indexed(name = ORDERS_FIELD_NAME)
    private final Set<Order> orders = new HashSet<>();

    public void setCategories(final Collection<? extends Category> categories) {
        this.getCategories().clear();
        Optional.ofNullable(categories)
                .orElseGet(Collections::emptyList)
                .forEach(category -> this.addCategory(category));
    }

    public void addCategory(final Category category) {
        if (Objects.nonNull(category)) {
            this.getCategories().add(category);
        }
    }

    public void setMainCategories(final Collection<? extends Category> mainCategories) {
        this.getMainCategories().clear();
        Optional.ofNullable(mainCategories)
                .orElseGet(Collections::emptyList)
                .forEach(mainCategory -> this.addMainCategory(mainCategory));
    }

    public void addMainCategory(final Category mainCategory) {
        if (Objects.nonNull(mainCategory)) {
            this.getMainCategories().add(mainCategory);
        }
    }

    public void setAttributes(final Collection<? extends Attribute> attributes) {
        this.getAttributes().clear();
        Optional.ofNullable(attributes)
                .orElseGet(Collections::emptyList)
                .forEach(attribute -> this.addAttribute(attribute));
    }

    public void addAttribute(final Attribute attribute) {
        if (Objects.nonNull(attribute)) {
            this.getAttributes().add(attribute);
        }
    }

    public void setOrders(final Collection<? extends Order> orders) {
        this.getOrders().clear();
        Optional.ofNullable(orders)
                .orElseGet(Collections::emptyList)
                .forEach(order -> this.addOrder(order));
    }

    public void addOrder(final Order order) {
        if (Objects.nonNull(order)) {
            this.getOrders().add(order);
        }
    }
}