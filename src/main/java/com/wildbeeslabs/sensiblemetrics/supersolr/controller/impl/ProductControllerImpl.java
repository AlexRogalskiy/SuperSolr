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
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.utils.OffsetPageRequest;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.ProductView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.ProductService;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom product controller implementation
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
    public String search(final Model model,
                         final @RequestParam(value = "q", required = false) String query,
                         final @PageableDefault Pageable pageable,
                         final HttpServletRequest request) {
        log.info("Search by query: {}", query);
        model.addAttribute("page", getService().findByTitle(query, pageable));
        model.addAttribute("pageable", pageable);
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/product/autocomplete")
    @ResponseBody
    public ResponseEntity<?> autoComplete(final Model model,
                                          final @RequestParam("term") String query,
                                          final @PageableDefault(size = 1) Pageable pageable) {
        log.info("Search autocomplete by query: {}", query);
        if (!StringUtils.hasText(query)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        final Set<String> titles = new LinkedHashSet<>();
        final FacetPage<? extends Product> result = getService().autocompleteTitleFragment(query, pageable);
        result.getFacetResultPages().stream().forEach(page -> {
            page.forEach(entry -> {
                if (entry.getValue().contains(query)) {
                    titles.add(entry.getValue());
                }
            });
        });
        return new ResponseEntity<>(MapperUtils.mapAll(titles, ProductView.class), getHeaders(result), HttpStatus.OK);
    }

    @GetMapping("/product/page")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(
            final @RequestParam String searchTerm,
            final @RequestParam(defaultValue = "0") int offset,
            final @RequestParam(defaultValue = "10") int limit) {
        final HighlightPage<Product> page = (HighlightPage<Product>) getService().find(searchTerm, new OffsetPageRequest(offset, limit));
        return new ResponseEntity<>(page
                .stream()
                .map(document -> getResult(document, page.getHighlights(document), ProductView.class))
                .collect(Collectors.toList()), getHeaders(page), HttpStatus.OK);
    }

    @GetMapping("/products")
    @ResponseBody
    public Iterable<? extends Product> find() {
        return getService().findAll();
    }

    @GetMapping("/product/{id}")
    public String search(final Model model,
                         final @PathVariable("id") String id,
                         final HttpServletRequest request) {
        model.addAttribute("product", getService().find(id));
        return "product";
    }

    protected ProductService getService() {
        return this.productService;
    }
}
