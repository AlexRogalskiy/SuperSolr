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
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.utils.OffsetPageRequest;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.CategoryView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.CategoryService;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom category controller implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(CategoryController.CONTROLLER_ID)
@RequestMapping("/api/category")
public class CategoryControllerImpl extends BaseModelControllerImpl<Category, CategoryView, String> implements CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/search")
    public String search(final Model model,
                         final @RequestParam(value = "q", required = false) String query,
                         final @PageableDefault(page = 0, size = DEFAULT_PAGE_SIZE) Pageable pageable,
                         final HttpServletRequest request) {
        log.info("Search by query: {}", query);
        model.addAttribute("page", getService().findByTitle(query, pageable));
        model.addAttribute("pageable", pageable);
        model.addAttribute("query", query);
        return "search";
    }

    @ResponseBody
    @RequestMapping(value = "/autocomplete", produces = "application/json")
    public Set<String> autoComplete(final Model model,
                                    final @RequestParam("term") String query,
                                    final @PageableDefault(page = 0, size = 1) Pageable pageable) {
        log.info("Search autocomplete by query: {}", query);
        if (!StringUtils.hasText(query)) {
            return Collections.emptySet();
        }
        final FacetPage<? extends Category> result = getService().autocompleteTitleFragment(query, pageable);
        final Set<String> titles = new LinkedHashSet<>();
        for (final Page<FacetFieldEntry> page : result.getFacetResultPages()) {
            for (final FacetFieldEntry entry : page) {
                if (entry.getValue().contains(query)) {
                    titles.add(entry.getValue());
                }
            }
        }
        return titles;
    }

    @GetMapping("/category/search")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(
            final @RequestParam String searchTerm,
            final @RequestParam(defaultValue = "0") int offset,
            final @RequestParam(defaultValue = "10") int limit) {
        final HighlightPage<Category> page = (HighlightPage<Category>) getService().find(searchTerm, new OffsetPageRequest(offset, limit));
        return new ResponseEntity<>(page
                .stream()
                .map((Category document) -> getResult(document, page.getHighlights(document), CategoryView.class))
                .collect(Collectors.toList()), getHeaders(page), HttpStatus.OK);
    }

    @RequestMapping("/category")
    @ResponseBody
    public Iterable<? extends Category> helloWorld() {
        return getService().findAll();
    }

    protected CategoryService getService() {
        return this.categoryService;
    }
}
