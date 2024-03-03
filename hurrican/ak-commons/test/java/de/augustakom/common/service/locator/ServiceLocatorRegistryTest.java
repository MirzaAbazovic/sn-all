/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.service.locator;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceContext;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceObject;


/**
 * Test-Klasse fuer de.augustakom.common.service.locator.ServiceLocatorRegistry
 *
 *
 */
public class ServiceLocatorRegistryTest extends BaseTest {

    /**
     * Testet die Methoden der ServiceLocatorRegistry
     */
    @Test(groups = { "unit" }, expectedExceptions = { IllegalArgumentException.class })
    public void testServiceLocatorException() {
        ServiceLocatorRegistry.instance().addServiceLocator(new DummyOneServiceLocator());
        ServiceLocatorRegistry.instance().addServiceLocator(new DummyOneServiceLocator());
    }

    /**
     * Testet die Methoden der ServiceLocatorRegistry
     */
    @Test(groups = { "unit" })
    public void testServiceLocator() {

        ServiceLocatorRegistry.instance().addServiceLocator(new DummyOneServiceLocator());

        IServiceLocator slOne = ServiceLocatorRegistry.instance().getServiceLocator("dummy-two");

        assertNotNull(slOne, "ServiceLocator 'DummyOne' is null!");

        assertEquals(ServiceLocatorRegistry.instance().getServiceLocatorNames().length, 1,
                "Count of registered locators is not valid!");
    }

    /**
     * Dummy-Implementierung eines ServiceLocators.
     *
     *
     */
    class DummyOneServiceLocator implements IServiceLocator {

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getService(java.lang.String,
         * de.augustakom.common.service.iface.IServiceContext)
         */
        public IServiceObject getService(String serviceName, IServiceContext serviceContext) throws ServiceNotFoundException {
            return null;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
         */
        public String getServiceLocatorName() {
            return "dummy-one";
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getService(java.lang.String, java.lang.Class,
         * de.augustakom.common.service.iface.IServiceContext)
         */
        public <T extends IServiceObject> T getService(String serviceName, Class<T> requiredType, IServiceContext serviceContext) throws ServiceNotFoundException {
            return null;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#closeServiceLocator()
         */
        public void closeServiceLocator() {
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getBean(java.lang.String)
         */
        public Object getBean(String name) {
            return null;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getBeansOfType(java.lang.Class)
         */
        public <T> Map<String, T> getBeansOfType(Class<T> type) {
            return null;
        }
    }

    /**
     * Dummy-Implementierung eines ServiceLocators.
     *
     *
     */
    class DummyTwoServiceLocator implements IServiceLocator {

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getService(java.lang.String,
         * de.augustakom.common.service.iface.IServiceContext)
         */
        public IServiceObject getService(String serviceName, IServiceContext serviceContext) throws ServiceNotFoundException {
            return null;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
         */
        public String getServiceLocatorName() {
            return "dummy-two";
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getService(java.lang.String, java.lang.Class,
         * de.augustakom.common.service.iface.IServiceContext)
         */
        public <T extends IServiceObject> T getService(String serviceName, Class<T> requiredType, IServiceContext serviceContext) throws ServiceNotFoundException {
            return null;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#closeServiceLocator()
         */
        public void closeServiceLocator() {
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getBean(java.lang.String)
         */
        public Object getBean(String name) {
            return null;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceLocator#getBeansOfType(java.lang.Class)
         */
        public <T> Map<String, T> getBeansOfType(Class<T> type) {
            return null;
        }
    }
}
