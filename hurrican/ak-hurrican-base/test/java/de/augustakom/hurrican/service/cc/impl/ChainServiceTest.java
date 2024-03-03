/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 11:35:43
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.ChainService;


/**
 * JUnit TestCase fuer ChainService.
 *
 *
 */
@Ignore
public class ChainServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(ChainServiceTest.class);

    public ChainServiceTest() {
        super();
    }

    public ChainServiceTest(String arg0) {
        super(arg0);
    }

    /**
     * Suite-Definition fuer den TestCase.
     *
     * @return
     *
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ChainServiceTest("testFindServiceCommands4Reference"));
        return suite;
    }

    /**
     * Test method for 'de.augustakom.hurrican.service.cc.impl.ChainServiceImpl.findServiceCommands4Reference(Integer,
     * Class, String)'
     */
    public void testFindServiceCommands4Reference() {
        try {
            ChainService cs = getCCService(ChainService.class);
            //            List<ServiceCommand> result = cs.findServiceCommands4Reference(
            //                    new Integer(0), TechLeistung.class, ServiceCommand.COMMAND_TYPE_LS_ZUGANG);
            //            assertNotEmpty("Es wurden keine ServiceCommands zu der angegebenen Leistung gefunden!", result);

            List<ServiceCommand> result = cs.findServiceCommands4Reference(Long.valueOf(54), ServiceChain.class, null);
            assertNotNull("Result object is null!", result);
            LOGGER.debug(">>>>>>>>>< result object: " + result);

            for (ServiceCommand cmd : result) {
                LOGGER.debug("Command: " + cmd.getType() + " - " + cmd.getName());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


