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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableProductView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.geo.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableBaseDocumentView.ID_FIELD_NAME;
import static com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableBaseDocumentView.SCORE_FIELD_NAME;
import static com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableProductView.*;

/**
 * Product document view {@link BaseDocumentView}
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(
    value = {
        ID_FIELD_NAME,
        SCORE_FIELD_NAME,
        NAME_FIELD_NAME,
        SHORT_DESCRIPTION_FIELD_NAME,
        LONG_DESCRIPTION_FIELD_NAME,
        PRICE_DESCRIPTION_FIELD_NAME,
        CATALOG_NUMBER_FIELD_NAME,
        PAGE_TITLE_FIELD_NAME,
        AVAILABLE_FIELD_NAME,
        PRICE_FIELD_NAME,
        RECOMMENDED_PRICE_FIELD_NAME,
        RATING_FIELD_NAME,
        AGE_RESTRICTION_FIELD_NAME,
        LOCK_TYPE_FIELD_NAME,
        GEO_LOCATION_FIELD_NAME,
        ATTRIBUTES_FIELD_NAME,
        CATEGORIES_FIELD_NAME,
        MAIN_CATEGORIES_FIELD_NAME
    },
    alphabetic = true)
@JsonRootName(ExposableProductView.VIEW_ID)
@JacksonXmlRootElement(localName = ExposableProductView.VIEW_ID)
@ApiModel(value = ExposableProductView.VIEW_ID, description = "All details about product document")
public class ProductView extends BaseDocumentView<String> implements ExposableProductView {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 5714315073889762969L;

    @ApiModelProperty(value = "Product name", name = "name", example = "name", required = true)
    @JacksonXmlProperty(localName = NAME_FIELD_NAME)
    @JsonProperty(NAME_FIELD_NAME)
    private String name;

    @ApiModelProperty(value = "Product short description", name = "shortDescription", example = "short description")
    @JacksonXmlProperty(localName = SHORT_DESCRIPTION_FIELD_NAME)
    @JsonProperty(SHORT_DESCRIPTION_FIELD_NAME)
    private String shortDescription;

    @ApiModelProperty(value = "Product long description", name = "longDescription", example = "long description")
    @JacksonXmlProperty(localName = LONG_DESCRIPTION_FIELD_NAME)
    @JsonProperty(LONG_DESCRIPTION_FIELD_NAME)
    private String longDescription;

    @ApiModelProperty(value = "Product price description", name = "priceDescription", example = "price description")
    @JacksonXmlProperty(localName = PRICE_DESCRIPTION_FIELD_NAME)
    @JsonProperty(PRICE_DESCRIPTION_FIELD_NAME)
    private String priceDescription;

    @ApiModelProperty(value = "Product catalog number", name = "catalogNumber", example = "catalog number")
    @JacksonXmlProperty(localName = CATALOG_NUMBER_FIELD_NAME)
    @JsonProperty(CATALOG_NUMBER_FIELD_NAME)
    private String catalogNumber;

    @ApiModelProperty(value = "Product page title", name = "pageTitle", example = "page title")
    @JacksonXmlProperty(localName = PAGE_TITLE_FIELD_NAME)
    @JsonProperty(PAGE_TITLE_FIELD_NAME)
    private String pageTitle;

    @ApiModelProperty(value = "Product availability", name = "availability", example = "product availability", required = true)
    @JacksonXmlProperty(localName = AVAILABLE_FIELD_NAME)
    @JsonProperty(AVAILABLE_FIELD_NAME)
    private boolean available;

    @ApiModelProperty(value = "Product price", name = "price", example = "product price", required = true)
    @JacksonXmlProperty(localName = PRICE_FIELD_NAME)
    @JsonProperty(PRICE_FIELD_NAME)
    private double price;

    @ApiModelProperty(value = "Product recommended price", name = "recommendedPrice", example = "recommended price")
    @JacksonXmlProperty(localName = RECOMMENDED_PRICE_FIELD_NAME)
    @JsonProperty(RECOMMENDED_PRICE_FIELD_NAME)
    private double recommendedPrice;

    @ApiModelProperty(value = "Product rating", name = "rating", example = "rating")
    @JacksonXmlProperty(localName = RATING_FIELD_NAME)
    @JsonProperty(RATING_FIELD_NAME)
    private Integer rating;

    @ApiModelProperty(value = "Product age restriction", name = "ageRestriction", example = "6+")
    @JacksonXmlProperty(localName = AGE_RESTRICTION_FIELD_NAME)
    @JsonProperty(AGE_RESTRICTION_FIELD_NAME)
    private Integer ageRestriction;

    @ApiModelProperty(value = "Product lock type", name = "lock type", example = "lock type", allowableValues = "1-registered,2-blocked,3-in stock,4-in sale,5-in decommission")
    @JacksonXmlProperty(localName = LOCK_TYPE_FIELD_NAME)
    @JsonProperty(LOCK_TYPE_FIELD_NAME)
    private Integer lockType;

    @ApiModelProperty(value = "Product location", name = "location", example = "location")
    @JacksonXmlProperty(localName = LOCATION_FIELD_NAME)
    @JsonProperty(LOCATION_FIELD_NAME)
    private Point location;

    @ApiModelProperty(value = "Product geo location", name = "geoLocation", example = "geoLocation")
    @JacksonXmlProperty(localName = GEO_LOCATION_FIELD_NAME)
    @JsonProperty(GEO_LOCATION_FIELD_NAME)
    private Point geoLocation;

    @ApiModelProperty(value = "List of attributes per product", name = "attributes", example = "attributes")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = ATTRIBUTES_FIELD_NAME)
    @JsonProperty(ATTRIBUTES_FIELD_NAME)
    private final List<AttributeView> attributes = new ArrayList<>();

    @ApiModelProperty(value = "List of categories per product", name = "categories", example = "categories")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = CATEGORIES_FIELD_NAME)
    @JsonProperty(CATEGORIES_FIELD_NAME)
    private final Set<CategoryView> categories = new HashSet<>();

    @ApiModelProperty(value = "List of main categories per product", name = "mainCategories", example = "mainCategories")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = MAIN_CATEGORIES_FIELD_NAME)
    @JsonProperty(MAIN_CATEGORIES_FIELD_NAME)
    private final Set<CategoryView> mainCategories = new HashSet<>();
}
