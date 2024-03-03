/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2009 11:01:05
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;


/**
 *
 */
public class CreateAuftragCommandTest extends AbstractHurricanBaseServiceTest {

    @Test(groups = BaseTest.SERVICE)
    public void testPrototypeCommand() {
        IServiceCommand createAuftragCommand1 = (IServiceCommand)
                getBean(CreateAuftragCommand.class.getName());
        IServiceCommand createAuftragCommand2 = (IServiceCommand)
                getBean(CreateAuftragCommand.class.getName());
        assertNotSame(createAuftragCommand1, createAuftragCommand2);
    }

}
