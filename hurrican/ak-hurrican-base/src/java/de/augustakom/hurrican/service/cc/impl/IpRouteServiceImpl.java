/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 11:58:22
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IpRouteService;
import de.augustakom.hurrican.validation.cc.IpRouteValidator;


/**
 * Service-Implementierung von {@link IpRouteService}.
 */
@CcTxRequired
public class IpRouteServiceImpl extends DefaultCCService implements IpRouteService {

    private static final Logger LOGGER = Logger.getLogger(IpRouteServiceImpl.class);

    @Resource(name = "ipRouteValidator")
    private IpRouteValidator ipRouteValidator;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Override
    public void deleteIpRoute(IpRoute toDelete, Long sessionId) throws DeleteException {
        if (toDelete == null) { throw new DeleteException(DeleteException.INVALID_PARAMETERS); }
        try {
            toDelete.setDeleted(Boolean.TRUE);
            if (toDelete.getIpAddressRef() != null) {
                toDelete.getIpAddressRef().setGueltigBis(new Date());
            }
            saveIpRoute(toDelete, sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<IpRoute> findIpRoutesByOrder(Long auftragId) throws FindException {
        try {
            IpRoute example = new IpRoute();
            example.setAuftragId(auftragId);
            example.setDeleted(Boolean.FALSE);

            return ((ByExampleDAO) getDAO()).queryByExample(example, IpRoute.class, new String[] { IpRoute.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveIpRoute(IpRoute toSave, Long sessionId) throws StoreException, ValidationException {
        if ((toSave == null) || (sessionId == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        validateIp(toSave);
        try {
            toSave.setUserW(getUserNameAndFirstNameSilent(sessionId));
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public void moveIpRoute(IpRoute toMove, Long auftragId, Long sessionId) throws StoreException {
        if ((toMove == null) || (auftragId == null) || (sessionId == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            if (NumberTools.equal(auftragId, toMove.getAuftragId())) {
                throw new StoreException("Route kann nicht auf sich selbst umgezogen werden!");
            }

            Auftrag auftrag = ccAuftragService.findAuftragById(auftragId);
            if (auftrag == null) {
                throw new StoreException("Auftrag mit ID {0} konnte nicht ermittelt werden!",
                        new Object[] { String.format("%s", auftragId) });
            }

            IpRoute ipRouteCopy = new IpRoute();
            PropertyUtils.copyProperties(ipRouteCopy, toMove);
            ipRouteCopy.setId(null);
            ipRouteCopy.setAuftragId(auftragId);
            ipRouteCopy.setVersion(Long.valueOf(0));
            if (toMove.getIpAddressRef() != null) {
                IPAddress ipAddressCopy = new IPAddress();
                PropertyUtils.copyProperties(ipAddressCopy, toMove.getIpAddressRef());
                ipAddressCopy.setId(null);
                ipAddressCopy.setGueltigVon(null);
                ipRouteCopy.setIpAddressRef(ipAddressCopy);
            }
            saveIpRoute(ipRouteCopy, sessionId);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void validateIp(IpRoute toValidate) throws ValidationException {
        ValidationException valEx = new ValidationException(toValidate, "IpRoute");
        ipRouteValidator.validate(toValidate, valEx);
        if (valEx.hasErrors()) {
            throw valEx;
        }
    }

    /**
     * Injected
     */
    public void setIpRouteValidator(IpRouteValidator ipRouteValidator) {
        this.ipRouteValidator = ipRouteValidator;
    }

    /**
     * Injected
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

}


