/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2009 13:46:52
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.cps.CPSDataChainConfig;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSConfigService;


/**
 * Service-Implementierung von <code>CPSConfigService</code>
 *
 *
 */
@CcTxRequired
public class CPSConfigServiceImpl extends DefaultCCService implements CPSConfigService {

    private static final Logger LOGGER = Logger.getLogger(CPSConfigServiceImpl.class);

    @Override
    public CPSDataChainConfig findCPSDataChainConfig(Long prodId, Long serviceOrderTypeRefId) throws FindException {
        if ((prodId == null) || (serviceOrderTypeRefId == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            CPSDataChainConfig example = new CPSDataChainConfig();
            example.setProdId(prodId);
            example.setServiceOrderTypeRefId(serviceOrderTypeRefId);

            List<CPSDataChainConfig> result =
                    ((ByExampleDAO) getDAO()).queryByExample(example, CPSDataChainConfig.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                else {
                    throw new FindException(FindException.INVALID_RESULT_SIZE,
                            new Object[] { Integer.valueOf(1), Integer.valueOf(result.size()) });
                }
            }

            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CPSDataChainConfig> findCPSDataChainConfigs(Long prodId) throws FindException {
        if (prodId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            CPSDataChainConfig example = new CPSDataChainConfig();
            example.setProdId(prodId);

            List<CPSDataChainConfig> result =
                    ((ByExampleDAO) getDAO()).queryByExample(example, CPSDataChainConfig.class);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteCPSDataChainConfig(CPSDataChainConfig toDelete) throws DeleteException {
        if ((toDelete == null) || (toDelete.getId() == null)) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }

        try {
            ((DeleteDAO) getDAO()).deleteById(toDelete.getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void storeCPSDataChainConfig(CPSDataChainConfig toStore) throws StoreException {
        try {
            ((StoreDAO) getDAO()).store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

}


