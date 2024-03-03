/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.service.locator;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceObject;


/**
 * Test-Klasse fuer de.augustakom.common.service.locator.AbstractSpringServiceLocator
 *
 *
 */
public class SpringLocatorServiceTest extends BaseTest {

    /**
     * Test fuer die Methode getService des ServiceLocators
     */
    @Test(groups = { "unit" })
    public void testGetService() throws Exception {
        TestSpringLocatorServiceImpl spring = new TestSpringLocatorServiceImpl();
        IServiceObject service = spring.getService("testService", null);

        assertTrue(service instanceof TestServiceImpl, "Object is not of Type ServiceImplTest!");
    }

    /**
     * Test fuer die Methode getService des ServiceLocators
     */
    @Test(groups = { "unit" }, expectedExceptions = { ServiceNotFoundException.class })
    public void testGetServiceException() throws Exception {
        TestSpringLocatorServiceImpl spring = new TestSpringLocatorServiceImpl();
        spring.getService("testService", DummyServiceObject.class, null);
    }

    /**
     * Dummy-Implementation eines ServiceObjects
     */
    class DummyServiceObject implements IServiceObject {
    }
}
