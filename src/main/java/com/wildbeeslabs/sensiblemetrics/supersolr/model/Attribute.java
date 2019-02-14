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
import java.util.HashSet;
import java.util.Set;

import static com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableProduct.ATTRIBUTES_FIELD_NAME;

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

    @ManyToMany(mappedBy = ATTRIBUTES_FIELD_NAME)
    @Indexed(name = ATTRIBUTES_FIELD_NAME)
    private final Set<Attribute> attributes = new HashSet<>();
}
