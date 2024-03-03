/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2017 11:43:13
 */
package de.mnet.hurrican.wholesale.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;

/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs {@link AuftragWholesale}.
 */
public interface WholesaleAuditDAO extends FindDAO, StoreDAO {


    /**
     * Ermittelt zu einem Id zugeh&ouml;rigen WholesaleAudit Objekt.
     *
     * @param id
     * @return das WholesaleAudit, dem die id zugeordnet ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    WholesaleAudit findById(Long id) throws FindException;

    /**
     * Ermittelt zu einem VorabstiummungsID eine Liste von den zugeh&ouml;rigen WholesaleAudit objekts.
     *
     * @param vorabstimmungsId
     * @return AuftragWholesale oder <code>null</code>, falls keinen WholesaleAuftrag.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<WholesaleAudit> findByVorabstimmungsId(String vorabstimmungsId) throws FindException;
}


