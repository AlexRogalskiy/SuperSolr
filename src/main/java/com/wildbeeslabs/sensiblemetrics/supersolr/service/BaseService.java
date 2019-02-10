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
package com.wildbeeslabs.sensiblemetrics.supersolr.service;

import java.io.Serializable;
import java.util.Optional;

/**
 * Custom base service declaration
 *
 * @param <E>
 * @param <ID>
 */
public interface BaseService<E, ID extends Serializable> {

    Iterable<E> findAll();

    Optional<E> find(final ID id);

    void save(final E target);

    void saveOrUpdate(final E target, final Class<E> clazz);

    void save(final Iterable<E> target);

    void delete(final E target);
}