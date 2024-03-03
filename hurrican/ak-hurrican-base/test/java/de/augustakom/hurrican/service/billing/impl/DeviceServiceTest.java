/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 10:39:00
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.billing.DeviceService;


/**
 * JUnit TestCase fuer <code>DeviceService</code>.
 *
 *
 */
@Ignore
public class DeviceServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(DeviceServiceTest.class);

    public DeviceServiceTest(String name) {
        super(name);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        //        suite.addTest(new DeviceServiceTest("testFindDevice"));
        suite.addTest(new DeviceServiceTest("testFindDevices4Auftrag"));
        return suite;
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.billing.impl.DeviceServiceImpl#findDevices4Auftrag(java.lang.Long,
     * java.lang.String)}.
     */
    public void testFindDevices4Auftrag() {
        try {
            DeviceService service = getBillingService(DeviceService.class);
            List<Device> result = service.findDevices4Auftrag(new Long(898645), null, null);

            assertNotEmpty("Keine Devices zu Auftrag gefunden!", result);
            for (Device dev : result) {
                LOGGER.debug("Device: " + dev.getDevType() + " - " + dev.getSerialNumber());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


