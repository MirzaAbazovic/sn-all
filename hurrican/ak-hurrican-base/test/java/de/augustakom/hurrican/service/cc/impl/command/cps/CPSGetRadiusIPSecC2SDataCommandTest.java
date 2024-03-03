/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusIPv4Data;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Integration test for {@link CPSGetRadiusIPSecC2SDataCommandTest}.
 *
 *
 */
public class CPSGetRadiusIPSecC2SDataCommandTest extends AbstractHurricanBaseServiceTest {

    private IPAddressService ipAddressService;

    private static final String ACCOUNT_NAME = "myaccount";
    private static final String ACCOUNT_PW = "geheim";
    private static final String REALM = "realm.m-net.de";
    private static final String PRODUCT_NAME_IPSEC = "IPSEC_CLIENT2SITE";
    private static final String ACCOUNT_TYPE = "IPSEC_C2S";
    private static final long VPN_NR = 888888;
    private static final String IP_ADDRESS = "80.81.0.7";

    private CPSTransaction cpsTx;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
    }

    private AuftragBuilder createTestData(String productName) throws FindException {
        ProduktGruppeBuilder produktGruppeBuilder = getBuilder(ProduktGruppeBuilder.class)
                .withRealm(REALM);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withProduktGruppeBuilder(produktGruppeBuilder)
                .withCpsAccountType(ACCOUNT_TYPE)
                .withCpsProductName(productName)
                .withCpsDSLProduct(Boolean.TRUE);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        Long taifunOrderNo = AuftragDatenBuilder.getLongId();
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(taifunOrderNo)
                .withAuftragBuilder(auftragBuilder)
                .withProdBuilder(produktBuilder);
        auftragDatenBuilder.build();

        // IntAccount
        IntAccountBuilder intAccountBuilder = getBuilder(IntAccountBuilder.class)
                .withAccount(ACCOUNT_NAME)
                .withPasswort(ACCOUNT_PW)
                .withLiNr(IntAccount.LINR_EINWAHLACCOUNT_KONFIG);

        // AuftragTechnik (fuer IntAccount-Zuordnung)
        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withIntAccountBuilder(intAccountBuilder)
                .withVPNBuilder(getBuilder(VPNBuilder.class)
                        .withVpnNr(VPN_NR))
                .build();

        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withOrderNoOrig(taifunOrderNo)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        // Services to mock
        createIPMocks();

        return auftragBuilder;
    }

    /**
     * Mocks fuer den IP Bereich erstellen
     */
    private void createIPMocks() throws FindException {
        ipAddressService = mock(IPAddressService.class);
        IPAddress address = new IPAddressBuilder()
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress(IP_ADDRESS)
                .setPersist(false)
                .build();
        when(ipAddressService.findFixedIPs4TechnicalOrder(cpsTx.getAuftragId()))
                .thenReturn(Arrays.asList(address));
    }

    /**
     * Testmethode fuer {@link CPSGetRadiusIPSecC2SDataCommand#execute()}.
     */
    @Test(groups = { BaseTest.SERVICE })
    public void testExecute() throws Exception {
        createTestData(PRODUCT_NAME_IPSEC);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusIPSecC2SDataCommand command = (CPSGetRadiusIPSecC2SDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusIPSecC2SDataCommand");
        command.setIpAddressService(ipAddressService);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotNull(cpsServiceOrderData.getRadius(), "No radius data found!");

        // analyse result
        CPSRadiusData radius = cpsServiceOrderData.getRadius();
        assertNotNull(radius.getRadiusAccount(), "No radius account generated!");

        CPSRadiusAccountData radiusAccount = radius.getRadiusAccount();
        Assert.assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        Assert.assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        Assert.assertEquals(radiusAccount.getRealm(), REALM, "REALM not as expected");
        Assert.assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        Assert.assertNull(radiusAccount.getAlwaysOn(), "always on not expected");
        Assert.assertNull(radiusAccount.getProfileDataRateDown(), "PROFILE_DATA_RATE_DOWN name not expected");
        Assert.assertNull(radiusAccount.getTarif(), "Tarif type not expected");
        Assert.assertNotNull(radiusAccount.getVpnId(), "VPN ID not defined!");
        Assert.assertEquals(radiusAccount.getVpnId(), "" + VPN_NR, "VPN ID not as expected!");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNotNull(ipV4, "IP v4 Address not found!");
        Assert.assertEquals(ipV4.getFixedIPv4(), IP_ADDRESS, "IP v4 address not as expected!");
    }

} // end
