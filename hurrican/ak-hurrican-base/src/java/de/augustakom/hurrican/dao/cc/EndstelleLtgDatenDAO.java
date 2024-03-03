/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2004 09:52:14
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;


/**
 * DAO-Interface fuer Objekte des Typs <code>EndstelleLtgDaten</code>.
 *
 *
 */
public interface EndstelleLtgDatenDAO extends StoreDAO, HistoryUpdateDAO<EndstelleLtgDaten>, FindDAO {

    /**
     * Sucht nach den aktuellen Leitungsdaten zu einer Endstelle.
     *
     * @param esId ID der Endstelle, deren Leitungsdaten gesucht werden.
     * @return EndstelleLtgDaten oder <code>null</code>.
     */
    public EndstelleLtgDaten findByEsId(Long esId);

}


