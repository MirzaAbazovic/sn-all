/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 10:30:52
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.LoadDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Lock;


/**
 * DAO-Interface fuer Objekte des Typs <code>Lock</code>
 *
 *
 */
public interface LockDAO extends FindDAO, StoreDAO, LoadDAO, FindAllDAO, ByExampleDAO {

    /**
     * Sucht nach allen Lock-Eintraegen fuer einen best. Auftrag.
     *
     * @param auftragId  techn. Auftrag
     * @param onlyActive Wenn gesetzt, dann liefere nur active zurück
     * @return Liste mit Objekten des Typs <code>Lock</code> (sortiert nach ID und Erstellungsdatum)
     */
    public List<Lock> findByAuftragId(Long auftragId, boolean onlyActive);

}
