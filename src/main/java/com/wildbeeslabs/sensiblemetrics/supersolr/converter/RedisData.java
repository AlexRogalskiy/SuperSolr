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
package com.wildbeeslabs.sensiblemetrics.supersolr.converter;

import lombok.*;

import java.io.Serializable;

/**
 * Custom redis data model
 */
public class RedisData {

    @Data
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode
    @ToString
    public static class RedisPort implements Serializable {

        /**
         * Default explicit serialVersionUID for interoperability
         */
        private static final long serialVersionUID = 8338897962608852437L;

        @NonNull
        @Getter
        private Integer port;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode
    @ToString
    public static class RedisHost implements Serializable {

        /**
         * Default explicit serialVersionUID for interoperability
         */
        private static final long serialVersionUID = 3605617087361538636L;

        @NonNull
        @Getter
        private String host;
    }
}
