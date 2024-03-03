/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.augustakom.hurrican.model.builder.cdm.location.v1;

import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.augustakom.hurrican.model.builder.cdm.location.LocationServiceTypeBuilder;

/**
 *
 */
public abstract class SearchRequestBuilder<T extends SearchRequest> implements LocationServiceTypeBuilder<T> {

    private Long limit;
    private SearchStrategy strategy;

    protected T enrich(T request) {
        if (limit != null) {
            request.setLimit(limit);
        }

        if (strategy != null) {
            request.setStrategy(strategy.name());
        }

        return request;
    }

    public SearchRequestBuilder<T> withLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    public SearchRequestBuilder<T> withStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
}
