/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2004 11:02:31
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;

/**
 * TestNG-Test fuer <code>AccountService</code>.
 */
@Test(groups = { "service" })
public class AccountServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(AccountServiceTest.class);

    @DataProvider(name = "testIsAccountMovableDP")
    public Object[][] testIsAccountMovableDP() {
        //@formatter:off
        return new Object[][] {
                { 9999999999L, 9999999999L, true, false, true},
                { 9999999999L, 9999999998L, true, false, false},
                { 9999999999L, 9999999999L, false, false, false},
                { 9999999999L, 9999999998L, false, false, false},
                { 9999999999L, 9999999999L, false, true, false},
        };
      //@formatter:on
    }

    @Test(dataProvider = "testIsAccountMovableDP")
    public void testIsAccountMovable(Long kundenNoOld, Long kundeNoNew, boolean newIntAccount,
            boolean accountAlreadyMoved, boolean expectedResult)
            throws StoreException {
        //@formatter:off
        AuftragBuilder auftragOldBuilder = getBuilder(AuftragBuilder.class)
                .withKundeNo(kundenNoOld);
        AuftragBuilder auftragNewBuilder = getBuilder(AuftragBuilder.class)
                .withKundeNo(kundeNoNew);
        IntAccountBuilder accountOldBuilder = getBuilder(IntAccountBuilder.class)
                .withLiNr(IntAccount.LINR_EINWAHLACCOUNT);
        IntAccountBuilder accountNewBuilder = getBuilder(IntAccountBuilder.class)
                .withLiNr(IntAccount.LINR_EINWAHLACCOUNT);
        AuftragTechnik auftragTechnikOld = getBuilder(AuftragTechnikBuilder.class)
                .withIntAccountBuilder(accountOldBuilder)
                .withAuftragBuilder(auftragOldBuilder)
                .build();
        AuftragTechnik auftragTechnikNew = getBean(AuftragTechnikBuilder.class)
                .withIntAccountBuilder(newIntAccount ? accountNewBuilder : null)
                .withAuftragBuilder(auftragNewBuilder)
                .build();

        if(accountAlreadyMoved){
            auftragTechnikNew.setIntAccountId(auftragTechnikOld.getIntAccountId());
        }

        //@formatter:on
        final boolean accountMovable = getAccountService().isAccountMovable(auftragTechnikNew.getAuftragId(),
                auftragTechnikOld.getAuftragId());
        assertEquals(accountMovable, expectedResult);
    }

    /**
     * Test fuer die Methode InternetService#createIntAccount(String, Integer)
     */
    public void testCreateIntAccount() throws StoreException {
        IntAccount result = getAccountService().createIntAccount(null, "XY", IntAccount.LINR_EINWAHLACCOUNT);
        Assert.assertNotNull(result, "Es wurde kein Account erstellt!");
        Assert.assertNotNull(result.getId(), "Der erstellte Account besitzt keine ID!");
        Assert.assertNotNull(result.getAccount(), "Dem Account wurde kein Account-Name zugeordnet!");
        Assert.assertEquals(result.getAccount().length(), 10, "Account-Laenge besitzt nicht die erwartete Laenge!");
        Assert.assertTrue(result.getAccount().indexOf(".") == -1, "Account enthaelt ungueltiges Zeichen!");
        Assert.assertNotNull(result.getPasswort(), "Dem Account wurde kein Passwort zugeordnet!");
        Assert.assertTrue(IntAccount.LINR_EINWAHLACCOUNT.equals(result.getLiNr()), "Der Account-Typ ist nicht korrekt!");
        LOGGER.debug("Account ID - Name - Passwort: " + result.getId() + " - " + result.getAccount() + " - "
                + result.getPasswort());
    }

    /**
     * Test fuer die Methode InternetService#saveIntAccount(String, boolean)
     */
    public void testSaveIntAccountWithoutHistory() throws StoreException, FindException {
        saveIntAccount(false);
    }

    /**
     * Test fuer die Methode InternetService#saveIntAccount(String, boolean)
     */
    public void testSaveIntAccountWithHistory() throws StoreException, FindException {
        saveIntAccount(true);
    }

    private void saveIntAccount(boolean withHistory) throws StoreException, FindException {
        IntAccount origIntAccount = getAccountService().createIntAccount(null, "XY", IntAccount.LINR_EINWAHLACCOUNT);
        flushAndClear(); // required, since the 'origIntAccount' is evicted inside saveIntAccount

        String bemerkung = "Some other comment";
        origIntAccount.setBemerkung(bemerkung);
        IntAccount savedIntAccount = getAccountService().saveIntAccount(origIntAccount, withHistory);

        origIntAccount = getAccountService().findIntAccountById(origIntAccount.getId());
        if(withHistory) {
            // check that the account was historised and that the new intAccount was returned
            assertEquals(savedIntAccount.getGueltigVon(), origIntAccount.getGueltigBis());
            assertNotEquals(savedIntAccount.getId(), origIntAccount.getId());
        }
        // check that the bemerkung was overwritten
        assertEquals(savedIntAccount.getBemerkung(), bemerkung);
    }

    /**
     * Test fuer die Methode InternetService#findIntAccounts4Auftrag(Long)
     */
    public void testFindIntAccounts4Auftrag() throws Exception {
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class).build();
        List<IntAccount> result = getAccountService().findIntAccounts4Auftrag(auftragTechnik.getAuftragId());
        assertNotEmpty(result, "Es wurden keine Accounts fuer den Auftrag gefunden!");
        for (IntAccount acc : result) {
            acc.debugModel(LOGGER);
        }
    }

    /**
     * Test fuer die Methode InternetService#findAccount(String, Integer)
     */
    public void testFindAccount() throws Exception {
        // Verwaltungsaccount suchen
        IntAccount intAccount = getBuilder(IntAccountBuilder.class).withLiNr(IntAccount.LINR_VERWALTUNGSACCOUNT)
                .build();
        IntAccount result = getAccountService().findIntAccount(intAccount.getAccount(),
                IntAccount.LINR_VERWALTUNGSACCOUNT);
        Assert.assertNotNull(result, "Es wurde kein Verwaltungsaccount fuer den Kunden  gefunden!");
        LOGGER.debug("Account-ID: " + result.getId());
    }

    public void testGetAccountRealmADSL() throws FindException {
        String adslRealm = "mdsl.mnet-online.de";
        String expectedRealm = "@" + adslRealm;
        AuftragTechnikBuilder auftragTechnikBuilder = createGetAccountRealmObjects(adslRealm, false, null);

        String realm = getAccountService().getAccountRealm(auftragTechnikBuilder.get().getAuftragId());
        assertEquals(realm, expectedRealm, "ADSL REALM not as expected");
    }

    public void testGetAccountRealmSDSL() throws FindException {
        String sdslRealm = "dsl.mnet-online.de";
        String expectedRealm = "@" + sdslRealm;

        AuftragTechnikBuilder auftragTechnikBuilder = createGetAccountRealmObjects(sdslRealm, false, null);

        String realm = getAccountService().getAccountRealm(auftragTechnikBuilder.get().getAuftragId());
        assertEquals(realm, expectedRealm, "SDSL REALM not as expected");
    }

    public void testGetAccountRealmVPN() throws FindException {
        String baseRealm = "bmw123";
        String expectedRealm = "@" + baseRealm;
        AuftragTechnikBuilder auftragTechnikBuilder = createGetAccountRealmObjects(baseRealm, true, baseRealm);

        String realm = getAccountService().getAccountRealm(auftragTechnikBuilder.get().getAuftragId());
        assertEquals(realm, expectedRealm, "VPN REALM not as expected");
    }

    public void testGetAccountRealmL2TP() throws FindException {
        String l2tpRealm = "#mnet@ipx-dsl.de";
        String expectedRealm = "%dsl.mnet-online.de" + l2tpRealm;
        AuftragTechnikBuilder auftragTechnikBuilder = createGetAccountRealmObjects(l2tpRealm, false, null);

        String realm = getAccountService().getAccountRealm(auftragTechnikBuilder.get().getAuftragId());
        LOGGER.debug("L2TP REALM: " + realm);
        assertEquals(realm, expectedRealm, "L2TP REALM not as expected");
    }

    public void testGetAccountRealmL2tpVPN() throws FindException {
        String l2tpRealm = "#mnet@ipx-dsl.de";
        String expectedRealm = "%bmw123" + l2tpRealm;
        AuftragTechnikBuilder auftragTechnikBuilder = createGetAccountRealmObjects(l2tpRealm, true, "bmw123");

        String realm = getAccountService().getAccountRealm(auftragTechnikBuilder.get().getAuftragId());
        assertEquals(realm, expectedRealm, "L2TP VPN REALM not as expected");
    }

    private AuftragTechnikBuilder createGetAccountRealmObjects(String baseRealm, boolean withVPN, String vpnRealm) {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class).withProduktGruppeBuilder(
                getBuilder(ProduktGruppeBuilder.class).withRealm(baseRealm));

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class).withProdBuilder(prodBuilder));

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(
                auftragBuilder);
        if (withVPN) {
            auftragTechnikBuilder.withVPNBuilder(getBuilder(VPNBuilder.class).withVpnNr(Long.valueOf(123456))
                    .withDatum(new Date()).withKundeNo(Long.valueOf(111)).withRealm(vpnRealm));
        }

        return auftragTechnikBuilder;
    }

    public void testIntAccount_EvnStatus() throws Exception {
        final IntAccount account = getAccountService().createIntAccount(null, "XY", IntAccount.LINR_EINWAHLACCOUNT);
        Assert.assertNotNull(account);
        Assert.assertNotNull(account.getId());
        Assert.assertNotNull(account.getAccount());
        Assert.assertNull(account.getEvnStatus());
        Assert.assertNull(account.getEvnStatusPending());

        final IntAccount foundAccount = getAccountService().findIntAccount(account.getAccount());
        Assert.assertNotNull(foundAccount);
        Assert.assertNotNull(foundAccount.getId());
        Assert.assertNotNull(foundAccount.getAccount());
        Assert.assertNull(foundAccount.getEvnStatus());
        Assert.assertNull(foundAccount.getEvnStatusPending());

        foundAccount.setEvnStatus(false);
        foundAccount.setEvnStatusPending(true);
        getAccountService().saveIntAccount(foundAccount, false);

        final IntAccount changedAccount = getAccountService().findIntAccount(account.getAccount());
        Assert.assertNotNull(changedAccount);

        Assert.assertNotNull(changedAccount.getEvnStatus());
        Assert.assertFalse(changedAccount.getEvnStatus());
        Assert.assertNotNull(changedAccount.getEvnStatusPending());
        Assert.assertTrue(changedAccount.getEvnStatusPending());

    }

    private AccountService getAccountService() {
        return getCCService(AccountService.class);
    }
}
