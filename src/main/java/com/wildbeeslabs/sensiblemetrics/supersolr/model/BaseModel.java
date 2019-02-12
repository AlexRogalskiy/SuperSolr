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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.constraint.ChronologicalDates;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.SearchableModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.repository.Score;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_PATTERN_EXT;

/**
 * Custom full-text search base model
 *
 * @param <ID> type of entity identifier
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@ChronologicalDates
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseModel<ID extends Serializable> implements SearchableModel, Persistable<ID>, Serializable {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 6444143028591284804L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @Indexed(name = "id")
    private ID id;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT)
    @Column(name = SearchableModel.CREATED_FIELD_NAME, nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Indexed(SearchableModel.CREATED_FIELD_NAME)
    private Date created;

    @CreatedBy
    @NotNull(message = "Field <createdBy> cannot be blank")
    @Column(name = SearchableModel.CREATED_BY_FIELD_NAME, nullable = false, updatable = false)
    @Indexed(SearchableModel.CREATED_BY_FIELD_NAME)
    private String createdBy;

    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT)
    @Column(name = SearchableModel.CHANGED_FIELD_NAME, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Indexed(SearchableModel.CHANGED_FIELD_NAME)
    private Date changed;

    @LastModifiedBy
    @Column(name = SearchableModel.CHANGED_BY_FIELD_NAME, insertable = false)
    @Indexed(SearchableModel.CHANGED_BY_FIELD_NAME)
    private String changedBy;

    @Version
    @ColumnDefault("0")
    @Column(name = SearchableModel.VERSION_BY_FIELD_NAME, insertable = false, updatable = false)
    //@Generated(GenerationTime.ALWAYS)
    @Indexed(SearchableModel.VERSION_BY_FIELD_NAME)
    private Long version;

    @Score
    private float score;

    @PrePersist
    protected void onCreate() {
        this.createdBy = getClass().getName();
    }

    @PreUpdate
    protected void onUpdate() {
        this.changedBy = getClass().getName();
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(this.getId());
    }
}
