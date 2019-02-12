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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Custom full-text search category document model
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "category", catalog = "market_data")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = SearchableCategory.ID_FIELD_NAME))
})
@Inheritance(strategy = InheritanceType.JOINED)
@SolrDocument(solrCoreName = SearchableCategory.MODEL_ID)
public class Category extends BaseModel<String> implements SearchableCategory {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -107452074862198456L;

    @Indexed(name = SearchableCategory.TITLE_FIELD_NAME, type = "string")
    private String title;

    @Indexed(name = SearchableCategory.DESCRIPTION_FIELD_NAME, type = "string")
    private String description;

    @OneToMany(mappedBy = SearchableCategory.CATEGORY_FIELD_NAME, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @Column(name = SearchableCategory.PRODUCTS_FIELD_NAME, nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @BatchSize(size = 10)
    @Indexed(name = SearchableCategory.PRODUCTS_FIELD_NAME)
    private final Set<Product> products = new HashSet<>();

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
}
