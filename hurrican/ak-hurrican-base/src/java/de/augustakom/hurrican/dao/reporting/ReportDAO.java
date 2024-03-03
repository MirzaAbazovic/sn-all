/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.Report;

public interface ReportDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion liefert alle Reports, die einem Produkt in einem best. Status zugeordnet sind.
     *
     * @param type     ReportTyp (Kunden- oder Auftragsreport)
     * @param prodId   Produkt-Id, falls ein Auftragsreport gesucht wird
     * @param statusId Status-Id, falls ein Auftragsreport gesucht wird
     * @return Liste mit verfuegbaren Reports
     *
     */
    public List<Report> findReportsByProdStati(Long type, Long prodId, Long statusId);

    /**
     * Liefert alle Reports, die nicht diabled sind.
     *
     * @return Liste mit aktiven Reports
     */
    public List<Report> findActiveReports();
}
