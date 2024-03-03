/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2005 13:14:36
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKDbDAO;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.service.AKDbService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Implementierung eines DB-Services. <br>
 *
 *
 */
@AuthenticationTx
public class AKDbServiceImpl implements AKDbService {

    private static final Logger LOGGER = Logger.getLogger(AKDbServiceImpl.class);

    @Resource(name = "de.augustakom.authentication.dao.AKDbDAO")
    private AKDbDAO dbDao;

    public List<AKDb> findAll() throws AKAuthenticationException {
        try {
            return dbDao.findAll();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void save(AKDb account) throws AKAuthenticationException {
        try {
            dbDao.saveOrUpdate(account);
            account.notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void delete(Long accountId) throws AKAuthenticationException {
        try {
            dbDao.delete(accountId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_ERROR_DELETE_DB, new Object[] { accountId }, e);
        }
    }

}
