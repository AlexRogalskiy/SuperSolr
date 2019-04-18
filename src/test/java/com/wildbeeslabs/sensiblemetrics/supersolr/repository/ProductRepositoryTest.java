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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.entity.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.iface.PersistableBaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.system.config.DBConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.ServiceUtils.getResultAsync;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Product repository implementation unit test
 */
@Slf4j
@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBConfig.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test.sql")
@Transactional
public class ProductRepositoryTest {

    private final ProductRepository productRepository;

    @Test
    @DisplayName("Test search all products")
    public void testFindAll() {
        // when
        final List<Product> products = getProductRepository().findAll();

        // then
        assertThat(products, not(empty()));
        assertThat(products, hasSize(17));
    }

    @Test
    @DisplayName("Test search all products by page")
    public void testFindAllByPage() {
        // when
        final Page<Product> productPage = getProductRepository().findAll(PageRequest.of(0, 5));

        // then
        assertNotNull("Should not be empty or null", productPage);
        assertThat(productPage.getContent(), not(empty()));
        assertThat(productPage.getContent(), hasSize(5));
        assertThat(productPage.getTotalElements(), IsEqual.equalTo(17L));
    }

    @Test
    @DisplayName("Test search all products by sort")
    public void testFindAllBySort() {
        // when
        final List<Product> products = getProductRepository().findAll(new Sort(Sort.Direction.DESC, PersistableBaseModel.ID_FIELD_NAME));

        // then
        assertThat(products, not(empty()));
        assertThat(products, hasSize(17));
        assertThat(products.get(0).getId(), IsEqual.equalTo(17L));
    }

    @Test
    @DisplayName("Test search product by id")
    public void testFindById() {
        // given
        final Long productId = Long.valueOf(1);

        // when
        final Optional<Product> productOptional = getProductRepository().findById(productId);

        // then
        assertTrue(productOptional.isPresent());
        assertEquals(productId, productOptional.get().getId());
    }

    @Test
    @DisplayName("Test search category by empty name")
    public void testFindByEmptyName() {
        // given
        final String name = "Test";

        // when
        final Iterable<? extends Product> products = getResultAsync(getProductRepository().findByName(name));
        assertThat(products, notNullValue());
        final List<? extends Product> productList = Lists.newArrayList(products);

        // then
        assertThat(productList, is(empty()));
    }

    @Test
    @DisplayName("Test search category by name")
    public void testFindByName() {
        // given
        final String name = "Product 01";

        // when
        final Iterable<? extends Product> products = getResultAsync(getProductRepository().findByName(name));
        assertThat(products, notNullValue());
        final List<? extends Product> productList = Lists.newArrayList(products);

        // then
        assertThat(productList, not(empty()));
        assertThat(productList, hasSize(1));
        assertEquals(name, productList.get(0).getName());
    }

    @Test
    @DisplayName("Test search category by rating")
    public void testFindByRating() {
        // given
        final Integer rating = 10;

        // when
        final Iterable<? extends Product> products = getResultAsync(getProductRepository().findByRating(rating));
        assertThat(products, notNullValue());
        final List<? extends Product> productList = Lists.newArrayList(products);

        // then
        assertThat(productList, not(empty()));
        assertEquals(1, productList.size());
        assertEquals(rating, productList.get(0).getRating());
    }
}
