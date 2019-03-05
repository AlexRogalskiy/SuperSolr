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

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

/**
 * Redis data converter implementation
 */
@Configuration
public class RedisDataConverter {

    @Bean
    @ConfigurationPropertiesBinding
    public Str2Host strToHost() {
        return new Str2Host();
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Int2Port intToPort() {
        return new Int2Port();
    }

    /**
     * Custom string to host redis converter {@link Converter}
     */
    public class Str2Host implements Converter<String, RedisData.RedisHost> {
        @Override
        public RedisData.RedisHost convert(final String source) {
            if (StringUtils.isNotBlank(source)) {
                return RedisData.RedisHost.of(source);
            }
            return null;
        }
    }

    /**
     * Custom integer to port redis converter {@link Converter}
     */
    public class Int2Port implements Converter<Integer, RedisData.RedisPort> {
        @Override
        public RedisData.RedisPort convert(final Integer source) {
            if (Objects.nonNull(source)) {
                return RedisData.RedisPort.of(source);
            }
            return null;
        }
    }
}
