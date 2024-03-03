/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2010 15:48:35
 */

package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;


/**
 * DAO Definition fuer {@link HVTStandortTechType} Objekte.
 *
 *
 */
public interface HVTStandortTechTypeDAO extends FindDAO, ByExampleDAO, StoreDAO, DeleteDAO {

}
