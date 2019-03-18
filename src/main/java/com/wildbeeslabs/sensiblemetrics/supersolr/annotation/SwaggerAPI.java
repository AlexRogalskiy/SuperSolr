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
package com.wildbeeslabs.sensiblemetrics.supersolr.annotation;

import io.swagger.annotations.*;
import org.springframework.http.MediaType;

import java.lang.annotation.*;

/**
 * Swagger API definition annotation
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SwaggerDefinition(
    info = @Info(
        description = "SuperSolr",
        version = "1.0.0-RELEASE",
        title = "SuperSolr REST API",
        contact = @Contact(
            name = "ARogalskiy",
            email = "alexander.rogalskiy@supersolr.com",
            url = "http://www.supersolr.com"
        ),
        license = @License(
            name = "MIT",
            url = "http://www.opensource.org/licenses/mit-license.php"
        )
    ),
    basePath = "/api/*",
    consumes = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    },
    produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    },
    schemes = {
        SwaggerDefinition.Scheme.HTTP,
        SwaggerDefinition.Scheme.HTTPS
    },
    externalDocs = @ExternalDocs(
        value = "SuperSolr Document Info",
        url = "http://supersolr.com")
)
public @interface SwaggerAPI {
}
