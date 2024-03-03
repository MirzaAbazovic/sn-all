/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:32:45
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.query.GeoIdClarificationQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.model.cc.view.GeoIdClarificationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * DAO Definition fuer die Verwaltung von Verfuegbarkeitsdaten.
 */
public interface AvailabilityDAO extends FindDAO, StoreDAO, ByExampleDAO {

    /**
     * Ermittelt alle {@link de.augustakom.hurrican.model.cc.GeoId} Objekte, die den Query-Parametern entsprechen.
     *
     * @param query
     * @return
     */
    public List<GeoId> findByQuery(GeoIdQuery query);

    /**
     * Ermittelt alle {@link de.augustakom.hurrican.model.cc.GeoId} Objekte, die den Query-Parametern entsprechen. Wenn
     * ONKZ und ASB gesetzt sind, werden diese Parameter in die WHERE Klausel eingebaut - ONKZ oder ASB alleine ist
     * nicht eindeutig.
     *
     * @param query
     * @return
     */
    public List<GeoId> findBySearchQuery(GeoIdSearchQuery query);

    /**
     * Ermittelt alle Einträge aus der Geo ID Klärungsliste, die mit der Liste der {@code statusIds} übereinstimmen.
     *
     * @param statusIds
     * @param geoIds    wenn vorhanden und nicht leer, schränkt die Ergebnismenge auf die enthaltenen GeoIds ein
     * @return die Liste der Klärungen
     */
    public List<GeoIdClarification> findGeoIdClarificationsByStatusId(List<Long> statusIds, List<Long> geoIds);

    /**
     * Ermittelt alle Einträge aus der Geo ID Klärungsliste, die mit der Liste der {@code statusIds} übereinstimmen.
     * Fuegt jeweils die Adressedaten der Geo ID hinzu.
     *
     * @return die Liste der Klärungen
     */
    public List<GeoIdClarificationView> findGeoIdClarificationViewsByStatusId(List<Long> statusIds);

    /**
     * Ermittelt alle Einträge aus der Geo ID Klärungsliste, die mit den Parametern in der Query übereinstimmen.
     *
     * @return die Liste der Klärungen
     */
    public List<GeoIdClarificationView> findGeoIdClarificationViewsByQuery(GeoIdClarificationQuery query);

    /**
     * Ermittelt alle moeglichen {@link GeoId2TechLocation} Eintraege fuer die Kombination aus {@code geoId} und {@code
     * prodId}. Die Ermittlung erfolgt dabei sortiert nach der Prioritaet Produkt / Standorttyp!
     *
     * @param geoId
     * @param prodId
     * @return
     */
    public List<GeoId2TechLocation> findPossibleGeoId2TechLocations(GeoId geoId, Long prodId);

    /**
     * Ermittelt alle View-Eintraege zu einer bestimmten Geo-ID.
     *
     * @param geoId Geo-ID, deren Views zu technischen Standorten ermittelt werden sollen.
     * @return Liste mit den Mappings zu technischen Standorten (Objekte vom Typ {@link GeoId2TechLocationView}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<GeoId2TechLocationView> findGeoId2TechLocationViews(Long geoId);


    /**
     * Ermittelt alle Klärfälle zu einer GeoId
     *
     * @param geoId Geo-ID
     * @return Liste von GeoIdClarifications
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<GeoIdClarification> findGeoIdClarificationByGeoId(Long geoId);

}
