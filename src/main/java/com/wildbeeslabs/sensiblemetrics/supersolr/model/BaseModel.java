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
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils;
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
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Custom full-text search base document model
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
public abstract class BaseModel<T> implements Persistable<T>, Serializable {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 6444143028591284804L;

    @Id
    @Indexed(name = "id", type = "string")
    private T id;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = DateUtils.DEFAULT_DATE_FORMAT_PATTERN_EXT)
    @Column(name = "created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @CreatedBy
    @NotNull(message = "Field <createdBy> cannot be blank")
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = DateUtils.DEFAULT_DATE_FORMAT_PATTERN_EXT)
    @Column(name = "changed", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date changed;

    @LastModifiedBy
    @Column(name = "changed_by", insertable = false)
    private String changedBy;

    @Version
    @ColumnDefault("0")
    @Column(name = "version", insertable = false, updatable = false)
    //@Generated(GenerationTime.ALWAYS)
    private Long version;

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
