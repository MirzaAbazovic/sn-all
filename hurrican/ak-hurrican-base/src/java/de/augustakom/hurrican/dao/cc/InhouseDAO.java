/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2004 08:21:48
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.Inhouse;


/**
 * DAO-Interface fuer Objekte des Typs <code>Inhouse</code>.
 *
 *
 */
public interface InhouseDAO extends StoreDAO, ByExampleDAO, HistoryUpdateDAO<Inhouse> {

    /**
     * Sucht nach einem aktuellen(!) Inhouse-Objekt, das einer best. Endstelle zugeordnet ist.
     *
     * @param esId ID der Endstelle
     * @return Inhouse oder <code>null</code>
     */
    public Inhouse findByEsId(Long esId);
}


