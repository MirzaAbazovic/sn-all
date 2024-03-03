/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 16:22:43
 */
package de.mnet.hurrican.scheduler.service.impl;

import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.base.DefaultDAOService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.service.base.impl.DefaultHurricanService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.hurrican.scheduler.service.utils.SchedulerService;

/**
 * Basis-Klasse fuer alle Scheduler-Services.
 *
 *
 */
public class BaseSchedulerService extends DefaultDAOService implements SchedulerService {

    private static final Logger LOGGER = Logger.getLogger(DefaultHurricanService.class);

    /**
     * @see CCServiceFinder#getCCService(Class)
     */
    protected <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(type);
    }

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services.
     * @param type Typ des gesuchten Services.
     * @return gesuchter Service vom Typ <code>type</code>
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected <T extends IServiceObject> T getAuthenticationService(String name, Class<T> type)
            throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(name, type, null);
    }

    /**
     * Sucht nach dem User mit der Session-ID <code>sessionId</code>.
     *
     * @param sessionId Session-ID zu deren User ermittelt werden soll.
     * @return Instanz von <code>AKUser</code>.
     * @throws ServiceNotFoundException
     * @throws AKAuthenticationException
     */
    protected AKUser getAKUserBySessionId(Long sessionId) throws ServiceNotFoundException, AKAuthenticationException {
        AKUserService userService = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE,
                AKUserService.class);
        return userService.findUserBySessionId(sessionId);
    }

    /**
     * @see getAKUserBySessionId(Long) <br/> Methode unterdrueckt alle Fehlermeldungen.
     */
    protected AKUser getAKUserBySessionIdSilent(Long sessionId) {
        try {
            return getAKUserBySessionId(sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

}
