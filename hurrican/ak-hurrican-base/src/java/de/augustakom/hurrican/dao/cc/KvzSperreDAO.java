/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.07.2012 09:00:40
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.KvzSperre;

/**
 *
 */
public interface KvzSperreDAO extends StoreDAO, FindDAO, ByExampleDAO, DeleteDAO {

     KvzSperre find(String onkz, Integer asb, String kvzNummer);
}


