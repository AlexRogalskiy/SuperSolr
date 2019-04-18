package com.wildbeeslabs.sensiblemetrics.supersolr.controller;

import com.wildbeeslabs.sensiblemetrics.supersolr.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

public abstract class AbstractBaseSearchControllerTest extends BaseTest {

    /**
     * Default authentication user name
     */
    public static final String DEFAULT_USERNAME = "USER";
    /**
     * Default authentication principal instance {@link Principal}
     */
    public static final Principal DEFAULT_PRINCIPAL = () -> DEFAULT_USERNAME;

    @Value("${supersolr.solr.server.url}")
    protected String url;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected UserDetailsService userDetailsService;
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//    @Resource
//    private final FilterChainProxy springSecurityFilterChain;

    //    @Before
//    public void before() {
//        this.mockMvc = MockMvcBuilders
//            .webAppContextSetup(this.webApplicationContext)
//            //.addFilters(this.springSecurityFilterChain)
//            .build();
//    }

    protected HttpHeaders getHeaders(final String url) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ORIGIN, url);
        return headers;
    }
}
