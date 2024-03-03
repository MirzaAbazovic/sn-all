/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 17:30:48
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsEndpointDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Test NG Klasse fuer {@link CPSGetFTTXDataCommand} Testet den FTTB Bereich des Commands!
 */
@Test(groups = BaseTest.SERVICE)
public class CPSGetFTTXDataCommandTest extends AbstractHurricanBaseServiceTest {

    private static final Long FTTB_TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String VBZ = "DV123456-2009";
    private static final String FTTB_PORT_ID = "0-1-2";
    private static final String FTTB_MANUFACTURER = "HUAWEI_FTTB";

    private static final Long FTTH_TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String FTTH_PORT_ID = "00-01-02-03";
    private static final String FTTH_ONT_ID = "123";
    private static final String FTTH_MANUFACTURER = "HUAWEI_FTTH";
    private static final Bandwidth bandwidth = Bandwidth.create(50000, 5000);
    private static final Integer PROFILE_TM_DOWN = 123;
    private static final Integer PROFILE_TM_UP = 456;
    private static final String HVT_NAME = "MUC-ARNUL-178";
    private static final String CUSTOMER_NAME = "Bäcker AG °";
    private static final String EXPECTED_PORT_ID = "DV123456-2009--BaeckerAG";
    private static final String BG_TYP = "MA5652G";
    private static final String ASB = "123456";
    public static final String FTTB_HW_SCHNITTSTELLe = "VDSL2";
    public static final String FTTB_HW_TYPE_NAME = "MDU";
    public static final String FTTB_NAME = "MA1234G_VDSL2";

    private HWBaugruppe bgFTTB;
    private HWMdu hwMDUFTTB;
    private AuftragBuilder auftragBuilderFTTB;
    private EquipmentBuilder eqInBuilderFTTB;
    private Kunde kunde;

    private Device deviceFTTH;
    private HWOlt hwOltFTTH;
    private AuftragBuilder auftragBuilderFTTH;
    private EquipmentBuilder eqInBuilderFTTH;
    private EkpFrameContractBuilder ekpFrameContractBuilder;

