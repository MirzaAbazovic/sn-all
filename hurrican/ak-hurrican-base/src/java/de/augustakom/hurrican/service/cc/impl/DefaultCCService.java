/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 08:30:48
 */
package de.augustakom.hurrican.service.cc.impl;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.impl.DefaultHurricanService;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;
import de.augustakom.hurrican.service.internet.IInternetService;
import de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Basisklasse fuer alle Hurrican Service-Implementierungen im CC-Bereich.
 *
 *
 */
public class DefaultCCService extends DefaultHurricanService implements ICCService, ICCServiceFinder,
        IBillingServiceFinder, IInternetServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(DefaultCCService.class);

    /**
     * Gibt einen Billing-Service zurueck.
     *
     * @param name Name des Services.
     * @param type Typ des Services
     * @return Billing-Service
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    @Override
    public <T extends IBillingService> T getBillingService(String name, Class<T> type) throws ServiceNotFoundException {
        return applicationContext.getBean(name, type);
    }

    /**
     * @see #getBillingService(String, Class)
     */
    @Override
    public <T extends IBillingService> T getBillingService(Class<T> type) throws ServiceNotFoundException {
        return applicationContext.getBean(type.getName(), type);
    }

    /**
     * Gibt einen anderen CC-Service zurueck.
     *
     * @param name Name des Services.
     * @param type Typ des Services
     * @return CC-Service
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    @Override
    public <T extends ICCService> T getCCService(String name, Class<T> type) throws ServiceNotFoundException {
        return applicationContext.getBean(name, type);
    }

    /**
     * @see #getCCService(String, Class)
     */
    @Override
    public <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
        return applicationContext.getBean(type.getName(), type);
    }

    public <T extends ICCService> T getCCServiceRE(Class<T> type) {
        try {
            return getCCService(type);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Service of type %s not found! %s",
                    type.getName(), e.getMessage()), e);  // NOSONAR squid:S00112 ; RuntimeEx
        }
    }

    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        throw new ServiceNotFoundException("findService not implemented in " + DefaultCCService.class.getName());
    }

    @Override
    public <T extends IInternetService> T getInternetService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return applicationContext.getBean(serviceName, serviceType);
    }

    @Override
    public <T extends IInternetService> T getInternetService(Class<T> serviceType) throws ServiceNotFoundException {
        return applicationContext.getBean(serviceType.getName(), serviceType);
    }

    /**
     * Allgemeine Methode für CCServices, um mit einem {@link AbstractCCHistoryModel} in historisierter Art und Weise
     * eine Entität zu persistieren
     *
     * @param toSave
     * @param dao
     * @throws StoreException
     */
    protected void saveHistoricized(AbstractCCHistoryModel toSave, StoreDAO dao) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            HistoryHelper.checkHistoryDates(toSave);
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

}


