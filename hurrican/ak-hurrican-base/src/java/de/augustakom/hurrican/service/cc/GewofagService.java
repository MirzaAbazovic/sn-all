/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 11:55:07
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GewofagWohnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 *
 *
 */
public interface GewofagService extends ICCService {

    /**
     * Findet eine Gewofag Wohnung per ID
     *
     * @param gewoWhgId Die ID der Wohnung
     * @return Eine GewofagWohnung, oder {@code null}
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    GewofagWohnung findGewofagWohnung(Long gewoWhgId) throws FindException;

    void saveGewofagWohnung(GewofagWohnung gewofagWohnung) throws StoreException;

    /**
     * Findet eine Gewofag Wohnung zu einem Port
     *
     * @param equipment Der Port, der der Wohnung zugeordnet ist
     * @return Eine GewofagWohnung, oder {@code null}
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    GewofagWohnung findGewofagWohnung(Equipment equipment) throws FindException;

    /**
     * Sucht alle Gewofag Wohnungen in einer bestimmten GeoID
     *
     * @param geoId GeoId, an der sich die gesuchten Wohnungen befinden
     * @return Liste von Gewofag Wohnungen, nie {@code null}
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    List<GewofagWohnung> findGewofagWohnungenByGeoId(GeoId geoId) throws FindException;

    /**
     * Ordnet der gegebenen Endstelle eine Physik zu
     *
     * @param endstelle      Die Endstelle, fuer die eine Physikzuordnung erfolgen soll
     * @param gewofagWohnung Die Gewofag Wohnung, die der Endstelle zugeordnet werden soll
     * @param sessionId
     * @throws StoreException Falls ein Fehler aufgetreten ist
     */
    void wohnungsZuordnung(Endstelle endstelle, GewofagWohnung gewofagWohnung, Long sessionId) throws StoreException;


    /**
     * Ersetzt eine GeoId durch eine Neue (Zusmmenfuehrung durch Vento ausgelöst)
     */
    public void replaceGeoIdsOfGewofag(GeoId geoId, GeoId replacedByGeoId) throws FindException;

}
