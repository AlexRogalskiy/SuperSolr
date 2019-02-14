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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableAttribute;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.*;
import java.util.*;

/**
 * Custom full-text search attribute document model
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "attributes", catalog = "market_data")
@AttributeOverrides({
        @AttributeOverride(name = SearchableBaseModel.ID_FIELD_NAME, column = @Column(name = SearchableAttribute.ID_FIELD_NAME))
})
@Inheritance(strategy = InheritanceType.JOINED)
@SolrDocument(solrCoreName = SearchableAttribute.MODEL_ID)
public class Attribute extends BaseModel<String> implements SearchableAttribute {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -2796804385398260344L;

    @Indexed(name = NAME_FIELD_NAME, type = "string")
    private String name;

    @Indexed(name = SYNONYM_FIELD_NAME, type = "string")
    private String name2;

    @Indexed(name = DESCRIPTION_TEXT_FIELD_NAME, type = "string")
    private String descriptionText;

    @Indexed(name = KEYWORDS_FIELD_NAME, type = "string")
    private String keywords;

    @ManyToMany(mappedBy = PRODUCTS_FIELD_NAME)
    @Indexed(name = PRODUCTS_FIELD_NAME)
    private final List<Product> products = new ArrayList<>();

    public void setProducts(final Collection<? extends Product> products) {
        this.products.clear();
        Optional.ofNullable(products)
                .orElseGet(Collections::emptyList)
                .forEach(product -> addProduct(product));
    }

    public void addProduct(final Product product) {
        if (Objects.nonNull(product)) {
            this.products.add(product);
        }
    }
}
