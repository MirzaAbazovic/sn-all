/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2007 10:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;

public interface Report2TxtBausteinGruppeDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {


    /**
     * Funktion loescht alle Zuordnungen von TxtBaustein-Gruppen zu einem Report
     *
     * @param reportId Id des Reports
     *
     */
    public void deleteAllTxtBausteinGruppen4Report(Long reportId);

}
