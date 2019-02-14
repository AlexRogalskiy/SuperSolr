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
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Category;
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
 * Category service implementation unit test
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoryServiceImplTest extends BaseModelServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Before
    public void before() {
        createSampleData();
    }

    @Test
    public void testFindAll() {
        final Iterable<? extends Category> categoryIterable = getCategoryService().findAll();
        final List<? extends Category> categories = Lists.newArrayList(categoryIterable);
        assertThat(categories, not(empty()));
        assertEquals(10, categories.size());
    }

    @Test
    public void testFindAllByIds() {
        final List<String> categoryIds = Arrays.asList("01", "02", "03");
        final Iterable<? extends Category> categoryIterable = getCategoryService().findAll(categoryIds);
        final List<? extends Category> categories = Lists.newArrayList(categoryIterable);
        assertThat(categories, not(empty()));
        assertEquals(categoryIds.size(), categories.size());
    }

    @Test
    public void testFindByTitle() {
        final Page<? extends Category> categoryPage = getCategoryService().findByTitle("Test", OffsetPageRequest.builder().offset(0).limit(1).build());
        assertEquals(4, categoryPage.getTotalElements());
        assertEquals(4, categoryPage.getTotalPages());
        final List<? extends Category> categories = categoryPage.getContent();
        assertTrue(containsIds(categories, "01", "02", "05", "08"));
    }

    @Test
    public void testFindByDescription() {
        final Page<? extends Category> categoryPage = getCategoryService().findByDescription("Island", PageRequest.of(0, 10));
        final List<? extends Category> categories = categoryPage.getContent();
        assertEquals(5, categories.size());
        assertTrue(containsIds(categories, "01", "02", "05", "07", "08"));
        assertEquals("07", categories.get(4).getId());
    }

    @Test
    public void testFindByTitles() {
        final Page<? extends Category> categoryPage = getCategoryService().findByTitles("Island Planet", PageRequest.of(0, 2));
        assertEquals(4, categoryPage.getTotalElements());
        assertEquals(4, categoryPage.getTotalPages());
        final List<? extends Category> categories = categoryPage.getContent();
        assertTrue(containsIds(categories, "01", "02", "05", "08"));
    }

    @Test
    public void testFindByAutoCompleteTitle() {
        final FacetPage<? extends Category> categoryFacetPage = getCategoryService().autoCompleteTitleFragment("Island Planet", PageRequest.of(0, 2));
        assertEquals(2, categoryFacetPage.getNumberOfElements());

        final Map<String, Long> categoryFacetCounts = getFacetCounts(categoryFacetPage);
        assertEquals(new Long(3), categoryFacetCounts.get("Test01"));
        assertEquals(new Long(2), categoryFacetCounts.get("Test02"));
        assertEquals(new Long(1), categoryFacetCounts.get("Test03"));
    }

    @Test
    public void testFindByCustomQuery() {
        final HighlightPage<? extends Category> categoryHighlightPage = getCategoryService().find("cookies", PageRequest.of(0, 10));
        categoryHighlightPage.getContent();
        assertTrue(containsSnipplet(categoryHighlightPage, "How to handle <highlight>cookies</highlight> in web applications"));
        assertTrue(containsSnipplet(categoryHighlightPage, "Bake your own <highlight>cookies</highlight>, on a secret island!"));
    }

    private void createSampleData() {
        if (getCategoryService().find("01").isPresent()) {
            log.debug("Data already exists");
            return;
        }
        Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null);
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller", null);
        category.addProduct(createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("03", 3, "Solr for dummies", "Get started with solr", null);
        category.addProduct(createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller", null);
        category.addProduct(createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("05", 5, "Spring Island", "The perfect island romance..", null);
        category.addProduct(createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.", null);
        category.addProduct(createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!", null);
        category.addProduct(createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!", null);
        category.addProduct(createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!", null);
        category.addProduct(createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        category = createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications", null);
        category.addProduct(createProduct("10", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
    }

    protected CategoryService getCategoryService() {
        return this.categoryService;
    }
}