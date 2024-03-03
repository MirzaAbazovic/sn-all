/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.command;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;


/**
 * Test for {@link CommandActivateMDUCommand}
 *
 *
 */
public class CommandActivateMDUCommandTest extends AbstractHurricanBaseServiceTest {

    private static final String SESSIONID = "38961270616848607";
    private static final String MDU = "MDU-4002239";
    private static final Date DATE = new Date();

    @DataProvider(name = "dataProviderMissingSessionID")
    protected Object[][] dataProviderMissingSessionID() {
        return new Object[][] {
                { null, CommandActivateMDUCommandTest.MDU, CommandActivateMDUCommandTest.DATE }
        };
    }

    @DataProvider(name = "dataProviderMissingMDU")
    protected Object[][] dataProviderMissingMDU() {
        return new Object[][] {
                { CommandActivateMDUCommandTest.SESSIONID, null, CommandActivateMDUCommandTest.DATE }
        };
    }

    @DataProvider(name = "dataProviderMissingDate")
    protected Object[][] dataProviderMissingDate() {
        return new Object[][] {
                { CommandActivateMDUCommandTest.SESSIONID, CommandActivateMDUCommandTest.MDU, null }
        };
    }

    @Test(groups = BaseTest.SERVICE, dataProvider = "dataProviderMissingSessionID", expectedExceptions = IllegalArgumentException.class)
    public void testMissingSessionId(String sessionId, String mdu, Date date) throws Exception {
        CommandActivateMDUCommand command = (CommandActivateMDUCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.command.CommandActivateMDUCommand");
        command.prepare(CommandActivateMDUCommand.KEY_SESSION_ID, sessionId);
        command.prepare(CommandActivateMDUCommand.KEY_MDU, mdu);
        command.prepare(CommandActivateMDUCommand.KEY_DATUM, date);
        command.execute();
    }

    @Test(groups = BaseTest.SERVICE, dataProvider = "dataProviderMissingMDU", expectedExceptions = IllegalArgumentException.class)
    public void testMissingMDU(String sessionId, String mdu, Date date) throws Exception {
        CommandActivateMDUCommand command = (CommandActivateMDUCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.command.CommandActivateMDUCommand");
        command.prepare(CommandActivateMDUCommand.KEY_SESSION_ID, SESSIONID);
        command.prepare(CommandActivateMDUCommand.KEY_MDU, null);
        command.prepare(CommandActivateMDUCommand.KEY_DATUM, DATE);
        command.execute();
    }

    @Test(groups = BaseTest.SERVICE, dataProvider = "dataProviderMissingDate", expectedExceptions = IllegalArgumentException.class)
    public void testMissingDate(String sessionId, String mdu, Date date) throws Exception {
        CommandActivateMDUCommand command = (CommandActivateMDUCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.command.CommandActivateMDUCommand");
        command.prepare(CommandActivateMDUCommand.KEY_SESSION_ID, SESSIONID);
        command.prepare(CommandActivateMDUCommand.KEY_MDU, MDU);
        command.prepare(CommandActivateMDUCommand.KEY_DATUM, null);
        command.execute();
    }
}


