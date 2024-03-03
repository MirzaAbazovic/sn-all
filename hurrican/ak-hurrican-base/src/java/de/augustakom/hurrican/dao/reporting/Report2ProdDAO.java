/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.0..2007 13:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.Report2Prod;
import de.augustakom.hurrican.model.reporting.view.Report2ProdView;

public interface Report2ProdDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion loescht alle Zuordnungen für einen bestimmten Report
     *
     * @param reportId Id des Reports
     *
     */
    public void deleteProds4Report(Long reportId);

    /**
     * Liefert einen Eintrag anhand der Report- und Produkt-Id
     *
     * @param reportId  Id des Reports
     * @param produktId Produkt-Id
     * @return Report2Prod Objekt, falls gefunden sonst null
     *
     */
    public Report2Prod findReport2ProdByIds(Long reportId, Long produktId);

    /**
     * Liefert die Report-Produkt-Konfiguration eines Reports
     *
     * @param reportId Id des Reports
     * @return Liste mit Report2Prod-Views
     *
     */
    public List<Report2ProdView> findReport2ProdView4Report(Long reportId);

}
