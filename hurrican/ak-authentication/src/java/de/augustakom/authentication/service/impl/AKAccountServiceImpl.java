/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 13:06:36
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKAccountDAO;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.service.AKAccountService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.lang.DesEncrypter;


/**
 * Implementierung eines Account-Services. <br>
 *
 *
 */
@AuthenticationTx
public class AKAccountServiceImpl implements AKAccountService {

    private static final Logger LOGGER = Logger.getLogger(AKAccountServiceImpl.class);

    @Resource(name = "de.augustakom.authentication.dao.AKAccountDAO")
    private AKAccountDAO accountDao;

    @Override
    public List<AKAccount> findAll() throws AKAuthenticationException {
        try {
            return accountDao.findAll();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKAccount> findByDB(Long dbId) throws AKAuthenticationException {
        try {
            return accountDao.findByDB(dbId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void save(AKAccount account) throws AKAuthenticationException {
        try {
            String password = DesEncrypter.getInstance().encrypt(account.getAccountPassword());
            account.setAccountPassword(password);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }

        try {
            accountDao.saveOrUpdate(account);
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
            accountDao.delete(accountId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_ERROR_DELETE_ACCOUNT, new Object[] { accountId }, e);
        }
    }

}
