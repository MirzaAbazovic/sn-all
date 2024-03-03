/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2005 12:06:17
 */
package de.augustakom.hurrican.service.billing.impl;

import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.model.billing.WebgatePW;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.billing.WebgatePWService;

/**
 * JUnit-Test fuer <code>WebgatePWService</code>.
 *
 *
 */
@Ignore
public class WebgatePWServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(WebgatePWServiceTest.class);

    public WebgatePWServiceTest() {
        super();
    }

    public WebgatePWServiceTest(String arg0) {
        super(arg0);
    }

    /**
     * Suite-Definition fuer den TestCase
     *
     * @return
     *
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        //suite.addTest(new WebgatePWServiceTest("testFindByLoginNo"));
        //suite.addTest(new WebgatePWServiceTest("testFindWebgatePW4Kunde"));
        //suite.addTest(new WebgatePWServiceTest("testFindPwNOs"));
        suite.addTest(new WebgatePWServiceTest("testFindWebgatePW4RInfo"));
        return suite;
    }

    /**
     * Test method for WebgatePWService#findWebgatePW4RInfo(Long)
     */
    public void testFindWebgatePW4RInfo() {
        try {
            WebgatePWService service = getBillingService(WebgatePWService.class);
            WebgatePW result = service.findWebgatePW4RInfo(new Long(200000266));
            assertNotNull("Kein WebgatePW-Eintrag zur RInfo gefunden!", result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for WebgatePWService#findFirstWebgatePW4Kunde(Long)
     */
    public void testFindFirstWebgatePW4Kunde() {
        try {
            WebgatePWService service = getBillingService(WebgatePWService.class);
            WebgatePW result = service.findFirstWebgatePW4Kunde(new Long(200000266));
            assertNotNull("Kein WebgatePW-Eintrag zur RInfo gefunden!", result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


