/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2004 13:00:52
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.hurrican.model.cc.UEVT2Ziel;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>UEVT2Ziel</code>.
 *
 *
 */
public interface UEVT2ZielDAO extends ByExampleDAO {

    /**
     * Loescht alle UEVT-Ziele, die einem best. UEVT zugeordnet sind.
     *
     * @param uevtId
     */
    public void deleteByUevtId(Long uevtId);

    /**
     * Speichert alle Objekte aus der Liste.
     *
     * @param toStore
     */
    public void store(List<UEVT2Ziel> toStore);

}


