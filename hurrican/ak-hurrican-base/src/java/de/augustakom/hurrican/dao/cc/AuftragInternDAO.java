/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 08:22:20
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AuftragIntern;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>AuftragIntern</code>.
 *
 *
 */
public interface AuftragInternDAO extends ByExampleDAO, StoreDAO, HistoryUpdateDAO<AuftragIntern> {

    /**
     * Sucht nach einem AuftragIntern-Datensatz zu der Auftrags-ID <code>auftragId</code>. Zusaetzlich muss das
     * Gueltig-von und Gueltig-bis Datum aktuell sein.
     *
     * @param auftragId die Auftrags-ID
     * @return Instanz von AuftragIntern.
     */
    public AuftragIntern findByAuftragId(Long auftragId);

}


