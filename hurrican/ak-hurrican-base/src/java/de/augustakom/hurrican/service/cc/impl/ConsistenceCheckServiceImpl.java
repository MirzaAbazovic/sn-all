/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2005 07:32:08
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.ConsistenceCheckDAO;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.consistence.HistoryConsistence;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ConsistenceCheckService;


/**
 * Service-Implementierung von <code>ConsistenceCheckService</code>
 *
 *
 */
public class ConsistenceCheckServiceImpl extends DefaultCCService implements ConsistenceCheckService {

    private static final Logger LOGGER = Logger.getLogger(ConsistenceCheckServiceImpl.class);

    /**
     * @see de.augustakom.hurrican.service.cc.ConsistenceCheckService#checkHistoryConsistence(java.lang.Class)
     */
    @CcTxRequiredReadOnly
    public List<HistoryConsistence> checkHistoryConsistence(Class classType) throws FindException {
        if (classType == null) { return null; }
        try {
            return ((ConsistenceCheckDAO) getDAO()).checkHistoryConsistence(classType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.ConsistenceCheckService#findMultipleUsedIntAccounts()
     */
    @CcTxRequiredReadOnly
    public List<IntAccount> findMultipleUsedIntAccounts() throws FindException {
        try {
            return ((ConsistenceCheckDAO) getDAO()).findMultipleUsedIntAccounts();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}


