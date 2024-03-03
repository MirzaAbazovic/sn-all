/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 13:30:39
 */
package de.augustakom.hurrican.dao.cc.fttx;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;

public interface A10NspPortDao extends FindDAO, StoreDAO, ByExampleDAO {
    void delete(A10NspPort port);

    void delete(A10Nsp a10Nsp);
}


