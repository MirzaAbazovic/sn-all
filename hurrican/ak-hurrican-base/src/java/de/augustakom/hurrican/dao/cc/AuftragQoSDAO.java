/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 13:54:57
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AuftragQoS;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>AuftragQoS</code>.
 *
 *
 */
public interface AuftragQoSDAO extends FindDAO, StoreDAO, ByExampleDAO, HistoryUpdateDAO<AuftragQoS> {

}


