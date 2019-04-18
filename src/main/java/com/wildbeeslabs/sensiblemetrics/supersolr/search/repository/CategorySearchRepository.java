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
package com.wildbeeslabs.sensiblemetrics.supersolr.search.repository;

import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.entity.Category;
import com.wildbeeslabs.sensiblemetrics.supersolr.search.document.interfaces.SearchableCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Custom category search repository declaration {@link BaseDocumentSearchRepository}
 */
@Repository
@RepositoryRestResource(collectionResourceRel = "category-search-repo", itemResourceDescription = @Description(value = "search operations on category documents"))
public interface CategorySearchRepository extends BaseDocumentSearchRepository<Category, String> {

    @RestResource(rel = "by-title-in-collection", description = @Description(value = "find categories by collection of titles"))
    @Highlight(prefix = "<highlight>", postfix = "</highlight>")
    @Query(fields = {
        SearchableCategory.ID_FIELD_NAME,
        SearchableCategory.TITLE_FIELD_NAME,
        SearchableCategory.DESCRIPTION_FIELD_NAME
    }, defaultOperator = org.springframework.data.solr.core.query.Query.Operator.AND)
    HighlightPage<? extends Category> findByTitleIn(final Collection<String> titles, final Pageable page);

    @RestResource(rel = "fetch-by-title-like", description = @Description(value = "find categories by title like"))
    @Query(name = "Category.findByTitleLike")
    Page<? extends Category> findByTitleLike(@Boost(1.5f) final String title, final Pageable page);

    @RestResource(rel = "fetch-by-title", description = @Description(value = "find categories by title"))
    Page<? extends Category> findByTitle(@Boost(2) final String title, final Pageable page);

    @RestResource(rel = "fetch-by-all", description = @Description(value = "find all categories"))
    @Query(name = "Category.findAll")
    Page<? extends Category> findAllCategories(final Pageable pageable);

    @RestResource(rel = "fetch-by-title-and-starting-with", description = @Description(value = "find categories by title starting with"))
    @Query(name = "Category.findByTitleStartingWith")
    @Facet(fields = {SearchableCategory.TITLE_FIELD_NAME})
    FacetPage<? extends Category> findByTitleStartingWith(final String searchTerm, final Pageable page);

    @RestResource(rel = "fetch-by-description", description = @Description(value = "find categories by description"))
    @Query(name = "Category.findByDescription")
    Page<? extends Category> findByDescription(final String description, final Pageable page);

    @RestResource(rel = "fetch-by-named-text", description = @Description(value = "find categories by named text"))
    @Query(name = "Category.findByText")
    Page<? extends Category> findByNamedQuery(@Boost(2) final String searchTerm, final Pageable page);

    @RestResource(rel = "fetch-by-title-like-in-collection", description = @Description(value = "find categories like in collection of titles"))
    List<? extends Category> findByTitleLike(final Collection<String> titles);

    @RestResource(rel = "fetch-by-top10-title-or-description", description = @Description(value = "find top ten categories by title or description"))
    Page<? extends Category> findTop10ByTitleOrDescription(final @Boost(2) String title, final String description, final Pageable page);
}
