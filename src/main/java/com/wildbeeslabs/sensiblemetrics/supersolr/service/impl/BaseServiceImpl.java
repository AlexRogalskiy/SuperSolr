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
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
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
    @Transactional(readOnly = true)
    public Iterable<E> findAll() {
        log.info("Fetching all target entities");
        return getRepository().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<? extends E> findAll(final Iterable<ID> ids) {
        return getRepository().findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<E> find(final ID id) {
        log.info("Fetching target entity by ID: {}", id);
        return getRepository().findById(id);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public <S extends E> S save(final S target) {
        log.info("Saving target entity: {}", target);
        return getRepository().save(target);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public <S extends E> Iterable<S> save(final Iterable<S> target) {
        log.info("Saving target entity: {}", StringUtils.join(target, "|"));
        return getRepository().saveAll(target);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void delete(final E target) {
        log.info("Deleting target entity: {}", target);
        getRepository().delete(target);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void deleteAll(final Iterable<? extends E> target) {
        log.info("Deleting target entities: {}", StringUtils.join(target, "|"));
        getRepository().deleteAll(target);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void deleteAll() {
        log.info("Deleting all target entities: {}");
        getRepository().deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(final ID id) {
        return find(id).isPresent();
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    protected abstract BaseRepository<E, ID> getRepository();
}
