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

import com.wildbeeslabs.sensiblemetrics.supersolr.BaseModelTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Product repository implementation unit test
 */
@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest extends BaseModelTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void before() {
        getProductRepository().saveAll(getSampleData());
    }

    @After
    public void after() {
        getProductRepository().deleteAll();
    }

    @Test
    public void testFindByName() {
        // given
        final Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null));

        this.entityManager.persist(product);
        this.entityManager.flush();

        // when
        final Page<? extends Product> productPage = getProductRepository().findByName(product.getName(), PageRequest.of(0, 2));
        final List<? extends Product> products = productPage.getContent();

        // then
        assertTrue(products.contains(product));
    }

    @Test
    public void testFindByDescription() {
        // initial search terms
        final String searchTerm = "Test";

        // given
        final Product product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller", null));

        this.entityManager.persist(product);
        this.entityManager.flush();

        // when
        final Page<? extends Product> productPage = getProductRepository().findByDescription(searchTerm, PageRequest.of(0, 2));
        final List<? extends Product> products = productPage.getContent();

        // then
        assertTrue(products.contains(product));
    }

    @Test
    public void testFindByNameStartsWith() {
        // initial search terms
        final List<String> titles = Arrays.asList("Title 01", "Title 02");

        //given
        final Product productFirst = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        productFirst.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller", null));
        this.entityManager.persist(productFirst);

        final Product productSecond = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        productSecond.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller", null));
        this.entityManager.persist(productSecond);

        final Product productThird = createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        productThird.addCategory(createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!", null));
        this.entityManager.persist(productThird);
        this.entityManager.flush();

        // when
        final FacetPage<? extends Product> productFacetPage = getProductRepository().findByNameStartsWith(titles, PageRequest.of(0, 10));
        final List<? extends Product> products = productFacetPage.getContent();

        // then
        assertTrue(products.contains(productFirst));
        assertTrue(products.contains(productSecond));
        assertFalse(products.contains(productThird));
    }

    @Test
    public void testFind() {
        // initial search terms
        final List<String> titles = Arrays.asList("Title 01", "Title 02");

        // given
        final Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null);
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        this.entityManager.persist(category);
        this.entityManager.flush();

        // when
        final HighlightPage<? extends Product> productHighlightPage = getProductRepository().findByQuery(titles, PageRequest.of(0, 10));
        final List<? extends Product> products = productHighlightPage.getContent();

        // then
        assertTrue(products.contains(category));
    }

    private List<Product> getSampleData() {
        if (getProductRepository().findById("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Product> products = new ArrayList<>();
        Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null));
        products.add(product);

        product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller", null));
        products.add(product);

        product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        products.add(product);

        product = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller", null));
        products.add(product);

        product = createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("05", 5, "Spring Island", "The perfect island romance..", null));
        products.add(product);

        product = createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.", null));
        products.add(product);

        product = createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!", null));
        products.add(product);

        product = createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!", null));
        products.add(product);

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!", null));
        products.add(product);

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications", null));
        products.add(product);
        return products;
    }

    protected ProductRepository getProductRepository() {
        return this.productRepository;
    }
}
