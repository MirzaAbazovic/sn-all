/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2010 14:14:12
 */

package de.augustakom.hurrican.dao.cc;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AuftragUMTS;

public interface AuftragUMTSDAO extends FindDAO, StoreDAO, HistoryUpdateDAO<AuftragUMTS> {

    public AuftragUMTS findAuftragUMTS(Long auftragId) throws IncorrectResultSizeDataAccessException;
}
