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
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.iface.OrderSearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.entity.OrderView;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.getString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Order controller implementation unit test {@link BaseTest}
 */
@Slf4j
@Getter(AccessLevel.PROTECTED)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "file:src/test/resources/application.properties")
@DirtiesContext
public class OrderSearchControllerImplTest extends AbstractBaseSearchControllerTest {

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
    @DisplayName("Test fetch all orders by rest template")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("rawtypes")
    public void testSearch() {
        // given
        final String urlTemplate = "/api/order/all";

        final HttpEntity<String> requestEntity = new HttpEntity<>(HttpHeaders.EMPTY);
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
    @DisplayName("Test fetch orders by non-existing url")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testNotFound() throws Exception {
        //given
        final String urlTemplate = "/api/orders";

        // then
        this.mockMvc.perform(get(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test fetch all orders")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testSearchAll() throws Exception {
        // given
        final String urlTemplate = "/api/order/all";
        final String responseText = "[{\"id\":\"01\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"02\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"03\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"04\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"},{\"id\":\"05\",\"score\":1.0,\"title\":\"title\",\"description\":\"description\"}]";

        // then
        this.mockMvc.perform(get(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().string(containsString("02")))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test fetch order by id")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testSearchById() throws Exception {
        // given
        final String urlTemplate = "/api/order/{id}";
        final String responseText = "{\"id\":\"01\",\"score\":1.3862944,\"title\":\"title\",\"description\":\"description\"}";

        // then
        this.mockMvc.perform(get(urlTemplate, "01")
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().string(containsString("01")))
            .andExpect(content().string(not(containsString("04"))))
            .andExpect(content().json(responseText));
    }

    @Test
    @DisplayName("Test fetch order by non-existing id")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testSearchByNonExistingId() throws Exception {
        // given
        final String urlTemplate = "/api/order/{id}";

        // then
        this.mockMvc.perform(get(urlTemplate, 100)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test delete order by id")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testDeleteById() throws Exception {
        // given
        final String urlTemplate = "/api/order/{id}";

        // then
        this.mockMvc.perform(delete(urlTemplate, "01")
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().string(containsString("01")));
    }

    @Test
    @DisplayName("Test create order")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testCreateOrder() throws Exception {
        // given
        final String urlTemplate = "/api/order/";

        final Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true);
        final Order order = createOrder("21", "mobile", "name", "title", "description", product);

        // then
        this.mockMvc.perform(post(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getGsonSerializer().toJson(MapperUtils.map(order, OrderView.class))))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test update order")
    @WithMockUser(roles = "MANAGER")
    @SuppressWarnings("unchecked")
    public void testUpdateOrder() throws Exception {
        // given
        final String urlTemplate = "/api/order/";

        final Product product = createProduct("01", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 8, 1.0, 2.0, true);
        final Order order = createOrder("01", "mobile", "name", "title", "description", product);
        final OrderView orderView = MapperUtils.map(order, OrderView.class);

        // then
        this.mockMvc.perform(put(urlTemplate)
            .session(getSession(userDetailsService, DEFAULT_USERNAME))
            .headers(getHeaders(this.url))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getGsonSerializer().toJson(orderView)))
            .andExpect(status().is(204));
    }

    @Test
    @DisplayName("Test unauthorized access")
    public void testForbiddenAccess() throws Exception {
        // given
        final String urlTemplate = "/api/order/all";

        this.mockMvc.perform(get(urlTemplate)
            .headers(getHeaders(this.url))
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
}
