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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.CategoryController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.CategoryView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.CategoryService;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * Custom category REST controller implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(CategoryController.CONTROLLER_ID)
@RequestMapping("/api")
public class CategoryControllerImpl extends BaseModelControllerImpl<Category, CategoryView, String> implements CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/search")
    @ResponseBody
    public ResponseEntity<?> search(final @RequestParam(value = "q", required = false) String query,
                                    final @PageableDefault Pageable pageable,
                                    final HttpServletRequest request) {
        log.info("Fetching categories by query: {}", query);
        final Page<? extends Category> categoryPage = getService().findByTitle(query, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(categoryPage.getContent(), CategoryView.class), getHeaders(categoryPage), HttpStatus.OK);
    }

    @GetMapping("/category/autocomplete")
    @ResponseBody
    public ResponseEntity<?> autoComplete(final @RequestParam("term") String searchTerm,
                                          final @PageableDefault(size = 1) Pageable pageable) {
        log.info("Fetching categories by autocomplete search term: {}", searchTerm);
        final FacetPage<? extends Category> categoryPage = getService().autoCompleteTitleFragment(searchTerm, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(getResultSetByTerm(categoryPage, searchTerm), CategoryView.class), getHeaders(categoryPage), HttpStatus.OK);
    }

    @GetMapping("/category/page")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(
            final @RequestParam String searchTerm,
            final @RequestParam(defaultValue = DEFAULT_OFFSET_VALUE) int offset,
            final @RequestParam(defaultValue = DEFAULT_LIMIT_VALUE) int limit) {
        log.info("Fetching categories by search term: {}, offset: {}, limit: {}", searchTerm, offset, limit);
        final HighlightPage<Category> page = (HighlightPage<Category>) findBy(searchTerm, offset, limit);
        return new ResponseEntity<>(page
                .stream()
                .map(document -> getHighLightPageResult(document, page.getHighlights(document), CategoryView.class))
                .collect(Collectors.toList()), getHeaders(page), HttpStatus.OK);
    }

    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<?> findAll() {
        log.info("Fetching all categories");
        try {
            return new ResponseEntity<>(MapperUtils.mapAll(this.getAllItems(), CategoryView.class), HttpStatus.OK);
        } catch (EmptyContentException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/category/{id}")
    @ResponseBody
    public ResponseEntity<?> search(final @PathVariable("id") String id,
                                    final HttpServletRequest request) {
        log.info("Fetching category by ID: {}", id);
        return new ResponseEntity<>(MapperUtils.map(this.getItem(id), CategoryView.class), HttpStatus.OK);
    }

    protected CategoryService getService() {
        return this.categoryService;
    }
}
