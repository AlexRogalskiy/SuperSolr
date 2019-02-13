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
import com.wildbeeslabs.sensiblemetrics.supersolr.model.utils.OffsetPageRequest;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.view.BaseModelView;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseModelService;
import com.wildbeeslabs.sensiblemetrics.supersolr.utility.MapperUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.formatMessage;

/**
 * Base model controller implementation
 *
 * @param <E>  type of entity model
 * @param <T>  type of entity view model
 * @param <ID> type of entity identifier
 */
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BaseModelControllerImpl<E extends BaseModel<ID>, T extends BaseModelView<ID>, ID extends Serializable> extends AuditModelControllerImpl<E, T, ID> implements BaseModelController<E, T, ID> {

    @Override
    public E updateItem(final ID id, final T itemDto, final Class<? extends E> entityClass) {
        final Optional<? extends E> currentItem = getService().find(id);
        if (!currentItem.isPresent()) {
            throw new ResourceNotFoundException(formatMessage(getMessageSource(), "error.no.item.id", id));
        }
        final E itemEntity = MapperUtils.map(itemDto, entityClass);
        getService().saveOrUpdate(itemEntity, entityClass);
        return currentItem.get();
    }

    protected T getHighLightSearchResult(final E entity, final List<HighlightEntry.Highlight> highlights, final Class<? extends T> dtoClass) {
        final Map<String, List<String>> highlightMap = highlights
                .stream()
                .collect(Collectors.toMap(h -> h.getField().getName(), HighlightEntry.Highlight::getSnipplets));
        final T updatedDto = MapperUtils.map(entity, dtoClass);
        updatedDto.setHighlights(highlightMap);
        return updatedDto;
    }

    protected Set<String> getResultSetByTerm(final FacetPage<? extends E> facetPage, final String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return Collections.emptySet();
        }
        final Set<String> resultSet = new LinkedHashSet<>();
        facetPage.getFacetResultPages().stream().forEach(page -> {
            page.forEach(entry -> {
                if (entry.getValue().contains(searchTerm)) {
                    resultSet.add(entry.getValue());
                }
            });
        });
        return resultSet;
    }

    protected Map<String, Long> getResultMapByTerm(final FacetPage<? extends E> facetPage, final String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return Collections.emptyMap();
        }
        final Map<String, Long> resultMap = new HashMap<>();
        facetPage.getAllFacets()
                .stream()
                .forEach(page -> page.forEach(facetEntry -> {
                    resultMap.put(facetEntry.getValue(), facetEntry.getValueCount());
                }));
        return resultMap;
    }

    protected List<String> getHighLightSearchSnipplets(final HighlightPage<? extends E> highlightPage) {
        final List<String> resultList = new ArrayList<>();
        highlightPage.getContent();
        highlightPage.getHighlighted().stream().forEach(content -> {
            content.getHighlights().forEach(highlight -> {
                resultList.addAll(highlight.getSnipplets());
            });
        });
        return resultList;
    }

    protected HighlightPage<? extends E> findBy(
            final String searchTerm,
            int offset,
            int limit) {
        return getService().find(searchTerm, OffsetPageRequest.builder().offset(offset).limit(limit).build());
    }

    protected abstract BaseModelService<E, ID> getService();
}
