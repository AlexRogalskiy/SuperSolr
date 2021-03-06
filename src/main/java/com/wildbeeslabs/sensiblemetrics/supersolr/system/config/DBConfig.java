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
package com.wildbeeslabs.sensiblemetrics.supersolr.system.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.jmx.ParentAwareNamingStrategy;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.UUID;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.getString;
import static org.hibernate.cfg.AvailableSettings.*;

/**
 * Custom database configuration
 */
@Configuration
@RequiredArgsConstructor
@EnableAsync
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "com.wildbeeslabs.sensiblemetrics.supersolr.model",
        "com.wildbeeslabs.sensiblemetrics.supersolr.repository"
    }
)
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:application.yml")
})
public class DBConfig {

    /**
     * Default model/repository source packages
     */
    public static final String DEFAULT_REPOSITORY_PACKAGE = "com.wildbeeslabs.sensiblemetrics.supersolr.repository";
    public static final String DEFAULT_MODEL_PACKAGE = "com.wildbeeslabs.sensiblemetrics.supersolr.model";
    /**
     * Default persistence unit name
     */
    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "local";
    /**
     * Default domain name prefix
     */
    public static final String DEFAULT_DOMAIN_NAME_PREFIX = "domain_";
    /**
     * Default hibernate property names
     */
    public static final String EJB_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    public static final String TEMP_USE_JDBC_METADATA_DEFAULTS = "hibernate.temp.use_jdbc_metadata_defaults";
    public static final String USE_UNICODE = "hibernate.connection.useUnicode";
    public static final String CHARSET = "hibernate.connection.charSet";
    public static final String CHARACTER_ENCODING = "hibernate.connection.characterEncoding";

    private final Environment env;

