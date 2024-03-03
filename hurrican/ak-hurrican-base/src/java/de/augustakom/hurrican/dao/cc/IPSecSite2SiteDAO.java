/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 10:56:18
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;


/**
 * DAO Interface fuer die Verwaltung von IPSec Site-to-Site Objekten.
 *
 *
 */
public interface IPSecSite2SiteDAO extends FindDAO, StoreDAO {

    /**
     * Ermittelt das IPSecSite2Site Objekt zu dem angegebenen Auftrag.
     *
     * @param auftragId
     * @return
     */
    public IPSecSite2Site findByAuftragId(Long auftragId);

}


