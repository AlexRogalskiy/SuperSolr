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
package com.wildbeeslabs.sensiblemetrics.supersolr.service.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.BaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.BaseModelRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseModelService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Custom base model service implementation
 *
 * @param <E>  type of entity model
 * @param <ID> type of entity identifier
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Transactional
public abstract class BaseModelServiceImpl<E extends BaseModel<ID>, ID extends Serializable> extends AuditModelServiceImpl<E, ID> implements BaseModelService<E, ID> {

    @Override
    public void saveOrUpdate(final E target, final Class<? extends E> clazz) {
        log.info("Saving or updating target entity: {}", target);
        if (target.isNew()) {
            getEntityManager().persist(target);
        } else {
            final E targetEntity = getEntityManager().find(clazz, target.getId());
            if (Objects.isNull(targetEntity)) {
                getEntityManager().persist(target);
            } else {
                getEntityManager().merge(target);
            }
        }
    }

    protected Collection<String> tokenize(final String searchTerm) {
        final String[] searchTerms = StringUtils.split(searchTerm, DEFAULT_SEARСH_TERM_DELIMITER);
        return Arrays.stream(searchTerms)
                .filter(StringUtils::isNotEmpty)
                .map(term -> DEFAULT_IGNORED_CHARS_PATTERN.matcher(term).replaceAll(DEFAULT_SEARСH_TERM_REPLACEMENT))
                .collect(Collectors.toList());
    }

    protected List<? extends E> search(final Query query, final Class<? extends E> clazz) {
        final Page<? extends E> results = getSolrTemplate().queryForPage(query, clazz);
        return results.getContent();
    }

    protected abstract BaseModelRepository<E, ID> getRepository();
}
