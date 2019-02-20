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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.CategorySearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.CategoryView;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.ProductView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Category controller implementation unit test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(CategorySearchControllerImpl.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "file:src/test/resources/application.properties")
@DirtiesContext
public class CategorySearchControllerImplTest {

    @Value("${supersolr.solr.server.url}")
    private String url;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CategorySearchService categoryService;

    @Test
    @SuppressWarnings("unchecked")
    public void testSearch() {
        // given
        final List<? extends CategoryView> list = new ArrayList<>();

        // when
        final ResponseEntity<? extends List<ProductView>> entity = (ResponseEntity<? extends List<ProductView>>) this.restTemplate.getForEntity(this.url, list.getClass());

        // then
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("Test", entity.getBody());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchAll() throws Exception {
        // given
        final Category category = new Category();

        // when
        final Iterable<Category> categories = (Iterable<Category>) getCategoryService().findAll();

        // then
        given(categories).willReturn(Arrays.asList(category));

        this.mvc.perform(get("/api/categories")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(category.getTitle())));
    }

    @Test
    public void testSearchById() throws Exception {
        // when
        given(getCategoryService().find("01"))
            .willReturn(Optional.of(new Category()));

        // then
        this.mvc.perform(get("/api/category/search")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello World"));
    }

    protected CategorySearchService getCategoryService() {
        return this.categoryService;
    }
}
