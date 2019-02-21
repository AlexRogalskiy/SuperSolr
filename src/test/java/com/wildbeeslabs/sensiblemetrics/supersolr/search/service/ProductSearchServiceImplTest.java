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
import com.wildbeeslabs.sensiblemetrics.supersolr.BaseTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableProduct;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.utils.OffsetPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.Cursor;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static com.wildbeeslabs.sensiblemetrics.supersolr.search.service.BaseDocumentSearchService.DEFAULT_DOCTYPE;
import static com.wildbeeslabs.sensiblemetrics.supersolr.search.service.BaseDocumentSearchService.DEFAULT_SEARСH_TERM_DELIMITER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Product service implementation unit test {@link BaseTest}
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
public class ProductSearchServiceImplTest extends BaseTest {

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
    @DisplayName("Test search all products")
    public void testFindAll() {
        // when
        final Iterable<? extends Product> productIterable = getProductService().findAll();
        final List<? extends Product> products = Lists.newArrayList(productIterable);

        // then
        assertThat(products, not(empty()));
        assertThat(products, hasSize(10));
    }

    @Test
    @DisplayName("Test search products by ids")
    public void testFindAllByIds() {
        // given
        final List<String> productIds = Arrays.asList("01", "02", "03");

        // when
        final Iterable<? extends Product> productIterable = getProductService().findAll(productIds);
        final List<? extends Product> products = Lists.newArrayList(productIterable);

        // then
        assertThat(products, not(empty()));
        assertThat(products, hasSize(productIds.size()));
    }

    @Test
    @DisplayName("Test search product by non-existing name")
    public void testFindByNonExistingName() {
        // when
        final Page<? extends Product> productPage = getProductService().findByName("Test", OffsetPageRequest.builder().offset(0).limit(10).build());

        // then
        assertThat(productPage.getContent(), is(empty()));
    }

    @Test
    @DisplayName("Test search product by name")
    public void testFindByName() {
        // given
        final String[] idsToCheck = {"06", "10"};

        // when
        final Page<? extends Product> productPage = getProductService().findByName("New", PageRequest.of(0, 10));

        // then
        assertEquals(2, productPage.getTotalElements());
        assertEquals(1, productPage.getTotalPages());

        // when
        final List<? extends Product> products = productPage.getContent();

        // then
        assertTrue(containsIds(products, idsToCheck));
    }

