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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wildbeeslabs.sensiblemetrics.supersolr.config.properties.RedisConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PreDestroy;

/**
 * Custom redis configuration
 */
@Configuration
@EnableAutoConfiguration
@EnableRedisRepositories
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper customObjectMapper;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            final StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (final Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        final StringRedisTemplate template = new StringRedisTemplate(jedisConnectionFactory());
        final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(customObjectMapper);
        template.setEnableTransactionSupport(true);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisSerializer<String> stringSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(env.getRequiredProperty("supersolr.jedis.maxIdle", Integer.class));
        jedisPoolConfig.setMinIdle(env.getRequiredProperty("supersolr.jedis.minIdle", Integer.class));
        jedisPoolConfig.setMaxWaitMillis(env.getRequiredProperty("supersolr.jedis.maxWaitMillis", Integer.class));
        jedisPoolConfig.setMaxTotal(env.getRequiredProperty("supersolr.jedis.maxTotal", Integer.class));
        jedisPoolConfig.setTestOnBorrow(env.getRequiredProperty("supersolr.jedis.testOnBorrow", Boolean.class));
        jedisPoolConfig.setTestOnReturn(env.getRequiredProperty("supersolr.jedis.testOnReturn", Boolean.class));
        jedisPoolConfig.setTestWhileIdle(env.getRequiredProperty("supersolr.jedis.testWhileIdle", Boolean.class));
        return jedisPoolConfig;
    }

    @Bean(destroyMethod = "destroy")
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(jedisPoolConfig());
    }

    @Bean
    public RedisSentinelConfiguration sentinelConfig() {
        return new RedisSentinelConfiguration()
            .master(env.getRequiredProperty("supersolr.redis.master"))
            .sentinel(env.getRequiredProperty("supersolr.redis.hosts.host1"), env.getRequiredProperty("supersolr.redis.hosts.port1", Integer.class))
            .sentinel(env.getRequiredProperty("supersolr.redis.hosts.host2"), env.getRequiredProperty("supersolr.redis.hosts.port2", Integer.class))
            .sentinel(env.getRequiredProperty("supersolr.redis.hosts.host3"), env.getRequiredProperty("supersolr.redis.hosts.port3", Integer.class));
    }

    @PreDestroy
    public void flushTestDb() {
        jedisConnectionFactory().getConnection().flushDb();
    }
}
