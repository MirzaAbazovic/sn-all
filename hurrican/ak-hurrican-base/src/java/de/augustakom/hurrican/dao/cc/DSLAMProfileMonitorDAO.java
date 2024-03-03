/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 12:22:57
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitor;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Schnittstelle fuer den Datenzugriff auf {@link DSLAMProfileMonitor}.
 *
 *
 * @since Release 11
 */
public interface DSLAMProfileMonitorDAO extends FindDAO, StoreDAO {

    /**
     * Ermittelt fuer eine angegebene Auftragsnummer die Daten fuer die Ueberwachung des zugehoerigen DSLAM Profils.
     *
     * @param auftragId
     * @return
     * @throws FindException
     */
    DSLAMProfileMonitor findByAuftragId(Long auftragId);

    /**
     * Ermittelt alle Auftragsnummern, die aktuell in Ueberwachung sind.
     *
     * @return
     */
    Collection<Long> findCurrentlyMonitoredAuftragIds();

}
