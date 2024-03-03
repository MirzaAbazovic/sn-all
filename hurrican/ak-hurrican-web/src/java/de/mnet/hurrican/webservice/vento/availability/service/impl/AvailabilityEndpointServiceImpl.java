/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.13
 */
package de.mnet.hurrican.webservice.vento.availability.service.impl;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.location.LocationNotificationHelper;
import de.mnet.hurrican.webservice.vento.availability.service.AvailabilityEndpointService;

public class AvailabilityEndpointServiceImpl implements AvailabilityEndpointService {

    private static final Logger LOGGER = Logger.getLogger(AvailabilityEndpointServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityServiceHelper")
    private AvailabilityServiceHelper availabilityServiceHelper;

    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;

    @Autowired
    private LocationNotificationHelper locationNotificationHelper;

    @Override
    public VentoGetAvailabilityInformationResponse getAvailabilityInformation(
            VentoGetAvailabilityInformationRequest request) throws FindException {
        try {
            syncGeoId2TechLocation(request.getGeoId());
        }
        catch (Exception e) {
            LOGGER.error("Fehler beim Sync der Standortzuordnung zur GeoID = " + request.getGeoId(), e);
        }
        return availabilityService.getAvailabilityInformation(request);
    }

    void syncGeoId2TechLocation(Long geoId) throws FindException,
            StoreException {
        GeoId building = availabilityServiceHelper.findOrCreateGeoId(geoId, null);
        if (building != null && StringUtils.isNotBlank(building.getOnkz()) && StringUtils.isNotBlank(building.getAsb())) {
            locationNotificationHelper.processExistingGeoId(building, -1L, null);
        }
    }
}
