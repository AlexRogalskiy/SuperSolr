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

import com.wildbeeslabs.sensiblemetrics.supersolr.repository.BaseRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.BaseService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Arrays;
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
public abstract class BaseServiceImpl<E, ID extends Serializable> implements BaseService<E, ID> {

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
