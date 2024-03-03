/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2004 11:00:25
 */
package de.augustakom.hurrican.service.cc.impl;

import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.CounterService;


/**
 * JUnit-Test fuer <code>CounterService</code>.
 *
 *
 */
@Ignore
public class CounterServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(CounterServiceTest.class);

    /**
     * Test fuer die Methode CounterService#getNewIntValue(String)
     */
    public void testGetNewIntValue() {
        try {
            CounterService cs = (CounterService) getCCService(CounterService.class);

            Integer newValue = cs.getNewIntValue("x");
            assertEquals("Counter lieferte falschen Wert!", 10001, newValue.intValue());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            fail(e.getMessage());
        }
    }

}


