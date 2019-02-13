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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @JacksonXmlProperty(localName = TITLE_FIELD_NAME)
    @JsonProperty(TITLE_FIELD_NAME)
    private String title;

    @JacksonXmlProperty(localName = DESCRIPTION_FIELD_NAME)
    @JsonProperty(DESCRIPTION_FIELD_NAME)
    private String description;

    @JacksonXmlProperty(localName = AVAILABLE_FIELD_NAME)
    @JsonProperty(AVAILABLE_FIELD_NAME)
    private boolean available;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = FEATURES_FIELD_NAME)
    @JsonProperty(FEATURES_FIELD_NAME)
    private final Set<String> features = new HashSet<>();

    @JacksonXmlProperty(localName = PRICE_FIELD_NAME)
    @JsonProperty(PRICE_FIELD_NAME)
    private double price;

    @JacksonXmlProperty(localName = CATEGORY_FIELD_NAME)
    @JsonProperty(CATEGORY_FIELD_NAME)
    private CategoryView category;

    @JacksonXmlProperty(localName = RATING_FIELD_NAME)
    @JsonProperty(RATING_FIELD_NAME)
    private Integer rating;

    @JacksonXmlProperty(localName = LOCATION_FIELD_NAME)
    @JsonProperty(LOCATION_FIELD_NAME)
    private Point location;

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
}
