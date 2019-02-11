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
package com.wildbeeslabs.sensiblemetrics.supersolr.repository;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.BaseModel;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Query;

import java.io.Serializable;
import java.util.Optional;

/**
 * Custom base model repository
 *
 * @param <E>
 * @param <ID>
 */
@NoRepositoryBean
public interface BaseModelRepository<E extends BaseModel<ID>, ID extends Serializable> extends BaseRepository<E, ID> {

    //@Query("id:*?0*")
    //Page<? extends E> findByCustomQuery(final String searchTerm, final Pageable pageable);

    @Query(name = "BaseModel.findById")
    Optional<E> findById(@Boost(2) final ID id);

    long count(final String searchTerm);
}
