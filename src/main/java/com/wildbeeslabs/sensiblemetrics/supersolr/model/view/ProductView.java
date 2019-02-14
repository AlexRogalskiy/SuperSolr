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
package com.wildbeeslabs.sensiblemetrics.supersolr.model.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.interfaces.ExposableProductView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.geo.Point;

import java.util.*;

/**
 * Custom product model view
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = ExposableProductView.VIEW_ID)
public class ProductView extends BaseModelView<String> implements ExposableProductView {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 5714315073889762969L;

    @JacksonXmlProperty(localName = NAME_FIELD_NAME)
    @JsonProperty(NAME_FIELD_NAME)
    private String name;

    @JacksonXmlProperty(localName = SHORT_DESCRIPTION_FIELD_NAME)
    @JsonProperty(SHORT_DESCRIPTION_FIELD_NAME)
    private String shortDescription;

    @JacksonXmlProperty(localName = LONG_DESCRIPTION_FIELD_NAME)
    @JsonProperty(LONG_DESCRIPTION_FIELD_NAME)
    private String longDescription;

    @JacksonXmlProperty(localName = PRICE_DESCRIPTION_FIELD_NAME)
    @JsonProperty(PRICE_DESCRIPTION_FIELD_NAME)
    private String priceDescription;

    @JacksonXmlProperty(localName = CATALOG_NUMBER_FIELD_NAME)
    @JsonProperty(CATALOG_NUMBER_FIELD_NAME)
    private String catalogNumber;

    @JacksonXmlProperty(localName = PAGE_TITLE_FIELD_NAME)
    @JsonProperty(PAGE_TITLE_FIELD_NAME)
    private String pageTitle;

    @JacksonXmlProperty(localName = AVAILABLE_FIELD_NAME)
    @JsonProperty(AVAILABLE_FIELD_NAME)
    private boolean available;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = ATTRIBUTES_FIELD_NAME)
    @JsonProperty(ATTRIBUTES_FIELD_NAME)
    private final List<AttributeView> attributes = new ArrayList<>();

    @JacksonXmlProperty(localName = PRICE_FIELD_NAME)
    @JsonProperty(PRICE_FIELD_NAME)
    private double price;

    @JacksonXmlProperty(localName = RECOMMENDE_PRICE_FIELD_NAME)
    @JsonProperty(RECOMMENDE_PRICE_FIELD_NAME)
    private double recommendedPrice;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = CATEGORIES_FIELD_NAME)
    @JsonProperty(CATEGORIES_FIELD_NAME)
    private final Set<CategoryView> categories = new HashSet<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = MAIN_CATEGORIES_FIELD_NAME)
    @JsonProperty(MAIN_CATEGORIES_FIELD_NAME)
    private final Set<CategoryView> mainCategories = new HashSet<>();

    @JacksonXmlProperty(localName = RATING_FIELD_NAME)
    @JsonProperty(RATING_FIELD_NAME)
    private Integer rating;

    @JacksonXmlProperty(localName = AGE_RESTRICTION_FIELD_NAME)
    @JsonProperty(AGE_RESTRICTION_FIELD_NAME)
    private Integer ageRestriction;

    @JacksonXmlProperty(localName = LOCK_TYPE_FIELD_NAME)
    @JsonProperty(LOCK_TYPE_FIELD_NAME)
    private Integer lockType;

    @JacksonXmlProperty(localName = LOCATION_FIELD_NAME)
    @JsonProperty(LOCATION_FIELD_NAME)
    private Point location;

    public void setAttributes(final Collection<? extends AttributeView> attributes) {
        this.attributes.clear();
        Optional.ofNullable(attributes)
                .orElseGet(Collections::emptyList)
                .forEach(attribute -> addAttribute(attribute));
    }

    public void addAttribute(final AttributeView attribute) {
        if (Objects.nonNull(attribute)) {
            this.attributes.add(attribute);
        }
    }

    public void setCategories(final Collection<? extends CategoryView> categories) {
        this.categories.clear();
        Optional.ofNullable(categories)
                .orElseGet(Collections::emptyList)
                .forEach(category -> addCategory(category));
    }

    public void addCategory(final CategoryView category) {
        if (Objects.nonNull(category)) {
            this.categories.add(category);
        }
    }

    public void setMainCategories(final Collection<? extends CategoryView> mainCategories) {
        this.mainCategories.clear();
        Optional.ofNullable(mainCategories)
                .orElseGet(Collections::emptyList)
                .forEach(mainCategory -> addMainCategory(mainCategory));
    }

    public void addMainCategory(final CategoryView mainCategory) {
        if (Objects.nonNull(mainCategory)) {
            this.mainCategories.add(mainCategory);
        }
    }
}
