/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wildbeeslabs.sensiblemetrics.supersolr.service.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.BaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.BaseRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Custom base service implementation
 *
 * @param <E>
 * @param <ID>
 */
@Slf4j
@EqualsAndHashCode
@ToString
public abstract class BaseServiceImpl<E extends BaseModel<ID>, ID extends Serializable> implements BaseService<E, ID> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<E> findAll() {
        log.info("Fetching all target entities");
        return getRepository().findAll();
    }

    @Override
    public Optional<E> find(final ID id) {
        log.info("Fetching target entity by ID: {}", id);
        return getRepository().findById(id);
    }

    @Override
    public void save(final E target) {
        log.info("Saving target entity: {}", target);
        getRepository().save(target);
    }

    @Override
    public void saveOrUpdate(final E target, final Class<E> clazz) {
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
    public void save(final Iterable<E> target) {
        log.info("Saving target entity: {}", Arrays.asList(target));
        getRepository().saveAll(target);
    }

    @Override
    public void delete(final E target) {
        log.info("Deleting target entity: {}", target);
        getRepository().delete(target);
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected abstract BaseRepository<E, ID> getRepository();
}
