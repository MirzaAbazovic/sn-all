/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 12:00:58
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GewofagWohnung;


/**
 * DAO Interface fuer die Verwaltung von Gewofag Daten.
 */
public interface GewofagDao extends FindDAO, ByExampleDAO, StoreDAO {

    /**
     * Findet eine Gewofag Wohnung zu einem Port
     *
     * @param equipment Der Port, der der Wohnung zugeordnet ist
     * @return Eine GewofagWohnung, oder {@code null}
     */
    GewofagWohnung findGewofagWohnung(Equipment equipment);

    /**
     * Sucht alle Gewofag Wohnungen in einer bestimmten GeoID
     *
     * @param geoId GeoId, an der sich die Wohnungen befinden
     * @return Liste von Gewofag Wohnungen, nie {@code null}
     */
    List<GewofagWohnung> findGewofagWohnungenByGeoId(GeoId geoId);
}
