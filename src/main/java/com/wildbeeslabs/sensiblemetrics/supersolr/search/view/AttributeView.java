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
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableAttributetView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 * Custom product document view
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = ExposableAttributetView.VIEW_ID)
public class AttributeView extends BaseDocumentView<String> implements ExposableAttributetView {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -6304630916069644472L;

    @JacksonXmlProperty(localName = NAME_FIELD_NAME)
    @JsonProperty(NAME_FIELD_NAME)
    private String name;

    @JacksonXmlProperty(localName = SYNONYM_FIELD_NAME)
    @JsonProperty(SYNONYM_FIELD_NAME)
    private String name2;

    @JacksonXmlProperty(localName = DESCRIPTION_TEXT_FIELD_NAME)
    @JsonProperty(DESCRIPTION_TEXT_FIELD_NAME)
    private String descriptionText;

    @JacksonXmlProperty(localName = KEYWORDS_FIELD_NAME)
    @JsonProperty(KEYWORDS_FIELD_NAME)
    private String keywords;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PRODUCTS_FIELD_NAME)
    @JsonProperty(PRODUCTS_FIELD_NAME)
    private final List<ProductView> products = new ArrayList<>();

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
}