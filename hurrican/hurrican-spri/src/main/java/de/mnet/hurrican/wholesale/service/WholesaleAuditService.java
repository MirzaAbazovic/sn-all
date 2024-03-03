/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 09:22:22
 */
package de.mnet.hurrican.wholesale.service;

import java.util.*;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;


/**
 * Service-Interface fuer das WholesaleAudit.
 *
 *
 */
public interface WholesaleAuditService extends ICCService {

    /**
     *
     * @param id
     * @return das WholesaleAudit, dem die id zugeordnet ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public WholesaleAudit findById(Long id) throws FindException;

    /**
     *
     * @param vorabstimmungsId
     * @return das WholesaleAudit, dem die vorabstimmungsId zugeordnet ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<WholesaleAudit> findByVorabstimmungsId(String vorabstimmungsId) throws FindException;


    /**
     * Speichert eine WholesaleAudit zu einem Vorabstimmung.
     */
    public void saveWholesaleAudit(WholesaleAudit wholesaleAudit) throws StoreException;
}