    @Test
    @DisplayName("Test search products by query and criteria")
    public void testFindByQueryAndCriteria() {
        // given
        final String searchTerms = "Name string";
        final String[] idsToCheck = {"13"};

        final Product product = createProduct("13", "Name", "Short description", "Long description", "Price string", "Catalog number", "Page title", 2, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setDoctype(SearchableProduct.COLLECTION_ID);
        getProductService().save(product);

        // when
        final Page<? extends Product> productPage = getProductService().findByCriteria(SearchableProduct.COLLECTION_ID, nameAndDescriptionCriteria(searchTerms), PageRequest.of(0, 10));

        // then
        assertEquals(1, productPage.getTotalElements());

        // when
        final List<? extends Product> products = productPage.getContent();

        // then
        assertTrue(containsIds(products, idsToCheck));
    }

    @Test
    @DisplayName("Test search products by simple query and criteria")
    public void testFindBySimpleQueryAndCriteria() {
        // given
        final String searhTerms = "New Page title";
        final String[] idsToCheck = {"02"};

        // when
        final Page<? extends Product> productPage = getProductService().findByCriteria(SearchableProduct.COLLECTION_ID, new Criteria(SearchableProduct.PAGE_TITLE_FIELD_NAME).is(searhTerms), PageRequest.of(0, 10));

        // then
        assertEquals(1, productPage.getTotalElements());

        // when
        final List<? extends Product> products = productPage.getContent();

        // then
        assertTrue(containsIds(products, idsToCheck));
        assertEquals(searhTerms, products.get(0).getPageTitle());
    }

    @Test
    @DisplayName("Test search product by description")
    public void testFindByDescription() {
        // given
        final String[] idsToCheck = {"04"};

        // when
        final Page<? extends Product> productPage = getProductService().findByDescription("Island", PageRequest.of(0, 10));
        final List<? extends Product> products = productPage.getContent();

        // then
        assertThat(products, hasSize(1));
        assertTrue(containsIds(products, idsToCheck));
    }

    @Test
    @DisplayName("Test search products by names")
    public void testFindByNames() {
        // given
        final String searchTerm = "Name collection";
        final String[] idsToCheck = {"09"};

        // when
        final Page<? extends Product> productPage = getProductService().findByNames(searchTerm, PageRequest.of(0, 10));

        // then
        assertEquals(1, productPage.getTotalElements());
        assertEquals(1, productPage.getTotalPages());

        // when
        final List<? extends Product> products = productPage.getContent();

        // then
        assertTrue(containsIds(products, idsToCheck));
    }

    @Test
    @DisplayName("Test search products by non-existing autocomplete name fragment")
    public void testFindByNonExistingAutoCompleteNameFragment() {
        // given
        final String searchTerms = "Is";

        // when
        final FacetPage<? extends Product> productFacetPage = getProductService().findByAutoCompleteNameFragment(searchTerms, PageRequest.of(0, 2));

        // then
        assertThat(productFacetPage.getContent(), is(empty()));
    }

    @Test
    @DisplayName("Test search products by autocomplete name fragment")
    public void testFindByAutoCompleteNameFragment() {
        // given
        final String searchTerms = "New";

        // when
        final FacetPage<? extends Product> productFacetPage = getProductService().findByAutoCompleteNameFragment(searchTerms, PageRequest.of(0, 2));

        // then
        assertEquals(2, productFacetPage.getNumberOfElements());

        // when
        final Map<String, Long> productFacetCounts = getFacetCounts(productFacetPage);

        // then
        assertNull(productFacetCounts.get("Test"));
        assertEquals(Long.valueOf(2), productFacetCounts.get("new"));
        assertEquals(Long.valueOf(1), productFacetCounts.get("06"));
        assertEquals(Long.valueOf(1), productFacetCounts.get("09"));
    }

    @Test
    @DisplayName("Test search products by location query string")
    public void testFindByLocationQueryString() {
        // given
        final Point location = new Point(15.10, -76.10);
        final String locationString = String.format(Locale.ROOT, "%.2f,%.2f", location.getX(), location.getY());
        final Distance distance = new Distance(0.3, Metrics.KILOMETERS);

        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setLocation(location);
        getProductService().save(product);

        // when
        final List<? extends Product> products = getProductService().findByLocationWithin(locationString, distance);

        // then
        assertThat(products, hasSize(1));
        assertThat(location.getX() - products.iterator().next().getLocation().getX(), is(lessThan(distance.getValue())));
        assertThat(location.getY() - products.iterator().next().getLocation().getY(), is(lessThan(distance.getValue())));
    }

    @Test
    @DisplayName("Test search products by non-distant location point")
    public void testFindByNonDistantLocationPoint() {
        // given
        final Point location = new Point(10, 20);
        final Distance distance = new Distance(0.5, Metrics.KILOMETERS);
        final org.springframework.data.solr.core.geo.Point point = new org.springframework.data.solr.core.geo.Point(location.getX() + 0.7, location.getY() + 0.8);

        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 2, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setLocation(location);
        getProductService().save(product);

        // when
        final List<? extends Product> products = getProductService().findByLocationNear(point, distance);

        // then
        assertThat(products, is(empty()));
    }

    @Test
    @DisplayName("Test search products by location near")
    public void testFindByLocationShapeNear() {
        // given
        final Box locationBox = new Box(new Point(20, -101), new Point(23, -90));
        final Point point = new Point(21, -92);
        final Criteria criteria = new Criteria(SearchableProduct.LOCATION_FIELD_NAME).near(locationBox);

        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 10, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setLocation(point);
        getProductService().save(product);

        // when
        final Page<? extends Product> productPage = getProductService().findByCriteria(SearchableProduct.COLLECTION_ID, criteria, PageRequest.of(0, 2));
        productPage.getContent();

        // then;
        assertEquals(1, productPage.getTotalElements());
        assertThat(productPage.getContent(), hasSize(1));
    }

    @Test
    @Ignore("GeoResults is null or not supported yet")
    @DisplayName("Test search products by geolocation near")
    public void testFindByGeoLocationNear() {
        // given
        final Point location = new Point(52.51790, 13.41239);
        final Distance distance = new Distance(0.2, Metrics.KILOMETERS);

        // when
        final GeoResults<? extends Product> products = getProductService().findByGeoLocationNear(location, distance);

        // then
        assertNotNull(products);
        assertThat(products.getContent(), hasSize(1));

        final Distance distanceResult = products.getContent().get(0).getDistance();
        assertThat(distanceResult.getMetric(), is(Metrics.KILOMETERS));
        assertThat(distanceResult.getValue(), closeTo(0.862, 0.001));
    }

    @Test
    @DisplayName("Test search product by non-existing rating")
    public void testFindByNonExistingRating() {
        // given
        final Integer rating = 10;

        // when
        final Page<? extends Product> productPage = getProductService().findByRating(rating, PageRequest.of(0, 5));

        // then
        assertThat(productPage.getContent(), is(empty()));
    }

    @Test
    @DisplayName("Test search product by rating")
    public void testFindByRating() {
        // given
        final Integer rating = 7;
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setRating(rating);
        getProductService().save(product);

        // when
        final Page<? extends Product> productPage = getProductService().findByRating(rating, PageRequest.of(0, 5));

        // then
        assertEquals(2, productPage.getTotalElements());
        assertThat(productPage.getContent(), hasSize(2));
    }

    @Test
    @DisplayName("Test search product by lock type")
    public void testFindByLockType() {
        // given
        final Integer lockType = 1;

        final Product product = createProduct("15", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("15", 3, "Solr for dummies", "Get started with solr"));
        product.setLockType(lockType);
        getProductService().save(product);

        // when
        final List<? extends Product> products = getProductService().findByLockType(lockType, new Sort(Sort.Direction.DESC, SearchableProduct.ID_FIELD_NAME));

        // then
        assertThat(products, hasSize(1));
    }

    @Test
    @DisplayName("Test search product by non-existing name or description")
    public void testFindByNonExistingNameOrDescription() {
        // given
        final String searchTerm = "Test";
        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));

