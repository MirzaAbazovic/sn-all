/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2005 08:59:29
 */
package de.augustakom.hurrican.dao.cc;

import java.io.*;
import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;

/**
 * DAO-Interface fuer Objekte des Typs <code>Port_gesamt</code>.
 *
 *
 */
public interface PortGesamtDAO extends StoreDAO, ByExampleDAO {

    /**
     * Leert die Tabelle T_PORT_GESAMT
     */
    public void deletePortGesamt();

    /**
     * Ermittelt das letzte Einspieldatum der EWSD-Daten
     *
     * @return Date
     */
    public Date selectPortGesamtDate();

    /**
     * Estellt die Ansicht der freizugebenden Rangierungen anhand zu beachtender Parameter. Das Resultset ist nach
     * RangierID, AuftragID sortiert (Aufträge sind Untergruppe der RangierID).
     *
     * @param freigabeDatum   zu beruecksichtigendes Freigabedatum
     * @param onlyKlaerfaelle ausschließlich Klärfälle zurückliefern
     * @return List Liste mit Objekten des Typs <code>PhysikFreigebenView</code>
     */
    public List<PhysikFreigebenView> createPhysikFreigabeView(Date freigabeDatum, Boolean onlyKlaerfaelle);

    /**
     * Loescht eine Rangierung KlärfallInfo (bzw. einen Datensatz) ueber dessen ID.
     */
    public void deleteRangierungFreigabeInfoById(Serializable id);

}
