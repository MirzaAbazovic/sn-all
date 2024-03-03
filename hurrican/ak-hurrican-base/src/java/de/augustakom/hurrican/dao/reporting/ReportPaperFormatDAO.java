/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2007 09:07:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;

public interface ReportPaperFormatDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion ermittelt den Namen des Papierfaches für best. Seite eines Reports
     *
     * @param printerName Name des Druckers
     * @param paperId     Id des Papierformats
     * @return Name des Papierfachs beim gesuchten Drucker
     *
     */
    public String findTrayName4Report(String printerName, Long paperId);
}
