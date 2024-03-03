/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 11:54:10
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.GUIService;


/**
 * Unit-Test fuer <code>GUIService</code>
 *
 *
 */
@Ignore
public class GUIServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(GUIServiceTest.class);

    public GUIServiceTest() {
        super();
    }

    public GUIServiceTest(String arg0) {
        super(arg0);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new GUIServiceTest("testGetActions4Reference"));
        return suite;
    }

    /**
     * Test fuer die Methode GUIService#getGUIDefinitions4Reference(Integer, String)
     */
    public void getGUIDefinitions4Reference() {
        try {
            GUIService service = getCCService(GUIService.class);

            Long refId = Long.valueOf(1);
            String refH = GUIDefinition.class.getName();
            List result = service.getGUIDefinitions4Reference(refId, refH, GUIDefinition.TYPE_PANEL);
            assertNotEmpty("Es wurden keine GUI-Definitionen gefunden. Ref-ID: " + refId, result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


