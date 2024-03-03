/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 16:44:56
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.view.IntDomain;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Definition fuer die Verwaltung von Domains.
 *
 *
 */
public interface DomainService extends IBillingService {

    /**
     * Sucht nach allen Domains, die einem best. Auftrag zugeordnet sind.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen zugeordnete Domains gesucht werden.
     * @return Liste mit Objekten des Typs <code>ccAuftragId</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<IntDomain> findDomains4Auftrag(Long ccAuftragId) throws FindException;

    /**
     * Sucht nach allen Domains fuer die angegebenen Auftraege.
     *
     * @param ccAuftragIDs
     * @return
     * @throws FindException
     *
     */
    public List<IntDomain> findDomains4Auftraege(List<Long> ccAuftragIDs) throws FindException;

}


