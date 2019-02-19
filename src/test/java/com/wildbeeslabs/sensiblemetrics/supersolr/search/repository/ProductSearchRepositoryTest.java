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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.repository;

import com.wildbeeslabs.sensiblemetrics.supersolr.BaseDocumentTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.annotation.PostgresDataJpaTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.config.SolrConfig;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

/**
 * Product search repository implementation unit test {@link BaseDocumentTest}
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SolrConfig.class)
@PostgresDataJpaTest
@AutoConfigurationPackage
@Transactional
public class ProductSearchRepositoryTest extends BaseDocumentTest {

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Before
    public void before() {
        getProductSearchRepository().saveAll(getSampleData());
    }

    @After
    public void after() {
        getProductSearchRepository().deleteAll();
    }

    @Test
    public void testFindByName() {
        // given
        final Product product = createProduct("01", "ProductTest", "Short description", "Long description", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("01", 1, "Treasure Island", "Best seller by R.L.S."));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final Page<? extends Product> productPage = getProductSearchRepository().findByName(product.getName(), PageRequest.of(0, 2));
        final List<? extends Product> products = productPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
        assertEquals(product.getName(), products.get(0).getName());
    }

    @Test
    public void testFindByEmptyDescriptionAndMaxPageSize() {
        // terms
        final String searchTerm = Strings.EMPTY;

        // given
        final Product product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final Page<? extends Product> productPage = getProductSearchRepository().findByDescription(searchTerm, PageRequest.of(0, 2));
        final List<? extends Product> products = productPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(2, products.size());
        assertEquals(product.getShortDescription(), products.get(0).getShortDescription());
    }

    @Test
    public void testFindByDescription() {
        // terms
        final String searchTerm = "Very";

        // given
        final Product product = createProduct("02", "Name", "Short description", "Very Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final Page<? extends Product> productPage = getProductSearchRepository().findByDescription(searchTerm, PageRequest.of(0, 2));
        final List<? extends Product> products = productPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
    }

    @Test
    public void testFindByNameStartingWith() {
        // terms
        final String searchExistingName = "Solr";
        final String searchNonExistingName = "Segment";

        //given
        Product product = createProduct("11", "Solr", "Short description", "Long description", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));
        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);

        product = createProduct("04", "New Trait", "Short description", "Long description", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("02", 4, "Moon landing", "All facts about Apollo 11, a best seller"));
        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);

        product = createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true);
        product.addCategory(createCategory("13", 8, "The Pirate Island", "Oh noes, the pirates are coming!"));
        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        FacetPage<? extends Product> productFacetPage = getProductSearchRepository().findByNameStartingWith(searchExistingName, PageRequest.of(0, 10));
        List<? extends Product> products = productFacetPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
        assertTrue(products.get(0).getName().startsWith(searchExistingName));

        // when
        productFacetPage = getProductSearchRepository().findByNameStartingWith(searchNonExistingName, PageRequest.of(0, 10));
        products = productFacetPage.getContent();

        // then
        assertThat(products, empty());
    }

    @Test
    public void testFindByNameIn() {
        // terms
        final List<String> names = Arrays.asList("Kitchen", "sink");

        // given
        final Product product = createProduct("14", "Kitchen sink with dishwasher machine", "TestCase 01", "TestCase 01 for product 01", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final HighlightPage<? extends Product> productHighlightPage = getProductSearchRepository().findByNameIn(names, PageRequest.of(0, 15));
        final List<? extends Product> products = productHighlightPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
        assertTrue(names.stream().allMatch(name -> products.get(0).getName().contains(name)));
    }

    @Test
    public void testFindByNonExistingNameLike() {
        // terms
        final List<String> names = Arrays.asList("Treasure");

        // given
        final Product product = createProduct("14", "Product 01", "TestCase 01", "TestCase 01 for product 01", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final List<? extends Product> products = getProductSearchRepository().findByNameLike(names);

        // then
        assertThat(products, empty());
    }

    @Test
    public void testFindByNameLike() {
        // terms
        final List<String> names = Arrays.asList("Product 01", "Product 02");

        // given
        final Product product = createProduct("14", "Product 01", "TestCase 01", "TestCase 01 for product 01", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final List<? extends Product> products = getProductSearchRepository().findByNameLike(names);

        // then
        assertThat(products, not(empty()));
        assertEquals(1, products.size());
    }

    @Test
    public void testFindByPriceInRange() {
        // terms
        double lowerBound = 10.0;
        double upperBound = 100.0;

        // given
        final Product product = createProduct("14", "Product 01", "TestCase 01", "TestCase 01 for product 01", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final Page<? extends Product> productsPage = getProductSearchRepository().findByPriceInRange(lowerBound, upperBound, PageRequest.of(0, 10));
        final List<? extends Product> products = productsPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(3, products.size());
    }

    @Test
    public void findByPriceInRangeExclusive() {
        // terms
        double lowerBound = 10.0;
        double upperBound = 100.0;

        // given
        final Product product = createProduct("14", "Product 01", "TestCase 01", "TestCase 01 for product 01", "Price description", "Catalog number", "Page title", 0, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));

        getSolrTemplate().saveBean(SearchableProduct.COLLECTION_ID, product);
        getSolrTemplate().commit(SearchableProduct.COLLECTION_ID);

        // when
        final Page<? extends Product> productsPage = getProductSearchRepository().findByPriceInRangeExclusive(lowerBound, upperBound, PageRequest.of(0, 10));
        final List<? extends Product> products = productsPage.getContent();

        // then
        assertThat(products, not(empty()));
        assertEquals(2, products.size());
    }

    @SuppressWarnings("unchecked")
    private List<Product> getSampleData() {
        if (getProductSearchRepository().findById("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Product> products = new ArrayList<>();
        Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 10.0, 2.0, true);
        product.addCategory(createCategory("01", 1, "Treasure Island", "Best seller by R.L.S."));
        products.add(product);

        product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 32, 1.0, 2.0, true);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller"));
        products.add(product);

        product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 2, 11.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        products.add(product);

        product = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 18.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));
        products.add(product);

        product = createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("05", 5, "Spring Island", "The perfect island romance.."));
        products.add(product);

        product = createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 8.0, 2.0, true);
        product.addCategory(createCategory("06", 6, "Refactoring", "It's about improving the design of existing code."));
        products.add(product);

        product = createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 10, 5.0, 2.0, true);
        product.addCategory(createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!"));
        products.add(product);

        product = createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 11, 3.0, 2.0, true);
        product.addCategory(createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!"));
        products.add(product);

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 12, 100.0, 2.0, true);
        product.addCategory(createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!"));
        products.add(product);

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 13, 120.0, 2.0, true);
        product.addCategory(createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications"));
        products.add(product);
        return products;
    }

    protected ProductSearchRepository getProductSearchRepository() {
        return this.productSearchRepository;
    }
}
