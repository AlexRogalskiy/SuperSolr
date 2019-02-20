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

import com.google.common.net.HttpHeaders;
import com.wildbeeslabs.sensiblemetrics.supersolr.BaseTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.CategorySearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.CategoryView;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.ProductView;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Category controller implementation unit test {@link BaseTest}
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "file:src/test/resources/application.properties")
@DirtiesContext
public class CategorySearchControllerImplTest extends BaseTest {

    @Value("${supersolr.solr.server.url}")
    private String url;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private CategorySearchService categorySearchService;

    @Before
    public void before() {
        getCategorySearchService().save(getSampleData());
    }

    @After
    public void after() {
        getCategorySearchService().deleteAll();
    }

    @Test
    @DisplayName("Test search all categories by rest template")
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
    @DisplayName("Test search categories by non-existing url")
    @SuppressWarnings("unchecked")
    public void testNotFound() throws Exception {
        this.mockMvc.perform(get("/api/categories")
            .session(getSession("USER"))
            .header(HttpHeaders.ORIGIN, this.url)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test search all categories")
    @SuppressWarnings("unchecked")
    public void testSearchAll() throws Exception {
        // given
        final Category category = new Category();
        final String responseText = "<ArrayList><item><score>0.0</score></item></ArrayList>";

        // when
        final Iterable<Category> categories = (Iterable<Category>) getCategorySearchService().findAll();

        // then
        given(categories).willReturn(Arrays.asList(category));

        this.mockMvc.perform(get("/api/category/all")
            .session(getSession("USER"))
            .header(HttpHeaders.ORIGIN, this.url)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_XML_UTF8_VALUE))
            .andExpect(content().xml(responseText));
        //.andExpect(jsonPath("$", hasSize(1)))
        //.andExpect(jsonPath("$[0].name", is(category.getTitle())));
    }

    @Test
    @DisplayName("Test search category by non-existing term")
    public void testSearchByNonExistingTerm() throws Exception {
        // when
        given(getCategorySearchService().find("01"))
            .willReturn(Optional.of(new Category()));

        // then
        this.mockMvc.perform(get("/api/category/search")
            .session(getSession("USER"))
            .header(HttpHeaders.ORIGIN, this.url)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test search category by term")
    public void testSearchByTerm() throws Exception {
        // when
        given(getCategorySearchService().find("01"))
            .willReturn(Optional.of(new Category()));

        // then
        this.mockMvc.perform(get("/api/category/search/name/1")
            .session(getSession("USER"))
            .header(HttpHeaders.ORIGIN, this.url)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello World"));
    }

    protected UsernamePasswordAuthenticationToken getPrincipal(final String username) {
        final UserDetails user = this.userDetailsService.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities());
        return authentication;
    }

    protected MockHttpSession getSession(final String username) {
        final UsernamePasswordAuthenticationToken principal = this.getPrincipal(username);
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            new MockSecurityContext(principal));
        return session;
    }

    @SuppressWarnings("unchecked")
    private List<Category> getSampleData() {
        if (getCategorySearchService().find("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Category> categories = new ArrayList<>();
        Category category = createCategory("01", 1, "Treasure Island", "Best seller by R.L.S.");
        category.addProduct(createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("02", 2, "Treasure Island 2.0", "Humorous remake of the famous best seller");
        category.addProduct(createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("03", 3, "Solr for dummies", "Get started with solr");
        category.addProduct(createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller");
        category.addProduct(createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("05", 5, "Spring Island", "The perfect island romance..");
        category.addProduct(createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("06", 6, "Refactoring", "It's about improving the design of existing code.");
        category.addProduct(createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("07", 7, "Baking for dummies", "Bake your own cookies, on a secret island!");
        category.addProduct(createProduct("07", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 19, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("08", 8, "The Pirate Island", "Oh noes, the pirates are coming!");
        category.addProduct(createProduct("08", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 11, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("09", 9, "Blackbeard", "It's the pirate Edward Teach!");
        category.addProduct(createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 13, 1.0, 2.0, true));
        categories.add(category);

        category = createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications");
        category.addProduct(createProduct("10", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 0, 1.0, 2.0, true));
        categories.add(category);
        return categories;
    }

    protected CategorySearchService getCategorySearchService() {
        return this.categorySearchService;
    }
}
