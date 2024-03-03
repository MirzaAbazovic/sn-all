/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 09:53:07
 */
package de.augustakom.common.service.base;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.DefaultServiceCommandException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;

/**
 * TestNG Test-Case fuer die Klasse <code>AKServiceCommandChain</code>.
 *
 *
 */
public class AKServiceCommandChainTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(AKServiceCommandChainTest.class);

    /**
     * Test method for 'de.augustakom.common.service.base.AKServiceCommandChain.executeChain()'
     */
    @Test(groups = { "unit" }, expectedExceptions = { DefaultServiceCommandException.class })
    public void testExecuteChainNoCommands() throws Exception {
        AKServiceCommandChain chain = new AKServiceCommandChain();
        chain.executeChain();
    }

    /**
     * Test method for 'de.augustakom.common.service.base.AKServiceCommandChain.executeChain()'
     */
    @Test(groups = { "unit" })
    public void testExecuteChain() throws Exception {
        AKServiceCommandChain chain = new AKServiceCommandChain();
        CommandOne cmd1 = new CommandOne();
        CommandTwo cmd2 = new CommandTwo();
        chain.addCommand(cmd1);
        chain.addCommand(cmd2);

        List<Object> list = chain.executeChain();

        assertEquals(list.size(), 2, "size of result list incorrect");
        assertEquals(list.get(0), serviceCommandOneResult, "Command result incorrect");
        assertEquals(list.get(1), cmd2, "Command result incorrect");
        assertEquals(chain.getLastExecuted(), cmd2, "last executed incorrect");

        assertEquals(chain.getWarnings().getAKMessages().size(), 1, "expected one warning");
        assertEquals(chain.getWarnings().getAKMessages().get(0).getMessage(), "Test-Warning", "missmatch expected warning text");
    }

    /**
     * Test method for 'de.augustakom.common.service.base.AKServiceCommandChain.executeChain()'
     */
    @Test(groups = { "unit" })
    public void testExecuteChainCheckResult() throws Exception {
        AKServiceCommandChain chain = new AKServiceCommandChain();
        CommandOne cmd1 = new CommandOne();
        chain.addCommand(cmd1);

        List<Object> list = chain.executeChain(true);

        assertEquals(list.size(), 1, "size of result list incorrect");
        assertEquals(list.get(0), serviceCommandOneResult, "Command result incorrect");
        assertEquals(chain.getLastExecuted(), cmd1, "last executed incorrect");

        assertEquals(0, chain.getWarnings().getAKMessages().size(), "expected no warnings");
    }

    ServiceCommandResult serviceCommandOneResult = ServiceCommandResult.createCmdResult(
            ServiceCommandResult.CHECK_STATUS_OK, "test status ok", CommandOne.class);

    /**
     * Einfache Test-Klasse fuer die Chain.
     */
    class CommandOne implements IServiceCommand {
        /**
         * @see de.augustakom.common.service.iface.IServiceCommand#prepare(java.lang.String, java.lang.Object)
         */
        public void prepare(String name, Object value) {
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceCommand#execute()
         */
        public Object execute() throws Exception {
            LOGGER.debug("... execute one");
            return serviceCommandOneResult;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceCommand#getServiceCommandName()
         */
        public String getServiceCommandName() {
            return this.getClass().toString();
        }
    }

    /**
     * Zweite Test-Klasse fuer die Chain.
     */
    class CommandTwo implements IServiceCommand, IWarningAware {
        /**
         * @see de.augustakom.common.service.iface.IServiceCommand#prepare(java.lang.String, java.lang.Object)
         */
        public void prepare(String name, Object value) {
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceCommand#execute()
         */
        public Object execute() throws Exception {
            LOGGER.debug("... execute two");
            return this;
        }

        /**
         * @see de.augustakom.common.tools.messages.IWarningAware#getWarnings()
         */
        public AKWarnings getWarnings() {
            AKWarnings warnings = new AKWarnings();
            warnings.addAKWarning(this, "Test-Warning");
            return warnings;
        }

        /**
         * @see de.augustakom.common.service.iface.IServiceCommand#getServiceCommandName()
         */
        public String getServiceCommandName() {
            return null;
        }
    }
}
