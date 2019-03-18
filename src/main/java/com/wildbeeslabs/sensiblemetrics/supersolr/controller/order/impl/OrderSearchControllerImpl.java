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
package com.wildbeeslabs.sensiblemetrics.supersolr.controller.order.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.annotation.SwaggerAPI;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.impl.BaseDocumentSearchControllerImpl;
import com.wildbeeslabs.sensiblemetrics.supersolr.controller.order.OrderSearchController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableOrder;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.OrderSearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.OrderView;
import io.swagger.annotations.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils.map;
import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils.mapAll;

/**
 * Order {@link OrderSearchController} implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(OrderSearchController.CONTROLLER_ID)
@RequestMapping(value = "/api/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@SwaggerAPI
@Api(
    value = "/api/order",
    description = "Endpoint for order search operations",
    consumes = "application/json, application/xml",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
    authorizations = {
        @Authorization(value = "order_store_auth",
            scopes = {
                @AuthorizationScope(scope = "write:documents", description = "modify order documents"),
                @AuthorizationScope(scope = "read:documents", description = "read order documents")
            })
    })
@Secured("ROLE_MANAGER")
public class OrderSearchControllerImpl extends BaseDocumentSearchControllerImpl<Order, OrderView, String> implements OrderSearchController {

    @Autowired
    private HttpServletRequest request;

    /**
     * Default {@link OrderSearchService} instance
     */
    @Autowired
    private OrderSearchService orderService;

    @GetMapping("/all")
    @ResponseBody
    @Override
    @ApiOperation(
        httpMethod = "GET",
        value = "Find all order documents",
        notes = "Returns list of all order documents",
        nickname = "fetchAll",
        tags = {"fetchAll"},
        response = OrderView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> getAll() {
        log.info("Fetching all orders");
        try {
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mapAll(this.getAllItems(), OrderView.class));
        } catch (EmptyContentException ex) {
            return ResponseEntity
                .noContent()
                .build();
        }
    }

    @PostMapping("/create")
    @ResponseStatus
    @ApiOperation(
        httpMethod = "POST",
        value = "Creates order document",
        notes = "Returns empty response",
        nickname = "createOrder",
        tags = {"newOrder"},
        position = 1,
        response = String.class,
        consumes = "application/json, application/xml",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key"),
        responseHeaders = {
            @ResponseHeader(name = "Location", description = "location with newly created order", response = String.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid order document value"),
        @ApiResponse(code = 405, message = "Validation exception")
    })
    public ResponseEntity<?> createOrder(@ApiParam(value = "Order that needs to be added to the store", required = true, readOnly = true) @Valid @RequestBody final OrderView order) {
        log.info("Creating new order by view: {}", order);
        final OrderView orderDtoCreated = map(this.createItem(order, Order.class), OrderView.class);
        final UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        final URI uri = ucBuilder.path(this.request.getRequestURI() + "/{id}").buildAndExpand(orderDtoCreated.getId()).toUri();
        return ResponseEntity
            .created(uri)
            .header(HttpHeaders.LOCATION, uri.toString())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .build();
    }

    @GetMapping("/{id}")
    @ResponseBody
    @Override
    @ApiOperation(
        httpMethod = "GET",
        value = "Find order document by ID",
        notes = "Returns order document by ID",
        nickname = "getById",
        tags = {"fetchById"},
        position = 2,
        response = OrderView.class,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid order ID value"),
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> getById(@ApiParam(value = "Order ID that needs to be fetched", required = true, readOnly = true) @PathVariable("id") final String id) {
        log.info("Fetching order by ID: {}", id);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(map(this.getItem(id), OrderView.class));
    }

    @PutMapping("/")
    @ResponseBody
    @ApiOperation(
        httpMethod = "PUT",
        value = "Updates order document in the store with form data",
        notes = "Returns updated order document",
        nickname = "updateOrder",
        tags = {"updateOrder"},
        position = 3,
        response = OrderView.class,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 405, message = "Invalid input value")
    })
    public ResponseEntity<?> updateOrder(@ApiParam(value = "Order that needs to be updated", required = true, readOnly = true) @Valid @RequestBody final OrderView order) {
        log.info("Updating order by view: {}", order);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(map(this.updateItem(order.getId(), order, Order.class), OrderView.class));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "DELETE",
        value = "Deletes order document by ID",
        notes = "For valid response try integer IDs with positive integer value. Negative or non-integer values will generate API errors",
        nickname = "deleteOrder",
        tags = {"removeOrder"},
        position = 4,
        response = OrderView.class,
        consumes = "application/json, application/xml",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid order ID value")
    })
    public ResponseEntity<?> deleteOrder(@ApiParam(value = "Security authentication API Key", required = true, readOnly = true) final String apiKey,
                                         @ApiParam(value = "Order ID that needs to be deleted", required = true) @PathVariable("id") final String id) {
        log.info("Updating order by ID: {}", id);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(map(this.deleteItem(id), OrderView.class));
    }

    @DeleteMapping("/delete-all")
    @ResponseStatus
    @ApiOperation(
        httpMethod = "DELETE",
        value = "Deletes all order documents",
        notes = "Returns empty response",
        nickname = "deleteAll",
        tags = {"removeAll"},
        position = 5,
        consumes = "application/json, application/xml",
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> deleteAll(@ApiParam(value = "Security authentication API Key", required = true, readOnly = true) final String apiKey) {
        log.info("Deleting all orders");
        this.deleteAllItems();
        return ResponseEntity
            .ok()
            .build();
    }

    @GetMapping("/desc/{desc}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds order documents by description",
        notes = "Returns list of order documents by description query",
        nickname = "findByDescription",
        tags = {"fetchByDesc"},
        position = 6,
        response = OrderView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid description value")
    })
    public ResponseEntity<?> findByDescription(@ApiParam(value = "Search description query to filter orders by", required = true) @PathVariable("desc") final String description,
                                               @ApiParam(value = "Page number to filter by", allowableValues = "range[1,infinity]", required = true) @PathVariable("page") int page) {
        log.info("Fetching order by description: {}, page: {}", description, page);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(mapAll(getSearchService().findByDescription(description, PageRequest.of(page, DEFAULT_PAGE_SIZE)).getContent(), OrderView.class));
    }

    @GetMapping("/search/{term}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds order documents by search term",
        notes = "Returns list of order documents by search term",
        nickname = "findBySearchTerm",
        tags = {"fetchByTerm"},
        position = 7,
        response = OrderView.class,
        responseContainer = "List",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search term value")
    })
    public ResponseEntity<?> findBySearchTerm(@ApiParam(value = "Search term query to fetch orders by", required = true) @PathVariable("term") final String searchTerm,
                                              @ApiParam(value = "Page number to filter by", allowableValues = "range[1,infinity]", required = true) @PathVariable int page) {
        log.info("Fetching order by term: {}, page: {}", searchTerm, page);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(mapAll(getSearchService().find(SearchableOrder.COLLECTION_ID, searchTerm, PageRequest.of(page, DEFAULT_PAGE_SIZE)).getContent(), OrderView.class));
    }

    /**
     * Returns {@link OrderSearchService} instance
     *
     * @return @link OrderSearchService} instance
     */
    @Override
    protected OrderSearchService getSearchService() {
        return this.orderService;
    }
}
