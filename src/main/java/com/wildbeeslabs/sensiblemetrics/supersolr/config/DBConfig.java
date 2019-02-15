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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.jmx.ParentAwareNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.UUID;

/**
 * Custom database configuration
 */
@Configuration
@EnableAsync
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.wildbeeslabs.sensiblemetrics.supersolr",
                "com.wildbeeslabs.sensiblemetrics.supersolr.search.repository",
        }
)
@PropertySource("classpath:application.properties")
public class DBConfig {

    /**
     * Default model/repository packages
     */
    public static final String DEFAULT_BASE_PACKAGE = "com.wildbeeslabs.sensiblemetrics.supersolr";
    public static final String DEFAULT_BASE_MODEL_PACKAGE = "com.wildbeeslabs.sensiblemetrics.supersolr.model";
    /**
     * Default persistence unit name
     */
    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "local";
    /**
     * Default domain name prefix
     */
    public static final String DEFAULT_DOMAIN_NAME_PREFIX = "domain_";

    @Autowired
    private Environment env;

    /**
     * Returns local container entity manager factory instance {@link LocalContainerEntityManagerFactoryBean}
     *
     * @param defaultDataSource -  initial datasource instance {@link DataSource}
     * @param jpaVendorAdapter  - initial jpa vendor adapter instance {@link JpaVendorAdapter}
     * @param jpaProperties     - initial jpa properties {@link Properties}
     * @param jpaDialect        - initial jpa dialect instance {@link JpaDialect}
     * @return local container entity manager factory {@link LocalContainerEntityManagerFactoryBean}
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource defaultDataSource,
                                                                       final JpaVendorAdapter jpaVendorAdapter,
                                                                       final @Qualifier("jpaProperties") Properties jpaProperties,
                                                                       final JpaDialect jpaDialect) {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(defaultDataSource);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setJpaProperties(jpaProperties);
        factoryBean.setJpaDialect(jpaDialect);
        factoryBean.setPackagesToScan(DEFAULT_BASE_PACKAGE, DEFAULT_BASE_MODEL_PACKAGE);
        factoryBean.setPersistenceUnitName(DEFAULT_PERSISTENCE_UNIT_NAME);
        return factoryBean;
    }

    /**
     * Returns datasource instance {@link DataSource}
     *
     * @return datasource {@link DataSource}
     */
    @Bean(destroyMethod = "close")
    public HikariDataSource defaultDataSource() {
        final HikariConfig dataSourceConfig = new HikariConfig();
        final Properties dataSourceProperties = new Properties();
        dataSourceProperties.put("spring.datasource.testOnBorrow", env.getRequiredProperty("supersolr.datasource.db.testOnBorrow"));
        dataSourceProperties.put("spring.datasource.initialization-mode", env.getRequiredProperty("supersolr.datasource.db.initializationMode"));
        dataSourceProperties.put("spring.datasource.testWhileIdle", env.getRequiredProperty("supersolr.datasource.db.testWhileIdle"));
        dataSourceProperties.put("spring.datasource.timeBetweenEvictionRunsMillis", env.getRequiredProperty("supersolr.datasource.db.timeBetweenEvictionRunsMillis"));
        dataSourceProperties.put("spring.datasource.minEvictableIdleTimeMillis", env.getRequiredProperty("supersolr.datasource.db.minEvictableIdleTimeMillis"));
        dataSourceProperties.put("spring.datasource.validationQuery", env.getRequiredProperty("supersolr.datasource.db.validationQuery"));
        dataSourceProperties.put("spring.datasource.max-active", env.getRequiredProperty("supersolr.datasource.db.maxActive"));
        dataSourceProperties.put("spring.datasource.max-idle", env.getRequiredProperty("supersolr.datasource.db.maxIdle"));
        dataSourceProperties.put("spring.datasource.max-wait", env.getRequiredProperty("supersolr.datasource.db.maxWait"));

        dataSourceProperties.put("spring.datasource.cachePrepStmts", env.getRequiredProperty("supersolr.datasource.db.cachePrepStmts"));
        dataSourceProperties.put("spring.datasource.prepStmtCacheSize", env.getRequiredProperty("supersolr.datasource.db.prepStmtCacheSize"));
        dataSourceProperties.put("spring.datasource.prepStmtCacheSqlLimit", env.getRequiredProperty("supersolr.datasource.db.prepStmtCacheSqlLimit"));
        dataSourceProperties.put("spring.datasource.useServerPrepStmts", env.getRequiredProperty("supersolr.datasource.db.useServerPrepStmts"));
        dataSourceProperties.put("spring.datasource.useLocalSessionState", env.getRequiredProperty("supersolr.datasource.db.useLocalSessionState"));
        dataSourceProperties.put("spring.datasource.rewriteBatchedStatements", env.getRequiredProperty("supersolr.datasource.db.rewriteBatchedStatements"));
        dataSourceProperties.put("spring.datasource.cacheResultSetMetadata", env.getRequiredProperty("supersolr.datasource.db.cacheResultSetMetadata"));
        dataSourceProperties.put("spring.datasource.cacheServerConfiguration", env.getRequiredProperty("supersolr.datasource.db.cacheServerConfiguration"));
        dataSourceProperties.put("spring.datasource.elideSetAutoCommits", env.getRequiredProperty("supersolr.datasource.db.elideSetAutoCommits"));
        dataSourceProperties.put("spring.datasource.maintainTimeStats", env.getRequiredProperty("supersolr.datasource.db.maintainTimeStats"));
        dataSourceProperties.put("spring.datasource.verifyServerCertificate", env.getRequiredProperty("supersolr.datasource.db.verifyServerCertificate"));
        dataSourceProperties.put("spring.datasource.useSqlComments", env.getRequiredProperty("supersolr.datasource.db.useSqlComments"));
        dataSourceProperties.put("spring.datasource.maxStatementsPerConnection", env.getRequiredProperty("supersolr.datasource.db.maxStatementsPerConnection"));
        dataSourceProperties.put("spring.datasource.maxStatements", env.getRequiredProperty("supersolr.datasource.db.maxStatements"));
        dataSourceProperties.put("spring.datasource.minPoolSize", env.getRequiredProperty("supersolr.datasource.db.minPoolSize"));
        dataSourceProperties.put("spring.datasource.maxPoolSize", env.getRequiredProperty("supersolr.datasource.db.maxPoolSize"));
        dataSourceProperties.put("spring.datasource.autoCommit", env.getRequiredProperty("supersolr.datasource.db.autoCommit"));
        dataSourceProperties.put("spring.datasource.allowPoolSuspension", env.getRequiredProperty("supersolr.datasource.db.allowPoolSuspension"));
        dataSourceProperties.put("spring.datasource.checkoutTimeout", env.getRequiredProperty("supersolr.datasource.db.checkoutTimeout"));
        dataSourceProperties.put("spring.datasource.idleConnectionTestPeriod", env.getRequiredProperty("supersolr.datasource.db.idleConnectionTestPeriod"));
        dataSourceProperties.put("spring.datasource.initialPoolSize", env.getRequiredProperty("supersolr.datasource.db.initialPoolSize"));
        dataSourceProperties.put("spring.datasource.testConnectionOnCheckin", env.getRequiredProperty("supersolr.datasource.db.testConnectionOnCheckin"));

        dataSourceConfig.setDriverClassName(env.getRequiredProperty("supersolr.datasource.db.driver"));
        dataSourceConfig.setPoolName(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.poolName"));
        dataSourceConfig.setRegisterMbeans(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.registerMbeans", Boolean.class));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty("supersolr.datasource.db.url"));
        dataSourceConfig.setUsername(env.getRequiredProperty("supersolr.datasource.db.username"));
        dataSourceConfig.setPassword(env.getRequiredProperty("supersolr.datasource.db.password"));
        dataSourceConfig.setConnectionTestQuery(env.getRequiredProperty("supersolr.datasource.db.connectionTestQuery"));
        //dataSourceConfig.setDataSourceClassName(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.dataSourceClassName"));
        dataSourceConfig.setMinimumIdle(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.minimumIdle", Integer.class));
        dataSourceConfig.setMaximumPoolSize(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.maximumPoolSize", Integer.class));
        dataSourceConfig.setIdleTimeout(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.idleTimeout", Integer.class));
        dataSourceConfig.setIsolateInternalQueries(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.isolateInternalQueries", Boolean.class));
        //dataSourceConfig.setLeakDetectionThreshold(env.getRequiredProperty("supersolr.datasource.config.hibernate.hikari.leakDetectionThreshold", Integer.class));
        dataSourceConfig.setDataSourceProperties(dataSourceProperties);
        return new HikariDataSource(dataSourceConfig);
    }

    /**
     * Returns jpa vendor adapter instance {@link JpaVendorAdapter}
     *
     * @return jpa vendor adapter {@link JpaVendorAdapter}
     */
    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(env.getRequiredProperty("supersolr.datasource.config.jpa.generateDdl", Boolean.class));
        jpaVendorAdapter.setShowSql(env.getRequiredProperty("supersolr.datasource.config.jpa.showSql", Boolean.class));
        jpaVendorAdapter.setDatabasePlatform(env.getRequiredProperty("supersolr.datasource.db.databasePlatform"));
        return jpaVendorAdapter;
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager(final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return transactionManager;
    }

    /**
     * Return parent aware naming strategy instance (@link ParentAwareNamingStrategy)
     *
     * @return parent aware naming strategy (@link ParentAwareNamingStrategy)
     */
    @Bean
    @ConditionalOnMissingBean(value = ObjectNamingStrategy.class, search = SearchStrategy.CURRENT)
    public ParentAwareNamingStrategy objectNamingStrategy() {
        final ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(new AnnotationJmxAttributeSource());
        namingStrategy.setDefaultDomain(DEFAULT_DOMAIN_NAME_PREFIX + UUID.randomUUID().toString());
        return namingStrategy;
    }

    /**
     * Returns jpa properties {@link Properties}
     *
     * @return jpa properties {@link Properties}
     */
    @Bean
    public Properties jpaProperties() {
        final Properties jpaProperties = new Properties();
        // Hibernate connection properties
        //jpaProperties.put("hibernate.connection.provider_class", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.providerClass"));
        jpaProperties.put("hibernate.connection.pool_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.poolSize"));
        jpaProperties.put("hibernate.connection.autocommit", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.autocommit"));
        jpaProperties.put("hibernate.connection.release_mode", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.releaseMode"));
        jpaProperties.put("hibernate.connection.useUnicode", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.useUnicode"));
        jpaProperties.put("hibernate.connection.charSet", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.charSet"));
        jpaProperties.put("hibernate.connection.characterEncoding", env.getRequiredProperty("supersolr.datasource.config.hibernate.connection.characterEncoding"));

        // Hibernate general properties
        jpaProperties.put("hibernate.dialect", env.getRequiredProperty("supersolr.datasource.config.hibernate.dialect"));
        jpaProperties.put("hibernate.current_session_context_class", env.getRequiredProperty("supersolr.datasource.config.hibernate.currentSessionContextClass"));
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("supersolr.datasource.config.hibernate.hbm2ddl.auto"));
        jpaProperties.put("hibernate.hbm2ddl.import_files", env.getRequiredProperty("supersolr.datasource.config.hibernate.hbm2ddl.importFiles"));
        jpaProperties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("supersolr.datasource.config.hibernate.ejb.namingStrategy"));
        jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("supersolr.datasource.config.hibernate.showSql"));
        jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("supersolr.datasource.config.hibernate.formatSql"));
        jpaProperties.put("hibernate.use_sql_comments", env.getRequiredProperty("supersolr.datasource.config.hibernate.useSqlComments"));
        jpaProperties.put("hibernate.enable_lazy_load_no_trans", env.getRequiredProperty("supersolr.datasource.config.hibernate.enableLazyLoadNoTrans"));
        jpaProperties.put("hibernate.max_fetch_depth", env.getRequiredProperty("supersolr.datasource.config.hibernate.maxFetchDepth"));
        jpaProperties.put("hibernate.default_batch_fetch_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.defaultBatchFetchSize"));
        jpaProperties.put("hibernate.generate_statistics", env.getRequiredProperty("supersolr.datasource.config.hibernate.generateStatistics"));
        jpaProperties.put("hibernate.globally_quoted_identifiers", env.getRequiredProperty("supersolr.datasource.config.hibernate.globallyQuotedIdentifiers"));
        jpaProperties.put("hibernate.id.new_generator_mappings", env.getRequiredProperty("supersolr.datasource.config.hibernate.id.newGeneratorMappings"));
        jpaProperties.put("hibernate.transaction.flush_before_completion", env.getRequiredProperty("supersolr.datasource.config.hibernate.transaction.flushBeforeCompletion"));
        jpaProperties.put("hibernate.transaction.auto_close_session", env.getRequiredProperty("supersolr.datasource.config.hibernate.transaction.autoCloseSession"));
        jpaProperties.put("hibernate.getString.merge.entity_copy_observer", env.getRequiredProperty("supersolr.datasource.config.hibernate.event.merge.entityCopyObserver"));
        jpaProperties.put("hibernate.bytecode.use_reflection_optimizer", env.getRequiredProperty("supersolr.datasource.config.hibernate.bytecode.useReflectionOptimizer"));
        jpaProperties.put("hibernate.temp.use_jdbc_metadata_defaults", env.getRequiredProperty("supersolr.datasource.config.hibernate.temp.useJdbcMetadataDefaults"));
        //jpaProperties.put("hibernate.multi_tenant_connection_provider", env.getRequiredProperty("supersolr.datasource.config.hibernate.multiTenantConnectionProvider"));
        //jpaProperties.put("hibernate.multiTenancy", env.getRequiredProperty("supersolr.datasource.config.hibernate.multiTenancy"));

        // Hibernate cache properties
//        jpaProperties.put("hibernate.cache.ehcache.missing_cache_strategy", env.getRequiredProperty("supersolr.datasource.config.hibernate.cache.ehcache.missingCacheStrategy"));
//        jpaProperties.put("hibernate.cache.region.factory_class", env.getRequiredProperty("supersolr.datasource.config.hibernate.cache.region.factoryClass"));
//        jpaProperties.put("hibernate.cache.use_second_level_cache", env.getRequiredProperty("supersolr.datasource.config.hibernate.cache.useSecondLevelCache"));
//        jpaProperties.put("hibernate.cache.use_structured_entries", env.getRequiredProperty("supersolr.datasource.config.hibernate.cache.useStructuredEntries"));
//        jpaProperties.put("hibernate.cache.use_query_cache", env.getRequiredProperty("supersolr.datasource.config.hibernate.cache.useQueryCache"));
//        jpaProperties.put("net.sf.ehcache.configurationResourceName", env.getRequiredProperty("supersolr.datasource.config.hibernate.cache.configurationResourceName"));

        // Hibernate search properties
        jpaProperties.put("hibernate.search.exclusive_index_use", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.exclusiveIndexUse"));
        jpaProperties.put("hibernate.search.lucene_version", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.luceneVersion"));
        jpaProperties.put("hibernate.search.default.worker.execution", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.worker.execution"));
        jpaProperties.put("hibernate.search.default.directory_provider", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.directoryProvider"));
        jpaProperties.put("hibernate.search.default.indexBase", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.indexBase"));
        jpaProperties.put("hibernate.search.default.indexPath", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.indexPath"));
        jpaProperties.put("hibernate.search.default.chunk_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.chunkSize"));
        jpaProperties.put("hibernate.search.default.indexmanager", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.indexmanager"));
        jpaProperties.put("hibernate.search.default.metadata_cachename", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.metadataCachename"));
        jpaProperties.put("hibernate.search.default.data_cachename", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.dataCachename"));
        jpaProperties.put("hibernate.search.default.locking_cachename", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.lockingCachename"));
        jpaProperties.put("hibernate.search.default.batch.merge_factor", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.batch.mergeFactor"));
        jpaProperties.put("hibernate.search.default.batch.max_buffered_docs", env.getRequiredProperty("supersolr.datasource.config.hibernate.search.default.batch.maxBufferedDocs"));

        // Hibernate fetch / batch properties
        jpaProperties.put("hibernate.jdbc.fetch_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.jdbc.fetchSize"));
        jpaProperties.put("hibernate.jdbc.batch_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.jdbc.batchSize"));
        jpaProperties.put("hibernate.jdbc.batch_versioned_data", env.getRequiredProperty("supersolr.datasource.config.hibernate.jdbc.batchVersionedData"));
        jpaProperties.put("hibernate.order_inserts", env.getRequiredProperty("supersolr.datasource.config.hibernate.orderInserts"));
        jpaProperties.put("hibernate.order_updates", env.getRequiredProperty("supersolr.datasource.config.hibernate.orderUpdates"));

        // Hibernate native properties
        jpaProperties.setProperty("org.hibernate.SQL", "true");
        jpaProperties.setProperty("org.hibernate.type", "true");

        // Hikari connection pool properties
        jpaProperties.put("hibernate.c3p0.min_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.c3p0.minSize"));
        jpaProperties.put("hibernate.c3p0.max_size", env.getRequiredProperty("supersolr.datasource.config.hibernate.c3p0.maxSize"));
        jpaProperties.put("hibernate.c3p0.timeout", env.getRequiredProperty("supersolr.datasource.config.hibernate.c3p0.timeout"));
        jpaProperties.put("hibernate.c3p0.max_statements", env.getRequiredProperty("supersolr.datasource.config.hibernate.c3p0.maxStatements"));
        jpaProperties.put("hibernate.c3p0.idle_test_period", env.getRequiredProperty("supersolr.datasource.config.hibernate.c3p0.idleTestPeriod"));
        return jpaProperties;
    }

    /**
     * Returns jpa dialect instance {@link JpaDialect}
     *
     * @return jpa dialect {@link JpaDialect}
     */
    @Bean
    public JpaDialect jpaDialect() {
        return new DefaultJpaDialect();
    }

    /**
     * Returns persistence exception translation post processor instance {@link PersistenceExceptionTranslationPostProcessor}
     *
     * @return persistence exception translation post processor {@link PersistenceExceptionTranslationPostProcessor}
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * Returns persistence unit manager {@link PersistenceUnitManager}
     *
     * @return persistence unit manager {@link PersistenceUnitManager}
     */
    @Bean
    public PersistenceUnitManager persistenceUnitManager() {
        final DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();
        manager.setDefaultDataSource(defaultDataSource());
        manager.setDefaultPersistenceUnitName(DEFAULT_PERSISTENCE_UNIT_NAME);
        return manager;
    }

    /**
     * Returns post processor instance {@link BeanPostProcessor}
     *
     * @return post processor instance {@link BeanPostProcessor}
     */
    @Bean
    public BeanPostProcessor postProcessor() {
        final PersistenceAnnotationBeanPostProcessor postProcessor = new PersistenceAnnotationBeanPostProcessor();
        postProcessor.setDefaultPersistenceUnitName(DEFAULT_PERSISTENCE_UNIT_NAME);
        return postProcessor;
    }
}