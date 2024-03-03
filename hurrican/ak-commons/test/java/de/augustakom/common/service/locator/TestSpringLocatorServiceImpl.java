/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.service.locator;

/**
 * Dummy-Implementierung fuer einen SpringServiceLocator.
 *
 *
 */
class TestSpringLocatorServiceImpl extends SpringStandaloneServiceLocator {

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
     */
    public String getServiceLocatorName() {
        return "dummy-service-locator";
    }

    /**
     * @see de.augustakom.common.service.locator.AbstractSpringServiceLocator#getXMLConfiguration()
     */
    public String getXMLConfiguration() {
        return "/de/augustakom/common/service/locator/SpringLocatorServiceImplTest.xml";
    }
}
