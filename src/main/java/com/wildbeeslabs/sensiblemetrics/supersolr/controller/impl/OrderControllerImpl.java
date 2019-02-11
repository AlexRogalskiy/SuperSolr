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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.OrderController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.EmptyContentException;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Order;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.OrderView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.OrderService;
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
import java.util.List;

/**
 * Custom order controller implementation
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RestController(OrderController.CONTROLLER_ID)
public class OrderControllerImpl extends BaseModelControllerImpl<Order, OrderView, Long> implements OrderController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    @Override
    public ResponseEntity<?> getAll() {
        try {
            return new ResponseEntity<>(MapperUtils.mapAll(this.getAllItems(), OrderView.class), HttpStatus.OK);
        } catch (EmptyContentException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(final @RequestBody OrderView orderDto) {
        final OrderView orderDtoCreated = MapperUtils.map(this.createItem(orderDto, Order.class), OrderView.class);
        final UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path(this.request.getRequestURI() + "/{id}").buildAndExpand(orderDtoCreated.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    @Override
    public ResponseEntity<?> getById(final @PathVariable Long orderId) {
        return new ResponseEntity<>(MapperUtils.map(this.getItem(orderId), OrderView.class), HttpStatus.OK);
    }

    @PutMapping("/order")
    public ResponseEntity<?> updateOrder(final @RequestBody OrderView orderDto) {
        log.info("Updating order by order: {}", orderDto);
        final OrderView orderDtoUpdated = MapperUtils.map(this.updateItem(orderDto.getId(), orderDto, Order.class), OrderView.class);
        return new ResponseEntity<>(orderDtoUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<?> deleteOrder(final @PathVariable Long orderId) {
        final OrderView orderDtoDeleted = MapperUtils.map(this.deleteItem(orderId), OrderView.class);
        return new ResponseEntity<>(orderDtoDeleted, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteAll() {
        this.deleteAllItems();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/order/desc/{description}/{page}")
    public ResponseEntity<?> findOrder(final @PathVariable String description, final @PathVariable int page) {
        final List<? extends OrderView> orderDtos = MapperUtils.mapAll(getService().findByDescription(description, PageRequest.of(page, 2)).getContent(), OrderView.class);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/order/search/{searchTerm}/{page}")
    public ResponseEntity<?> findOrderBySearchTerm(final @PathVariable String searchTerm, final @PathVariable int page) {
        final List<? extends OrderView> orderDtos = MapperUtils.mapAll(getService().findByCustomQuery(searchTerm, PageRequest.of(page, 2)).getContent(), OrderView.class);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @Override
    protected OrderService getService() {
        return this.orderService;
    }
}
