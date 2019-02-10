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

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.BaseRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.repository.CategoryRepository;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.CategoryService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Custom category service implementation
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Service(CategoryService.SERVICE_ID)
public class CategoryServiceImpl extends BaseServiceImpl<Category, String> implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    protected BaseRepository<Category, String> getRepository() {
        return this.categoryRepository;
    }
}
