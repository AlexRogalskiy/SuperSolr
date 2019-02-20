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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.ProductSearchController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Product;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.ProductSearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.ProductView;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import io.swagger.annotations.*;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom product search controller implementation {@link BaseDocumentSearchControllerImpl}
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(ProductSearchController.CONTROLLER_ID)
@RequestMapping("/api")
@ApiModel(value = "ProductSearchController", description = "Product search controller documentation")
@Api(value = "/api/product", description = "Operations on product documents", authorizations = {
    @Authorization(value = "order_store_auth",
        scopes = {
            @AuthorizationScope(scope = "write:documents", description = "modify product documents"),
            @AuthorizationScope(scope = "read:documents", description = "read product documents")
        })
})
public class ProductSearchControllerImpl extends BaseDocumentSearchControllerImpl<Product, ProductView, String> implements ProductSearchController {

    @Autowired
    private ProductSearchService productService;

    @GetMapping("/search")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds product documents by search query",
        notes = "Returns list of product documents by search query",
        nickname = "search",
        tags = {"fetchByQuery"},
        response = List.class,
        responseContainer = "List",
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search query value"),
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> search(final @ApiParam(value = "Search query to fetch by", required = true) @RequestParam(value = "q", required = false) String query,
                                    final @PageableDefault Pageable pageable,
                                    final HttpServletRequest request) {
        log.info("Fetching products by query: {}", query);
        final Page<? extends Product> productPage = getSearchService().findByName(query, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(productPage.getContent(), ProductView.class), getHeaders(productPage), HttpStatus.OK);
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds product documents by autocomplete search term",
        notes = "Returns list of product documents by autocomplete search query",
        nickname = "autoComplete",
        tags = {"fetchByAutocomplete"},
        position = 1,
        response = List.class,
        responseContainer = "List",
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search term value")
    })
    public ResponseEntity<?> autoComplete(final @ApiParam(value = "Search term to fetch by", required = true) @RequestParam("term") String searchTerm,
                                          final @PageableDefault(size = 1) Pageable pageable) {
        log.info("Fetching products by autocomplete search term: {}", searchTerm);
        final FacetPage<? extends Product> productPage = getSearchService().findByAutoCompleteNameFragment(searchTerm, pageable);
        return new ResponseEntity<>(MapperUtils.mapAll(getResultSetByTerm(productPage, searchTerm), ProductView.class), getHeaders(productPage), HttpStatus.OK);
    }

    @GetMapping("/page")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds product documents by search term",
        notes = "Returns list of product documents by search term",
        nickname = "find",
        tags = {"fetchByTerm"},
        position = 2,
        response = List.class,
        responseContainer = "List",
        responseHeaders = {
            @ResponseHeader(name = "X-Expires-After", description = "date in UTC when token expires", response = Date.class),
            @ResponseHeader(name = "X-Total-Elements", description = "total number of results in response", response = Integer.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 405, message = "Invalid input value")
    })
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> find(
        final @ApiParam(value = "Search term to filter by", required = true) @RequestParam String searchTerm,
        final @RequestParam(defaultValue = DEFAULT_PAGE_OFFSET_VALUE) int offset,
        final @RequestParam(defaultValue = DEFAULT_PAGE_LIMIT_VALUE) int limit) {
        log.info("Fetching products by search term: {}, offset: {}, limit: {}", searchTerm, offset, limit);
        final HighlightPage<Product> page = (HighlightPage<Product>) findBy(searchTerm, offset, limit);
        return new ResponseEntity<>(page
            .stream()
            .map(document -> getHighLightSearchResult(document, page.getHighlights(document), ProductView.class))
            .collect(Collectors.toList()), getHeaders(page), HttpStatus.OK);
    }

    @GetMapping("/all")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds all product documents",
        notes = "Returns list of all product documents",
        nickname = "findAll",
        tags = {"fetchAll"},
        position = 3,
        response = List.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> findAll() {
        log.info("Fetching all products");
        try {
            return new ResponseEntity<>(MapperUtils.mapAll(this.getAllItems(), ProductView.class), HttpStatus.OK);
        } catch (EmptyContentException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/desc/{description}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds product documents by description",
        notes = "Returns list of product documents by description query",
        nickname = "findByDescription",
        tags = {"fetchByDesc"},
        position = 6,
        response = List.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid description value")
    })
    public ResponseEntity<?> findByDescription(
        final @ApiParam(value = "Description to filter by", required = true) @PathVariable String description,
        final @ApiParam(value = "Page to filter by", allowableValues = "range[1,infinity]", required = true) @PathVariable int page) {
        log.info("Fetching product by description: {}, page: {}", description, page);
        final List<? extends ProductView> productViews = MapperUtils.mapAll(getSearchService().findByDescription(description, PageRequest.of(page, 2)).getContent(), ProductView.class);
        return new ResponseEntity<>(productViews, HttpStatus.OK);
    }

    @GetMapping("/search/{searchTerm}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds product documents by search term",
        notes = "Returns list of product documents by search term",
        nickname = "findBySearchTerm",
        tags = {"fetchByTermAnPage"},
        position = 5,
        response = List.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search term value")
    })
    public ResponseEntity<?> findBySearchTerm(
        final @ApiParam(value = "Search term to filter by", required = true) @PathVariable String searchTerm,
        final @ApiParam(value = "Page to filter by", allowableValues = "range[1,infinity]", required = true) @PathVariable int page) {
        log.info("Fetching product by term: {}, page: {}", searchTerm, page);
        final List<? extends ProductView> productViews = MapperUtils.mapAll(getSearchService().find(searchTerm, PageRequest.of(page, 2)).getContent(), ProductView.class);
        return new ResponseEntity<>(productViews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds product document by ID",
        notes = "For valid response try integer IDs with positive integer value. Negative or non-integer values will generate API errors",
        nickname = "searchById",
        tags = {"fetchById"},
        position = 4,
        response = List.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid product ID value"),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 405, message = "Validation exception")
    })
    public ResponseEntity<?> search(
        final @ApiParam(value = "ID of the order that needs to be fetched", allowableValues = "range[1,infinity]", required = true) @PathVariable("id") String id,
        final HttpServletRequest request) {
        log.info("Fetching product by ID: {}", id);
        return new ResponseEntity<>(MapperUtils.map(this.getItem(id), ProductView.class), HttpStatus.OK);
    }

    protected ProductSearchService getSearchService() {
        return this.productService;
    }
}
