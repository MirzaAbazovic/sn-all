package de.augustakom.hurrican.service.location.impl;

import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;

import org.apache.camel.ExchangeTimedOutException;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.augustakom.hurrican.service.location.LocationService;
import de.augustakom.hurrican.service.location.LocationServiceDispatcher;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;

/**
 * LocationServiceDispatcherImpl
 */
public class LocationServiceDispatcherImpl implements LocationServiceDispatcher {

    @Autowired
    private CamelProxyLookupService camelProxyLookupService;

    @Override
    public <T extends SearchRequest, R extends SearchResponse> R searchLocations(T request) throws ExchangeTimedOutException {
        LocationService locationService = camelProxyLookupService.lookupCamelProxy(PROXY_LOCATION_SERVICE,
                LocationService.class);
        return locationService.searchLocations(request);
    }
}
