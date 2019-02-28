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
package com.wildbeeslabs.sensiblemetrics.supersolr.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Locale;

/**
 * Custom swagger configuration
 */
@Configuration
public class SwaggerConfig {

    /**
     * Default resource management tag
     */
    public static final String TAG_RESOURCE_MANAGEMENT = "Resource Management";
    /**
     * Default main functional tag
     */
    public static final String TAG_MAIN_FUNCTIONAL = "Main functional";

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .paths(PathSelectors.regex("/api/.*"))
            .build()
            .tags(
                new Tag(TAG_MAIN_FUNCTIONAL, "All apis relating to main service purpose"),
                new Tag(TAG_RESOURCE_MANAGEMENT, "All apis relating to upload resources"));
    }

    @Component
    @Primary
    public class CustomObjectMapper extends ObjectMapper {

        /**
         * Default explicit serialVersionUID for interoperability
         */
        private static final long serialVersionUID = -5589192375212209265L;

        public CustomObjectMapper() {
            setDefaultMergeable(Boolean.TRUE);
            setLocale(Locale.getDefault());
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
            setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

            disable(SerializationFeature.INDENT_OUTPUT);
            disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        }

        public ObjectMapper copy() {
            _checkInvalidCopy(ObjectMapper.class);
            return new CustomObjectMapper();
        }
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Super Solr API")
            .description("description")
            .contact(new Contact("supersolr", "supersolr.com", "info@supersolr.com"))
            .license("MIT")
            .licenseUrl("http://www.opensource.org/licenses/mit-license.php")
            .version("2.0")
            .build();
    }
}
