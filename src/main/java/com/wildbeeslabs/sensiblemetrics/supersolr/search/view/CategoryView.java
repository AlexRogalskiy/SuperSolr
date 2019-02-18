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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableCategoryView;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 * Custom category document view {@link BaseDocumentView}
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = ExposableCategoryView.VIEW_ID)
@ApiModel(value = ExposableCategoryView.VIEW_ID, description = "All details about category document")
public class CategoryView extends BaseDocumentView<String> implements ExposableCategoryView {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -878245565646636436L;

    @JacksonXmlProperty(localName = INDEX_FIELD_NAME)
    @JsonProperty(INDEX_FIELD_NAME)
    private Integer index;

    @JacksonXmlProperty(localName = TITLE_FIELD_NAME)
    @JsonProperty(TITLE_FIELD_NAME)
    private String title;

    @JacksonXmlProperty(localName = DESCRIPTION_FIELD_NAME)
    @JsonProperty(DESCRIPTION_FIELD_NAME)
    private String description;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PRODUCTS_FIELD_NAME)
    @JsonProperty(PRODUCTS_FIELD_NAME)
    private final Set<ProductView> products = new HashSet<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = MAIN_PRODUCTS_FIELD_NAME)
    @JsonProperty(MAIN_PRODUCTS_FIELD_NAME)
    private final Set<ProductView> mainProducts = new HashSet<>();

    public void setProducts(final Collection<? extends ProductView> products) {
        this.getProducts().clear();
        Optional.ofNullable(products)
                .orElseGet(Collections::emptyList)
                .forEach(product -> this.addProduct(product));
    }

    public void addProduct(final ProductView product) {
        if (Objects.nonNull(product)) {
            this.getProducts().add(product);
        }
    }

    public void setMainProducts(final Collection<? extends ProductView> mainProducts) {
        this.getMainProducts().clear();
        Optional.ofNullable(mainProducts)
                .orElseGet(Collections::emptyList)
                .forEach(mainProduct -> this.addMainProduct(mainProduct));
    }

    public void addMainProduct(final ProductView mainProduct) {
        if (Objects.nonNull(mainProduct)) {
            this.getMainProducts().add(mainProduct);
        }
    }
}
