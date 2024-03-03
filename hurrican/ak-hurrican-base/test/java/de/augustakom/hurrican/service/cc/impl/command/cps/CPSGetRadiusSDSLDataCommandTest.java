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
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.EQCrossConnectionBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusIPv4Data;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.IPAddressService;


/**
 * Tests for {@link CPSGetRadiusSDSLDataCommandTest}.
 *
 *
 */
public class CPSGetRadiusSDSLDataCommandTest extends AbstractHurricanBaseServiceTest {

    private LeistungService billingLeistungService;
    private IPAddressService ipAddressService;

    private static final Long TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String ACCOUNT_NAME = "myaccount";
    private static final String ACCOUNT_PW = "geheim";
    private static final String PRODUCT_NAME_SDSL_2233 = "SDSL_2233";
    private static final String PRODUCT_NAME_SDSL_VPN = "SDSL_VPN";
    private static final String PRODUCT_DATA_RATE_DOWN = "2233";
    private static final Long DOWNSTREAM_TECH_LS = Long.valueOf(9000);
    private static final Integer BRAS_OUTER = Integer.valueOf(8);
    private static final Integer BRAS_INNER = Integer.valueOf(100);

    private static final String ACCOUNT_TYPE = "SDSL-HSI";

    private CPSTransaction cpsTx;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
    }

    private AuftragBuilder createTestData(String productName) throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withCpsAccountType(ACCOUNT_TYPE)
                .withCpsProductName(productName)
                .withCpsDSLProduct(Boolean.TRUE)
                .withCpsIPDefault(Boolean.TRUE);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilder)
                .withProdBuilder(produktBuilder);
        auftragDatenBuilder.build();

        // IntAccount
        IntAccount intAccount = getBuilder(IntAccountBuilder.class)
                .withAccount(ACCOUNT_NAME)
                .withPasswort(ACCOUNT_PW)
                .withLiNr(IntAccount.LINR_EINWAHLACCOUNT)
                .build();

        // AuftragTechnik (fuer IntAccount-Zuordnung)
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withIntAccountId(intAccount.getId());
        auftragTechnikBuilder.build();

        EquipmentBuilder eqBuilder = getBuilder(EquipmentBuilder.class);
        getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(eqBuilder)
                .withBrasInner(BRAS_INNER)
                .withBrasOuter(BRAS_OUTER)
                .build();

        getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqInBuilder(eqBuilder))
                .build();

        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withOrderNoOrig(TAIFUN_ORDER__NO)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        // Services to mock
        createBillingMocks();
        createIPMocks();

        return auftragBuilder;
    }

    /* Mocks fuer den Billing Bereich erstellen */
    private void createBillingMocks() throws FindException {
        billingLeistungService = mock(LeistungService.class);
        when(billingLeistungService.getUDRTarifType4Auftrag(TAIFUN_ORDER__NO, cpsTx.getEstimatedExecTime()))
                .thenReturn(Leistung.LEISTUNG_VOL_TYPE_VOLUME);
    }

    /* Mocks fuer den IP Bereich erstellen */
    private void createIPMocks() throws FindException {
        ipAddressService = mock(IPAddressService.class);
        IPAddress firstIpAddress = new IPAddressBuilder().withAddress("80.81.0.7")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        IPAddress secondIpAddress = new IPAddressBuilder().withAddress("192.168.1.1")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        when(ipAddressService.findFixedIPs4TechnicalOrder(cpsTx.getAuftragId())).thenReturn(
                Arrays.asList(firstIpAddress, secondIpAddress));

        //        when(ipService.findIPsOnly4Auftrag(any(Integer.class))).thenReturn(ips);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecute() throws Exception {
        createTestData(PRODUCT_NAME_SDSL_2233);
        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusSDSLDataCommand command = (CPSGetRadiusSDSLDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusSDSLDataCommand");
        command.setBillingLeistungsService(billingLeistungService);
        command.setIpAddressService(ipAddressService);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);
        command.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().withRandomAuftragId().build());

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotNull(cpsServiceOrderData.getRadius(), "No radius data found!");

        // analyse result
        CPSRadiusData radius = cpsServiceOrderData.getRadius();
        assertNotNull(radius.getRadiusAccount(), "No radius account generated!");

        CPSRadiusAccountData radiusAccount = radius.getRadiusAccount();
        Assert.assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        Assert.assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        Assert.assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        Assert.assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        Assert.assertEquals(radiusAccount.getAlwaysOn(), "1", "always on not as expected");
        Assert.assertEquals(radiusAccount.getProductDataRateDown(), PRODUCT_DATA_RATE_DOWN, "PRODUCT_DATA_RATE_DOWN not as expected");
        Assert.assertEquals(radiusAccount.getProfileDataRateDown(), null, "PROFILE_DATA_RATE_DOWN name not as expected");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNotNull(ipV4, "IP v4 Address not found!");
        Assert.assertEquals(ipV4.getFixedIPv4(), "80.81.0.7", "IP v4 address not as expected!");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecuteWithDownstreamTechLs() throws Exception {
        AuftragBuilder auftragBuilder = createTestData(PRODUCT_NAME_SDSL_2233);
        // Downstream-Leistung anlegen und Auftrag zuordnen
        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withTechleistungBuilder(getBuilder(TechLeistungBuilder.class)
                        .withExternLeistungNo(Long.valueOf(-1))
                        .withTyp(TechLeistung.TYP_DOWNSTREAM)
                        .withLongValue(DOWNSTREAM_TECH_LS))
                .build();

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusSDSLDataCommand command = (CPSGetRadiusSDSLDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusSDSLDataCommand");
        command.setBillingLeistungsService(billingLeistungService);
        command.setIpAddressService(ipAddressService);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);
        command.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().withRandomAuftragId().build());

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotNull(cpsServiceOrderData.getRadius(), "No radius data found!");

        // analyse result
        CPSRadiusData radius = cpsServiceOrderData.getRadius();
        assertNotNull(radius.getRadiusAccount(), "No radius account generated!");

        CPSRadiusAccountData radiusAccount = radius.getRadiusAccount();
        Assert.assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        Assert.assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        Assert.assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        Assert.assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        Assert.assertEquals(radiusAccount.getAlwaysOn(), "1", "always on not as expected");
        Assert.assertEquals(radiusAccount.getProductDataRateDown(), "" + DOWNSTREAM_TECH_LS, "PRODUCT_DATA_RATE_DOWN not as expected");
        Assert.assertEquals(radiusAccount.getProfileDataRateDown(), null, "PROFILE_DATA_RATE_DOWN name not as expected");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNotNull(ipV4, "IP v4 Address not found!");
        Assert.assertEquals(ipV4.getFixedIPv4(), "80.81.0.7", "IP v4 address not as expected!");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecuteWithDownstreamTechLsInFuture() throws Exception {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withCpsAccountType(ACCOUNT_TYPE)
                .withCpsProductName(PRODUCT_NAME_SDSL_2233)
                .withCpsDSLProduct(Boolean.TRUE)
                .withCpsIPDefault(Boolean.TRUE);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        // Downstream-Leistung anlegen und Auftrag zuordnen

        auftragBuilder.getAuftragDatenBuilder()
                .withStatusId(AuftragStatus.ERFASSUNG_SCV)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(TAIFUN_ORDER__NO)
                .withProdBuilder(produktBuilder)
                .build();

        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAktivVon(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .withTechleistungBuilder(getBuilder(TechLeistungBuilder.class)
                                .withExternLeistungNo(Long.valueOf(-1))
                                .withTyp(TechLeistung.TYP_DOWNSTREAM)
                                .withLongValue(DOWNSTREAM_TECH_LS)
                ).build();

        // IntAccount
        IntAccount intAccount = getBuilder(IntAccountBuilder.class)
                .withAccount(ACCOUNT_NAME)
                .withPasswort(ACCOUNT_PW)
                .withLiNr(IntAccount.LINR_EINWAHLACCOUNT)
                .build();

        // AuftragTechnik (fuer IntAccount-Zuordnung)
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withIntAccountId(intAccount.getId());
        auftragTechnikBuilder.build();

        EquipmentBuilder eqBuilder = getBuilder(EquipmentBuilder.class);
        getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(eqBuilder)
                .withBrasInner(BRAS_INNER)
                .withBrasOuter(BRAS_OUTER)
                .build();

        getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqInBuilder(eqBuilder))
                .build();

        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withOrderNoOrig(TAIFUN_ORDER__NO)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        // Services to mock
        createBillingMocks();
        createIPMocks();

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusSDSLDataCommand command = (CPSGetRadiusSDSLDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusSDSLDataCommand");
        command.setBillingLeistungsService(billingLeistungService);
        command.setIpAddressService(ipAddressService);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);
        command.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().withRandomAuftragId().build());

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotNull(cpsServiceOrderData.getRadius(), "No radius data found!");

        // analyse result
        CPSRadiusData radius = cpsServiceOrderData.getRadius();
        assertNotNull(radius.getRadiusAccount(), "No radius account generated!");

        CPSRadiusAccountData radiusAccount = radius.getRadiusAccount();
        Assert.assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        Assert.assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        Assert.assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        Assert.assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        Assert.assertEquals(radiusAccount.getAlwaysOn(), "1", "always on not as expected");
        Assert.assertEquals(radiusAccount.getProductDataRateDown(), "" + DOWNSTREAM_TECH_LS, "PRODUCT_DATA_RATE_DOWN not as expected");
        Assert.assertEquals(radiusAccount.getProfileDataRateDown(), null, "PROFILE_DATA_RATE_DOWN name not as expected");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNotNull(ipV4, "IP v4 Address not found!");
        Assert.assertEquals(ipV4.getFixedIPv4(), "80.81.0.7", "IP v4 address not as expected!");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecuteWithoutDataRateDown() throws Exception {
        createTestData(PRODUCT_NAME_SDSL_VPN);
        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusSDSLDataCommand command = (CPSGetRadiusSDSLDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusSDSLDataCommand");
        command.setBillingLeistungsService(billingLeistungService);
        command.setIpAddressService(ipAddressService);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNull(cpsServiceOrderData.getRadius(), "Radius data found but should not!");
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "State of ServiceCommandResult is not invalid!");
    }

} // end
