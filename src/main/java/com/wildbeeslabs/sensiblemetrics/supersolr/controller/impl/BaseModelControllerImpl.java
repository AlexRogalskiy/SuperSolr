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

import com.wildbeeslabs.sensiblemetrics.supersolr.controller.BaseModelController;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.ResourceNotFoundException;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.BaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.BaseView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseModelService;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Base model controller implementation
 *
 * @param <E>
 * @param <T>
 * @param <ID>
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BaseModelControllerImpl<E extends BaseModel<ID>, T extends BaseView<ID>, ID extends Serializable> extends BaseControllerImpl<E, T, ID> implements BaseModelController<E, T, ID> {

    @RequestMapping("/search")
    public String search(final Model model,
                         final @RequestParam(value = "q", required = false) String query,
                         final @PageableDefault(page = 0, size = DEFAULT_PAGE_SIZE) Pageable pageable,
                         final HttpServletRequest request) {
        log.info("Search by query: {}", query);
        model.addAttribute("page", getService().findByName(query, pageable));
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
        final FacetPage<? extends E> result = getService().autocompleteNameFragment(query, pageable);
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

    @Override
    public E updateItem(final ID id,
                        final T itemDto,
                        final Class<? extends E> entityClass) {
        log.info("Updating item by ID: {}", id);
        final Optional<? extends E> currentItem = getService().find(id);
        if (!currentItem.isPresent()) {
            throw new ResourceNotFoundException(com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.formatMessage(getMessageSource(), "error.no.item.id", id));
        }
        final E itemEntity = MapperUtils.map(itemDto, entityClass);
        getService().saveOrUpdate(itemEntity, entityClass);
        return currentItem.get();
    }

    protected abstract BaseModelService<E, ID> getService();
}
