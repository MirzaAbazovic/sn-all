/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2007 14:28:37
 */
package de.augustakom.hurrican.service.exmodules.sap;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.exmodules.sap.SAPBankverbindung;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;


/**
 * JUnit Test-Case fuer <code>SAPService</code>.
 *
 *
 */
@Ignore
public class SAPServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(SAPServiceTest.class);

    public SAPServiceTest() {
        super();
    }

    public SAPServiceTest(String arg0) {
        super(arg0);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new SAPServiceTest("testBankverbindung"));
        return suite;
    }

    /**
     * Test method
     */
    public void testSap() {
        try {
            SAPService sapService = (SAPService) getSAPService(SAPService.class);
            List<SAPBuchungssatz> list = sapService.findBuchungssaetze("A100000002");

            int i = 0;
            if (list != null) {
                for (SAPBuchungssatz bs : list) {
                    i++;
                    LOGGER.debug("Datensatz " + i + ": ");
                    LOGGER.debug("Datum: " + DateTools.formatDate(bs.getBlineDate(), DateTools.PATTERN_DAY_MONTH_YEAR) + " ---Betrag: " + bs.getLcAmount().toString());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testMahnstufe() {
        try {
            SAPService sapService = (SAPService) getSAPService(SAPService.class);
            Integer mahn = sapService.findMahnstufe("A100000002");

            LOGGER.debug("Mahnstufe: " + mahn);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testBankverbindung() {
        try {
            SAPService sapService = (SAPService) getSAPService(SAPService.class);
            SAPBankverbindung bank = sapService.findBankverbindung("A100000002");

            LOGGER.debug("Bank: Nr " + bank.getAccount() + " -- BLZ " + bank.getBlz());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }


}


