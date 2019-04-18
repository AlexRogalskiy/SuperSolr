package com.wildbeeslabs.sensiblemetrics.supersolr.service.iface;

import org.springframework.data.domain.Example;

/**
 * Base query service declaration
 *
 * @param <E> type of document
 */
public interface BaseQueryService<E> {

    <S extends E> boolean exists(final Example<S> example);
}
