/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 13:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.Report2ProdStati;

public interface Report2ProdStatiDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion löscht alle Zuordnungen für einen bestimmten Report
     *
     * @param id des report2Prod-Objekts
     *
     */
    public void deleteReport2ProdStati(Long report2ProdId);

    /**
     * Funktion findet einen Datensatz anhand der Report2Produkt-Id und des Status-Id
     *
     * @param rep2ProdId Report2Produkt-ID
     * @param statusId   Id des Auftrag-Status
     * @return Datensatz falls vorhanden, sonst null
     *
     */
    public Report2ProdStati findReport2ProdStatiByIds(Long rep2ProdId, Long statusId);

}
