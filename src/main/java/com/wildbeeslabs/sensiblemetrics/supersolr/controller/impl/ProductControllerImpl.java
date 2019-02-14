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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.ProductController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.ProductView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.ProductService;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom product REST controller implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(ProductController.CONTROLLER_ID)
@RequestMapping("/api")
public class ProductControllerImpl extends BaseModelControllerImpl<Product, ProductView, String> implements ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/search")
    @ResponseBody
    public ResponseEntity<?> search(final @RequestParam(value = "q", required = false) String query,
                                    final @PageableDefault Pageable pageable,
                                    final HttpServletRequest request) {
        log.info("Fetching products by query: {}", query);
        final Page<? extends Product> productPage = getService().findByName(query, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(productPage.getContent(), ProductView.class), getHeaders(productPage), HttpStatus.OK);
    }

    @GetMapping("/product/autocomplete")
    @ResponseBody
    public ResponseEntity<?> autoComplete(final @RequestParam("term") String searchTerm,
                                          final @PageableDefault(size = 1) Pageable pageable) {
        log.info("Fetching products by autocomplete search term: {}", searchTerm);
        final FacetPage<? extends Product> productPage = getService().autoCompleteNameFragment(searchTerm, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(getResultSetByTerm(productPage, searchTerm), ProductView.class), getHeaders(productPage), HttpStatus.OK);
    }

    @GetMapping("/product/page")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(
            final @RequestParam String searchTerm,
            final @RequestParam(defaultValue = DEFAULT_OFFSET_VALUE) int offset,
            final @RequestParam(defaultValue = DEFAULT_LIMIT_VALUE) int limit) {
        log.info("Fetching products by search term: {}, offset: {}, limit: {}", searchTerm, offset, limit);
        final HighlightPage<Product> page = (HighlightPage<Product>) findBy(searchTerm, offset, limit);
        return new ResponseEntity<>(page
                .stream()
                .map(document -> getHighLightSearchResult(document, page.getHighlights(document), ProductView.class))
                .collect(Collectors.toList()), getHeaders(page), HttpStatus.OK);
    }

    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<?> findAll() {
        log.info("Fetching all products");
        try {
            return new ResponseEntity<>(MapperUtils.mapAll(this.getAllItems(), ProductView.class), HttpStatus.OK);
        } catch (EmptyContentException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/product/desc/{description}/{page}")
    @ResponseBody
    public ResponseEntity<?> findByDescription(
            final @PathVariable String description,
            final @PathVariable int page) {
        log.info("Fetching product by description: {}, page: {}", description, page);
        final List<? extends ProductView> productViews = MapperUtils.mapAll(getService().findByDescription(description, PageRequest.of(page, 2)).getContent(), ProductView.class);
        return new ResponseEntity<>(productViews, HttpStatus.OK);
    }

    @GetMapping("/product/search/{searchTerm}/{page}")
    @ResponseBody
    public ResponseEntity<?> findBySearchTerm(
            final @PathVariable String searchTerm,
            final @PathVariable int page) {
        log.info("Fetching product by term: {}, page: {}", searchTerm, page);
        final List<? extends ProductView> productViews = MapperUtils.mapAll(getService().findByCustomQuery(searchTerm, PageRequest.of(page, 2)).getContent(), ProductView.class);
        return new ResponseEntity<>(productViews, HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    @ResponseBody
    public ResponseEntity<?> search(
            final @PathVariable("id") String id,
            final HttpServletRequest request) {
        log.info("Fetching product by ID: {}", id);
        return new ResponseEntity<>(MapperUtils.map(this.getItem(id), ProductView.class), HttpStatus.OK);
    }

    protected ProductService getService() {
        return this.productService;
    }
}
