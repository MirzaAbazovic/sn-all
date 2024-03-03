/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.service.base.exceptions.ReportException;

public interface ReportDataDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion loescht alle Report-Data Datensaetze zu einem bestimmten Report-Request.
     *
     * @param requestId Id des Report-Requests, dessen ReportData-Datensatze geloescht werden sollen
     * @throws ReportException
     *
     */
    public void deleteData4Request(Long requestId);
}
