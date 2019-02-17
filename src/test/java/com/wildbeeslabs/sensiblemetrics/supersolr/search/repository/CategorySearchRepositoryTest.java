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
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
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
 * Category repository implementation unit test
 */
@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class CategorySearchRepositoryTest extends BaseDocumentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategorySearchRepository categorySearchRepository;

    @Before
    public void before() {
        getCategorySearchRepository().saveAll(getSampleData());
    }

    @After
    public void after() {
        getCategorySearchRepository().deleteAll();
    }

    @Test
    public void testFindByTitle() {
        // given
        final Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null);
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        this.entityManager.persist(category);
        this.entityManager.flush();

        // when
        final Page<? extends Category> categoryPage = getCategorySearchRepository().findByTitle(category.getTitle(), PageRequest.of(0, 2));
        final List<? extends Category> categories = categoryPage.getContent();

        // then
        assertTrue(categories.contains(category));
    }

    @Test
    public void testFindByDescription() {
        // initial search terms
        final String searchTerm = "Test";

        // given
        final Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null);
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));

        this.entityManager.persist(category);
        this.entityManager.flush();

        // when
        final Page<? extends Category> categoryPage = getCategorySearchRepository().findByDescription(searchTerm, PageRequest.of(0, 2));
        final List<? extends Category> categories = categoryPage.getContent();

        // then
        assertTrue(categories.contains(category));
    }

    @Test
    public void testFindByTitleStartsWith() {
        // initial search terms
        final List<String> titles = Arrays.asList("Title 01", "Title 02");

        //given
        final Category categoryFirst = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null);
        categoryFirst.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        this.entityManager.persist(categoryFirst);

        final Category categorySecond = createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller", null);
        categorySecond.addProduct(createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        this.entityManager.persist(categorySecond);

        final Category categoryThird = createCategory("03", 3, "Solr for dummies", "Get started with solr", null);
        categoryThird.addProduct(createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        this.entityManager.persist(categoryThird);
        this.entityManager.flush();

        // when
        final FacetPage<? extends Category> categoryFacetPage = getCategorySearchRepository().findByTitleStartsWith(titles, PageRequest.of(0, 10));
        final List<? extends Category> categories = categoryFacetPage.getContent();

        // then
        assertTrue(categories.contains(categoryFirst));
        assertTrue(categories.contains(categorySecond));
        assertFalse(categories.contains(categoryThird));
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
        final HighlightPage<? extends Category> categoryHighlightPage = getCategorySearchRepository().findByHighlightedValueIn(titles, PageRequest.of(0, 10));
        final List<? extends Category> categories = categoryHighlightPage.getContent();

        // then
        assertTrue(categories.contains(category));
    }

    private List<Category> getSampleData() {
        if (getCategorySearchRepository().findById("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Category> categories = new ArrayList<>();
        Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.", null);
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller", null);
        category.addProduct(createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("03", 3, "Solr for dummies", "Get started with solr", null);
        category.addProduct(createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller", null);
        category.addProduct(createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("05", 5, "Spring Island", "The perfect island romance..", null);
        category.addProduct(createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.", null);
        category.addProduct(createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!", null);
        category.addProduct(createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!", null);
        category.addProduct(createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!", null);
        category.addProduct(createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);

        category = createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications", null);
        category.addProduct(createProduct("10", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 1.0, 2.0, true, null));
        categories.add(category);
        return categories;
    }

    protected CategorySearchRepository getCategorySearchRepository() {
        return this.categorySearchRepository;
    }
}