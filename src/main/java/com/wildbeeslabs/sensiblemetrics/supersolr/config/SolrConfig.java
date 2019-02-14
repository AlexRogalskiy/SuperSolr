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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Custom solr configuration
 */
@Configuration
@EnableAsync
@EnableSolrRepositories(
        basePackages = "com.wildbeeslabs.sensiblemetrics.supersolr.repository",
        namedQueriesLocation = "classpath:solr-named-queries.properties",
        multicoreSupport = true,
        schemaCreationSupport = true)
@ComponentScan
@PropertySource("classpath:application.properties")
public class SolrConfig {

    @Autowired
    private Environment environment;

    @Bean
    public SolrClient solrClient(final @Value("${supersolr.solr.server.url}") String baseUrl) {
        return new HttpSolrClient.Builder()
                .withBaseSolrUrl(baseUrl)
                .build();
    }

    @Bean
    public EmbeddedSolrServerFactoryBean solrServerFactoryBean(final @Value("${supersolr.solr.solr.home}") String baseUrl) {
        final EmbeddedSolrServerFactoryBean factory = new EmbeddedSolrServerFactoryBean();
        factory.setSolrHome(environment.getRequiredProperty(baseUrl));
        return factory;
    }

    @Bean
    public SolrTemplate solrTemplate(final SolrClient solrClient) {
        return new SolrTemplate(solrClient);
    }
}