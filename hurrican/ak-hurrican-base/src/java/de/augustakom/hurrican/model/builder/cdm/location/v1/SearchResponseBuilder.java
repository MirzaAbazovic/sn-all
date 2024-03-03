/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.augustakom.hurrican.model.builder.cdm.location.v1;

import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;
import de.augustakom.hurrican.model.builder.cdm.location.LocationServiceTypeBuilder;

/**
 *
 */
public abstract class SearchResponseBuilder<T extends SearchResponse> implements LocationServiceTypeBuilder<T> {

    private Long limit;

    protected T enrich(T response) {
        if (limit != null) {
            response.setLimit(limit);
        }

        return response;
    }

    public SearchResponseBuilder<T> withLimit(Long limit) {
        this.limit = limit;
        return this;
    }
}
