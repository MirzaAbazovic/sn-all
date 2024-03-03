/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.service.locator;

import org.apache.log4j.Logger;

import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceObject;


/**
 * Einfache Implementierung eines Services.
 *
 *
 */
public class TestServiceImpl implements IServiceObject {

    private static final Logger LOGGER = Logger.getLogger(TestServiceImpl.class);

    public void doSomething() {
        LOGGER.debug("Service-Class: " + this.getClass().getName());
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceObject
     */
    public void setServiceLocator(IServiceLocator serviceLocator) {
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceObject
     */
    public IServiceLocator getServiceLocator() {
        return null;
    }

}
