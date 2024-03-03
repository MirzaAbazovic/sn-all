/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2004 09:09:59
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AK0800;


/**
 * DAO-Interface fuer Objekte des Typs <code>AK0800</code> und <code>AK0800Modell</code>.
 *
 *
 */
public interface AK0800DAO extends FindAllDAO, StoreDAO, HistoryUpdateDAO<AK0800> {

    /**
     * Sucht nach allen aktuellen IN-Diensten, die einem bestimmten Auftrag zugeordnet sind.
     *
     * @param auftragId ID des CC-Auftrags dessen IN-Dienste gesucht werden.
     * @return Liste mit Objekten des Typs <code>AK0800</code>.
     */
    public List<AK0800> findByAuftragId(Long auftragId);

}


