/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2009 18:48:43
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * TestNG Klasse fuer {@link CheckAccountsOptionalCommand}
 *
 *
 */
@Test(groups = BaseTest.UNIT)
public class CheckAccountsOptionalCommandTest extends BaseTest {

    public void testExecuteWithoutAccount() throws Exception {
        CheckAccountsOptionalCommand cmd = new CheckAccountsOptionalCommand();

        AccountService accountServiceMock = mock(AccountService.class);
        when(accountServiceMock.findIntAccounts4Auftrag(any(Long.class))).thenReturn(null);

        AbstractServiceCommand cmdSpy = spy(cmd);
        doReturn(new AuftragTechnik()).when(cmdSpy).getAuftragTechnikTx(any(Long.class));
        doReturn(accountServiceMock).when(cmdSpy).getCCService(AccountService.class);

        Object result = cmdSpy.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }

}