    /**
     * Returns {@link LocalContainerEntityManagerFactoryBean} configuration
     *
     * @return {@link LocalContainerEntityManagerFactoryBean} configuration
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(defaultDataSource());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaProperties(jpaProperties());
        factoryBean.setJpaDialect(jpaDialect());
        factoryBean.setPackagesToScan(DEFAULT_REPOSITORY_PACKAGE, DEFAULT_MODEL_PACKAGE);
        factoryBean.setPersistenceUnitName(DEFAULT_PERSISTENCE_UNIT_NAME);
        //factoryBean.setPersistenceUnitManager(persistenceUnitManager());
        return factoryBean;
    }

    /**
     * Returns spring datasource {@link Properties} properties
     *
     * @return spring datasource {@link Properties} properties
     */
    @Bean
    public Properties properties() {
        final Properties properties = new Properties();
        //properties.put("spring.datasource.testOnBorrow", env.getRequiredProperty("supersolr.datasource.testOnBorrow"));
        //properties.put("spring.datasource.initialization-mode", env.getRequiredProperty("supersolr.datasource.initializationMode"));
        //properties.put("spring.datasource.testWhileIdle", env.getRequiredProperty("supersolr.datasource.testWhileIdle"));
        //properties.put("spring.datasource.timeBetweenEvictionRunsMillis", env.getRequiredProperty("supersolr.datasource.timeBetweenEvictionRunsMillis"));
        //properties.put("spring.datasource.minEvictableIdleTimeMillis", env.getRequiredProperty("supersolr.datasource.minEvictableIdleTimeMillis"));
        //properties.put("spring.datasource.validationQuery", env.getRequiredProperty("supersolr.datasource.validationQuery"));
        //properties.put("spring.datasource.max-active", env.getRequiredProperty("supersolr.datasource.maxActive"));
        //properties.put("spring.datasource.max-idle", env.getRequiredProperty("supersolr.datasource.maxIdle"));
        //properties.put("spring.datasource.max-wait", env.getRequiredProperty("supersolr.datasource.maxWait"));
        //properties.put("spring.datasource.cachePrepStmts", env.getRequiredProperty("supersolr.datasource.cachePrepStmts"));
        //properties.put("spring.datasource.prepStmtCacheSize", env.getRequiredProperty("supersolr.datasource.prepStmtCacheSize"));
        //properties.put("spring.datasource.prepStmtCacheSqlLimit", env.getRequiredProperty("supersolr.datasource.prepStmtCacheSqlLimit"));
        //properties.put("spring.datasource.useServerPrepStmts", env.getRequiredProperty("supersolr.datasource.useServerPrepStmts"));
        //properties.put("spring.datasource.useLocalSessionState", env.getRequiredProperty("supersolr.datasource.useLocalSessionState"));
        //properties.put("spring.datasource.useLocalTransactionState", env.getRequiredProperty("supersolr.datasource.useLocalTransactionState"));
        //properties.put("spring.datasource.rewriteBatchedStatements", env.getRequiredProperty("supersolr.datasource.rewriteBatchedStatements"));
        //properties.put("spring.datasource.cacheResultSetMetadata", env.getRequiredProperty("supersolr.datasource.cacheResultSetMetadata"));
        //properties.put("spring.datasource.cacheServerConfiguration", env.getRequiredProperty("supersolr.datasource.cacheServerConfiguration"));
        //properties.put("spring.datasource.elideSetAutoCommits", env.getRequiredProperty("supersolr.datasource.elideSetAutoCommits"));
        //properties.put("spring.datasource.maintainTimeStats", env.getRequiredProperty("supersolr.datasource.maintainTimeStats"));
        //properties.put("spring.datasource.verifyServerCertificate", env.getRequiredProperty("supersolr.datasource.verifyServerCertificate"));
        //properties.put("spring.datasource.useSqlComments", env.getRequiredProperty("supersolr.datasource.useSqlComments"));
        //properties.put("spring.datasource.maxStatementsPerConnection", env.getRequiredProperty("supersolr.datasource.maxStatementsPerConnection"));
        //properties.put("spring.datasource.maxStatements", env.getRequiredProperty("supersolr.datasource.maxStatements"));
        //properties.put("spring.datasource.minPoolSize", env.getRequiredProperty("supersolr.datasource.minPoolSize"));
        //properties.put("spring.datasource.maxPoolSize", env.getRequiredProperty("supersolr.datasource.maxPoolSize"));
        //properties.put("spring.datasource.autoCommit", env.getRequiredProperty("supersolr.datasource.autoCommit"));
        //properties.put("spring.datasource.allowPoolSuspension", env.getRequiredProperty("supersolr.datasource.allowPoolSuspension"));
        //properties.put("spring.datasource.checkoutTimeout", env.getRequiredProperty("supersolr.datasource.checkoutTimeout"));
        //properties.put("spring.datasource.idleConnectionTestPeriod", env.getRequiredProperty("supersolr.datasource.idleConnectionTestPeriod"));
        //properties.put("spring.datasource.initialPoolSize", env.getRequiredProperty("supersolr.datasource.initialPoolSize"));
        //properties.put("spring.datasource.testConnectionOnCheckin", env.getRequiredProperty("supersolr.datasource.testConnectionOnCheckin"));
        //properties.put("spring.datasource.allowUrlInLocalInfile", env.getRequiredProperty("supersolr.datasource.allowUrlInLocalInfile"));
        //properties.put("spring.datasource.useReadAheadInput", env.getRequiredProperty("supersolr.datasource.useReadAheadInput"));
        //properties.put("spring.datasource.useUnbufferedIO", env.getRequiredProperty("supersolr.datasource.useUnbufferedIO"));
        return properties;
    }

