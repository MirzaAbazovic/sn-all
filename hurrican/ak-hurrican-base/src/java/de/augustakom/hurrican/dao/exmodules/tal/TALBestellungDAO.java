/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 09:46:56
 */
package de.augustakom.hurrican.dao.exmodules.tal;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.exmodules.tal.TALBestellung;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.model.exmodules.tal.TALVorfall;


/**
 * DAO-Interface fuer die Verwaltung von TAL-Bestellungen.
 *
 *
 */
public interface TALBestellungDAO extends StoreDAO, FindDAO, ByExampleDAO, FindAllDAO {

    /**
     * Ermittelt alle TALBestellungen mit der angegebenen 'First-ID'. <br> Die Datensaetze werden aufsteigend sortiert
     * (aeltester Datensatz zuerst).
     *
     * @param tbsFirstId
     * @return Liste mit Objekten des Typs <code>TALBestellung</code>
     *
     */
    public List<TALBestellung> findTALBestellungenByFirstId(Long tbsFirstId);

    /**
     * Ermittelt Objekte vom Typ <code>TALVorfall</code> anhand der angegebenen IDs.
     *
     * @param ids IDs, deren Objekte geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>TALVorfall</code>.
     *
     */
    public List<TALVorfall> findTALVorfaelleByIds(List<Long> ids);

    /**
     * Ermittelt ein bestimmtes TALSegment
     *
     * @param talIdentifier
     * @param tbsId
     * @return TALSegment
     */
    public List<TALSegment> findTALSegment(String talIdentifier, Number tbsId);
}


