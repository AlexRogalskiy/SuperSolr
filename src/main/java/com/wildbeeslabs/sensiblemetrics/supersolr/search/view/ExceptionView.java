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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableExceptionView;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_LOCALE;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.DateUtils.DEFAULT_DATE_FORMAT_PATTERN_EXT;

/**
 * Custom exception document view
 */
@Builder
@Data
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = ExposableExceptionView.VIEW_ID)
public class ExceptionView implements ExposableExceptionView, Serializable {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -328743858984114195L;

    @JacksonXmlProperty(localName = PATH_FIELD_NAME)
    @JsonProperty(PATH_FIELD_NAME)
    private String path;

    @JacksonXmlProperty(localName = CODE_FIELD_NAME)
    @JsonProperty(CODE_FIELD_NAME)
    private Integer code;

    @JacksonXmlProperty(localName = ERROR_FIELD_NAME)
    @JsonProperty(ERROR_FIELD_NAME)
    private String error;

    @JacksonXmlProperty(localName = MESSAGE_FIELD_NAME)
    @JsonProperty(MESSAGE_FIELD_NAME)
    private String message;

    @JacksonXmlProperty(localName = TIMESTAMP_FIELD_NAME)
    @JsonProperty(TIMESTAMP_FIELD_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT_PATTERN_EXT, locale = DEFAULT_DATE_FORMAT_LOCALE)
    private Date timestamp;
}