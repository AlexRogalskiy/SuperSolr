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
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableCategory;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.utils.OffsetPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.Cursor;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Category service implementation unit test {@link BaseTest}
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
public class CategorySearchServiceImplTest extends BaseTest {

    @Autowired
    private CategorySearchService categoryService;

    @Before
    public void before() {
        getCategoryService().save(getSampleData());
    }

    @After
    public void after() {
        getCategoryService().deleteAll();
    }

    @Test
    @DisplayName("Test search all categories")
    public void testFindAll() {
        // given
        final int totalElements = 10;

        // when
        final Iterable<? extends Category> categoryIterable = getCategoryService().findAll();
        final List<? extends Category> categories = Lists.newArrayList(categoryIterable);

        // then
        assertThat(categories, not(empty()));
        assertThat(categories, hasSize(totalElements));
    }

    @Test
    @DisplayName("Test search categories by ids")
    public void testFindAllByIds() {
        // given
        final List<String> categoryIds = Arrays.asList("01", "02", "03");

        // when
        final Iterable<? extends Category> categoryIterable = getCategoryService().findAll(categoryIds);
        final List<? extends Category> categories = Lists.newArrayList(categoryIterable);

        // then
        assertThat(categories, not(empty()));
        assertThat(categories, hasSize(categoryIds.size()));
    }

    @Test
    @DisplayName("Test search category by title")
    public void testFindByTitle() {
        // given
        final String[] idsToCheck = {"05"};
        final String titleTerm = "Spring";
        final int totalElements = 1;

        // when
        final Page<? extends Category> categoryPage = getCategoryService().findByTitle(titleTerm, OffsetPageRequest.builder().offset(0).limit(10).build());

        // then
        assertEquals(totalElements, categoryPage.getTotalElements());
        assertEquals(totalElements, categoryPage.getTotalPages());

        // when
        final List<? extends Category> categories = categoryPage.getContent();

        // then
        assertTrue(containsIds(categories, idsToCheck));
    }

    @Test
    @DisplayName("Test search category by description")
    public void testFindByDescription() {
        // given
        final String[] idsToCheck = {"05", "07"};
        final String descriptionTerm = "Island";
        final int totalElements = 2;

        // when
        final Page<? extends Category> categoryPage = getCategoryService().findByDescription(descriptionTerm, PageRequest.of(0, 10));
        final List<? extends Category> categories = categoryPage.getContent();

        // then
        assertThat(categories, hasSize(totalElements));
        assertTrue(containsIds(categories, idsToCheck));
    }

    @Test
    @DisplayName("Test search category by titles")
    public void testFindByTitles() {
        // given
        String titleTerms = "Pirate Planet";

        // when
        Page<? extends Category> categoryPage = getCategoryService().findByTitles(titleTerms, PageRequest.of(0, 10));

        assertThat(categoryPage.getContent(), is(empty()));

        // given
        final String[] idsToCheck = {"08"};
        titleTerms = "Pirate Island";
        int totalElements = 1;

        // when
        categoryPage = getCategoryService().findByTitles(titleTerms, PageRequest.of(0, 10));

        // then
        assertEquals(totalElements, categoryPage.getTotalElements());
        assertEquals(totalElements, categoryPage.getTotalPages());

        // when
        final List<? extends Category> categories = categoryPage.getContent();

        // then
        assertTrue(containsIds(categories, idsToCheck));
    }

    @Test
    @DisplayName("Test search category by autocomplete title fragment")
    public void testFindByAutoCompleteTitleFragment() {
        // given
        final String titleTerms = "Island Planet";

        // when
        final FacetPage<? extends Category> categoryFacetPage = getCategoryService().findByAutoCompleteTitleFragment(titleTerms, PageRequest.of(0, 5));

        // then
        assertEquals(4, categoryFacetPage.getNumberOfElements());

        // when
        final Map<String, Long> categoryFacetCounts = getFacetCounts(categoryFacetPage);

        // then
        assertNull(categoryFacetCounts.get("Test"));
        assertEquals(Long.valueOf(4), categoryFacetCounts.get("island"));
        assertEquals(Long.valueOf(2), categoryFacetCounts.get("treasure"));
    }

    @Test
    @DisplayName("Test search category by query")
    public void testFindByQuery() {
        // given
        final String queryString = "index:[1 TO 3] OR title:*land*";

        // when
        final Page<? extends Category> productPage = getCategoryService().findByQuery(SearchableCategory.COLLECTION_ID, getQuery(queryString));
        productPage.getContent();

        // then
        assertEquals(6, productPage.getTotalElements());
    }

    @Test
    @DisplayName("Test search category by facet query")
    public void testFindByFacetQuery() {
        // given
        final Criteria criteria = new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD);
        final FacetQuery facetQuery = new SimpleFacetQuery(criteria)
            .setFacetOptions(new FacetOptions()
                .addFacetOnField(SearchableCategory.TITLE_FIELD_NAME)
                .setFacetLimit(5));

        // when
        final FacetPage<? extends Category> productPage = getCategoryService().findByFacetQuery(SearchableCategory.COLLECTION_ID, facetQuery);

        // then
        assertThat(productPage.getContent(), hasSize(10));

        // when
        final Map<String, Long> categoryFacetCounts = getFacetCounts(productPage);

        // then
        assertNull(categoryFacetCounts.get("Test"));
        assertEquals(Long.valueOf(4), categoryFacetCounts.get("island"));
        assertEquals(Long.valueOf(2), categoryFacetCounts.get("treasure"));
    }

    @Test
    @DisplayName("Test search category by custom query")
    public void testFindByCustomQuery() {
        // given
        final String searchTerm = "cookies";

        // when
        final HighlightPage<? extends Category> categoryHighlightPage = getCategoryService().find(Category.COLLECTION_ID, searchTerm, PageRequest.of(0, 10));
        categoryHighlightPage.getContent();

        // then
        assertTrue(containsSnipplet(categoryHighlightPage, "Handling <highlight>Cookies</highlight>"));
        assertFalse(containsSnipplet(categoryHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    protected Cursor<? extends Category> findAllCategories() {
        return getSolrTemplate().queryForCursor(SearchableCategory.COLLECTION_ID, getQuery("*:*").addSort(Sort.by("id")), Category.class);
    }

    private List<Category> getSampleData() {
        if (getCategoryService().find("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Category> categories = new ArrayList<>();
        Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.");
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 3, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller");
        category.addProduct(createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("03", 3, "Solr for dummies", "Get started with solr");
        category.addProduct(createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller");
        category.addProduct(createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("05", 5, "Spring Island", "The perfect island romance..");
        category.addProduct(createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.");
        category.addProduct(createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 10, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!");
        category.addProduct(createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!");
        category.addProduct(createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 0, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!");
        category.addProduct(createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 20, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications");
        category.addProduct(createProduct("10", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 10, 1.0, 2.0, true));
        categories.add(category);
        return categories;
    }

    protected CategorySearchService getCategoryService() {
        return this.categoryService;
    }
}
