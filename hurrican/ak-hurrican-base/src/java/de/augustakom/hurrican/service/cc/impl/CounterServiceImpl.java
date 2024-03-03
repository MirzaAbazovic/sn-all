/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2004 10:30:20
 */
package de.augustakom.hurrican.service.cc.impl;

import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.CounterDAO;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CounterService;


/**
 * Service-Implementierung von <code>CounterService</code>.
 *
 *
 */
public class CounterServiceImpl extends DefaultCCService implements CounterService {

    private static final Logger LOGGER = Logger.getLogger(CounterServiceImpl.class);

    /**
     * @see de.augustakom.hurrican.service.cc.CounterService#getNewIntValue(java.lang.String)
     */
    @CcTxRequiresNew
    public Integer getNewIntValue(String counterName) throws StoreException {
        try {
            return ((CounterDAO) getDAO()).incrementIntValue(counterName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException();
        }
    }

}


