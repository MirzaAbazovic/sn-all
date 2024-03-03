/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2015
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;

/**
 *
 */
public interface Auftrag2PeeringPartnerDAO extends ByExampleDAO, StoreDAO {

    /**
     * Ermittelt alle PeeringPartner zu der angegebenen Auftrags-Id.
     * @param auftragId
     * @return
     */
    public List<Auftrag2PeeringPartner> findByAuftragId(Long auftragId);

    /**
     * Ermittelt alle gültigen Peering Partnern für die angegebene AuftragId und das Referenzdatum.
     * @param ccAuftragId
     * @param validAt
     * @return
     */
    public List<Auftrag2PeeringPartner> findValidAuftrag2PeeringPartner(Long ccAuftragId, Date validAt);

    /**
     * Ermittlet alle {@link Auftrag2PeeringPartner} Eintraege zum angegebenen PeeringPartner und Datum.
     * @param peeringPartnerId
     * @param validAt
     * @return
     */
    public List<Auftrag2PeeringPartner> findAuftrag2PeeringPartner(Long peeringPartnerId, Date validAt);

}
