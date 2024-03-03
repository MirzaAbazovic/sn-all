/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;

import java.util.*;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import de.augustakom.hurrican.model.builder.cdm.location.v1.SearchBuildingsBuilder;
import de.augustakom.hurrican.model.builder.cdm.location.v1.SearchStrategy;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.augustakom.hurrican.service.location.LocationService;
import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildingsResponse;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.service.WbciLocationService;

/**
 * Service sends location search requests to Atlas ESB via Camel proxy.
 *
 *
 */
public class WbciLocationServiceImpl implements WbciLocationService {

    private static final Logger LOGGER = Logger.getLogger(WbciSendMessageServiceImpl.class);

    @Autowired
    private CamelProxyLookupService camelProxyLookupService;

    @Autowired
    private FeatureService featureService;

    @Override
    public List<Long> getLocationGeoIds(Standort standort) {
        try {
            LOGGER.info("Sending location search request for standort: " + standort);
            LocationService locationService = camelProxyLookupService.lookupCamelProxy(PROXY_LOCATION_SERVICE, LocationService.class);
            SearchBuildingsResponse searchResponse = locationService.searchLocations(getExactMatchBuildingQuery(standort));

            List<Long> geoIds = extractGeoIds(searchResponse);
            if (CollectionUtils.isEmpty(geoIds) && featureService.isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH)) {
                LOGGER.info(String.format("Sending phonetic location search request for standort: " + standort));
                searchResponse = locationService.searchLocations(getPhoneticBuildingQuery(standort));
                geoIds = extractGeoIds(searchResponse);
            }

            if (CollectionUtils.isEmpty(geoIds)) {
                LOGGER.info(String.format("Es wurden keine GeoIds für den Standort '%s' gefunden!", standort.toString()));
            }

            return geoIds;
        }
        catch (ExchangeTimedOutException e) {
            LOGGER.error(e);
            return new ArrayList<>();
        }
    }

    /**
     * Extracts geoIds from search building response.
     *
     * @param searchResponse
     * @return
     */
    private List<Long> extractGeoIds(SearchBuildingsResponse searchResponse) {
        List<Long> geoIds = new ArrayList<>();

        if (searchResponse != null) {
            for (Building building : searchResponse.getBuilding()) {
                if (building.getId() > 0) {
                    geoIds.add(building.getId());
                }
            }
        }

        return geoIds;
    }

    /**
     * Get exact match buildings query. Phonetic search is disabled and whole building properties are specified in order
     * to get exact results as much as possible.
     *
     * @param standort
     * @return
     */
    protected SearchBuildings getExactMatchBuildingQuery(Standort standort) {
        return new SearchBuildingsBuilder()
                .withCity(standort.getOrt())
                .withZipCode(standort.getPostleitzahl())
                .withStreetSection(standort.getStrasse().getStrassenname())
                .withBuilding(standort.getStrasse().getHausnummer(), standort.getStrasse().getHausnummernZusatz())
                .build();
    }

    /**
     * Get phonetic search buildings query. Just use city, street and building in order to get phonetic results.
     *
     * @param standort
     * @return
     */
    protected SearchBuildings getPhoneticBuildingQuery(Standort standort) {
        return new SearchBuildingsBuilder()
                .withCity(standort.getOrt())
                .withStreetSection(standort.getStrasse().getStrassenname())
                .withBuilding(standort.getStrasse().getHausnummer())
                .withStrategy(SearchStrategy.PHONETIC)
                .build();
    }

}
