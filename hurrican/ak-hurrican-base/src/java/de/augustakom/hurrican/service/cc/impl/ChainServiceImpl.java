/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 10:38:57
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.ServiceCommandDAO;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.command.ServiceCommandMapping;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ChainService;


/**
 * Service-Implementierung von ChainService.
 *
 *
 */
@CcTxRequired
public class ChainServiceImpl extends DefaultCCService implements ChainService {

    private static final Logger LOGGER = Logger.getLogger(ChainServiceImpl.class);

    private ServiceCommandDAO serviceCommandDAO = null;

    @Override
    public List<ServiceChain> findServiceChains(String chainType) throws FindException {
        try {
            ServiceChain example = new ServiceChain();
            example.setType(chainType);

            return getServiceCommandDAO().queryByExample(example, ServiceChain.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveServiceChain(ServiceChain toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            getServiceCommandDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteServiceChain(Long chainId) throws DeleteException {
        if (chainId == null) { throw new DeleteException(DeleteException.INVALID_PARAMETERS); }
        try {
            ServiceCommandDAO dao = getServiceCommandDAO();
            dao.deleteCommands4Reference(chainId, ServiceChain.class);
            dao.deleteServiceChain(chainId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ServiceCommand> findServiceCommands4Reference(Long refId, Class<?> refClass, String commandType) throws FindException {
        if ((refId == null) || (refClass == null)) { return null; }
        try {
            return getServiceCommandDAO().findCommands4Reference(refId, refClass, commandType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveCommands4Reference(Long refId, Class<?> refClass, List<ServiceCommand> commands) throws StoreException {
        if ((refId == null) || (refClass == null) || (commands == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            ServiceCommandDAO dao = getServiceCommandDAO();
            dao.deleteCommands4Reference(refId, refClass);

            int orderNo = 0;
            for (ServiceCommand cmd : commands) {
                ServiceCommandMapping scm = new ServiceCommandMapping();
                scm.setRefId(refId);
                scm.setRefClass(refClass.getName());
                scm.setCommandId(cmd.getId());
                scm.setOrderNo(++orderNo);
                dao.store(scm);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ServiceCommand> findServiceCommands(String commandType) throws FindException {
        try {
            ServiceCommand example = new ServiceCommand();
            example.setType(commandType);

            return getServiceCommandDAO().queryByExample(example, ServiceCommand.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @CcTxRequiresNew
    public void deleteCommands4Reference(Long refId, Class<?> refClass) throws DeleteException {
        if ((refId == null) || (refClass == null)) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getServiceCommandDAO().deleteCommands4Reference(refId, refClass);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException.INVALID_PARAMETERS, e);
        }
    }

    /**
     * @return Returns the serviceCommandDAO.
     */
    public ServiceCommandDAO getServiceCommandDAO() {
        return serviceCommandDAO;
    }

    /**
     * @param serviceCommandDAO The serviceCommandDAO to set.
     */
    public void setServiceCommandDAO(ServiceCommandDAO serviceCommandDAO) {
        this.serviceCommandDAO = serviceCommandDAO;
    }
}


