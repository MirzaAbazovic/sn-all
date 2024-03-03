/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 11:40:09
 */
package de.augustakom.hurrican.dao.cc.fttx;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;

/**
 * DAO-Interface fuer die Verwaltung von S/C-Vlans.
 */
public interface VlanDao extends ByExampleDAO, FindDAO, StoreDAO {

    /**
     * Ermittelt alle gespeicherten EqVlans zu einem Equipment.
     */
    List<EqVlan> findEqVlans(Long equipmentId);

    /**
     * Ermittelt alle EqVlan zu einem Equipment, die zum angegebenen Datum gueltig sind.
     */
    List<EqVlan> findEqVlans(Long equipmentId, Date when);

    /**
     * Findet alle EqVlan zu einem Equipment, zum gegebenen Zeitpunkt oder später beginnen.
     */
    List<EqVlan> findEqVlansForFuture(Long equipmentId, Date when);

    /**
     * Löscht die übergebene Entity
     */
    void delete(EqVlan entity);

    /**
     * Findet alle EqVlans für alle Equipments der Baugruppe.
     */
    List<EqVlan> findEqVlansByBaugruppe(Long baugruppeId);

    List<?> findEqVlans4Auftrag(final Long auftragId);

}
