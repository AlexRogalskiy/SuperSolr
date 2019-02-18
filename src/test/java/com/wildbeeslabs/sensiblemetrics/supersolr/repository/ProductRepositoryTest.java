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
package com.wildbeeslabs.sensiblemetrics.supersolr.repository;

import com.wildbeeslabs.sensiblemetrics.supersolr.config.DBConfig;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Product repository implementation unit test
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBConfig.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test.sql")
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindById() {
        // given
        final Long productId = Long.valueOf(1);
        final Optional<Product> productOptional = getProductRepository().findById(productId);
        // assert
        assertTrue(productOptional.isPresent());
        assertEquals(productId, productOptional.get().getId());
    }

    @Test
    public void testFindByEmptyName() {
        // given
        final String name = "Test";
        final List<? extends Product> products = getProductRepository().findByName(name);
        // assert
        assertThat(products, is(empty()));
    }

    @Test
    public void testFindByName() {
        // given
        final String name = "Product 01";
        final List<? extends Product> products = getProductRepository().findByName(name);
        // assert
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
        assertEquals(name, products.get(0).getName());
    }

    @Test
    public void testFindByRating() {
        // given
        final Integer rating = 10;
        final List<? extends Product> products = getProductRepository().findByRating(rating);
        // assert
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
        assertEquals(rating, products.get(0).getRating());
    }

    protected ProductRepository getProductRepository() {
        return this.productRepository;
    }
}