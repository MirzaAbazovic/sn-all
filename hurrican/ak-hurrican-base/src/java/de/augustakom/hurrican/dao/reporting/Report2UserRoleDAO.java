/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2007 13:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;

public interface Report2UserRoleDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion loescht alle Zuordnungen für einen bestimmten Report
     *
     * @param reportId Id des Reports
     *
     */
    public void deleteRoles4Report(Long reportId);


}
