/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalDialUpAccessCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private AccountService accountService;

    @Mock
    private CCAuftragService ccAuftragService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalDialUpAccessCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalDialUpAccessCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }

    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        final String realm = "asdf";
        final String account1 = "account1", account2 = "account2";
        final String pw1 = "pw1", pw2 = "pw2";

        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().setPersist(false).build();
        doReturn(auftragDaten).when(testling).getAuftragDaten();
        when(accountService.findIntAccounts4Auftrag(auftragDaten.getAuftragId()))
                .thenReturn(Arrays.asList(createAccount(account1, pw1), createAccount(account2, pw2)));
        when(accountService.getAccountRealm(auftragDaten.getAuftragId())).thenReturn(realm);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.DialUpAccess> dialUpAccess = workforceOrder.getDescription().getTechParams().getDialUpAccess();
        Assert.assertEquals(dialUpAccess.size(), 2);
        Assert.assertNotNull(dialUpAccess.get(0));
        Assert.assertEquals(dialUpAccess.get(0).getAccountId(), account1);
        Assert.assertEquals(dialUpAccess.get(0).getPassword(), pw1);
        Assert.assertEquals(dialUpAccess.get(0).getRealm(), realm);
        Assert.assertNotNull(dialUpAccess.get(1));
        Assert.assertEquals(dialUpAccess.get(1).getAccountId(), account2);
        Assert.assertEquals(dialUpAccess.get(1).getPassword(), pw2);
        Assert.assertEquals(dialUpAccess.get(1).getRealm(), realm);

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
        verify(accountService).getAccountRealm(auftragDaten.getAuftragId());
    }

    private IntAccount createAccount(String account, String password) {
        return new IntAccountBuilder()
                .withAccount(account)
                .withPasswort(password)
                .setPersist(false)
                .build();
    }

}
