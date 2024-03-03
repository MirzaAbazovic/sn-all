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

import de.augustakom.authentication.dao.AKRoleDAO;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKRoleService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;


/**
 * Implementierung eines Role-Services. <br>
 *
 *
 */
@AuthenticationTx
public class AKRoleServiceImpl implements AKRoleService {

    private static final Logger LOGGER = Logger.getLogger(AKRoleServiceImpl.class);

    @Resource(name = "de.augustakom.authentication.dao.AKRoleDAO")
    private AKRoleDAO roleDao;

    @Override
    public List<AKRole> findAll() throws AKAuthenticationException {
        try {
            return roleDao.findAll();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKRole> findByApplication(Long applicationId) throws AKAuthenticationException {
        try {
            return roleDao.findByApplication(applicationId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void save(AKRole role) throws AKAuthenticationException {
        try {
            roleDao.saveOrUpdate(role);
            role.notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void delete(Long roleId) throws AKAuthenticationException {
        try {
            roleDao.delete(roleId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_ERROR_DELETING_ROLE, new Object[] { roleId }, e);
        }
    }

    @Override
    public AKRole findById(Long id) throws AKAuthenticationException {
        try {
            return roleDao.findById(id, AKRole.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(e.getMessage(), e);
        }
    }
}
