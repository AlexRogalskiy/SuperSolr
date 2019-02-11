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
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Objects;

/**
 * Custom base model service implementation
 *
 * @param <E>
 * @param <ID>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BaseModelServiceImpl<E extends BaseModel<ID>, ID extends Serializable> extends BaseServiceImpl<E, ID> implements BaseModelService<E, ID> {

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
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

    @Override
    public long count(final String searchTerm) {
        return getRepository().count(searchTerm);
    }

    protected abstract BaseModelRepository<E, ID> getRepository();
}
