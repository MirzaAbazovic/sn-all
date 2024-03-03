/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2011 11:08:19
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDeleteDAO;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.AvailabilityDAO;
import de.augustakom.hurrican.exceptions.AvailabilityException;
import de.augustakom.hurrican.model.builder.cdm.location.v1.SearchBuildingsBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.location.LocationNotificationHelper;
import de.augustakom.hurrican.service.location.LocationServiceDispatcher;
import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyUpdateLocation;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildingsResponse;

/**
 * Implementiert Hilfmethoden für GeoId Entitäten.
 */
@CcTxRequired
public class AvailabilityServiceHelperImpl extends DefaultCCService implements AvailabilityServiceHelper {

    private static final Logger LOGGER = Logger.getLogger(AvailabilityServiceHelperImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Resource(name = "defaultDeleteDAO")
    private Hibernate4DefaultDeleteDAO defaultDeleteDAO;

    @Resource(name = "de.augustakom.hurrican.service.location.LocationServiceDispatcher")
    private LocationServiceDispatcher locationServiceDispatcher;

    @Autowired
    private LocationNotificationHelper locationNotificationHelper;

    @Override
    public <T> T findById(Serializable id, Class<T> type) {
        return ((AvailabilityDAO) getDAO()).findById(id, type);
    }

    @Override
    public void deleteById(Serializable id, Class<?> type) {
        defaultDeleteDAO.deleteById(id, type);
    }

    @Override
    public <T extends Serializable> T store(T toStore) {
        return ((StoreDAO) getDAO()).store(toStore);
    }

    @Override
    public GeoId findGeoId(Long geoId) throws AvailabilityException {
        return findOrCreateGeoId(geoId, null);
    }

    @Override
    @CcTxRequiresNew
    public GeoId findOrCreateGeoIdNewTx(Long geoId, Long sessionId) {
        return findOrCreateGeoId(geoId, sessionId);
    }

    @Override
    public GeoId findOrCreateGeoId(Long geoId, Long sessionId) {
        if (geoId == null) {
            return null;
        }

        GeoId existingGeoId = findById(geoId, GeoId.class);
        if (existingGeoId != null) {
            return existingGeoId;
        }

        GeoId createdGeoId = readAndCacheGeoId(geoId, sessionId);
        return createdGeoId;
    }


    /* Liest die GeoId ueber den ESB und speichert sie im Hurrican GeoId Cache. */
    GeoId readAndCacheGeoId(Long geoId, Long sessionId) {
        try {
            LOGGER.info(String.format("Sending location search request for GeoId: %s", geoId));

            SearchBuildings searchBuildings = new SearchBuildingsBuilder()
                    .withBuildingId(geoId)
                    .build();

            SearchBuildingsResponse searchResponse = locationServiceDispatcher.searchLocations(searchBuildings);

            if (searchResponse != null && CollectionUtils.isNotEmpty(searchResponse.getBuilding())) {
                NotifyUpdateLocation updateLocation = new NotifyUpdateLocation();
                // da "search by Id" --> max. 1 Entitaet im Result!
                updateLocation.setBuilding(searchResponse.getBuilding().get(0));

                Pair<GeoIdLocation, AKWarnings> result =
                        locationNotificationHelper.updateLocation(updateLocation, true, sessionId);
                return (result != null) ? (GeoId) result.getFirst() : null;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new AvailabilityException(String.format("Fehler beim Erstellen des GeoID Cache Eintrags: %s", e.getMessage()), e);
        }
    }


    @Override
    public List<GeoId> findExact(String street, String houseNum, String houseNumExt, String zipCode, String city,
            String district) {

        SearchBuildingsBuilder searchBuildings = new SearchBuildingsBuilder()
                .withZipCode(zipCode)
                .withCity(city)
                .withDistrict(district)
                .withStreetSection(street)
                .withBuilding(houseNum, houseNumExt);

        List<GeoId> geoIds = new ArrayList<>();
        try {
            SearchBuildingsResponse searchResponse = locationServiceDispatcher.searchLocations(searchBuildings.build());

            if (searchResponse != null && CollectionUtils.isNotEmpty(searchResponse.getBuilding())) {
                for (Building building : searchResponse.getBuilding()) {
                    AvailabilityServiceHelper txProxy = getCCService(AvailabilityServiceHelper.class);
                    geoIds.add(txProxy.findOrCreateGeoIdNewTx(building.getId(), null));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new AvailabilityException(String.format(
                    "Fehler bei der Suche nach den GeoIDs zu folgenden Parametern: %s%n%s",
                    searchBuildings.toString(), e.getMessage()), e);
        }
        return geoIds;
    }

    @Override
    public List<GeoId2TechLocation> findGeoId2TechLocations(Long geoId) throws FindException {
        if (geoId == null) { return Collections.emptyList(); }
        try {
            GeoId2TechLocation example = new GeoId2TechLocation();
            example.setGeoId(geoId);

            return ((ByExampleDAO) getDAO()).queryByExample(example, GeoId2TechLocation.class);
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<GeoId2TechLocationView> findGeoId2TechLocationViews(Long geoId) throws FindException {
        if (geoId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return ((AvailabilityDAO) getDAO()).findGeoId2TechLocationViews(geoId);
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public GeoId2TechLocation findGeoId2TechLocation(Long geoId, Long hvtIdStandort) throws FindException {
        if (geoId == null) { return null; }
        try {
            GeoId2TechLocation example = new GeoId2TechLocation();
            example.setGeoId(geoId);
            example.setHvtIdStandort(hvtIdStandort);

            List<GeoId2TechLocation> result = ((ByExampleDAO) getDAO()).queryByExample(example, GeoId2TechLocation.class);
            if ((result != null) && (result.size() == 1)) {
                return result.get(0);
            }
            return null;
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public GeoId2TechLocation saveGeoId2TechLocation(GeoId2TechLocation toSave, Long sessionId)
            throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            final String user = (sessionId == null) ? "unknown" : getUserNameAndFirstNameSilent(sessionId);
            toSave.setUserW(user);
            return ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteGeoIdClarification4GeoId(Long geoId, List<GeoIdClarification.Status> states) {
        List<GeoIdClarification> clarifications = ((AvailabilityDAO) getDAO()).findGeoIdClarificationByGeoId(geoId);
        List<Long> statusIds = new ArrayList<Long>();
        if (states != null) {
            for (GeoIdClarification.Status status : states) {
                statusIds.add(status.getRefId());
            }
        }
        for (GeoIdClarification clarification : clarifications) {
            if (CollectionUtils.isEmpty(statusIds) || statusIds.contains(clarification.getStatus().getId())) {
                defaultDeleteDAO.delete(clarification);
            }
        }
    }

    @Override
    public GeoIdClarification createGeoIdClarification(Long sessionId, long geoId, GeoIdClarification.Type type,
            String info) throws FindException, StoreException {
        Reference statusOpen = referenceService.findReference(GeoIdClarification.Status.OPEN.getRefId());
        Reference referenceType = referenceService.findReference(type.getRefId());
        GeoIdClarification toSave = new GeoIdClarification();
        toSave.setGeoId(Long.valueOf(geoId));
        toSave.setStatus(statusOpen);
        toSave.setType(referenceType);
        toSave.setInfo(info);
        return saveGeoIdClarification(toSave, sessionId);
    }

    @Override
    public GeoIdClarification saveGeoIdClarification(GeoIdClarification toSave, Long sessionId) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            toSave.setUserW(getUserNameAndFirstNameSilent(sessionId));
            return ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

}
