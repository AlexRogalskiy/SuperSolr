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

import com.wildbeeslabs.sensiblemetrics.supersolr.BaseTest;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.OrderSearchService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.getString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Order controller implementation unit test {@link BaseTest}
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "file:src/test/resources/application.properties")
@DirtiesContext
public class OrderSearchControllerImplTest extends BaseTest {

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

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OrderSearchService orderService;

    @Before
    public void before() {
        getOrderService().save(getSampleData());
    }

    @After
    public void after() {
        getOrderService().deleteAll();
    }

    @Test
    @DisplayName("Test search all orders by rest template")
    @SuppressWarnings("rawtypes")
    public void testSearch() {
        // given
        final String urlTemplate = "/api/order/all";

        final HttpEntity<String> requestEntity = new HttpEntity<>(org.springframework.http.HttpHeaders.EMPTY);
        final Map<String, String> params = new HashMap<>();
        params.put("page", "0");
        params.put("size", "5");

        // when
        final ResponseEntity<List> response = this.restTemplate.exchange(getString(this.url, urlTemplate), HttpMethod.GET, requestEntity, List.class, params);

        // then
        assertThat(response.getBody(), is(nullValue()));
        //assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        //assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertEquals("Test", response.getBody());
    }

    @Test
    @DisplayName("Test search orders by non-existing url")
    @SuppressWarnings("unchecked")
    public void testNotFound() throws Exception {
        //given
        final String urlTemplate = "/api/orders";

        // headers
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ORIGIN, this.url);

        // then
        this.mockMvc.perform(get(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test search all orders")
    @SuppressWarnings("unchecked")
    public void testSearchAll() throws Exception {
        // given
        final String urlTemplate = "/api/order/all";
        final String responseText = "[{\"id\":\"01\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"02\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"03\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"04\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"05\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"}]";

        // headers
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ORIGIN, this.url);

        // then
        this.mockMvc.perform(get(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test unauthorized access")
    public void testForbiddenAccess() throws Exception {
        // given
        final String urlTemplate = "/api/order/all";

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
    private List<Order> getSampleData() {
        if (getOrderService().find("01").isPresent()) {
            log.debug("Data already exists");
            return Collections.emptyList();
        }
        final List<Order> orders = new ArrayList<>();
        Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true);
        Order order = createOrder("01", "mobile", "name", "title", "description", product);
        orders.add(order);

        product = createProduct("02", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 1.0, 2.0, true);
        order = createOrder("02", "mobile", "name", "title", "description", product);
        orders.add(order);

        product = createProduct("03", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 1.0, 2.0, true);
        order = createOrder("03", "mobile", "name", "title", "description", product);
        orders.add(order);

        product = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 5, 1.0, 2.0, true);
        order = createOrder("04", "mobile", "name", "title", "description", product);
        orders.add(order);

        product = createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true);
        order = createOrder("05", "mobile", "name", "title", "description", product);
        orders.add(order);

        return orders;
    }

    protected OrderSearchService getOrderService() {
        return this.orderService;
    }
}
