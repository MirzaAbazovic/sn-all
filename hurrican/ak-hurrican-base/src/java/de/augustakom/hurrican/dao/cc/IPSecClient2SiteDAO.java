/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 10:56:18
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 *
 */
public interface IPSecClient2SiteDAO extends FindDAO, StoreDAO {


    /**
     * @return Liste mit allen IPSecClient2Site-Tokens.
     */
    List<IPSecClient2SiteToken> findAllClient2SiteTokens();

    /**
     * @param serialNumber
     * @return
     */
    List<IPSecClient2SiteToken> findAllClient2SiteTokens(String serialNumber);

    /**
     * Sucht IPSecClient2Site-Tokens nach Auftrags-Id.
     *
     * @param auftragId Auftrags-Id nach der gesucht werden soll.
     * @return Liste mit allen gefundenen IPSecClient2Site-Tokens.
     */
    List<IPSecClient2SiteToken> findClient2SiteTokens(Long auftragId);

    /**
     * @return
     */
    List<IPSecClient2SiteToken> findFreeClient2SiteTokens() throws FindException;

    /**
     * @param serialNumber
     * @return
     */
    List<IPSecClient2SiteToken> findFreeClient2SiteTokens(String serialNumber) throws FindException;

    /**
     * @param token
     */
    void deleteClient2SiteToken(IPSecClient2SiteToken token) throws StoreException;

}


