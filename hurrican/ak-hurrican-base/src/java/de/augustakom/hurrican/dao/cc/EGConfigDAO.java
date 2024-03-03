/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 14:02:45
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.EGConfig;


/**
 * DAO-Interface fuer die Verwaltung von IP-Endgeraeten.
 *
 *
 */
public interface EGConfigDAO extends StoreDAO, FindDAO {

    /**
     * Ermittelt die aktuelle Endgeraete-Konfiguration zu dem Endgeraete-2-Auftrag Mapping mit der ID
     * <code>eg2AuftragId</code>.
     *
     * @param eg2AuftragId
     * @return Objekt vom Typ <code>EGConfig</code>.
     *
     */
    public EGConfig findEGConfig(Long eg2AuftragId);

}


