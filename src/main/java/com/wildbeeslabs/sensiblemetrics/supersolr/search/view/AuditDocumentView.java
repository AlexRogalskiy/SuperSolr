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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableAuditDocumentView;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableAuditModel.CHANGED_FIELD_NAME;
import static com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableAuditModel.CREATED_FIELD_NAME;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_LOCALE;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_PATTERN_EXT;

/**
 * Custom audit document view
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(
        value = {CREATED_FIELD_NAME, CHANGED_FIELD_NAME},
        allowGetters = true
)
@ApiModel(description = "All details about the audit document.")
public abstract class AuditDocumentView implements ExposableAuditDocumentView, Serializable {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -3372313387648971848L;

    @JacksonXmlProperty(localName = CREATED_FIELD_NAME)
    @JsonProperty(CREATED_FIELD_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT, locale = DEFAULT_DATE_FORMAT_LOCALE)
    private Date created;

    @JacksonXmlProperty(localName = CREATED_BY_FIELD_NAME)
    @JsonProperty(CREATED_BY_FIELD_NAME)
    private String createdBy;

    @JacksonXmlProperty(localName = CHANGED_FIELD_NAME)
    @JsonProperty(CHANGED_FIELD_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT, locale = DEFAULT_DATE_FORMAT_LOCALE)
    private Date changed;

    @JacksonXmlProperty(localName = CHANGED_BY_FIELD_NAME)
    @JsonProperty(CHANGED_BY_FIELD_NAME)
    private String changedBy;
}
