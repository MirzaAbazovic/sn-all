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
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.EQCrossConnectionBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.model.cc.IpRouteBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusIPv4Data;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusIPv6Data;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusRoutesData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.IPAddressService;


/**
 * Unit Test for {@link CPSGetRadiusDataCommandTest}
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class CPSGetRadiusDataCommandTest extends AbstractHurricanBaseServiceTest {

    private LeistungService billingLeistungService;
    private IPAddressService ipAddressService;

    private static final Long TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String ACCOUNT_NAME = "myaccount";
    private static final String ACCOUNT_PW = "geheim";
    private static final String REALM = "realm.m-net.de";
    private static final String PRODUCT_DATA_RATE_DOWN = "3000";
    private static final String PROFILE_DATA_RATE_DOWN = "1152";
    private static final Integer BRAS_OUTER = 8;
    private static final Integer BRAS_INNER = 100;
    private static final String RADIUS_PORT_ID = "8_100";
    private static final String IP_ADDRESS_V4 = "80.81.0.7";
    private static final String NET_IP_V4 = "192.168.1.0/24";
    private static final String IP_ADDRESS_V6 = "2001:a60:a004::/48";
    private static final String NET_IP_V6 = "2001:db8:a001::/48";
    private static final String IP_ADDRESS_V6R = "::1:0:0:0:1/64";
    private static final String IP_ADDRESS_V6_ABSOLUTE = "2001:db8:a001:1::1/64";

    private static final String ACCOUNT_TYPE = "MAXI-HSI";

    private IpRoute ipRoute;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = BaseTest.SERVICE, dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
    }

    private AuftragBuilder buildBase(Date date, Boolean cpsIPDefault, boolean mitIpV4Leisung, boolean mitIpV6Leisung) {
        ProduktGruppeBuilder produktGruppeBuilder = getBuilder(ProduktGruppeBuilder.class)
                .withRealm(REALM);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withProduktGruppeBuilder(produktGruppeBuilder)
                .withCpsAccountType(ACCOUNT_TYPE)
                .withCpsDSLProduct(Boolean.TRUE)
                .withCpsIPDefault(cpsIPDefault);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilder)
                .withProdBuilder(produktBuilder);
        auftragDatenBuilder.build();

        if (mitIpV6Leisung) {
            Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder2 = getBuilder(Auftrag2TechLeistungBuilder.class);
            auftrag2TechLeistungBuilder2.withAuftragId(auftragDatenBuilder.get().getAuftragId());
            auftrag2TechLeistungBuilder2.withTechLeistungId(TechLeistung.ID_DYNAMIC_IP_V6);
            auftrag2TechLeistungBuilder2.build();
        }
        if (mitIpV4Leisung) {
            Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder2 = getBuilder(Auftrag2TechLeistungBuilder.class);
            auftrag2TechLeistungBuilder2.withAuftragId(auftragDatenBuilder.get().getAuftragId());
            auftrag2TechLeistungBuilder2.withTechLeistungId(TechLeistung.ID_DYNAMIC_IP_V4);
            auftrag2TechLeistungBuilder2.build();
        }

        DSLAMProfileBuilder dslamProfileBuilderAct = getBuilder(DSLAMProfileBuilder.class).withRandomId().withBandwidth(1152);
        DSLAMProfileBuilder dslamProfileBuilderOld = getBuilder(DSLAMProfileBuilder.class).withRandomId().withBandwidth(3072);

        // DSLAM-Profil zuordnen
        // aktuelles Profil
        getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withDSLAMProfileBuilder(dslamProfileBuilderAct)
                .withGueltigVon(date)
                .withGueltigBis(DateTools.getHurricanEndDate())
                .build();
        // Profil-History
        getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withDSLAMProfileBuilder(dslamProfileBuilderOld)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(date)
                .build();

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

        // IpRoute
        IPAddress prefix = getBuilder(IPAddressBuilder.class)
                .withAddress(NET_IP_V6)
                .withNetId(1L)
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .build();
        IPAddressBuilder ipAddressBuilder = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV6_relative)
                .withAddress(IP_ADDRESS_V6R)
                .withPrefixRef(prefix);
        ipRoute = getBuilder(IpRouteBuilder.class)
                .withIpAddressRefBuilder(ipAddressBuilder)
                .withAuftragBuilder(auftragBuilder)
                .withPrefixLength(64L)
                .build();

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
        return auftragBuilder;
    }

    private TechLeistungBuilder buildTechLeistungDownstream(Date date) {
        // TechLeistung Downstream anlegen
        TechLeistungBuilder downstreamTLBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_DOWNSTREAM)
                .withLongValue(3000L)
                .withProdNameStr(PRODUCT_DATA_RATE_DOWN)
                .withRandomName()
                .withGueltigVon(date);
        downstreamTLBuilder.build();
        // Downstream History
        TechLeistungBuilder downstreamHistoryTLBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_DOWNSTREAM)
                .withLongValue(2000L)
                .withProdNameStr("2000")
                .withRandomName()
                .withGueltigBis(date);
        downstreamHistoryTLBuilder.build();

        return downstreamTLBuilder;
    }

    private TechLeistungBuilder buildTechLeistungOnlineIP(Date date) {
        // TechLeistung Downstream anlegen
        TechLeistungBuilder downstreamTLBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_ONLINE_IP)
                .withRandomName()
                .withGueltigVon(date);
        downstreamTLBuilder.build();
        // Downstream History
        TechLeistungBuilder downstreamHistoryTLBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_ONLINE_IP)
                .withRandomName()
                .withGueltigBis(date);
        downstreamHistoryTLBuilder.build();

        return downstreamTLBuilder;
    }

    private TechLeistungBuilder buildTechLeistungOnlineIPTomorrow() {
        // TechLeistung Downstream anlegen
        Date tomorrow = DateTools.changeDate(new Date(), Calendar.DATE, 1);
        TechLeistungBuilder downstreamTLBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_ONLINE_IP)
                .withRandomName()
                .withGueltigVon(tomorrow);
        downstreamTLBuilder.build();

        return downstreamTLBuilder;
    }

    private CPSTransaction buildCPSTx(AuftragBuilder auftragBuilder, TechLeistungBuilder techLeistungBuilder) throws FindException {
        // Auftrag2TechLs
        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withTechleistungBuilder(techLeistungBuilder)
                .withAktivVon(techLeistungBuilder.get().getGueltigVon())
                .build();

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withOrderNoOrig(TAIFUN_ORDER__NO)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        // Services to mock
        createBillingMocks(cpsTx);
        createIPMocks(cpsTx);

        return cpsTx;
    }

    /* Mocks fuer den Billing Bereich erstellen */
    private void createBillingMocks(CPSTransaction cpsTx) throws FindException {
        billingLeistungService = mock(LeistungService.class);
        when(billingLeistungService.getUDRTarifType4Auftrag(TAIFUN_ORDER__NO, cpsTx.getEstimatedExecTime()))
                .thenReturn(Leistung.LEISTUNG_VOL_TYPE_VOLUME);
    }

    /* Mocks fuer den IP Bereich erstellen */
    private void createIPMocks(CPSTransaction cpsTx) throws FindException {
        ipAddressService = mock(IPAddressService.class);
        IPAddress fixedIpAddressV4 = new IPAddressBuilder().withAddress(IP_ADDRESS_V4)
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        IPAddress fixedIpAddressV6 = new IPAddressBuilder().withAddress(IP_ADDRESS_V6)
                .withAddressType(AddressTypeEnum.IPV6_prefix).withNetId(1L).setPersist(false).build();
        when(ipAddressService.findFixedIPs4TechnicalOrder(cpsTx.getAuftragId())).thenReturn(
                Arrays.asList(fixedIpAddressV4));
        when(ipAddressService.findDHCPv6PDPrefix4TechnicalOrder(cpsTx.getAuftragId())).thenReturn(fixedIpAddressV6);

        IPAddress netIpAddressV4 = new IPAddressBuilder().withAddress(NET_IP_V4)
                .withAddressType(AddressTypeEnum.IPV4_prefix).withNetId(2L).setPersist(false).build();
        IPAddress netIpAddressV6 = new IPAddressBuilder().withAddress(NET_IP_V6)
                .withAddressType(AddressTypeEnum.IPV6_prefix).withNetId(3L).setPersist(false).build();
        // Arrays.asList() liefer ein Arrays$ArrayList Objekt, das nicht alle
        // Operationen einer 'richtigen' Liste unterstützt -> Workaround mit
        // 'richtiger' ArrayList um fuer den CollectionUtils.filter() keine
        // OperationNotSupported Exception zu bekommen.
        List<IPAddress> nets = new ArrayList<>();
        nets.addAll(Arrays.asList(netIpAddressV4, netIpAddressV6, fixedIpAddressV6));
        when(ipAddressService.findNets4TechnicalOrder(cpsTx.getAuftragId())).thenReturn(nets);
    }

    @Test(groups = { BaseTest.SERVICE })
    public void testDownstream() throws Exception {
        Date yesterday = DateTools.changeDate(new Date(), Calendar.DATE, -1);

        AuftragBuilder auftragBuilder = buildBase(yesterday, Boolean.FALSE, false, false);
        TechLeistungBuilder techLeistungBuilder = buildTechLeistungDownstream(yesterday);
        CPSTransaction cpsTx = buildCPSTx(auftragBuilder, techLeistungBuilder);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusDataCommand command = (CPSGetRadiusDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusDataCommand");
        command.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().withRandomAuftragId().build());
        command.setBillingLeistungsService(billingLeistungService);
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
        assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        assertEquals(radiusAccount.getRealm(), REALM, "REALM not as expected");
        assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        assertEquals(radiusAccount.getAlwaysOn(), "0", "always on not as expected");
        assertEquals(radiusAccount.getProductDataRateDown(), PRODUCT_DATA_RATE_DOWN, "PRODUCT_DATA_RATE_DOWN not as expected");
        assertEquals(radiusAccount.getProfileDataRateDown(), PROFILE_DATA_RATE_DOWN, "PROFILE_DATA_RATE_DOWN name not as expected");
        assertEquals(radiusAccount.getPortId(), RADIUS_PORT_ID, "Radius Port ID has not the expected value!");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNull(ipV4, "IP v4 Address present where none should be found!");
    }

    @DataProvider
    public Object[][] testOnlineIPData() {
        return new Object[][] {
                { CPSGetRadiusDataCommand.IP_V4, false, false },
                { CPSGetRadiusDataCommand.IP_V4, true, false },
                { CPSGetRadiusDataCommand.IP_V6_DS_LITE, false, true },
                { CPSGetRadiusDataCommand.IP_V6_DS, true, true }
        };
    }

    @Test(dataProvider = "testOnlineIPData", groups = BaseTest.SERVICE)
    public void testOnlineIP(String ipMode, boolean mitIpV4Leisung, boolean mitIpV6Leisung) throws Exception {
        Date yesterday = DateTools.changeDate(new Date(), Calendar.DATE, -1);

        AuftragBuilder auftragBuilder = buildBase(yesterday, Boolean.FALSE, mitIpV4Leisung, mitIpV6Leisung);
        TechLeistungBuilder techLeistungBuilder = buildTechLeistungOnlineIP(yesterday);
        CPSTransaction cpsTx = buildCPSTx(auftragBuilder, techLeistungBuilder);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusDataCommand command = (CPSGetRadiusDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusDataCommand");
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
        assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        assertEquals(radiusAccount.getAlwaysOn(), "0", "always on not as expected");
        assertEquals(radiusAccount.getProfileDataRateDown(), PROFILE_DATA_RATE_DOWN, "PROFILE_DATA_RATE_DOWN name not as expected");
        assertEquals(radiusAccount.getPortId(), RADIUS_PORT_ID, "Radius Port ID has not the expected value!");
        assertEquals(radiusAccount.getIpMode(), ipMode);

        //V4
        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNotNull(ipV4, "IP v4 Address not found!");
        assertEquals(ipV4.getFixedIPv4(), IP_ADDRESS_V4, "IP v4 address not as expected!");
        assertNotEmpty(ipV4.getRoutes(), "Keine Routen definiert!");
        assertEquals(ipV4.getRoutes().size(), 1);
        List<CPSRadiusRoutesData> routes = ipV4.getRoutes();
        for (CPSRadiusRoutesData cpsRadiusRoute : routes) {
            if (StringUtils.equals(cpsRadiusRoute.getIpV4Address(), NET_IP_V4)) {
                assertEquals(cpsRadiusRoute.getMetric(), Long.valueOf(0));
                assertEquals(cpsRadiusRoute.getPrefixLength(), Long.valueOf(24));
            }
            else {
                fail("IP of route not expected! IP: " + cpsRadiusRoute.getIpV4Address());
            }
        }

        //V6
        CPSRadiusIPv6Data ipV6 = radiusAccount.getIpv6();
        assertNotNull(ipV6, "IP V6 Address not found!");
        assertEquals(ipV6.getFixedIPv6(), IP_ADDRESS_V6, "IP v6 address not as expected!");
        assertNotEmpty(ipV6.getRoutes(), "Keine Routen definiert!");
        assertEquals(ipV6.getRoutes().size(), 2);
        routes = ipV6.getRoutes();
        for (CPSRadiusRoutesData cpsRadiusRoute : routes) {
            if (StringUtils.equals(cpsRadiusRoute.getIpV6Address(), NET_IP_V6)) {
                assertEquals(cpsRadiusRoute.getMetric(), Long.valueOf(0));
                assertEquals(cpsRadiusRoute.getPrefixLength(), Long.valueOf(48));
            }
            else if (StringUtils.equals(cpsRadiusRoute.getIpV6Address(), IP_ADDRESS_V6_ABSOLUTE)) {
                assertEquals(cpsRadiusRoute.getMetric(), ipRoute.getMetrik());
                assertEquals(cpsRadiusRoute.getPrefixLength(), Long.valueOf(64));
            }
            else {
                fail("IP of route not expected! IP: " + cpsRadiusRoute.getIpV4Address());
            }
        }
    }

    @Test(groups = { BaseTest.SERVICE })
    public void testOnlineIPTomorrow() throws Exception {
        Date yesterday = DateTools.changeDate(new Date(), Calendar.DATE, -1);

        AuftragBuilder auftragBuilder = buildBase(yesterday, Boolean.FALSE, false, false);
        TechLeistungBuilder techLeistungBuilder = buildTechLeistungOnlineIPTomorrow();
        CPSTransaction cpsTx = buildCPSTx(auftragBuilder, techLeistungBuilder);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusDataCommand command = (CPSGetRadiusDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusDataCommand");
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
        assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        assertEquals(radiusAccount.getAlwaysOn(), "0", "always on not as expected");
        assertEquals(radiusAccount.getProfileDataRateDown(), PROFILE_DATA_RATE_DOWN, "PROFILE_DATA_RATE_DOWN name not as expected");
        assertEquals(radiusAccount.getPortId(), RADIUS_PORT_ID, "Radius Port ID has not the expected value!");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNull(ipV4, "IP v4 Address present where none should be found!");
    }

    @Test(groups = { BaseTest.SERVICE })
    public void testCPSIPDefault() throws Exception {
        Date yesterday = DateTools.changeDate(new Date(), Calendar.DATE, -1);

        AuftragBuilder auftragBuilder = buildBase(yesterday, Boolean.TRUE, false, false);
        TechLeistungBuilder techLeistungBuilder = buildTechLeistungDownstream(yesterday);
        CPSTransaction cpsTx = buildCPSTx(auftragBuilder, techLeistungBuilder);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetRadiusDataCommand command = (CPSGetRadiusDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusDataCommand");
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
        assertEquals(radiusAccount.getUserName(), ACCOUNT_NAME, "Account name not as expected");
        assertEquals(radiusAccount.getPassword(), ACCOUNT_PW, "Account password not as expected");
        assertEquals(radiusAccount.getTarif(), CPSRadiusAccountData.TARIF_VOLUME, "Tarif type not as expected");
        assertEquals(radiusAccount.getAccountType(), ACCOUNT_TYPE, "Account type not as expected");
        assertEquals(radiusAccount.getAlwaysOn(), "0", "always on not as expected");
        assertEquals(radiusAccount.getProductDataRateDown(), PRODUCT_DATA_RATE_DOWN, "PRODUCT_DATA_RATE_DOWN not as expected");
        assertEquals(radiusAccount.getProfileDataRateDown(), PROFILE_DATA_RATE_DOWN, "PROFILE_DATA_RATE_DOWN name not as expected");
        assertEquals(radiusAccount.getPortId(), RADIUS_PORT_ID, "Radius Port ID has not the expected value!");

        CPSRadiusIPv4Data ipV4 = radiusAccount.getIpv4();
        assertNotNull(ipV4, "IP v4 Address not found!");
        assertEquals(ipV4.getFixedIPv4(), IP_ADDRESS_V4, "IP v4 address not as expected!");
    }

} // end
