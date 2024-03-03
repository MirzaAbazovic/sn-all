/*
 * Copyright (c) 2004 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.04.2005 11:27:03
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.NiederlassungService;


/**
 * JUnit-TestCase fuer <code>NiederlassungService</code>.
 *
 *
 */
@Ignore
public class NiederlassungServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(NiederlassungServiceTest.class);

    public NiederlassungServiceTest(String name) {
        super(name);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        //        suite.addTest(new NiederlassungServiceTest("testAbteilung"));
        //        suite.addTest(new NiederlassungServiceTest("testNiederlassung"));
        suite.addTest(new NiederlassungServiceTest("testFindNiederlassung4Kunde"));
        return suite;
    }

    /**
     * Test fuer die Methode NiederlassungService#findNL4Abteilung()
     */
    public void testNiederlassung() {
        try {
            NiederlassungService ns = getCCService(NiederlassungService.class);
            List<Niederlassung> list = ns.findNL4Abteilung(Long.valueOf(1));

            if (CollectionTools.isNotEmpty(list)) {
                for (Niederlassung n : list) {
                    LOGGER.debug(n.getName());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode NiederlassungService#findNiederlassung4Kunde(java.lang.Long)
     */
    public void testFindNiederlassung4Kunde() {
        try {
            NiederlassungService ns = getCCService(NiederlassungService.class);
            Niederlassung result = ns.findNiederlassung4Kunde(new Long(200000407));
            assertNotNull("Keine Niederlassung zum Kunden ermittelbar!", result);
            assertEquals("ermittelte Niederlassung nicht wie erwartet!", result.getId(), Niederlassung.ID_AUGSBURG);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


