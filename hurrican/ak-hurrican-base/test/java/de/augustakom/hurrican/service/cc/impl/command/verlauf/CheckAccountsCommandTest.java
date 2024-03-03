/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2009 18:48:43
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Account;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * TestNG Klasse fuer {@link CheckAccountsCommand}
 *
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CheckAccountsCommandTest extends BaseTest {

    /**
     * Test fuer {@link CheckAccountsCommand#filterBillingAccounts(List)}
     */
    @Test
    public void filterBillingAccounts() {
        Account acc1 = new Account();
        acc1.setAccountId("test123");

        Account acc2 = new Account();
        acc2.setAccountId("test_VOL");

        List<Account> accounts = new ArrayList<Account>();
        accounts.add(acc1);
        accounts.add(acc2);

        CheckAccountsCommand cmd = new CheckAccountsCommand();
        cmd.filterBillingAccounts(accounts);

        assertNotEmpty(accounts, "Filter zu eng gesetzt!");
        assertEquals(accounts.size(), 1, "Anzahl gefilterter Accounts nicht i.O.");
    }

    /**
     * Testmethode fuer {@link CheckAccountsCommand#getAccountId(Integer, boolean)}.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = FindException.class)
    public void getAccountId_NoAccountIdAndAccountNecessary() throws Exception {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setIntAccountId(null);
        Long auftragId = 12341234L;
        doReturn(auftragTechnik).when(spy).getAuftragTechnikTx(Mockito.eq(auftragId));
        spy.getAccountId(auftragId, true);
    }

    /**
     * Testmethode fuer {@link CheckAccountsCommand#getAccountId(Integer, boolean)}.
     *
     * @throws Exception
     */
    @Test
    public void getAccountId_AccountIdButNotNecessary() throws FindException {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final AuftragTechnik auftragTechnik = new AuftragTechnik();
        final Long accountId = 97979L;
        auftragTechnik.setIntAccountId(accountId);
        Long auftragId = 12341234L;
        doReturn(auftragTechnik).when(spy).getAuftragTechnikTx(Mockito.eq(auftragId));
        Long result = spy.getAccountId(auftragId, false);
        assertNotNull(result);
        assertEquals(result, accountId);
    }

    /**
     * Testmethode fuer {@link CheckAccountsCommand#getAccountId(Integer, boolean)}.
     *
     * @throws Exception
     */
    @Test
    public void getAccountId_AccountIdAndNecessary() throws FindException {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final AuftragTechnik auftragTechnik = new AuftragTechnik();
        final Long accountId = 97979L;
        auftragTechnik.setIntAccountId(accountId);
        Long auftragId = 12341234L;
        doReturn(auftragTechnik).when(spy).getAuftragTechnikTx(Mockito.eq(auftragId));
        Long result = spy.getAccountId(auftragId, true);
        assertNotNull(result);
        assertEquals(result, accountId);
    }

    /**
     * Testmethode fuer {@link CheckAccountsCommand#execute()}. Es wird kein {@link Account} gefunden. Allerdings wird
     * auch keiner benoetigt.
     *
     * @throws Exception
     */
    @Test
    public void execute_NoAccountButNotNecessary() throws Exception {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final Long auftragId = 1234L;
        doReturn(auftragId).when(spy).getAuftragId();
        final boolean isAccountNecessary = false;
        doReturn(isAccountNecessary).when(spy).isAccountNecessary();
        final Long accountId = 12341234L;
        doReturn(accountId).when(spy).getAccountId(Mockito.eq(auftragId), Mockito.eq(isAccountNecessary));
        doReturn(null).when(spy).getAccount(Mockito.eq(accountId));

        Object result = spy.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }

    /**
     * Testmethode fuer {@link CheckAccountsCommand#execute()}. Es wird kein {@link Account} gefunden. Nachdem einer
     * gebraucht wird endet das in einem Fehler.
     *
     * @throws Exception
     */
    @Test
    public void execute_NoAccountButNecessary() throws Exception {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final Long auftragId = 1234L;
        doReturn(auftragId).when(spy).getAuftragId();
        final boolean isAccountNecessary = true;
        doReturn(isAccountNecessary).when(spy).isAccountNecessary();
        final Long accountId = 12341234L;
        doReturn(accountId).when(spy).getAccountId(Mockito.eq(auftragId), Mockito.eq(isAccountNecessary));
        doReturn(null).when(spy).getAccount(Mockito.eq(accountId));

        Object result = spy.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void execute_AccountGesperrtButNecessaryAndAccountHasToBeLocked() throws Exception {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final Long auftragId = 1234L;
        doReturn(auftragId).when(spy).getAuftragId();
        final boolean isAccountNecessary = true;
        doReturn(isAccountNecessary).when(spy).isAccountNecessary();
        final Long accountId = 12341234L;
        doReturn(accountId).when(spy).getAccountId(Mockito.eq(auftragId), Mockito.eq(isAccountNecessary));
        IntAccount account = new IntAccount();
        account.setGesperrt(true);
        doReturn(account).when(spy).getAccount(Mockito.eq(accountId));
        doReturn(true).when(spy).checkMustAccountBeLocked(Mockito.eq(auftragId));

        Object result = spy.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void execute_AccountGesperrtButNecessaryAndAccountDoesNotHaveToBeLocked() throws Exception {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final Long auftragId = 1234L;
        doReturn(auftragId).when(spy).getAuftragId();
        final boolean isAccountNecessary = true;
        doReturn(isAccountNecessary).when(spy).isAccountNecessary();
        final Long accountId = 12341234L;
        doReturn(accountId).when(spy).getAccountId(Mockito.eq(auftragId), Mockito.eq(isAccountNecessary));
        IntAccount account = new IntAccount();
        account.setGesperrt(true);
        doReturn(account).when(spy).getAccount(Mockito.eq(accountId));
        doReturn(false).when(spy).checkMustAccountBeLocked(Mockito.eq(auftragId));

        Object result = spy.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void execute_AccountGesperrtButNotNecessary() throws Exception {
        CheckAccountsCommand cmd = new CheckAccountsCommand();
        CheckAccountsCommand spy = spy(cmd);
        final Long auftragId = 1234L;
        doReturn(auftragId).when(spy).getAuftragId();
        final boolean isAccountNecessary = false;
        doReturn(isAccountNecessary).when(spy).isAccountNecessary();
        final Long accountId = 12341234L;
        doReturn(accountId).when(spy).getAccountId(Mockito.eq(auftragId), Mockito.eq(isAccountNecessary));
        IntAccount account = new IntAccount();
        account.setGesperrt(true);
        doReturn(account).when(spy).getAccount(Mockito.eq(accountId));

        Object result = spy.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }


} // end
