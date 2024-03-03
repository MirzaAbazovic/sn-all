/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 16:02:19
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>VPN</code>.
 *
 *
 */
public interface VpnDAO extends FindAllDAO, FindDAO, StoreDAO, HistoryUpdateDAO<VPNKonfiguration>, ByExampleDAO {

    /**
     * Sucht nach den VPN-Daten, die einem best. Auftrag zugeordnet sind.
     *
     * @param ccAuftragId ID des CC-Auftrags.
     * @return VPN oder <code>null</code>
     */
    public VPN findVPNByAuftragId(Long ccAuftragId);

    /**
     * Sucht nach der aktuellen(!) VPN-Konfiguration fuer einen best. Auftrag.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen VPN-Konfiguration gesucht wird.
     * @return VPNKonfiguration oder <code>null</code>
     */
    public VPNKonfiguration findVPNKonfiguration4Auftrag(Long ccAuftragId);

}


