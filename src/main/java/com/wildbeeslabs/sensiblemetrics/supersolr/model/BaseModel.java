/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
