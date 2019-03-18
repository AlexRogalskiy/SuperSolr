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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Search result model
 *
 * @param <T> type of search item key
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "result")
public class SearchResult<T extends Serializable> {

    /**
     * Default search item {@code T}
     */
    @NonNull
    @JacksonXmlProperty(localName = "item")
    @JsonProperty("item")
    private T item;

    /**
     * Default error message list {@link List}
     */
    @Getter
    @JsonProperty("error")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "errors")
    private final Collection<String> errors = new ArrayList<>();

    /**
     * Default {@link SearchResult} constructor by input parameters
     *
     * @param key          - initial input search item key {@code T}
     * @param errorMessage - initial input error message
     */
    public SearchResult(final T key, final String errorMessage) {
        this(key);
        addError(errorMessage);
    }

    public boolean isSuccess() {
        return CollectionUtils.isEmpty(getErrors());
    }

    public void addError(final String errorMessage) {
        if (Objects.nonNull(errorMessage)) {
            this.getErrors().add(errorMessage);
        }
    }
}
