/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 11:43:13
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs {@link AuftragWholesale}.
 */
public interface AuftragWholesaleDAO extends FindDAO, StoreDAO {

    /**
     * Ermittelt zu einem Auftrag den zugeh&ouml;rigen WholesaleAuftrag.
     *
     * @param auftragId Auftrags-ID, zu der Wholesale Auftr&auml;ge ermittelt werden sollen.
     * @return AuftragWholesale oder <code>null</code>, falls keinen WholesaleAuftrag.
     * @throws FindException wenn mehr als ein Auftrag ermittelt wird.
     */
    AuftragWholesale findByAuftragId(Long auftragId) throws FindException;
}


