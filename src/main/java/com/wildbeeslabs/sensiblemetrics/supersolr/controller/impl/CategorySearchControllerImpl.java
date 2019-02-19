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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.CategorySearchController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.CategorySearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.CategoryView;
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
 * Custom category search controller implementation {@link BaseDocumentSearchControllerImpl}
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(CategorySearchController.CONTROLLER_ID)
@RequestMapping("/api")
public class CategorySearchControllerImpl extends BaseDocumentSearchControllerImpl<Category, CategoryView, String> implements CategorySearchController {

    @Autowired
    private CategorySearchService categoryService;

    @GetMapping("/category/search")
    @ResponseBody
    public ResponseEntity<?> search(final @RequestParam(value = "q", required = false) String query,
                                    final @PageableDefault Pageable pageable,
                                    final HttpServletRequest request) {
        log.info("Fetching categories by query: {}", query);
        final Page<? extends Category> categoryPage = getSearchService().findByTitle(query, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(categoryPage.getContent(), CategoryView.class), getHeaders(categoryPage), HttpStatus.OK);
    }

    @GetMapping("/category/autocomplete")
    @ResponseBody
    public ResponseEntity<?> autoComplete(final @RequestParam("term") String searchTerm,
                                          final @PageableDefault(size = 1) Pageable pageable) {
        log.info("Fetching categories by autocomplete search term: {}", searchTerm);
        final FacetPage<? extends Category> categoryPage = getSearchService().findByAutoCompleteTitleFragment(searchTerm, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(getResultSetByTerm(categoryPage, searchTerm), CategoryView.class), getHeaders(categoryPage), HttpStatus.OK);
    }

    @GetMapping("/category/page")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(
            final @RequestParam String searchTerm,
            final @RequestParam(defaultValue = DEFAULT_PAGE_OFFSET_VALUE) int offset,
            final @RequestParam(defaultValue = DEFAULT_PAGE_LIMIT_VALUE) int limit) {
        log.info("Fetching categories by search term: {}, offset: {}, limit: {}", searchTerm, offset, limit);
        final HighlightPage<Category> page = (HighlightPage<Category>) findBy(searchTerm, offset, limit);
        return new ResponseEntity<>(page
                .stream()
                .map(document -> getHighLightSearchResult(document, page.getHighlights(document), CategoryView.class))
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

    @GetMapping("/category/desc/{description}/{page}")
    @ResponseBody
    public ResponseEntity<?> findByDescription(
            final @PathVariable String description,
            final @PathVariable int page) {
        log.info("Fetching category by description: {}, page: {}", description, page);
        final List<? extends CategoryView> categoryViews = MapperUtils.mapAll(getSearchService().findByDescription(description, PageRequest.of(page, 2)).getContent(), CategoryView.class);
        return new ResponseEntity<>(categoryViews, HttpStatus.OK);
    }

    protected CategorySearchService getSearchService() {
        return this.categoryService;
    }
}
