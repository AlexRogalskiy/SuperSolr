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

import com.google.common.collect.ImmutableMap;
import com.wildbeeslabs.sensiblemetrics.supersolr.BaseTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.wrapper.SearchRequest;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.iface.ProductSearchService;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.getString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Product controller implementation unit test {@link BaseTest}
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "file:src/test/resources/application.properties")
@DirtiesContext
public class ProductSearchControllerImplTest extends BaseTest {

    /**
     * Default authentication user name
     */
    public static final String DEFAULT_USERNAME = "USER";

    @Value("${supersolr.solr.server.url}")
    private String url;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Autowired
//    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserDetailsService userDetailsService;

    //@Resource
    //private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ProductSearchService productService;

//    @Before
//    public void before() {
//        this.mockMvc = MockMvcBuilders
//            .webAppContextSetup(this.webApplicationContext)
//            //.addFilters(this.springSecurityFilterChain)
//            .build();
//    }

    @Before
    public void before() {
        getProductService().save(getSampleData());
        SecurityContextHolder.clearContext();
    }

    @After
    public void after() {
        getProductService().deleteAll();
    }

    @Test
    @DisplayName("Test fetch all products by rest template")
    @WithMockUser(roles = "USER")
    @SuppressWarnings("rawtypes")
    public void testSearch() {
        // given
        final String urlTemplate = "/api/product/all";

        final HttpEntity<String> requestEntity = new HttpEntity<>(org.springframework.http.HttpHeaders.EMPTY);
        final Map<String, String> params = new ImmutableMap.Builder<String, String>()
            .put("page", "0")
            .put("size", "5")
            .build();

        // when
        final ResponseEntity<List> response = this.restTemplate.exchange(getString(this.url, urlTemplate), HttpMethod.GET, requestEntity, List.class, params);

        // then
        assertThat(response.getBody(), is(nullValue()));
        //assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        //assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertEquals("Test", response.getBody());
    }

    @Test
    @DisplayName("Test fetch products by non-existing url")
    @WithMockUser(roles = "USER")
    @SuppressWarnings("unchecked")
    public void testNotFound() throws Exception {
        // when
        final String urlTemplate = "/api/products";

        // then
        this.mockMvc.perform(get(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test fetch all products")
    @WithMockUser(roles = "USER")
    @SuppressWarnings("unchecked")
    public void testSearchAll() throws Exception {
        // given
        final String urlTemplate = "/api/product/all";
        final String responseText = "[{\"id\":\"01\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":10.0,\"recommendedPrice\":2.0,\"rating\":4},{\"id\":\"02\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":1.0,\"recommendedPrice\":2.0,\"rating\":32},{\"id\":\"03\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":11.0,\"recommendedPrice\":2.0,\"rating\":2},{\"id\":\"04\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":18.0,\"recommendedPrice\":2.0,\"rating\":4},{\"id\":\"05\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":1.0,\"recommendedPrice\":2.0,\"rating\":7},{\"id\":\"06\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":8.0,\"recommendedPrice\":2.0,\"rating\":9},{\"id\":\"07\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":5.0,\"recommendedPrice\":2.0,\"rating\":10},{\"id\":\"08\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":3.0,\"recommendedPrice\":2.0,\"rating\":11},{\"id\":\"09\",\"score\":1.0,\"name\":\"Name\",\"shortDescription\":\"Short description\",\"longDescription\":\"Long description\",\"priceDescription\":\"Price description\",\"catalogNumber\":\"Catalog number\",\"pageTitle\":\"Page title\",\"inStock\":true,\"price\":120.0,\"recommendedPrice\":2.0,\"rating\":13}]";

        // then
        this.mockMvc.perform(get(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test fetch product by non-existing term")
    @WithMockUser(roles = "USER")
    public void testSearchByNonExistingTerm() throws Exception {
        // given
        final String urlTemplate = "/api/product/search/{term}/{page}";
        final String responseText = "[]";

        // then
        this.mockMvc.perform(get(urlTemplate, "test", 1)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders())
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            //.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test fetch product by term")
    @WithMockUser(roles = "USER")
    public void testSearchById() throws Exception {
        // given
        final String urlTemplate = "/api/product/search/{term}/{page}";
        final String responseText = "[]";

        // then
        this.mockMvc.perform(get(urlTemplate, "local", 1)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().string(responseText));
    }

    @Test
    @DisplayName("Test search products by non-existing names")
    @WithMockUser(roles = "USER")
    public void testSearchByNonExistingTitles() throws Exception {
        // given
        final String urlTemplate = "/api/product/search/name";
        final String responseText = "[]";

        // when
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setKeywords(Arrays.asList("Small", "Huge"));

        // then
        this.mockMvc.perform(post(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders())
            .contentType(MediaType.APPLICATION_JSON)
            .content(getGsonSerializer().toJson(searchRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test search products by names")
    @WithMockUser(roles = "USER")
    public void testSearchByTitles() throws Exception {
        // given
        final String urlTemplate = "/api/product/search/name";
        final String responseText = "[{\"id\":\"06\",\"score\":2.5651255,\"name\":\"Small and tiny handkerchief\",\"catalogNumber\":\"Catalog number\",\"inStock\":false,\"price\":0.0,\"recommendedPrice\":0.0,\"rating\":9,\"highlights\":{\"name\":[\"<highlight>Small</highlight> and tiny <highlight>handkerchief</highlight>\"]}}]";

        // when
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setKeywords(Arrays.asList("Small", "handkerchief"));

        // then
        this.mockMvc.perform(post(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders())
            .contentType(MediaType.APPLICATION_JSON)
            .content(getGsonSerializer().toJson(searchRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test unauthorized access")
    public void testForbiddenAccess() throws Exception {
        // given
        final String urlTemplate = "/api/product/all";

        // headers
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ORIGIN, this.url);

        this.mockMvc.perform(get(urlTemplate)
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @SuppressWarnings("unchecked")
    private List<Product> getSampleData() {
        if (getProductService().find("01").isPresent()) {
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

        product = createProduct("04", "Simple name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 18.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));
        products.add(product);

        product = createProduct("05", "Complex name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("05", 5, "Spring Island", "The perfect island romance.."));
        products.add(product);

        product = createProduct("06", "Small and tiny handkerchief", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 8.0, 2.0, true);
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

        product = createProduct("09", "Name without specialities", "Short description", "Long description", "Price description", "Catalog number", "Page title", 13, 120.0, 2.0, true);
        product.addCategory(createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications"));
        products.add(product);
        return products;
    }

    protected HttpHeaders getHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ORIGIN, this.url);
        //headers.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encode(("greg:turnquist").getBytes())));
        return headers;
    }

    /**
     * Default
     *
     * @return
     */
    protected ProductSearchService getProductService() {
        return this.productService;
    }
}
