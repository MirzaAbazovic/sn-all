/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2004 16:55:11
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;


/**
 * DAO-Interface fuer Objekte des Typs <code>Endstelle</code>.
 *
 *
 */
public interface EndstelleDAO extends FindDAO, StoreDAO, ByExampleDAO, HistoryUpdateDAO<EndstelleAnsprechpartner> {

    /**
     * Erzeugt eine neue Mapping-ID fuer die Verbindung zwischen Objekten des Typs <code>AuftragTechnik</code> und
     * <code>Endstelle</code>.
     *
     * @return
     */
    public Long createNewMappingId();

}


