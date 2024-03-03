package de.augustakom.hurrican.service.location;

import org.apache.camel.ExchangeTimedOutException;

import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;

/**
 * LocationServiceDispatcher
 */
public interface LocationServiceDispatcher {

    <T extends SearchRequest, R extends SearchResponse> R searchLocations(T request) throws ExchangeTimedOutException;
}
