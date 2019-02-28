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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableAuditDocumentView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableAuditModel.*;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_LOCALE;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_PATTERN_EXT;

/**
 * Custom audit document view
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(
    value = {
        CREATED_FIELD_NAME,
        CREATED_BY_FIELD_NAME,
        CHANGED_FIELD_NAME,
        CHANGED_BY_FIELD_NAME
    },
    allowGetters = true,
    ignoreUnknown = true
)
@JacksonXmlRootElement(localName = ExposableAuditDocumentView.VIEW_ID)
@ApiModel(value = ExposableAuditDocumentView.VIEW_ID, description = "All audit details about document")
public abstract class AuditDocumentView implements ExposableAuditDocumentView, Serializable {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -3372313387648971848L;

    @ApiModelProperty(value = "Audit created date", name = "createdDate", example = "01/01/2018 11:00:00", required = true)
    @JacksonXmlProperty(localName = CREATED_FIELD_NAME)
    @JsonProperty(CREATED_FIELD_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT, locale = DEFAULT_DATE_FORMAT_LOCALE)
    private Date created;

    @ApiModelProperty(value = "Audit created by", name = "createdBy", example = "createdBy", required = true)
    @JacksonXmlProperty(localName = CREATED_BY_FIELD_NAME)
    @JsonProperty(CREATED_BY_FIELD_NAME)
    private String createdBy;

    @ApiModelProperty(value = "Audit changed date", name = "changedDate", example = "01/01/2019 18:00:00")
    @JacksonXmlProperty(localName = CHANGED_FIELD_NAME)
    @JsonProperty(CHANGED_FIELD_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT, locale = DEFAULT_DATE_FORMAT_LOCALE)
    private Date changed;

    @ApiModelProperty(value = "Audit changed by", name = "changedBy", example = "changedBy")
    @JacksonXmlProperty(localName = CHANGED_BY_FIELD_NAME)
    @JsonProperty(CHANGED_BY_FIELD_NAME)
    private String changedBy;
}
