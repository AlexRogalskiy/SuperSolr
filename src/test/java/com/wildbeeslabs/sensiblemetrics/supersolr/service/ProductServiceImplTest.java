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
package com.wildbeeslabs.sensiblemetrics.supersolr.service;

import com.google.common.collect.Lists;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.utils.OffsetPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Product service implementation unit test
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductServiceImplTest extends BaseModelServiceImplTest {

    @Autowired
    private ProductService productService;

    @Before
    public void before() {
        createSampleData();
    }

    @Test
    public void testFindAll() {
        final Iterable<? extends Product> productIterable = getProductService().findAll();
        final List<? extends Product> products = Lists.newArrayList(productIterable);
        assertThat(products, not(empty()));
        assertEquals(10, products.size());
    }

    @Test
    public void testFindAllByIds() {
        final List<String> productIds = Arrays.asList("01", "02", "03");
        final Iterable<? extends Product> productIterable = getProductService().findAll(productIds);
        final List<? extends Product> products = Lists.newArrayList(productIterable);
        assertThat(products, not(empty()));
        assertEquals(productIds.size(), products.size());
    }

    @Test
    public void testFindByName() {
        final Page<? extends Product> productPage = getProductService().findByName("Test", OffsetPageRequest.builder().offset(0).limit(1).build());
        assertEquals(4, productPage.getTotalElements());
        assertEquals(4, productPage.getTotalPages());
        final List<? extends Product> products = productPage.getContent();
        assertTrue(containsIds(products, "01", "02", "05", "08"));
    }

    @Test
    public void testFindByDescription() {
        final Page<? extends Product> productPage = getProductService().findByDescription("Island", PageRequest.of(0, 10));
        final List<? extends Product> products = productPage.getContent();
        assertEquals(5, products.size());
        assertTrue(containsIds(products, "01", "02", "05", "07", "08"));
        assertEquals("07", products.get(4).getId());
    }

    @Test
    public void testFindByNames() {
        final Page<? extends Product> productPage = getProductService().findByNames("Island Planet", PageRequest.of(0, 2));
        assertEquals(4, productPage.getTotalElements());
        assertEquals(4, productPage.getTotalPages());
        final List<? extends Product> products = productPage.getContent();
        assertTrue(containsIds(products, "01", "02", "05", "08"));
    }

    @Test
    public void testFindByAutoCompleteNameFragment() {
        final FacetPage<? extends Product> productFacetPage = getProductService().autoCompleteNameFragment("Island Planet", PageRequest.of(0, 2));
        assertEquals(2, productFacetPage.getNumberOfElements());

        final Map<String, Long> productFacetCounts = getFacetCounts(productFacetPage);
        assertEquals(new Long(3), productFacetCounts.get("Test01"));
        assertEquals(new Long(2), productFacetCounts.get("Test02"));
        assertEquals(new Long(1), productFacetCounts.get("Test03"));
    }

    @Test
    public void testFindByCustomQuery() {
        final HighlightPage<? extends Product> productHighlightPage = getProductService().find("cookies", PageRequest.of(0, 10));
        productHighlightPage.getContent();
        assertTrue(containsSnipplet(productHighlightPage, "How to handle <highlight>cookies</highlight> in web applications"));
        assertTrue(containsSnipplet(productHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    private void createSampleData() {
        if (getProductService().find("01").isPresent()) {
            log.debug("Data already exists");
            return;
        }
        Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null));

        product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller", null));

        product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));

        product = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller", null));

        product = createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("05", 5, "Spring Island", "The perfect island romance..", null));

        product = createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.", null));

        product = createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!", null));

        product = createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!", null));

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!", null));

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications", null));
    }

    protected ProductService getProductService() {
        return this.productService;
    }
}