        // when
        final Page<? extends Product> productPage = getProductService().findByNameOrDescription(searchTerm, PageRequest.of(0, 2));

        // then
        assertThat(productPage.getContent(), is(empty()));
    }

    @Test
    @DisplayName("Test search product by name or description")
    public void testFindByNameOrDescription() {
        // given
        final String searchTerm = "Short";

        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 2, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));

        // when
        final Page<? extends Product> productPage = getProductService().findByNameOrDescription(searchTerm, PageRequest.of(0, 2));

        // then
        assertEquals(10, productPage.getTotalElements());
        assertThat(productPage.getContent(), hasSize(2));
    }

    @Test
    @DisplayName("Test search product by name or category")
    public void testFindByNameOrCategory() {
        // given
        final String searchTerm = "Name string";

        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setDoctype(SearchableProduct.COLLECTION_ID);
        getProductService().save(product);

        // when
        final Page<? extends Product> productPage = getProductService().findByNameOrCategory(searchTerm, PageRequest.of(0, 10));

        // then
        assertEquals(1, productPage.getTotalElements());
    }

    @Test
    @DisplayName("Test search products by location query")
    public void testFindByLocationQuery() {
        // given
        final Point location = new Point(20.15, -92.85);
        final Criteria criteria = new Criteria(SearchableProduct.LOCATION_FIELD_NAME).near(location, new Distance(5, Metrics.NEUTRAL));

        final Product product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setLocation(location);
        getProductService().save(product);

        // when
        final Page<? extends Product> productPage = getProductService().findByQuery(SearchableProduct.COLLECTION_ID, new SimpleQuery(criteria));
        productPage.getContent();

        // then
        assertEquals(1, productPage.getTotalElements());
        assertThat(productPage.getContent(), hasSize(1));
    }

    @Test
    @DisplayName("Test search products by location shape query")
    public void testFindByLocationShapeWithin() {
        // given
        final Point location = new Point(51.8911912, -0.4979756);
        final Circle innerCircle = new Circle(location, new Distance(0.2, Metrics.KILOMETERS));
        final Criteria criteria = new Criteria(SearchableProduct.LOCATION_FIELD_NAME).within(innerCircle);

        final Product product = createProduct("13", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        product.setLocation(location);
        getProductService().save(product);

        // when
        final Page<? extends Product> productPage = getProductService().findByCriteria(SearchableProduct.COLLECTION_ID, criteria, PageRequest.of(0, 2));
        productPage.getContent();

        // then
        assertEquals(1, productPage.getTotalElements());
        assertThat(productPage.getContent(), hasSize(1));
    }

    @Test
    @DisplayName("Test search product by facet query")
    public void testFindByFacetQuery() {
        // given
        final Criteria criteria = new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD);
        final FacetQuery facetQuery = new SimpleFacetQuery(criteria)
            .setFacetOptions(new FacetOptions()
                .addFacetOnField(SearchableProduct.NAME_FIELD_NAME)
                .setFacetLimit(5));

        // when
        final FacetPage<? extends Product> productPage = getProductService().findByFacetQuery(SearchableProduct.COLLECTION_ID, facetQuery);

        // then
        assertThat(productPage.getContent(), hasSize(10));

        // when
        final Map<String, Long> categoryFacetCounts = getFacetCounts(productPage);

        // then
        assertNull(categoryFacetCounts.get("Test"));
        assertEquals(Long.valueOf(8), categoryFacetCounts.get("name"));
        assertEquals(Long.valueOf(2), categoryFacetCounts.get("new"));
    }

    @Test
    @DisplayName("Test search product by non-existing search term")
    public void testFindByNonExistingSearchTerm() {
        // given
        final String searchTerm = "cookies";

        // when
        final HighlightPage<? extends Product> productHighlightPage = getProductService().find(SearchableProduct.COLLECTION_ID, searchTerm, PageRequest.of(0, 10));

        // then
        assertThat(productHighlightPage.getContent(), is(empty()));
    }

    @Test
    @DisplayName("Test search product by existing search term")
    public void testFindByExistingSearchTerm() {
        // given
        final String searchTerm = "New";

        // when
        final HighlightPage<? extends Product> productHighlightPage = getProductService().find(SearchableProduct.COLLECTION_ID, searchTerm, PageRequest.of(0, 10));
        productHighlightPage.getContent();

        // then
        assertTrue(containsSnipplet(productHighlightPage, "<highlight>New</highlight> Page title"));
        assertTrue(containsSnipplet(productHighlightPage, "<highlight>New</highlight> 06"));
        assertTrue(containsSnipplet(productHighlightPage, "<highlight>New</highlight> 09"));
        assertFalse(containsSnipplet(productHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    @Test
    @DisplayName("Test search product by highlighted name in collection")
    public void testFindByHighlightedNameIn() {
        // given
        final List<String> searchTerms = Arrays.asList("New", "09");

        // when
        final HighlightPage<? extends Product> productHighlightPage = getProductService().findByHighlightedNameIn(searchTerms, PageRequest.of(0, 10));
        productHighlightPage.getContent();

        // then
        assertTrue(containsSnipplet(productHighlightPage, "<highlight>New</highlight> <highlight>09</highlight>"));
        assertFalse(containsSnipplet(productHighlightPage, "<highlight>cookies</highlight>"));
    }

    protected Cursor<? extends Product> findAllProducts() {
        return getSolrTemplate().queryForCursor(SearchableProduct.COLLECTION_ID, getQuery("*:*").addSort(Sort.by("id")), Product.class);
    }

    private List<Product> getSampleData() {
        if (getProductService().find("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Product> products = new ArrayList<>();
        Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 0, 1.0, 2.0, true);
        product.addCategory(createCategory("01", 1, "Treasure Island", "Best seller by R.L.S."));
        products.add(product);

        product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "New Page title", 1, 1.0, 2.0, true);
        product.addCategory(createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller"));
        products.add(product);

        product = createProduct("03", "Name string", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 1.0, 2.0, true);
        product.addCategory(createCategory("03", 3, "Solr for dummies", "Get started with solr"));
        products.add(product);

        product = createProduct("04", "Name", "Short description", "Long Island", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));
        products.add(product);

        product = createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 2, 1.0, 2.0, true);
        product.addCategory(createCategory("05", 5, "Spring Island", "The perfect island romance.."));
        products.add(product);

        product = createProduct("06", "New 06", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("06", 6, "Refactoring", "It's about improving the design of existing code."));
        products.add(product);

        product = createProduct("07", "Name data", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1, 1.0, 2.0, true);
        product.addCategory(createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!"));
        products.add(product);

        product = createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 3, 1.0, 2.0, true);
        product.addCategory(createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!"));
        products.add(product);

        product = createProduct("09", "Name collection", "Short description", "Long description", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true);
        product.addCategory(createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!"));
        products.add(product);

        product = createProduct("10", "New 09", "Short description", "Long description", "Price description", "Catalog number", "Page title", 6, 1.0, 2.0, true);
        product.addCategory(createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications"));
        products.add(product);
        return products;
    }

    private Criteria nameAndDescriptionCriteria(final String searchTerms) {
        final String[] words = searchTerms.split(DEFAULT_SEARСH_TERM_DELIMITER);
        Criteria criteria = new Criteria(DEFAULT_DOCTYPE).is(SearchableProduct.COLLECTION_ID);
        for (final String word : words) {
            criteria = criteria.and(new Criteria(SearchableProduct.NAME_FIELD_NAME).contains(word)
                .or(SearchableProduct.LONG_DESCRIPTION_FIELD_NAME).contains(word)
                .or(SearchableProduct.SHORT_DESCRIPTION_FIELD_NAME).contains(word)
                .or(SearchableProduct.PRICE_DESCRIPTION_FIELD_NAME).contains(word));
        }
        return criteria;
    }

    protected ProductSearchService getProductService() {
        return this.productService;
    }
}
