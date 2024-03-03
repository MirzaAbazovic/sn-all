/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 16:52:39
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.view.IntDomain;


/**
 * DAO-Interface fuer die Verwaltung von Domain-Objekten.
 *
 *
 */
public interface DomainDao {

    /**
     * Sucht nach allen Domains, die den Taifun Auftraegen zugeordnet sind.
     *
     * @param auftragNoOrigs ID der Taifun Auftraege
     * @return Liste mit Objekten des Typs <code>IntDomain</code> oder <code>null</code>.
     */
    public List<IntDomain> findByAuftragNoOrigs(List<Long> auftragNoOrigs);

}


