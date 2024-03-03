/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2005 13:26:13
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.RegistryDAO;
import de.augustakom.hurrican.model.cc.Registry;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 * Implementierung von <code>RegistryService</code>.
 *
 *
 */
@CcTxRequired
public class RegistryServiceImpl extends DefaultCCService implements RegistryService {

    private static final Logger LOGGER = Logger.getLogger(RegistryServiceImpl.class);

    @Override
    public String getStringValue(Long id) throws FindException {
        try {
            Registry reg = ((FindDAO) getDAO()).findById(id, Registry.class);
            if ((reg != null) && (reg.getStringValue() != null)) {
                return reg.getStringValue();
            }

            throw new FindException(FindException.REGISTRY_KEY_NOT_FOUND, new Object[] { id });
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException.REGISTRY_KEY_NOT_FOUND, new Object[] { id }, e);
        }
    }

    @Override
    public Integer getIntValue(Long id) throws FindException {
        try {
            Registry reg = ((FindDAO) getDAO()).findById(id, Registry.class);
            if ((reg != null) && (reg.getIntValue() != null)) {
                return reg.getIntValue();
            }

            throw new FindException(FindException.REGISTRY_KEY_NOT_FOUND, new Object[] { id });
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException.REGISTRY_KEY_NOT_FOUND, new Object[] { id }, e);
        }
    }

    @Override
    public List<Registry> findRegistries() throws FindException {
        try {
            return ((RegistryDAO) getDAO()).findAll(Registry.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveRegistry(Registry toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }

        try {
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

}