    /**
     * Test method for {@link CPSGetFTTXDataCommand#execute()} - FTTB Daten
     */
    @Test
    public void testExecuteFTTBSuccess() throws Exception {
        // Preperations
        buildFTTB();

        KundenService kundenService = mock(KundenService.class);
        when(kundenService.findKunde(any(Long.class))).thenReturn(kunde);

        CPSGetFTTXDataCommand dataCommand = (CPSGetFTTXDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetFTTXDataCommand");
        dataCommand.setKundenService(kundenService);

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(FTTB_TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilderFTTB)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .build();

        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTB)
                .withTechLeistungId(TechLeistung.TECH_LEISTUNG_SIPTRUNK_QOS_PROFILE)
                .withAktivVon(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAktivBis(DateTools.getHurricanEndDate())
                .build();

        // Execution
        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        serviceOrderData.setAsb(Integer.valueOf(ASB));
        dataCommand.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        dataCommand.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        Object result = dataCommand.execute();

        // Evaluations
        Vector<String> vector = new Vector<String>();
        vector.add(hwMDUFTTB.getOltFrame());
        vector.add(hwMDUFTTB.getOltSubrack());
        vector.add(hwMDUFTTB.getOltSlot());
        vector.add(hwMDUFTTB.getOltGPONPort());
        vector.add(hwMDUFTTB.getOltGPONId());
        String gpon = StringTools.join(vector.toArray(new String[vector.size()]), "-", true);

        assertTrue((result instanceof ServiceCommandResult),
                "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK,
                "ServiceCommand not successful!");

        assertNotNull(serviceOrderData.getAccessDevice(), "No FTTB AccessDevice data defined!");

        CpsAccessDevice fttbAccessDevice = serviceOrderData.getAccessDevice();
        assertEquals(fttbAccessDevice.getItems().size(), 1, "One AccessDevice expected.");
        CpsItem fttbItem = fttbAccessDevice.getItems().get(0);
        assertNotNull(fttbItem.getEndpointDevice(), "EndpointDevice missing.");
        CpsEndpointDevice endpointDevice = fttbItem.getEndpointDevice();
        assertEquals(endpointDevice.getManufacturer(), FTTB_MANUFACTURER, "Manufacturer not as expected.");
        assertEquals(endpointDevice.getName(), hwMDUFTTB.getGeraeteBez(), "Rack.Geraetebezeichnung not as expected.");
        assertEquals(endpointDevice.getHardwareModel(), hwMDUFTTB.getMduType(), "BaugruppenTyp not as expected.");
        assertEquals(endpointDevice.getSerialNo(), hwMDUFTTB.getSerialNo(), "MDU serial no not as expected.");
        assertEquals(endpointDevice.getPort(), eqInBuilderFTTB.get().getHwEQN(), "Port not as expected.");
        assertEquals(endpointDevice.getPortType(), eqInBuilderFTTB.get().getHwSchnittstelle(), "Port Typ not as expected.");
        assertEquals(endpointDevice.getUpstream(), bandwidth.getUpstreamAsString());
        assertEquals(endpointDevice.getDownstream(), bandwidth.getDownstreamAsString());
        assertEquals(endpointDevice.getPortName(), EXPECTED_PORT_ID);

        assertNotEmpty(fttbItem.getLayer2Config().getPbits());

    }

    /**
     * Test method for {@link CPSGetFTTXDataCommand#execute()} - FTTH Daten
     */
    @Test
    public void testExecuteFTTHSuccess() throws Exception {
        // Preperations
        buildFTTH();

        KundenService kundenService = mock(KundenService.class);
        when(kundenService.findKunde(any(Long.class))).thenReturn(kunde);

        CPSGetFTTXDataCommand dataCommand = (CPSGetFTTXDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetFTTXDataCommand");
        dataCommand.setKundenService(kundenService);

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(FTTH_TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilderFTTH)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .build();

        // Execution
        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        serviceOrderData.setAsb(Integer.valueOf(ASB));
        dataCommand.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        dataCommand.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        Object result = dataCommand.execute();

        // Evaluations
        assertTrue((result instanceof ServiceCommandResult),
                "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK,
                "ServiceCommand not successful!");

        assertNotNull(serviceOrderData.getAccessDevice(), "No FTTH data defined!");
        CpsAccessDevice accessDevice = serviceOrderData.getAccessDevice();
        CpsItem item = accessDevice.getItems().get(0);
        CpsEndpointDevice endpointDevice = item.getEndpointDevice();

        assertEquals(endpointDevice.getHardwareModel(), HWOnt.ONT_TYPE_O123T);
        assertEquals(endpointDevice.getPort(), FTTH_PORT_ID);
        assertEquals(endpointDevice.getPortName(), EXPECTED_PORT_ID);
        assertEquals(endpointDevice.getTargetMarginDown(), PROFILE_TM_DOWN.toString());
        assertEquals(endpointDevice.getTargetMarginUp(), PROFILE_TM_UP.toString());

    }

    /* Generiert die fuer FTTB notwendige Daten-Struktur */
    private void buildFTTB() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withCpsDSLProduct(Boolean.TRUE);

        auftragBuilderFTTB = getBuilder(AuftragBuilder.class);
        auftragBuilderFTTB.getAuftragDatenBuilder()
                .withAuftragNoOrig(FTTB_TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilderFTTB)
                .withProdBuilder(produktBuilder);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil(HVT_NAME);

        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withStandortTypRefId(11002L);

        EquipmentBuilder eqOutBuilder = createEqOutBuilderFTTB();
        eqInBuilderFTTB = createEqInBuilderFTTB(hvtStandortBuilder);
        RangierungBuilder rangierungBuilder = buildRangierungFTTB(eqInBuilderFTTB, eqOutBuilder);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withRangierungBuilder(rangierungBuilder);

        DSLAMProfileBuilder dslamProfileBuilderAct = getBuilder(DSLAMProfileBuilder.class)
                .withRandomId()
                .withBandwidth(bandwidth);

        getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTB)
                .withDSLAMProfileBuilder(dslamProfileBuilderAct)
                .build();

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTB)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class)
                        .withRandomId()
                        .withVbz(VBZ))
                .withEndstelleBuilder(endstelleBBuilder)
                .build();

        kunde = new KundeBuilder()
                .withKundeNo(auftragBuilderFTTB.get().getKundeNo())
                .withName(CUSTOMER_NAME)
                .build();

        ekpFrameContractBuilder = getBuilder(EkpFrameContractBuilder.class).withRandomId();

        getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTB).withRandomId()
                .withEkpFrameContractBuilder(ekpFrameContractBuilder).build();
    }

    private EquipmentBuilder createEqInBuilderFTTB(HVTStandortBuilder hvtStandortBuilder) {
        HWBaugruppeBuilder hwBaugruppeBuilder = createHwBaugruppeBuilderFTTB(hvtStandortBuilder);
        bgFTTB = hwBaugruppeBuilder.build();
        return getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withHwEQN(FTTB_PORT_ID)
                .withHwSchnittstelle(FTTB_HW_SCHNITTSTELLe);
    }

    private EquipmentBuilder createEqOutBuilderFTTB() {
        return getBuilder(EquipmentBuilder.class).withUETV(Uebertragungsverfahren.H13);
    }

    private HWBaugruppeBuilder createHwBaugruppeBuilderFTTB(HVTStandortBuilder hvtStandortBuilder) {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withHwTypeName(FTTB_HW_TYPE_NAME)
                .withName(FTTB_NAME)
                .withHwSchnittstelleName(FTTB_HW_SCHNITTSTELLe);

        HWOltBuilder hwRackOltBuilder = getBuilder(HWOltBuilder.class)
                .withHwProducerBuilder(
                        getBuilder(HVTTechnikBuilder.class)
                                .withHersteller(FTTB_MANUFACTURER)
                                .withCpsName(FTTB_MANUFACTURER)
                );

        HWMduBuilder hwRackBuilder = getBuilder(HWMduBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withHWRackOltBuilder(hwRackOltBuilder)
                .withHwProducerBuilder(
                        getBuilder(HVTTechnikBuilder.class)
                                .withHersteller(FTTB_MANUFACTURER)
                                .withCpsName(FTTB_MANUFACTURER)

                );
        hwMDUFTTB = hwRackBuilder.build();

        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(hwRackBuilder);

        return hwBaugruppeBuilder;
    }

    private RangierungBuilder buildRangierungFTTB(EquipmentBuilder eqInBuilder, EquipmentBuilder eqOutBuilder) {
        PhysikTypBuilder ptBuilder = getBuilder(PhysikTypBuilder.class);

        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withEqOutBuilder(eqOutBuilder)
                .withPhysikTypBuilder(ptBuilder)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class));
        return rangierungBuilder;
    }

    /**
     * Generiert die fuer FTTH notwendige Daten-Struktur
     */
    private void buildFTTH() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withCpsDSLProduct(Boolean.TRUE);

        auftragBuilderFTTH = getBuilder(AuftragBuilder.class);
        auftragBuilderFTTH.getAuftragDatenBuilder()
                .withAuftragNoOrig(FTTH_TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilderFTTH)
                .withProdBuilder(produktBuilder);

        eqInBuilderFTTH = createEqInBuilderFTTH();
        RangierungBuilder rangierungBuilder = buildRangierungFTTH(eqInBuilderFTTH);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil(HVT_NAME);

        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withRangierungBuilder(rangierungBuilder);

        DSLAMProfileBuilder dslamProfileBuilderAct = getBuilder(DSLAMProfileBuilder.class)
                .withRandomId()
                .withBandwidth(bandwidth)
                .withTmDown(PROFILE_TM_DOWN)
                .withTmUp(PROFILE_TM_UP);

        getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTH)
                .withDSLAMProfileBuilder(dslamProfileBuilderAct)
                .build();

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTH)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class)
                        .withRandomId()
                        .withVbz(VBZ))
                .withEndstelleBuilder(endstelleBBuilder)
                .build();

        kunde = new KundeBuilder()
                .withKundeNo(auftragBuilderFTTH.get().getKundeNo())
                .withName(CUSTOMER_NAME)
                .build();

        ekpFrameContractBuilder = getBuilder(EkpFrameContractBuilder.class).withRandomId();

        getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTH).withRandomId()
                .withEkpFrameContractBuilder(ekpFrameContractBuilder).build();
    }

    private EquipmentBuilder createEqInBuilderFTTH() {
        HWBaugruppeBuilder hwBaugruppeBuilder = createHwBaugruppeBuilderFTTH()
                .withModNumber(BG_TYP);
        hwBaugruppeBuilder.build();
        return getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withHwEQN(FTTH_PORT_ID);
    }

    private HWBaugruppeBuilder createHwBaugruppeBuilderFTTH() {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withHwTypeName("FTTH_AGB")
                .withName("FTTH");
        bgTypBuilder.build();

        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH);

        HVTTechnikBuilder hvtTechnikBuilder = getBuilder(HVTTechnikBuilder.class)
                .withCpsName(FTTH_MANUFACTURER)
                .withHersteller(FTTH_MANUFACTURER);

        HWOntBuilder hwOntBuilder = getBuilder(HWOntBuilder.class)
                .withHwProducerBuilder(hvtTechnikBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder);
        hwOntBuilder.build();

        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(hwOntBuilder);

        return hwBaugruppeBuilder;
    }

    private RangierungBuilder buildRangierungFTTH(EquipmentBuilder eqInBuilder) {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH);

        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withPhysikTypBuilder(getBuilder(PhysikTypBuilder.class))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withOntId(FTTH_ONT_ID);
        return rangierungBuilder;
    }
}