    /**
     * Returns default {@link DataSource} configuration
     *
     * @return default {@link DataSource} configuration
     */
    @Bean(destroyMethod = "close")
    public HikariDataSource defaultDataSource() {
        final HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(env.getRequiredProperty("supersolr.datasource.driver"));
        dataSourceConfig.setPoolName(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.poolName"));
        dataSourceConfig.setRegisterMbeans(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.registerMbeans", Boolean.class));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty("supersolr.datasource.url"));
        dataSourceConfig.setUsername(env.getRequiredProperty("supersolr.datasource.username"));
        dataSourceConfig.setPassword(env.getRequiredProperty("supersolr.datasource.password"));
        dataSourceConfig.setConnectionTestQuery(env.getRequiredProperty("supersolr.datasource.connectionTestQuery"));
        //dataSourceConfig.setDataSourceClassName(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.dataSourceClassName"));
        dataSourceConfig.setMinimumIdle(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.minimumIdle", Integer.class));
        dataSourceConfig.setMaximumPoolSize(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.maximumPoolSize", Integer.class));
        dataSourceConfig.setIdleTimeout(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.idleTimeout", Integer.class));
        dataSourceConfig.setIsolateInternalQueries(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.isolateInternalQueries", Boolean.class));
        //dataSourceConfig.setLeakDetectionThreshold(env.getRequiredProperty("supersolr.datasource.hibernate.hikari.leakDetectionThreshold", Integer.class));
        dataSourceConfig.setDataSourceProperties(properties());
        return new HikariDataSource(dataSourceConfig);
    }

//    /**
//     * Returns default {@link DataSourceProperties} configuration
//     *
//     * @return default {@link DataSourceProperties} configuration
//     */
//    @Bean
//    @Primary
//    @ConditionalOnMissingBean
//    @ConfigurationProperties(ignoreInvalidFields = true, prefix = "supersolr.datasource")
//    public DataSourceProperties dataSourceProperties() {
//        return new DataSourceProperties();
//    }

    /**
     * Returns {@link JpaVendorAdapter} configuration
     *
     * @return {@link JpaVendorAdapter} configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(env.getRequiredProperty("supersolr.datasource.jpa.generateDdl", Boolean.class));
        jpaVendorAdapter.setShowSql(env.getRequiredProperty("supersolr.datasource.jpa.showSql", Boolean.class));
        jpaVendorAdapter.setDatabasePlatform(env.getRequiredProperty("supersolr.datasource.databasePlatform"));
        return jpaVendorAdapter;
    }

//    @Bean
//    public JpaTransactionManager jpaTransactionManager(final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
//        final JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
//        return transactionManager;
//    }

    /**
     * Returns {@link HibernateTransactionManager} configuration by initial {@link SessionFactory} instance
     *
     * @param sessionFactory - initial {@link SessionFactory} instance
     * @return {@link HibernateTransactionManager} configuration
     */
    @Bean
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory) {
        final HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    @Bean
    @Primary
    public TransactionTemplate transactionTemplate(final PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    /**
     * Returns {@link ParentAwareNamingStrategy} configuration
     *
     * @return {@link ParentAwareNamingStrategy} configuration
     */
    @Bean
    @ConditionalOnMissingBean(value = ObjectNamingStrategy.class, search = SearchStrategy.CURRENT)
    public ParentAwareNamingStrategy objectNamingStrategy() {
        final ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(new AnnotationJmxAttributeSource());
        namingStrategy.setDefaultDomain(getString(DEFAULT_DOMAIN_NAME_PREFIX, UUID.randomUUID().toString()));
        return namingStrategy;
    }

    /**
     * Returns jpa {@link Properties} configuration
     *
     * @return jpa {@link Properties} configuration
     */
    @Bean
    public Properties jpaProperties() {
        final Properties jpaProperties = new Properties();
        // connection properties
        //jpaProperties.put(CONNECTION_PROVIDER, env.getRequiredProperty("supersolr.datasource.hibernate.connection.providerClass"));
        jpaProperties.put(POOL_SIZE, env.getRequiredProperty("supersolr.datasource.hibernate.connection.poolSize"));
        jpaProperties.put(AUTOCOMMIT, env.getRequiredProperty("supersolr.datasource.hibernate.connection.autocommit"));
//        jpaProperties.put(RELEASE_CONNECTIONS, env.getRequiredProperty("supersolr.datasource.hibernate.connection.releaseMode"));
        jpaProperties.put(USE_UNICODE, env.getRequiredProperty("supersolr.datasource.hibernate.connection.useUnicode"));
        jpaProperties.put(CHARSET, env.getRequiredProperty("supersolr.datasource.hibernate.connection.charSet"));
        jpaProperties.put(CHARACTER_ENCODING, env.getRequiredProperty("supersolr.datasource.hibernate.connection.characterEncoding"));

        // general properties
        jpaProperties.put(DIALECT, env.getRequiredProperty("supersolr.datasource.hibernate.dialect"));
        jpaProperties.put(CURRENT_SESSION_CONTEXT_CLASS, env.getRequiredProperty("supersolr.datasource.hibernate.currentSessionContextClass"));
        jpaProperties.put(HBM2DDL_AUTO, env.getRequiredProperty("supersolr.datasource.hibernate.hbm2ddl.auto"));
        jpaProperties.put(DEFAULT_SCHEMA, env.getRequiredProperty("supersolr.datasource.hibernate.hbm2ddl.defaultSchema"));
        jpaProperties.put(HBM2DDL_IMPORT_FILES, env.getRequiredProperty("supersolr.datasource.hibernate.hbm2ddl.importFiles"));
        jpaProperties.put(EJB_NAMING_STRATEGY, env.getRequiredProperty("supersolr.datasource.hibernate.ejb.namingStrategy"));
        jpaProperties.put(SHOW_SQL, env.getRequiredProperty("supersolr.datasource.hibernate.showSql"));
        jpaProperties.put(FORMAT_SQL, env.getRequiredProperty("supersolr.datasource.hibernate.formatSql"));
        jpaProperties.put(USE_SQL_COMMENTS, env.getRequiredProperty("supersolr.datasource.hibernate.useSqlComments"));
        jpaProperties.put(ENABLE_LAZY_LOAD_NO_TRANS, env.getRequiredProperty("supersolr.datasource.hibernate.enableLazyLoadNoTrans"));
        jpaProperties.put(MAX_FETCH_DEPTH, env.getRequiredProperty("supersolr.datasource.hibernate.maxFetchDepth"));
        jpaProperties.put(DEFAULT_BATCH_FETCH_SIZE, env.getRequiredProperty("supersolr.datasource.hibernate.defaultBatchFetchSize"));
        jpaProperties.put(GENERATE_STATISTICS, env.getRequiredProperty("supersolr.datasource.hibernate.generateStatistics"));
        jpaProperties.put(GLOBALLY_QUOTED_IDENTIFIERS, env.getRequiredProperty("supersolr.datasource.hibernate.globallyQuotedIdentifiers"));
        jpaProperties.put(USE_NEW_ID_GENERATOR_MAPPINGS, env.getRequiredProperty("supersolr.datasource.hibernate.id.newGeneratorMappings"));
        jpaProperties.put(FLUSH_BEFORE_COMPLETION, env.getRequiredProperty("supersolr.datasource.hibernate.transaction.flushBeforeCompletion"));
        jpaProperties.put(AUTO_CLOSE_SESSION, env.getRequiredProperty("supersolr.datasource.hibernate.transaction.autoCloseSession"));
        jpaProperties.put(MERGE_ENTITY_COPY_OBSERVER, env.getRequiredProperty("supersolr.datasource.hibernate.event.merge.entityCopyObserver"));
        jpaProperties.put(USE_REFLECTION_OPTIMIZER, env.getRequiredProperty("supersolr.datasource.hibernate.bytecode.useReflectionOptimizer"));
        jpaProperties.put(TEMP_USE_JDBC_METADATA_DEFAULTS, env.getRequiredProperty("supersolr.datasource.hibernate.temp.useJdbcMetadataDefaults"));
        //jpaProperties.put(MULTI_TENANT_CONNECTION_PROVIDER, env.getRequiredProperty("supersolr.datasource.hibernate.multiTenantConnectionProvider"));
        //jpaProperties.put(MULTI_TENANT, env.getRequiredProperty("supersolr.datasource.hibernate.multiTenancy"));

        // cache properties
        //jpaProperties.put("hibernate.cache.ehcache.missing_cache_strategy", env.getRequiredProperty("supersolr.datasource.hibernate.cache.ehcache.missingCacheStrategy"));
        //jpaProperties.put(CACHE_REGION_FACTORY, env.getRequiredProperty("supersolr.datasource.hibernate.cache.region.factoryClass"));
        //jpaProperties.put(USE_SECOND_LEVEL_CACHE, env.getRequiredProperty("supersolr.datasource.hibernate.cache.useSecondLevelCache"));
        //jpaProperties.put(USE_STRUCTURED_CACHE, env.getRequiredProperty("supersolr.datasource.hibernate.cache.useStructuredEntries"));
        //jpaProperties.put("hibernate.javax.cache.provider", env.getRequiredProperty("pem.datasource.hibernate.cache.providerClass"));
        //jpaProperties.put(USE_QUERY_CACHE, env.getRequiredProperty("supersolr.datasource.hibernate.cache.useQueryCache"));
        //jpaProperties.put(DEFAULT_CACHE_CONCURRENCY_STRATEGY, env.getRequiredProperty("pem.datasource.hibernate.cache.defaultCacheConcurrencyStrategy"));
        //jpaProperties.put("net.sf.ehcache.configurationResourceName", env.getRequiredProperty("supersolr.datasource.hibernate.cache.configurationResourceName"));

        // search properties
        jpaProperties.put("hibernate.search.exclusive_index_use", env.getRequiredProperty("supersolr.datasource.hibernate.search.exclusiveIndexUse"));
        jpaProperties.put("hibernate.search.lucene_version", env.getRequiredProperty("supersolr.datasource.hibernate.search.luceneVersion"));
        //jpaProperties.put("hibernate.search.default.worker.execution", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.worker.execution"));
        //jpaProperties.put("hibernate.search.default.directory_provider", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.directoryProvider"));
        //jpaProperties.put("hibernate.search.default.indexBase", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.indexBase"));
        //jpaProperties.put("hibernate.search.default.indexPath", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.indexPath"));
        //jpaProperties.put("hibernate.search.default.chunk_size", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.chunkSize"));
        //jpaProperties.put("hibernate.search.default.indexmanager", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.indexmanager"));
        //jpaProperties.put("hibernate.search.default.metadata_cachename", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.metadataCachename"));
        //jpaProperties.put("hibernate.search.default.data_cachename", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.dataCachename"));
        //jpaProperties.put("hibernate.search.default.locking_cachename", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.lockingCachename"));
        //jpaProperties.put("hibernate.search.default.batch.merge_factor", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.batch.mergeFactor"));
        //jpaProperties.put("hibernate.search.default.batch.max_buffered_docs", env.getRequiredProperty("supersolr.datasource.hibernate.search.default.batch.maxBufferedDocs"));

        // fetch/batch properties
        jpaProperties.put(STATEMENT_FETCH_SIZE, env.getRequiredProperty("supersolr.datasource.hibernate.jdbc.fetchSize"));
        jpaProperties.put(STATEMENT_BATCH_SIZE, env.getRequiredProperty("supersolr.datasource.hibernate.jdbc.batchSize"));
        jpaProperties.put(BATCH_VERSIONED_DATA, env.getRequiredProperty("supersolr.datasource.hibernate.jdbc.batchVersionedData"));
        jpaProperties.put(ORDER_INSERTS, env.getRequiredProperty("supersolr.datasource.hibernate.orderInserts"));
        jpaProperties.put(ORDER_UPDATES, env.getRequiredProperty("supersolr.datasource.hibernate.orderUpdates"));

        // connection pool properties
        jpaProperties.put(C3P0_MIN_SIZE, env.getRequiredProperty("supersolr.datasource.hibernate.c3p0.minSize"));
        jpaProperties.put(C3P0_MAX_SIZE, env.getRequiredProperty("supersolr.datasource.hibernate.c3p0.maxSize"));
        jpaProperties.put(C3P0_TIMEOUT, env.getRequiredProperty("supersolr.datasource.hibernate.c3p0.timeout"));
        jpaProperties.put(C3P0_MAX_STATEMENTS, env.getRequiredProperty("supersolr.datasource.hibernate.c3p0.maxStatements"));
        jpaProperties.put(C3P0_ACQUIRE_INCREMENT, env.getRequiredProperty("supersolr.datasource.hibernate.c3p0.idleTestPeriod"));

        // native properties
        jpaProperties.setProperty("org.hibernate.SQL", "true");
        jpaProperties.setProperty("org.hibernate.type", "true");
        return jpaProperties;
    }

    /**
     * Returns {@link JpaDialect} configuration
     *
     * @return {@link JpaDialect} configuration
     */
    @Bean
    public JpaDialect jpaDialect() {
        return new DefaultJpaDialect();
    }

    /**
     * Returns {@link PersistenceExceptionTranslationPostProcessor} configuration
     *
     * @return {@link PersistenceExceptionTranslationPostProcessor} configuration
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * Returns {@link PersistenceUnitManager} configuration
     *
     * @return {@link PersistenceUnitManager} configuration
     */
    @Bean
    public PersistenceUnitManager persistenceUnitManager() {
        final DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();
        manager.setDefaultDataSource(defaultDataSource());
        manager.setDefaultPersistenceUnitName(DEFAULT_PERSISTENCE_UNIT_NAME);
        return manager;
    }

    /**
     * Returns {@link BeanPostProcessor} configuration
     *
     * @return {@link BeanPostProcessor} configuration
     */
    @Bean
    public BeanPostProcessor postProcessor() {
        final PersistenceAnnotationBeanPostProcessor postProcessor = new PersistenceAnnotationBeanPostProcessor();
        postProcessor.setDefaultPersistenceUnitName(DEFAULT_PERSISTENCE_UNIT_NAME);
        return postProcessor;
    }

    /**
     * Returns {@link LocalSessionFactoryBean} configuration
     *
     * @return {@link LocalSessionFactoryBean} configuration
     */
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(defaultDataSource());
        sessionFactory.setPackagesToScan(DEFAULT_REPOSITORY_PACKAGE, DEFAULT_MODEL_PACKAGE);
        sessionFactory.setHibernateProperties(jpaProperties());
        return sessionFactory;
    }
}
