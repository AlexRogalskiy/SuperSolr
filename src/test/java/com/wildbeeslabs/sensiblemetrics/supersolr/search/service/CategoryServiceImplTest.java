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
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
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
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Category service implementation unit test
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
public class CategoryServiceImplTest extends BaseDocumentTest {

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
    public void testFindAll() {
        // given
        final int totalElements = 10;

        // when
        final Iterable<? extends Category> categoryIterable = getCategoryService().findAll();
        final List<? extends Category> categories = Lists.newArrayList(categoryIterable);

        // assert
        assertThat(categories, not(empty()));
        assertEquals(totalElements, categories.size());
    }

    @Test
    public void testFindAllByIds() {
        // given
        final List<String> categoryIds = Arrays.asList("01", "02", "03");

        // when
        final Iterable<? extends Category> categoryIterable = getCategoryService().findAll(categoryIds);
        final List<? extends Category> categories = Lists.newArrayList(categoryIterable);

        // assert
        assertThat(categories, not(empty()));
        assertEquals(categoryIds.size(), categories.size());
    }

    @Test
    public void testFindByTitle() {
        // given
        final String[] idsToCheck = {"05"};
        final String titleTerm = "Spring";
        final int totalElements = 1;

        // when
        final Page<? extends Category> categoryPage = getCategoryService().findByTitle(titleTerm, OffsetPageRequest.builder().offset(0).limit(10).build());

        // assert
        assertEquals(totalElements, categoryPage.getTotalElements());
        assertEquals(totalElements, categoryPage.getTotalPages());

        // when
        final List<? extends Category> categories = categoryPage.getContent();

        // assert
        assertTrue(containsIds(categories, idsToCheck));
    }

    @Test
    public void testFindByDescription() {
        // given
        final String[] idsToCheck = {"05", "07"};
        final String descriptionTerm = "Island";
        final int totalElements = 2;

        // when
        final Page<? extends Category> categoryPage = getCategoryService().findByDescription(descriptionTerm, PageRequest.of(0, 10));
        final List<? extends Category> categories = categoryPage.getContent();

        // assert
        assertEquals(totalElements, categories.size());
        assertTrue(containsIds(categories, idsToCheck));
    }

    @Test
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

        // assert
        assertEquals(totalElements, categoryPage.getTotalElements());
        assertEquals(totalElements, categoryPage.getTotalPages());

        // when
        final List<? extends Category> categories = categoryPage.getContent();

        // assert
        assertTrue(containsIds(categories, idsToCheck));
    }

    @Test
    public void testFindByAutoCompleteTitle() {
        // given
        final String titleTerms = "Island Planet";

        // when
        final FacetPage<? extends Category> categoryFacetPage = getCategoryService().findByAutoCompleteTitleFragment(titleTerms, PageRequest.of(0, 5));

        // assert
        assertEquals(2, categoryFacetPage.getNumberOfElements());

        // when
        final Map<String, Long> categoryFacetCounts = getFacetCounts(categoryFacetPage);

        // assert
        assertEquals(new Long(3), categoryFacetCounts.get("Test01"));
        assertEquals(new Long(2), categoryFacetCounts.get("Test02"));
        assertEquals(new Long(1), categoryFacetCounts.get("Test03"));
    }

    @Test
    public void testFindByQuery() {
        //given
        final Criteria criteria = new SimpleStringCriteria("doctype:category");

        //assert
        final Page<? extends Category> productPage = getCategoryService().findByQuery(new SimpleQuery(criteria));
        productPage.getContent();

        // assert
        assertEquals(11, productPage.getTotalElements());
    }

    @Test
    public void testFindByCustomQuery() {
        // given
        final String searchTerm = "cookies";

        // when
        final HighlightPage<? extends Category> categoryHighlightPage = getCategoryService().find(searchTerm, PageRequest.of(0, 10));
        categoryHighlightPage.getContent();

        // assert
        assertTrue(containsSnipplet(categoryHighlightPage, "How to handle <highlight>cookies</highlight> in web applications"));
        assertTrue(containsSnipplet(categoryHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    private List<Category> getSampleData() {
        if (getCategoryService().find("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Category> categories = new ArrayList<>();
        Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.");
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller");
        category.addProduct(createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("03", 3, "Solr for dummies", "Get started with solr");
        category.addProduct(createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller");
        category.addProduct(createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("05", 5, "Spring Island", "The perfect island romance..");
        category.addProduct(createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.");
        category.addProduct(createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!");
        category.addProduct(createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!");
        category.addProduct(createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!");
        category.addProduct(createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications");
        category.addProduct(createProduct("10", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true));
        categories.add(category);
        return categories;
    }

    protected CategorySearchService getCategoryService() {
        return this.categoryService;
    }
}