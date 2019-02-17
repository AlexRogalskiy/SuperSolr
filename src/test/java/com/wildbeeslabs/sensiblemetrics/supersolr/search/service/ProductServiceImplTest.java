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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.service;

import com.google.common.collect.Lists;
import com.wildbeeslabs.sensiblemetrics.supersolr.BaseDocumentTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableProduct;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.utils.OffsetPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Distance;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static com.wildbeeslabs.sensiblemetrics.supersolr.search.service.BaseDocumentSearchService.DEFAULT_DOCTYPE;
import static com.wildbeeslabs.sensiblemetrics.supersolr.search.service.BaseDocumentSearchService.DEFAULT_SEARСH_TERM_DELIMITER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Product service implementation unit test
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
public class ProductServiceImplTest extends BaseDocumentTest {

    @Autowired
    private ProductSearchService productService;

    @Before
    public void before() {
        getProductService().save(getSampleData());
    }

    @After
    public void after() {
        getProductService().deleteAll();
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
    public void testFindBySimpleQuery() {
        //given
        final String searhTerms = "Test01 Test02";
        final Page<? extends Product> productPage = getProductService().findByQueryAndCriteria("product", nameAndDescriptionCriteria(searhTerms), PageRequest.of(0, 10), Product.class);
        //assert
        assertEquals(4, productPage.getTotalElements());
        assertEquals(4, productPage.getTotalPages());
        final List<? extends Product> products = productPage.getContent();
        assertTrue(containsIds(products, "01", "02", "05", "08"));
    }

    @Test
    public void testFindBySimpleQueryAndCriteria() {
        //given
        final String searhTerms = "Test01 Test02";
        final Page<? extends Product> productPage = getProductService().findByQueryAndCriteria(searhTerms, new Criteria(DEFAULT_DOCTYPE).is(SearchableProduct.DOCUMENT_ID), PageRequest.of(0, 10), Product.class);
        //assert
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
        final FacetPage<? extends Product> productFacetPage = getProductService().findByAutoCompleteNameFragment("Island Planet", PageRequest.of(0, 2));
        assertEquals(2, productFacetPage.getNumberOfElements());

        final Map<String, Long> productFacetCounts = getFacetCounts(productFacetPage);
        assertEquals(new Long(3), productFacetCounts.get("Test01"));
        assertEquals(new Long(2), productFacetCounts.get("Test02"));
        assertEquals(new Long(1), productFacetCounts.get("Test03"));
    }

    @Test
    public void testFindByLocation() {
        //given
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final List<? extends Product> products = getProductService().findByLocationWithin("35.10,-96.102", 30);
        assertEquals(2, products.size());
        assertTrue(products.contains(product));
        assertThat(products.iterator().next().getLocation().getX(), is(lessThan((double) 30)));
    }

    @Test
    public void testFindByLocationPoint() {
        //given
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final Page<? extends Product> productPage = getProductService().findByLocation(new Point(10, 20), PageRequest.of(0, 2));
        assertEquals(2, productPage.getTotalElements());
        assertTrue(productPage.getContent().contains(product));
    }

    @Test
    public void testFindByLocationNear() {
        //given
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final List<? extends Product> products = getProductService().findByLocationNear(new Box(new Point(22, -91), new Point(23, -90)));
        assertEquals(2, products.size());
    }

    @Test
    public void testFindByRating() {
        //given
        final Integer rating = 10;
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final Page<? extends Product> productPage = getProductService().findByRating(rating, PageRequest.of(0, 2));
        assertEquals(3, productPage.getTotalElements());
        assertTrue(productPage.getContent().contains(product));
    }

    @Test
    public void testFindByLockType() {
        //given
        final Integer lockType = 1;
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final List<? extends Product> products = getProductService().findByLockType(lockType, new Sort(Sort.Direction.DESC, SearchableProduct.ID_FIELD_NAME));
        assertEquals(3, products.size());
    }

    @Test
    public void testFindByNameOrDescription() {
        //given
        final String searchTerm = "Test";
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final Page<? extends Product> productPage = getProductService().findByNameOrDescription(searchTerm, PageRequest.of(0, 2));
        assertEquals(3, productPage.getTotalElements());
        assertTrue(productPage.getContent().contains(product));
    }

    @Test
    public void testFindByNameOrCategory() {
        //given
        final String searchTerm = "Test";
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr", null));
        //assert
        final Page<? extends Product> productPage = getProductService().findByNameOrCategory(searchTerm, PageRequest.of(0, 10));
        assertEquals(3, productPage.getTotalElements());
        assertTrue(productPage.getContent().contains(product));
    }

    @Test
    public void testFindByQuery() {
        //given
        final Point location = new Point(22.15, -90.85);
        final Criteria criteria = new Criteria("store").near(location, new Distance(5));
        //assert
        final Page<? extends Product> productPage = getProductService().findByQuery(new SimpleQuery(criteria));
        productPage.getContent();
        assertEquals(3, productPage.getTotalElements());
    }

    @Test
    public void testFindByCustomQuery() {
        final HighlightPage<? extends Product> productHighlightPage = getProductService().find("cookies", PageRequest.of(0, 10));
        productHighlightPage.getContent();
        assertTrue(containsSnipplet(productHighlightPage, "<highlight>cookies</highlight>"));
        assertTrue(containsSnipplet(productHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    @Test
    public void testFindByHighlightedMultiQuery() {
        //given
        final List<String> searhTerms = Arrays.asList("aaa", "bbb");
        final HighlightPage<? extends Product> productHighlightPage = getProductService().findByHighlightedNameIn(searhTerms, PageRequest.of(0, 10));
        productHighlightPage.getContent();
        //assert
        assertTrue(containsSnipplet(productHighlightPage, "<highlight>cookies</highlight>"));
        assertTrue(containsSnipplet(productHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    private List<Product> getSampleData() {
        if (getProductService().find("01").isPresent()) {
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

    private Criteria nameAndDescriptionCriteria(final String searchTerms) {
        final String[] words = searchTerms.split(DEFAULT_SEARСH_TERM_DELIMITER);
        Criteria criteria = new Criteria();
        for (final String word : words) {
            criteria = criteria
                    .and(new Criteria(SearchableProduct.NAME_FIELD_NAME).contains(word)
                            .or(SearchableProduct.LONG_DESCRIPTION_FIELD_NAME).contains(word)
                            .or(SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME).contains(word)
                            .or(SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME).contains(word));
        }
        return criteria.and(new Criteria(DEFAULT_DOCTYPE).is(SearchableProduct.DOCUMENT_ID));
    }

    protected ProductSearchService getProductService() {
        return this.productService;
    }
}