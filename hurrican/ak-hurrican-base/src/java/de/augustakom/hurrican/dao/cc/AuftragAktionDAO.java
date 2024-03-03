/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 11:43:13
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragAktion;

/**
 *
 */
public interface AuftragAktionDAO extends FindDAO, StoreDAO {
    /**
     * Ermittelt zu einem Auftrag aktive (keine Cancel) offene (in der Zukunft liegende) Leistungsaenderungs-Aktionen.
     *
     * @param auftragId
     * @param type      des Aktion
     * @return Aktion oder null, falls keine offene Aenderung
     */
    AuftragAktion getActiveAktion(Long auftragId, AuftragAktion.AktionType type);
}


