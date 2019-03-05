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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.interfaces.ExposableBaseDocumentView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

/**
 * Base document view {@link AuditDocumentView}
 *
 * @param <ID> type of document identifier
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = ExposableBaseDocumentView.VIEW_ID)
@ApiModel(value = ExposableBaseDocumentView.VIEW_ID, description = "All details about document")
public abstract class BaseDocumentView<ID extends Serializable> extends AuditDocumentView implements ExposableBaseDocumentView {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 602615748387358410L;

    @ApiModelProperty(value = "Document ID", name = "id", example = "id", required = true)
    @JacksonXmlProperty(localName = ID_FIELD_NAME)
    @JsonProperty(ID_FIELD_NAME)
    private ID id;

    @ApiModelProperty(value = "Document score", name = "score", example = "score")
    @JacksonXmlProperty(localName = SCORE_FIELD_NAME)
    @JsonProperty(SCORE_FIELD_NAME)
    private float score;

    @ApiModelProperty(value = "Document highlights", name = "highlights", example = "highlights", access = "limited")
    @JacksonXmlProperty(localName = HIGHLIGHTS_FIELD_NAME)
    @JsonProperty(HIGHLIGHTS_FIELD_NAME)
    private final Map<String, List<String>> highlights = new HashMap<>();

    public void setHighlights(final Map<String, List<String>> highlights) {
        this.getHighlights().clear();
        Optional.ofNullable(highlights)
            .orElseGet(Collections::emptyMap)
            .entrySet()
            .forEach(entry -> this.addHighlight(entry.getKey(), entry.getValue()));
    }

    public void addHighlight(final String key, final List<String> value) {
        if (Objects.nonNull(key)) {
            this.getHighlights().put(key, value);
        }
    }
}
