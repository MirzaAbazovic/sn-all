/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 08:51:32
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AuftragVoIP;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>AuftragVoIP</code>.
 *
 *
 */
public interface AuftragVoIPDAO extends FindDAO, StoreDAO, HistoryUpdateDAO<AuftragVoIP> {

    /**
     * Ermittelt die VoIP-Daten zu einem bestimmten Auftrag.
     *
     * @param auftragId ID des Auftrags, dessen VoIP-Daten ermittelt werden sollen.
     * @return Objekt vom Typ <code>AuftragVoIP</code> oder <code>null</code>.
     *
     */
    public AuftragVoIP findAuftragVoIP(Long auftragId);

}


