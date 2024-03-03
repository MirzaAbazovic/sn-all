/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 13:49:55
 */
package de.augustakom.authentication.gui.basics;

import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.locator.ServiceLocator;

/**
 * OptionDialog-Implementierung fuer das Authentication-Projekt.
 *
 *
 */
public abstract class AbstractAuthenticationOptionDialog extends AKJAbstractOptionDialog {

    /**
     * @param resource
     * @param useScrollPane
     */
    public AbstractAuthenticationOptionDialog(String resource, boolean useScrollPane) {
        super(resource, useScrollPane);
    }

    /**
     * @param resource
     */
    public AbstractAuthenticationOptionDialog(String resource) {
        super(resource);
    }

    /**
     * Gibt einen Authentication-Service (z.B. AKUserService) zurueck.
     *
     * @param serviceName Name des gesuchten Services.
     * @param type        Typ des gesuchten Services.
     * @return gesucheter Service
     * @throws ServiceNotFoundException
     */
    protected <T> T getAuthenticationService(String serviceName, Class<T> type) throws ServiceNotFoundException {
        return ServiceLocator.instance().getService(serviceName, type);
    }

}
