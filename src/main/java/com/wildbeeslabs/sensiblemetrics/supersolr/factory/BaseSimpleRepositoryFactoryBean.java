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
package com.wildbeeslabs.sensiblemetrics.supersolr.factory;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.BaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.impl.BaseSimpleServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;

import java.io.Serializable;

/**
 * Custom base simple repository factory bean implementation {@link SolrRepositoryFactoryBean}
 *
 * @param <E>  type of entity model
 * @param <ID> type of entity identifier
 */
public class BaseSimpleRepositoryFactoryBean<E extends BaseModel<ID>, ID extends Serializable> extends SolrRepositoryFactoryBean<BaseSimpleServiceImpl<E, ID>, E, ID> {

    /**
     * Custom string simple repository factory bean
     */
    public static final BaseSimpleRepositoryFactoryBean<BaseModel<String>, String> DEFAULT_STRING_SIMPLE_REPOSITORY_FACTORY_BEAN = new BaseSimpleRepositoryFactoryBean<>();

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new SimpleSolrRepositoryFactory<>(getSolrOperations());
    }

    /**
     * Simple solr repository factory implementation {@link SolrRepositoryFactory}
     *
     * @param <E>  type of entity model
     * @param <ID> type of entity identifier
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    private static class SimpleSolrRepositoryFactory<E extends BaseModel<ID>, ID extends Serializable> extends SolrRepositoryFactory {

        /**
         * Default solr operations solr operations {@link SolrOperations}
         */
        private final SolrOperations solrOperations;

        /**
         * Default base simple repository factory bean with initial solr operations instance {@link SolrOperations}
         *
         * @param solrOperations - initial solr operations instance {@link SolrOperations}
         */
        public SimpleSolrRepositoryFactory(final SolrOperations solrOperations) {
            super(solrOperations);
            this.solrOperations = solrOperations;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object getTargetRepository(final RepositoryInformation metadata) {
            return new BaseSimpleServiceImpl<>(getSolrOperations(), (Class<E>) metadata.getDomainType());
        }

        @Override
        protected Class<?> getRepositoryBaseClass(final RepositoryMetadata metadata) {
            return BaseSimpleServiceImpl.class;
        }
    }
}