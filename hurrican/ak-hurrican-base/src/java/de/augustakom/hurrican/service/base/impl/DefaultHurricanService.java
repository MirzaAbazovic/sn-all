/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 08:37:41
 */
package de.augustakom.hurrican.service.base.impl;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.base.DefaultDAOService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.service.base.iface.IHurricanService;


/**
 * Basisklasse fuer alle Service-Implementierungen innerhalb des Hurrican-Projekts.
 *
 *
 */
public class DefaultHurricanService extends DefaultDAOService implements IHurricanService, ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(DefaultHurricanService.class);

    protected ApplicationContext applicationContext;

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    protected AKUserService userService;

    /**
     * Sucht nach dem User mit der Session-ID <code>sessionId</code>.
     *
     * @param sessionId Session-ID zu deren User ermittelt werden soll.
     * @return Instanz von <code>AKUser</code>.
     * @throws ServiceNotFoundException
     * @throws AKAuthenticationException
     */
    public AKUser getAKUserBySessionId(Long sessionId) throws AKAuthenticationException {
        return userService.findUserBySessionId(sessionId);
    }

    /**
     * @param sessionId
     * @return
     * @see #getAKUserBySessionId(Long) Methode unterdrueckt alle Fehlermeldungen.
     */
    protected AKUser getAKUserBySessionIdSilent(Long sessionId) {
        try {
            return getAKUserBySessionId(sessionId);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get the login name for a user (used as "userW" value), or unknown if the user count not be found.
     *
     * @param sessionId
     * @return
     */
    protected String getLoginNameSilent(Long sessionId) {
        AKUser user = null;
        try {
            user = getAKUserBySessionId(sessionId);
        }
        catch (Exception e) {
            LOGGER.warn("getAKUserBySessionId failed: " + e.getMessage());
            LOGGER.debug("getAKUserBySessionId failed", e);
        }
        return user != null ? user.getLoginName() : HurricanConstants.UNKNOWN;
    }

    /**
     * Gibt den Namen+Vornamen des Users zurueck, der ueber die Session-ID identifiziert wird. Fehlermeldungen werden
     * komplett unterdrueckt!
     *
     * @param sessionId
     * @return Name+Vorname des Users bzw. 'unknown', falls der User nicht ermittelt werden konnte.
     */
    protected String getUserNameAndFirstNameSilent(Long sessionId) {
        AKUser user = getAKUserBySessionIdSilent(sessionId);
        return (user != null) ? user.getNameAndFirstName() : HurricanConstants.UNKNOWN;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}


