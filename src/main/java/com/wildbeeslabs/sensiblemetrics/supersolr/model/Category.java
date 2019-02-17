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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.*;

/**
 * Custom category model
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity(name = PersistableCategory.MODEL_ID)
@BatchSize(size = 10)
@Table(name = "category", catalog = "public")
@AttributeOverrides({
        @AttributeOverride(name = BaseModel.ID_FIELD_NAME, column = @Column(name = PersistableCategory.ID_FIELD_NAME, unique = true, nullable = false))
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Category extends BaseModel<Long> implements PersistableCategory {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -107452074862198456L;

    @Column(name = INDEX_FIELD_NAME)
    private Integer index;

    @Column(name = TITLE_FIELD_NAME)
    private String title;

    @Column(name = DESCRIPTION_FIELD_NAME, columnDefinition = "text")
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<Product> products = new HashSet<>();

    @ManyToMany(mappedBy = "mainCategories", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<Product> mainProducts = new HashSet<>();

//    @OneToMany(mappedBy = CATEGORY_FIELD_NAME, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    @Column(name = PRODUCTS_FIELD_NAME, nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @BatchSize(size = 10)
//    private final Set<Product> products = new HashSet<>();

    public void setProducts(final Collection<? extends Product> products) {
        this.getProducts().clear();
        Optional.ofNullable(products)
                .orElseGet(Collections::emptyList)
                .forEach(product -> this.addProduct(product));
    }

    public void addProduct(final Product product) {
        if (Objects.nonNull(product)) {
            this.getProducts().add(product);
        }
    }

    public void setMainProducts(final Collection<? extends Product> mainProducts) {
        this.getMainProducts().clear();
        Optional.ofNullable(mainProducts)
                .orElseGet(Collections::emptyList)
                .forEach(mainProduct -> this.addMainProduct(mainProduct));
    }

    public void addMainProduct(final Product mainProduct) {
        if (Objects.nonNull(mainProduct)) {
            this.getMainProducts().add(mainProduct);
        }
    }
}
