/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2007 08:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.ReportTemplate;

public interface ReportTemplateDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion liefert die aktuelle Report-Vorlage fuer einen bestimmten Report.
     *
     * @param reportId Id des Reports zu dem die Vorlage geliefert werden soll.
     * @return ReportTemplate
     *
     */
    public ReportTemplate findTemplate4Report(Long reportId);

}
