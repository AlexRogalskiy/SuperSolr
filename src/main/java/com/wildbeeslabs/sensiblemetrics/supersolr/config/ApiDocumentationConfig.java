package com.wildbeeslabs.sensiblemetrics.supersolr.config;

import io.swagger.annotations.*;
import org.springframework.http.MediaType;

@SwaggerDefinition(
        info = @Info(
                description = "SuperSolr",
                version = "V01012",
                title = "SuperSolr Resource API",
                contact = @Contact(
                        name = "ARogalskiy",
                        email = "alexander.rogalskiy@supersol.com",
                        url = "http://www.supersol.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        basePath = "/api/*",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        externalDocs = @ExternalDocs(value = "Read This For Sure", url = "http://supersolr.com")
)
public interface ApiDocumentationConfig {
}