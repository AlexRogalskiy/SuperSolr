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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableBaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableProduct.CATEGORIES_FIELD_NAME;
import static com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableProduct.MAiN_CATEGORIES_FIELD_NAME;

/**
 * Custom full-text search category document model
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "category", catalog = "market_data")
@AttributeOverrides({
        @AttributeOverride(name = SearchableBaseModel.ID_FIELD_NAME, column = @Column(name = SearchableCategory.ID_FIELD_NAME))
})
@Inheritance(strategy = InheritanceType.JOINED)
@SolrDocument(solrCoreName = SearchableCategory.MODEL_ID)
public class Category extends BaseModel<String> implements SearchableCategory {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -107452074862198456L;

    @Indexed(name = INDEX_FIELD_NAME)
    private Integer index;

    @Indexed(name = TITLE_FIELD_NAME, type = "string")
    private String title;

    @Indexed(name = DESCRIPTION_FIELD_NAME, type = "string")
    private String description;

    @ManyToMany(mappedBy = CATEGORIES_FIELD_NAME)
    @Indexed(name = PRODUCTS_FIELD_NAME)
    private final Set<Product> products = new HashSet<>();

    @ManyToMany(mappedBy = MAiN_CATEGORIES_FIELD_NAME)
    @Indexed(name = MAIN_PRODUCTS_FIELD_NAME)
    private final Set<Product> mainProducts = new HashSet<>();

//    @OneToMany(mappedBy = CATEGORY_FIELD_NAME, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    @Column(name = PRODUCTS_FIELD_NAME, nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @BatchSize(size = 10)
//    @Indexed(name = PRODUCTS_FIELD_NAME)
//    private final Set<Product> products = new HashSet<>();

    public void setProducts(final List<? extends Product> products) {
        this.products.clear();
        if (Objects.nonNull(products)) {
            this.products.addAll(products);
        }
    }

    public void addProduct(final Product product) {
        if (Objects.nonNull(product)) {
            this.products.add(product);
        }
    }

    public void setMainProducts(final List<? extends Product> mainProducts) {
        this.mainProducts.clear();
        if (Objects.nonNull(mainProducts)) {
            this.mainProducts.addAll(mainProducts);
        }
    }

    public void addMainProduct(final Product mainProduct) {
        if (Objects.nonNull(mainProduct)) {
            this.mainProducts.add(mainProduct);
        }
    }
}
