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
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.ProductSearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.ProductView;
import lombok.extern.slf4j.Slf4j;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    private ProductSearchService productSearchService;

//    @Before
//    public void before() {
//        this.mockMvc = MockMvcBuilders
//            .webAppContextSetup(this.webApplicationContext)
//            //.addFilters(this.springSecurityFilterChain)
//            .build();
//    }

    @Test
    @DisplayName("Test search all products by rest template")
    @SuppressWarnings("unchecked")
    public void testSearch() {
        final List<? extends ProductView> list = new ArrayList<>();
        final ResponseEntity<? extends List<ProductView>> entity = (ResponseEntity<? extends List<ProductView>>) this.restTemplate.getForEntity(this.url, list.getClass());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("Test", entity.getBody());
    }

    @Test
    @DisplayName("Test search products by non-existing url")
    @SuppressWarnings("unchecked")
    public void testNotFound() throws Exception {
        this.mockMvc.perform(get("/api/products")
            .session(getSession("USER"))
            .header(HttpHeaders.ORIGIN, this.url)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test search all products")
    @SuppressWarnings("unchecked")
    public void testSearchAll() throws Exception {
        final Product product = new Product();
        final Iterable<Product> products = (Iterable<Product>) getProductSearchService().findAll();
        given(products).willReturn(Arrays.asList(product));

        this.mockMvc.perform(get("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ORIGIN, this.url))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(product.getName())));
    }

    @Test
    @DisplayName("Test search product by id")
    public void testSearchById() throws Exception {
        given(getProductSearchService().find("01"))
            .willReturn(Optional.of(new Product()));

        this.mockMvc.perform(get("/api/product/search")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ORIGIN, this.url))
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
    private List<Product> getSampleData() {
        if (getProductSearchService().find("01").isPresent()) {
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

        product = createProduct("04", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 4, 18.0, 2.0, true);
        product.addCategory(createCategory("04", 4, "Moon landing", "All facts about Apollo 11, a best seller"));
        products.add(product);

        product = createProduct("05", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 7, 1.0, 2.0, true);
        product.addCategory(createCategory("05", 5, "Spring Island", "The perfect island romance.."));
        products.add(product);

        product = createProduct("06", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 9, 8.0, 2.0, true);
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

        product = createProduct("09", "Name", "Short description", "Long description", "Price description", "Catalog number", "Page title", 13, 120.0, 2.0, true);
        product.addCategory(createCategory("10", 10, "Handling Cookies", "How to handle cookies in web applications"));
        products.add(product);
        return products;
    }

    protected ProductSearchService getProductSearchService() {
        return this.productSearchService;
    }
}
