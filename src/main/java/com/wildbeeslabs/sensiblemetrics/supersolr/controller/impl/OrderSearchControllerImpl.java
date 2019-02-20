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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.OrderSearchController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.service.OrderSearchService;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.view.OrderView;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import io.swagger.annotations.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Custom order search controller implementation {@link BaseDocumentSearchControllerImpl}
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(OrderSearchController.CONTROLLER_ID)
@RequestMapping("/api/order")
@ApiModel(value = "OrderSearchController", description = "Order search controller documentation")
@Api(value = "/api/order", description = "Operations on order documents", authorizations = {
    @Authorization(value = "order_store_auth",
        scopes = {
            @AuthorizationScope(scope = "write:documents", description = "modify order documents"),
            @AuthorizationScope(scope = "read:documents", description = "read order documents")
        })
})
public class OrderSearchControllerImpl extends BaseDocumentSearchControllerImpl<Order, OrderView, String> implements OrderSearchController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrderSearchService orderService;

    @GetMapping("/all")
    @ResponseBody
    @Override
    @ApiOperation(
        httpMethod = "GET",
        value = "Find all order documents",
        notes = "Returns list of all order documents",
        nickname = "getAll",
        tags = {"fetchAll"},
        response = List.class,
        responseContainer = "List",
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> getAll() {
        log.info("Fetching all orders");
        try {
            return new ResponseEntity<>(MapperUtils.mapAll(this.getAllItems(), OrderView.class), HttpStatus.OK);
        } catch (EmptyContentException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/")
    @ResponseStatus
    @ApiOperation(
        httpMethod = "POST",
        value = "Creates order document",
        notes = "Returns empty response",
        nickname = "createOrder",
        tags = {"newOrder"},
        position = 1,
        response = String.class,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key"),
        responseHeaders = {
            @ResponseHeader(name = "Location", description = "location with newly created order", response = String.class)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid order document value"),
        @ApiResponse(code = 405, message = "Validation exception")
    })
    public ResponseEntity<?> createOrder(final @ApiParam(value = "Order document that needs to be added to the store", required = true) @Valid @RequestBody OrderView orderDto) {
        log.info("Creating new order by view: {}", orderDto);
        final OrderView orderDtoCreated = MapperUtils.map(this.createItem(orderDto, Order.class), OrderView.class);
        final UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path(this.request.getRequestURI() + "/{id}").buildAndExpand(orderDtoCreated.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
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
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid order ID value"),
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> getById(final @ApiParam(value = "ID of order document that needs to be fetched", allowableValues = "range[1,infinity]", required = true) @PathVariable String id) {
        log.info("Fetching order by ID: {}", id);
        return new ResponseEntity<>(MapperUtils.map(this.getItem(id), OrderView.class), HttpStatus.OK);
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
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 405, message = "Invalid input value")
    })
    public ResponseEntity<?> updateOrder(final @ApiParam(value = "Order document that needs to be updated", required = true) @Valid @RequestBody OrderView orderDto) {
        log.info("Updating order by view: {}", orderDto);
        final OrderView orderDtoUpdated = MapperUtils.map(this.updateItem(orderDto.getId(), orderDto, Order.class), OrderView.class);
        return new ResponseEntity<>(orderDtoUpdated, HttpStatus.OK);
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
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid order ID value")
    })
    public ResponseEntity<?> deleteOrder(
        final @ApiParam() String apiKey,
        final @ApiParam(value = "ID of the order that needs to be deleted", allowableValues = "range[1,infinity]", required = true) @PathVariable String id) {
        log.info("Updating order by ID: {}", id);
        final OrderView orderDtoDeleted = MapperUtils.map(this.deleteItem(id), OrderView.class);
        return new ResponseEntity<>(orderDtoDeleted, HttpStatus.OK);
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
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<?> deleteAll(final @ApiParam() String apiKey) {
        log.info("Deleting all orders");
        this.deleteAllItems();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/desc/{description}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds order documents by description",
        notes = "Returns list of order documents by description query",
        nickname = "findByDescription",
        tags = {"fetchByDesc"},
        position = 6,
        response = List.class,
        responseContainer = "List",
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid description value")
    })
    public ResponseEntity<?> findByDescription(
        final @ApiParam(value = "Description search query to filter by", required = true) @PathVariable String description,
        final @ApiParam(value = "Page to filter by", allowableValues = "range[1,infinity]", required = true) @PathVariable int page) {
        log.info("Fetching order by description: {}, page: {}", description, page);
        final List<? extends OrderView> orderDtos = MapperUtils.mapAll(getSearchService().findByDescription(description, PageRequest.of(page, 2)).getContent(), OrderView.class);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/search/{searchTerm}/{page}")
    @ResponseBody
    @ApiOperation(
        httpMethod = "GET",
        value = "Finds order documents by search term",
        notes = "Returns list of order documents by search term",
        nickname = "findBySearchTerm",
        tags = {"fetchByTerm"},
        position = 7,
        response = List.class,
        responseContainer = "List",
        authorizations = @Authorization(value = "api_key")
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid search term value")
    })
    public ResponseEntity<?> findBySearchTerm(
        final @ApiParam(value = "Search term to filter by", required = true) @PathVariable String searchTerm,
        final @ApiParam(value = "Page to filter by", allowableValues = "range[1,infinity]", required = true) @PathVariable int page) {
        log.info("Fetching order by term: {}, page: {}", searchTerm, page);
        final List<? extends OrderView> orderDtos = MapperUtils.mapAll(getSearchService().find(searchTerm, PageRequest.of(page, 2)).getContent(), OrderView.class);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @Override
    protected OrderSearchService getSearchService() {
        return this.orderService;
    }
}
