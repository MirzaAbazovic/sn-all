/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2005 14:47:47
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.KubenaService;


/**
 * JUnit-Test fuer <code>KubenaService</code>.
 *
 *
 */
@Ignore
public class KubenaServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(KubenaServiceTest.class);

    /**
     * Test fuer die Methode KubenaService#createKubena(java.lang.Integer)
     */
    public void testCreateKubena() {
        try {
            Long kubenaId = Long.valueOf(4);
            File file = getService().createKubena(kubenaId);
            assertNotNull("Kubena-File wurde nicht erzeugt!!!", file);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /* Gibt den Kubena-Service zurueck. */
    private KubenaService getService() {
        return getCCService(KubenaService.class);
    }

}


