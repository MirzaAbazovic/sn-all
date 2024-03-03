/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2005 15:00:02
 */
package de.augustakom.hurrican.service.cc.impl;

import java.net.*;
import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EWSDService;

/**
 * TestNG class for {@link EWSDService}
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class EWSDServiceTest extends AbstractHurricanBaseServiceTest {

    // TODO Tests verbessern!

    private EWSDService ewsdService;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        ewsdService = getCCService(EWSDService.class);
    }

    public void testPortGesamtDelete() throws DeleteException {
        // Wenn keine Exception fliegt wird angenommen das der Import erfolgreich war
        ewsdService.deletePortGesamt();
    }

    public void testSelectPortGesamtDate() throws FindException {
        // Wenn keine Exception fliegt wird angenommen das der Import erfolgreich war
        ewsdService.selectPortGesamtDate();
    }

    public void testImportEWSDFiles() throws StoreException, FindException {
        ArrayList<String> files = new ArrayList<String>(1);
        String filename = "EWSD_ImportTest.txt";
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        files.add(url.getPath());
        // Wenn keine Exception fliegt wird angenommen das der Import erfolgreich war
        ewsdService.importEWSDFiles(files);
    }

}
