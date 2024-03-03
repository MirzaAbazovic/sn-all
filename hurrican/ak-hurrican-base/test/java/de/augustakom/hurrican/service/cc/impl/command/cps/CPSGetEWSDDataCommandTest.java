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
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.LeistungBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Leistung2DNBuilder;
import de.augustakom.hurrican.model.cc.Leistung4DnBuilder;
import de.augustakom.hurrican.model.cc.Leistungsbuendel2ProduktBuilder;
import de.augustakom.hurrican.model.cc.LeistungsbuendelBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNServiceData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPortData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSTelephoneData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSTelephoneNumberData;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Unit Test for {@link CPSGetEWSDDataCommandTest}
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class CPSGetEWSDDataCommandTest extends AbstractHurricanBaseServiceTest {

    private RufnummerService rufnummerService;
    private LeistungService leistungService;
    private List<Rufnummer> dns4Order;

    private static final int EXPECTED_NUMBER_COUNT = 1;
    private static final int EXPECTED_PORT_COUNT = 1;

    private static final Long TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String EWSD_V5_PORT = "12345678";
    private static final String EWSD_EQN = "0010-01-07-09";

    private static final String ONKZ = "0821";
    private static final String ACT_DN_BASE = "456789";
    private static final String DN_SERVICE_PROV_NAME_ACTIVE = "CPS1";
    private static final String DN_SERVICE_VALUE_ACTIVE = "01-123456&02-456789";
    private static final String DN_SERVICE_VALUE_ACTIVE_RESULT = "01-123456;02-456789";

    private Rufnummer aktDN;
    private Long dnExtLeistungNo;

    private CPSTransaction cpsTx;

    /**
     * Initialize the tests
     *
     * @throws FindException
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withAnschlussart("test")
                .withMaxDnCount(Integer.valueOf(10))
                .withDNBlock(Boolean.FALSE);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = auftragBuilder.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(TAIFUN_ORDER__NO)
                .withProdBuilder(produktBuilder);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withEwsdOr1(Integer.valueOf(999));

        PhysikTypBuilder ptBuilder = getBuilder(PhysikTypBuilder.class)
                .withCpsTransferMethod("ADSL2+");

        RangierungBuilder rangAddBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(getBuilder(EquipmentBuilder.class)
                        .withHwEQN(EWSD_EQN)
                        .withV5Port(EWSD_V5_PORT))
                .withPhysikTypBuilder(ptBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder);

        // AuftragTechnik + Endstelle
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withRangierungAdditionalBuilder(rangAddBuilder);
        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withEndstelleBuilder(endstelleBuilder)
                .build();

        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withOrderNoOrig(TAIFUN_ORDER__NO)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        buildDNs();
        buildDNServices();

        createBillingMocks();
    }

    /* Generiert die DN-Services */
    private void buildDNServices() {
        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class);

        Leistungsbuendel2ProduktBuilder l2pBuilder = getBuilder(Leistungsbuendel2ProduktBuilder.class)
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withRandomLeistungNoOrig();
        Leistungsbuendel2Produkt l2p = l2pBuilder.build();
        dnExtLeistungNo = l2p.getLeistungNoOrig();

        // aktive Rufnummernleistung
        getBuilder(Leistung2DNBuilder.class)
                .withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(getBuilder(Leistung4DnBuilder.class)
                        .withProvisioningName(DN_SERVICE_PROV_NAME_ACTIVE))
                .withLeistungParameter(DN_SERVICE_VALUE_ACTIVE)
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, -1))
                .build();

        // beendete Rufnummernleistung
        getBuilder(Leistung2DNBuilder.class)
                .withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(getBuilder(Leistung4DnBuilder.class).withProvisioningName("CPS2"))
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, -10))
                .withScvKuendigung(DateTools.changeDate(new Date(), Calendar.DATE, -1))
                .build();

        // zukuenftige (noch nicht aktive) Rufnummernleistung
        getBuilder(Leistung2DNBuilder.class)
                .withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(getBuilder(Leistung4DnBuilder.class).withProvisioningName("CPS3"))
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, 20))
                .build();
    }

    /* Billing-Services mocken */
    private void createBillingMocks() throws FindException {
        rufnummerService = mock(RufnummerService.class);
        when(rufnummerService.findRNs4Auftrag(cpsTx.getOrderNoOrig(), cpsTx.getEstimatedExecTime())).thenReturn(dns4Order);
        when(rufnummerService.findLastRN(aktDN.getDnNoOrig())).thenReturn(aktDN);

        Leistung phoneService = getBuilder(LeistungBuilder.class)
                .withLeistungNoOrig(dnExtLeistungNo)
                .build();

        leistungService = mock(LeistungService.class);
        when(leistungService.findProductLeistung4Auftrag(cpsTx.getAuftragId(), ProduktMapping.MAPPING_PART_TYPE_PHONE)).thenReturn(phoneService);
    }

    /* Generiert die Rufnummern */
    private void buildDNs() {
        dns4Order = new ArrayList<Rufnummer>();
        aktDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNo()
                .withRandomDnNoOrig()
                .withAuftragNoOrig(TAIFUN_ORDER__NO)
                .withOnKz(ONKZ)
                .withDnBase(ACT_DN_BASE)
                .withMainNumber(true)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withHistCnt(Integer.valueOf(0))
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        dns4Order.add(aktDN);
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecute() throws Exception {
        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetEWSDDataCommand command = (CPSGetEWSDDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetEWSDDataCommand");
        command.setRufnummerService(rufnummerService);
        command.setLeistungService(leistungService);

        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        flushAndClear();

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotNull(cpsServiceOrderData.getTelephone(), "No telephone data for EWSD found!");

        // analyse result
        CPSTelephoneData telData = cpsServiceOrderData.getTelephone();
        assertNotEmpty(telData.getTelephoneNumbers(), "No phone numbers for EWSD found!");
        assertNotNull(telData.getOrig1(), "ORIG1 not defined");
        assertTrue(StringUtils.isNotBlank(telData.getType()), "Telephone type not defined");
        assertEquals(telData.getType(), CPSTelephoneData.TELEPHONE_TYPE_ISDN);

        List<CPSPortData> ports = telData.getPorts();
        assertNotEmpty(ports, "No EWSD ports found!");
        assertEquals(ports.size(), EXPECTED_PORT_COUNT, "Count of ports unexpected");

        CPSPortData portData = ports.get(0);
        assertEquals(portData.getEqn(), EWSD_EQN, "EQN of port unexpected!");
        assertEquals(portData.getV5Port(), EWSD_V5_PORT, "V5-Port unexpected!");

        List<CPSTelephoneNumberData> telNumbers = telData.getTelephoneNumbers();
        assertNotEmpty(telNumbers, "No telephone numbers found for EWSD!");
        assertEquals(telNumbers.size(), EXPECTED_NUMBER_COUNT, "Count of telephone numbers unexpected!");

        CPSTelephoneNumberData telNumber = telNumbers.get(0);
        assertEquals(telNumber.getLac(), ONKZ, "ONKZ not as expected");
        assertEquals(telNumber.getDn(), ACT_DN_BASE, "DN-Base not as expected");
        assertEquals(telNumber.getMainDN(), "1", "Main Number not as expected");
        assertEquals(telNumber.getDirectDial(), null, "DirectDial not as expected");

        List<CPSDNServiceData> dnServices = telNumber.getDnServices();
        assertNotEmpty(dnServices, "No DN-Services found!");
        assertEquals(dnServices.size(), 1, "count of DN-Services is unexpected!");
        assertEquals(dnServices.get(0).getServiceName(), DN_SERVICE_PROV_NAME_ACTIVE);
        assertEquals(dnServices.get(0).getServiceValue(), DN_SERVICE_VALUE_ACTIVE_RESULT);
    }

}


