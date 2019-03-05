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
package com.wildbeeslabs.sensiblemetrics.supersolr.batch;

import com.wildbeeslabs.sensiblemetrics.supersolr.config.properties.BatchConfigProperties;
import com.wildbeeslabs.sensiblemetrics.supersolr.exception.SolrItemWriterException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.ContentStreamBase;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Custom base resource writer {@link ItemWriter}
 */
@Slf4j
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BaseResourceWriter implements ItemWriter<BaseResource> {

    /**
     * Default file identifier
     */
    private static final String FILE_ID_LITERAL = "literal.file.id";
    /**
     * Default solr client instance {@link SolrClient}
     */
    private SolrClient solrClient;
    /**
     * Default {@link BatchConfigProperties} properties
     */
    private BatchConfigProperties batchConfigProperties;

    @Override
    public void write(final List<? extends BaseResource> list) {
        list.stream().map(this::updateRequest).forEach(this::request);
    }

    private ContentStreamUpdateRequest updateRequest(final BaseResource resource) {
        try {
            final ContentStreamUpdateRequest updateRequest = new ContentStreamUpdateRequest(getBatchConfigProperties().getExtractPath());
            updateRequest.addContentStream(new ContentStreamBase.StringStream(resource.getData(), "text/html;charset=UTF-8"));
            updateRequest.setParam(FILE_ID_LITERAL, resource.getResource().getFile().getAbsolutePath());
            updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
            return updateRequest;
        } catch (IOException ex) {
            throw new SolrItemWriterException("Could not retrieve filename", ex);
        }
    }

    private void request(final ContentStreamUpdateRequest updateRequest) {
        try {
            getSolrClient().request(updateRequest, BaseOptimizeTasklet.DEFAULT_COLLECTION_NAME);
            log.info("Updated document in Solr: {}", updateRequest.getParams().get(FILE_ID_LITERAL));
        } catch (SolrServerException | IOException ex) {
            throw new SolrItemWriterException("Could not index document", ex);
        }
    }
}
