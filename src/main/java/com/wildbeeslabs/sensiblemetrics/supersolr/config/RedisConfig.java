///*
// * The MIT License
// *
// * Copyright 2019 WildBees Labs, Inc.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.wildbeeslabs.sensiblemetrics.supersolr.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * Custom redis configuration
// */
//@Configuration
//@EnableAutoConfiguration
//@EnableRedisRepositories
//public class RedisConfig {
//
//    @Autowired
//    private Environment env;
//
//    @Bean
//    public StringRedisTemplate redisTemplate() {
//        final StringRedisTemplate template = new StringRedisTemplate(jedisConnectionFactory());
//        template.setEnableTransactionSupport(true);
//        return template;
//    }
//
//    @Bean
//    public RedisSerializer<String> stringSerializer() {
//        return new StringRedisSerializer();
//    }
//
//    @Bean
//    public JedisPoolConfig jedisPoolConfig() {
//        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(env.getRequiredProperty("supersolr.jedis.config.maxIdle", Integer.class));
//        jedisPoolConfig.setMaxWaitMillis(env.getRequiredProperty("supersolr.jedis.config.maxWaitMillis", Integer.class));
//        jedisPoolConfig.setMaxTotal(env.getRequiredProperty("supersolr.jedis.config.maxTotal", Integer.class));
//        jedisPoolConfig.setTestOnBorrow(env.getRequiredProperty("supersolr.jedis.config.testOnBorrow", Boolean.class));
//        return jedisPoolConfig;
//    }
//
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//        return jedisConnectionFactory;
//    }
//}
