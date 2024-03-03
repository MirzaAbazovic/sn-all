/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2011 11:08:19
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.exceptions.AvailabilityException;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

public interface AvailabilityServiceHelper extends ICCService {

    <T> T findById(Serializable id, Class<T> type);

    void deleteById(Serializable id, Class<?> type);

    <T extends Serializable> T store(T toStore);

    public GeoId findGeoId(Long geoId) throws AvailabilityException;

    public GeoId findOrCreateGeoId(Long geoId, Long sessionId) throws AvailabilityException;

    public GeoId findOrCreateGeoIdNewTx(Long geoId, Long sessionId) throws AvailabilityException;

    List<GeoId> findExact(String street, String houseNum, String houseNumExt, String zipCode, String city,
            String district);

    public List<GeoId2TechLocation> findGeoId2TechLocations(Long geoId) throws FindException;

    public List<GeoId2TechLocationView> findGeoId2TechLocationViews(Long geoId) throws FindException;

    public GeoId2TechLocation findGeoId2TechLocation(Long geoId, Long hvtIdStandort) throws FindException;

    public GeoId2TechLocation saveGeoId2TechLocation(GeoId2TechLocation toSave, Long sessionId)
            throws StoreException;

    /**
     * Löscht alle {@link GeoIdClarification} Einträge für eine GeoId.
     *
     * @param geoId
     * @param states wenn nicht leer, werden nur elemente mit den angegebenen Status gelöscht
     */
    void deleteGeoIdClarification4GeoId(Long geoId, List<GeoIdClarification.Status> states);

    /**
     * @param sessionId
     * @param geoId
     * @param type
     * @param info
     * @return
     * @throws FindException
     * @throws StoreException
     */
    GeoIdClarification createGeoIdClarification(Long sessionId, long geoId, GeoIdClarification.Type type,
            String info) throws FindException, StoreException;

    /**
     * Speichert die angegebene Klärung.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId aktuelle Session ID des Users
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     */
    public GeoIdClarification saveGeoIdClarification(GeoIdClarification toSave, Long sessionId) throws StoreException;
}
