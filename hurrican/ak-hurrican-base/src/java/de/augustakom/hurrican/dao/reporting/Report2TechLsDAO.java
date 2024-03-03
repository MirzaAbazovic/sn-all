/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2007 08:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.Report2TechLs;

public interface Report2TechLsDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion loescht alle Zuordnungen für einen bestimmten Report
     *
     * @param id des report2Prod-Objekts
     *
     */
    public void deleteReport2TechLs(Long report2ProdId);

    /**
     * Funktion findet eine Zuordnung anhand der Report2ProduktId und der Id der techn. Leistung
     *
     * @param rep2ProdId Report2Produkt-Id
     * @param techLsId   Id der techn. Leistung
     * @return Datensatz falls vorhanden, sonst null
     *
     */
    public Report2TechLs findReport2TechLsByIds(Long rep2ProdId, Long techLsId);

    /**
     * Liefert alle Ids der techn. Leistungen die einem Report bei einem best. Produkt zugeordnet sind
     *
     * @param reportId  Id des Reports
     * @param produktId Produkt-ID
     * @return Ids der techn. Leistungen
     *
     */
    public List<Long> findTechLsIds4ReportProdukt(Long reportId, Long produktId);
}
