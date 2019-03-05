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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.category.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.category.CategorySearchController;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.impl.BaseDocumentSearchControllerImpl;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.wrapper.SearchRequest;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.BadRequestException;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableCategory;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.CategorySearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.CategoryView;
import io.swagger.annotations.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils.map;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils.mapAll;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.formatMessage;

/**
 * Category {@link CategorySearchController} implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(CategorySearchController.CONTROLLER_ID)
@RequestMapping(value = "/api/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(
    value = "/api/category",
    description = "Endpoint for category search operations",
    consumes = "application/json, application/xml",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
    authorizations = {
        @Authorization(value = "category_store_auth",
            scopes = {
                @AuthorizationScope(scope = "write:documents", description = "modify category documents"),
                @AuthorizationScope(scope = "read:documents", description = "read category documents")
            })
    })
public class CategorySearchControllerImpl extends BaseDocumentSearchControllerImpl<Category, CategoryView, String> implements CategorySearchController {

    /**
     * Default {@link CategorySearchService} instance
     */
    @Autowired
    private CategorySearchService categoryService;

    @GetMapping("/search")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds category documents by search query",
        notes = "Returns list of category documents by search query",
        nickname = "search",
        tags = {"fetchByQuery"},
        response = CategoryView.class,
        responseContainer = "List",
        consumes = "application/json, application/xml",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search query value"),
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> search(@ApiParam(value = "Search query query to fetch categories by", allowEmptyValue = true, readOnly = true) @RequestParam(value = "q", required = false) final String query,
                                    @ApiParam(value = "Page number to filter by") @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable,
                                    final HttpServletRequest request) {
        log.info("Fetching categories by search query: {}", query);
        final Page<? extends Category> categoryPage = getSearchService().findByTitle(query, pageable);
        if (Objects.isNull(categoryPage)) {
            throw new BadRequestException(formatMessage(getMessageSource(), "error.bad.request"));
        }
        return ResponseEntity
            .ok()
            .headers(getHeaders(categoryPage))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(mapAll(categoryPage.getContent(), CategoryView.class));
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds category documents by autocomplete search term",
        notes = "Returns list of category documents by autocomplete search query",
        nickname = "autoComplete",
        tags = {"fetchByAutocomplete"},
        position = 1,
        response = CategoryView.class,
        responseContainer = "List",
        consumes = "application/json, application/xml",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search term value")
    })
    public ResponseEntity<?> autoComplete(@ApiParam(value = "Search term query to fetch categories by", required = true, readOnly = true) @RequestParam("term") final String searchTerm,
                                          @ApiParam(value = "Page number to filter by") @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
        log.info("Fetching categories by autocomplete search term: {}", searchTerm);
        final FacetPage<? extends Category> categoryPage = getSearchService().findByAutoCompleteTitleFragment(searchTerm, pageable);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .headers(getHeaders(categoryPage))
            .body(mapAll(getResultSetByTerm(categoryPage, searchTerm), CategoryView.class));
    }

    @GetMapping("/page")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds category documents by search term",
        notes = "Returns list of category documents by search term",
        nickname = "find",
        tags = {"fetchByTerm"},
        position = 2,
        response = CategoryView.class,
        responseContainer = "List",
        consumes = "application/json, application/xml",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 405, message = "Invalid input value")
    })
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(@ApiParam(value = "Search term query to fetch categories by", required = true, readOnly = true) @RequestParam("term") final String searchTerm,
                                  @ApiParam(value = "Offset number to filter by", required = true, readOnly = true) @RequestParam(value = "offset", defaultValue = DEFAULT_PAGE_OFFSET_VALUE) int offset,
                                  @ApiParam(value = "Limit number to filter by", required = true, readOnly = true) @RequestParam(value = "limit", defaultValue = DEFAULT_PAGE_LIMIT_VALUE) int limit) {
        log.info("Fetching categories by search term: {}, offset: {}, limit: {}", searchTerm, offset, limit);
        final HighlightPage<Category> page = (HighlightPage<Category>) findBy(SearchableCategory.COLLECTION_ID, searchTerm, offset, limit);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .headers(getHeaders(page))
            .body(page
                .stream()
                .map(document -> getHighLightSearchResult(document, page.getHighlights(document), CategoryView.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/all")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds all category documents",
        notes = "Returns list of all category documents",
        nickname = "findAll",
        tags = {"fetchAll"},
        position = 3,
        response = CategoryView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> findAll() {
        log.info("Fetching all categories");
        try {
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mapAll(this.getAllItems(), CategoryView.class));
        } catch (EmptyContentException ex) {
            return ResponseEntity
                .noContent()
                .build();
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds category document by ID",
        notes = "For valid response try integer IDs with positive integer value. Negative or non-integer values will generate API errors",
        nickname = "searchById",
        tags = {"fetchById"},
        position = 4,
        response = CategoryView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid category ID value"),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 405, message = "Validation exception")
    })
    public ResponseEntity<?> search(@ApiParam(value = "Category ID to fetch by", required = true, readOnly = true) @PathVariable("id") final String id,
                                    final HttpServletRequest request) {
        log.info("Fetching categories by ID: {}", id);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(map(this.getItem(id), CategoryView.class));
    }

    @GetMapping("/search/{term}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds category documents by search term",
        notes = "Returns list of category documents by search term",
        nickname = "findBySearchTerm",
        tags = {"fetchByTermAnPage"},
        position = 5,
        response = CategoryView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search term value")
    })
    public ResponseEntity<?> findBySearchTerm(@ApiParam(value = "Search term query to fetch categories by", required = true, readOnly = true) @PathVariable("term") final String searchTerm,
                                              @ApiParam(value = "Page number to filter by", allowableValues = "range[1,infinity]", required = true, readOnly = true) @PathVariable("page") int page) {
        log.info("Fetching products by search term: {}, page: {}", searchTerm, page);
        final HighlightPage<? extends Category> categoryPage = getSearchService().find(SearchableCategory.COLLECTION_ID, searchTerm, PageRequest.of(page, DEFAULT_PAGE_SIZE));
        if (Objects.isNull(categoryPage)) {
            throw new BadRequestException(formatMessage(getMessageSource(), "error.bad.request"));
        }
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(mapAll(categoryPage.getContent(), CategoryView.class));
    }

    @GetMapping("/desc/{desc}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds category documents by description",
        notes = "Returns list of category documents by description query",
        nickname = "findByDescription",
        tags = {"fetchByDesc"},
        position = 6,
        response = CategoryView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid description value")
    })
    public ResponseEntity<?> findByDescription(@ApiParam(value = "Search description query to fetch categories by", required = true, readOnly = true) @PathVariable("desc") final String description,
                                               @ApiParam(value = "Page number to filter by", allowableValues = "range[1,infinity]", required = true, readOnly = true) @PathVariable("page") int page) {
        log.info("Fetching categories by description: {}, page: {}", description, page);
        final Page<? extends Category> categoryPage = getSearchService().findByDescription(description, PageRequest.of(page, DEFAULT_PAGE_SIZE));
        if (Objects.isNull(categoryPage)) {
            throw new BadRequestException(formatMessage(getMessageSource(), "error.bad.request"));
        }
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(mapAll(categoryPage.getContent(), CategoryView.class));
    }

    @PostMapping("/search/title")
    @ResponseBody
    @ApiOperation(
        httpMethod = "POST",
        value = "Finds category documents by title",
        notes = "Returns list of category documents by title",
        nickname = "findByTitles",
        tags = {"fetchByTitles"},
        position = 7,
        response = CategoryView.class,
        responseContainer = "List",
        consumes = "application/json, application/xml",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 405, message = "Invalid input value")
    })
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> findByTitles(@ApiParam(value = "Search request to fetch categories by title", required = true, readOnly = true) @Valid @RequestBody final SearchRequest searchRequest,
                                          @ApiParam(value = "Page number to filter by") @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
        log.info("Fetching categories by title: {}", StringUtils.join(searchRequest.getKeywords(), "|"));
        final HighlightPage<Category> page = (HighlightPage<Category>) getSearchService().findByTitleIn(searchRequest.getKeywords(), pageable);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .headers(getHeaders(page))
            .body(page
                .stream()
                .map(document -> getHighLightSearchResult(document, page.getHighlights(document), CategoryView.class))
                .collect(Collectors.toList()));
    }

    /**
     * Returns {@link CategorySearchService} service
     *
     * @return {@link CategorySearchService} service
     */
    protected CategorySearchService getSearchService() {
        return this.categoryService;
    }
}
