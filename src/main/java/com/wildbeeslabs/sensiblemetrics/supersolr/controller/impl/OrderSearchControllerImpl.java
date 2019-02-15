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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Custom order search controller implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(OrderSearchController.CONTROLLER_ID)
@RequestMapping("/api")
public class OrderSearchControllerImpl extends BaseDocumentSearchControllerImpl<Order, OrderView, Long> implements OrderSearchController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrderSearchService orderService;

    @GetMapping("/orders")
    @ResponseBody
    @Override
    public ResponseEntity<?> getAll() {
        log.info("Fetching all orders");
        try {
            return new ResponseEntity<>(MapperUtils.mapAll(this.getAllItems(), OrderView.class), HttpStatus.OK);
        } catch (EmptyContentException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/order")
    @ResponseStatus
    public ResponseEntity<?> createOrder(final @Valid @RequestBody OrderView orderDto) {
        log.info("Creating new order by view: {}", orderDto);
        final OrderView orderDtoCreated = MapperUtils.map(this.createItem(orderDto, Order.class), OrderView.class);
        final UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path(this.request.getRequestURI() + "/{id}").buildAndExpand(orderDtoCreated.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/order/{id}")
    @ResponseBody
    @Override
    public ResponseEntity<?> getById(final @PathVariable Long id) {
        log.info("Fetching order by ID: {}", id);
        return new ResponseEntity<>(MapperUtils.map(this.getItem(id), OrderView.class), HttpStatus.OK);
    }

    @PutMapping("/order")
    @ResponseBody
    public ResponseEntity<?> updateOrder(final @Valid @RequestBody OrderView orderDto) {
        log.info("Updating order by view: {}", orderDto);
        final OrderView orderDtoUpdated = MapperUtils.map(this.updateItem(orderDto.getId(), orderDto, Order.class), OrderView.class);
        return new ResponseEntity<>(orderDtoUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/order/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteOrder(final @PathVariable Long id) {
        log.info("Updating order by ID: {}", id);
        final OrderView orderDtoDeleted = MapperUtils.map(this.deleteItem(id), OrderView.class);
        return new ResponseEntity<>(orderDtoDeleted, HttpStatus.OK);
    }

    @DeleteMapping("/orders")
    @ResponseStatus
    @Override
    public ResponseEntity<?> deleteAll() {
        log.info("Deleting all orders");
        this.deleteAllItems();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/order/desc/{description}/{page}")
    @ResponseBody
    public ResponseEntity<?> findByDescription(
            final @PathVariable String description,
            final @PathVariable int page) {
        log.info("Fetching order by description: {}, page: {}", description, page);
        final List<? extends OrderView> orderDtos = MapperUtils.mapAll(getSearchService().findByDescription(description, PageRequest.of(page, 2)).getContent(), OrderView.class);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/order/search/{searchTerm}/{page}")
    @ResponseBody
    public ResponseEntity<?> findBySearchTerm(
            final @PathVariable String searchTerm,
            final @PathVariable int page) {
        log.info("Fetching order by term: {}, page: {}", searchTerm, page);
        final List<? extends OrderView> orderDtos = MapperUtils.mapAll(getSearchService().findByCustomQuery(searchTerm, PageRequest.of(page, 2)).getContent(), OrderView.class);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @Override
    protected OrderSearchService getSearchService() {
        return this.orderService;
    }
}
