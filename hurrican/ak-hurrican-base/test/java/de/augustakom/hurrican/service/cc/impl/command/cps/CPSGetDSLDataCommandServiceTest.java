/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2009 14:54:25
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
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.EQCrossConnectionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDSLData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Unit Test fuer {@link CPSGetDSLDataCommand}
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class CPSGetDSLDataCommandServiceTest extends AbstractHurricanBaseServiceTest {

    private static final String PORT_ID = "001-04";
    private static final String PORT_ID_EXPECTED = "1-4";
    private static final String MANUFACTURER = "HUAWEI";
    private static final Bandwidth bandwidth = Bandwidth.create(1152, 320);
    private static final String PROFILE_FASTPATH = "0";
    private static final String VBZ = "DV123456-2009";
    private static final String CUSTOMER_NAME = "Bäcker AG °";
    private static final String EXPECTED_PORT_ID = "DV123456-2009--BaeckerAG";
    private static final String DSLAM_PROFILE_ADSL2P = "xxxxx_2336_640_H_D001";
    private static final String DSLAM_PROFILE_ADSL1 = "yyyyy_2336_640_H_D001_ADSL1";
    private static final String KVZ_NR = "A999";

    private AuftragBuilder auftragBuilder;
    private HWBaugruppenTyp bgTyp;
    private HWRack hwRack;
    private Kunde kunde;

    @Test(groups = BaseTest.SERVICE)
    public void testExecuteADSL2P() throws Exception {
        buildTestData(DSLAM_PROFILE_ADSL2P, Schicht2Protokoll.ATM.name());

        CPSGetDSLDataCommand dslDataCommand = (CPSGetDSLDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDSLDataCommand");

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(Long.valueOf(123))
                .withAuftragBuilder(auftragBuilder)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .build();

        getBuilder(Auftrag2TechLeistungBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withTechLeistungId(TechLeistung.TECH_LEISTUNG_SIPTRUNK_QOS_PROFILE)
                .withAktivVon(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAktivBis(DateTools.getHurricanEndDate())
                .build();

        KundenService kundenService = mock(KundenService.class);
        when(kundenService.findKunde(any(Long.class))).thenReturn(kunde);

        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        dslDataCommand.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        dslDataCommand.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        dslDataCommand.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().withAuftragBuilder(auftragBuilder).build());
        dslDataCommand.setKundenService(kundenService);
        Object result = dslDataCommand.execute();

        assertTrue((result instanceof ServiceCommandResult),
                "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK,
                "ServiceCommand not successful!");

        assertNotNull(serviceOrderData.getDsl(), "No DSL data defined!");

        CPSDSLData dslData = serviceOrderData.getDsl();
        assertEquals(dslData.getCardType(), bgTyp.getName());
        assertEquals(dslData.getDslamManufacturer(), MANUFACTURER);
        assertEquals(dslData.getDslamName(), hwRack.getGeraeteBez());
        assertEquals(dslData.getDslamPort(), PORT_ID_EXPECTED);
        assertEquals(dslData.getKvzNummer(), KVZ_NR);
        assertEquals(dslData.getDslamPortType(), Schicht2Protokoll.ATM.name());
        assertEquals(dslData.getPortId(), EXPECTED_PORT_ID);
        assertEquals(dslData.getDownstream(), bandwidth.getDownstreamAsString());
        assertEquals(dslData.getUpstream(), bandwidth.getUpstreamAsString());
        assertEquals(dslData.getFastpath(), PROFILE_FASTPATH);
        assertEquals(dslData.getL2PowerSafeMode(), BooleanTools.getBooleanAsString(Boolean.TRUE));
        assertEquals(dslData.getTransferMethod(), CPSDSLData.TRANSFER_METHOD_ADSL2P);
        assertNotEmpty(dslData.getCrossConnections(), "CrossConnections not loaded!");
        assertEquals(dslData.getCrossConnections().size(), 1, "count of cross connections is not valid");
        assertNotEmpty(dslData.getPbits());
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecuteADSL1ProfileOnADSL2PPort() throws Exception {
        buildTestData(DSLAM_PROFILE_ADSL1, Schicht2Protokoll.EFM.name());

        CPSGetDSLDataCommand dslDataCommand =
                (CPSGetDSLDataCommand) getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDSLDataCommand");

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(Long.valueOf(123))
                .withAuftragBuilder(auftragBuilder)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .build();

        KundenService kundenService = mock(KundenService.class);
        when(kundenService.findKunde(any(Long.class))).thenReturn(kunde);

        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        dslDataCommand.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        dslDataCommand.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        dslDataCommand.prepare(AbstractCPSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().withRandomAuftragId().build());
        dslDataCommand.setKundenService(kundenService);
        Object result = dslDataCommand.execute();

        assertTrue((result instanceof ServiceCommandResult),
                "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK,
                "ServiceCommand not successful!");

        assertNotNull(serviceOrderData.getDsl(), "No DSL data defined!");

        CPSDSLData dslData = serviceOrderData.getDsl();
        assertEquals(dslData.getCardType(), bgTyp.getName());
        assertEquals(dslData.getDslamManufacturer(), MANUFACTURER);
        assertEquals(dslData.getDslamName(), hwRack.getGeraeteBez());
        assertEquals(dslData.getDslamPort(), PORT_ID_EXPECTED);
        assertEquals(dslData.getKvzNummer(), KVZ_NR);
        assertEquals(dslData.getDslamPortType(), Schicht2Protokoll.EFM.name());
        assertEquals(dslData.getPortId(), EXPECTED_PORT_ID);
        assertEquals(dslData.getDownstream(), bandwidth.getDownstreamAsString());
        assertEquals(dslData.getUpstream(), bandwidth.getUpstreamAsString());
        assertEquals(dslData.getFastpath(), PROFILE_FASTPATH);
        assertEquals(dslData.getTransferMethod(), CPSDSLData.TRANSFER_METHOD_ADSL);
    }

    private void buildTestData(String dslamProfileName, String dslamPortType) {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withCpsDSLProduct(Boolean.TRUE);

        auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = auftragBuilder.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withProdBuilder(produktBuilder);

        DSLAMProfileBuilder dslamProfileBuilderAct = getBuilder(DSLAMProfileBuilder.class)
                .withRandomId()
                .withName(dslamProfileName)
                .withBandwidth(bandwidth)
                .withFastpath(Boolean.FALSE)
                .withL2Power(true);
        dslamProfileBuilderAct.build();

        getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withDSLAMProfileBuilder(dslamProfileBuilderAct)
                .build();

        EquipmentBuilder eqOutBuilder = createEqOutBuilder();
        EquipmentBuilder eqInBuilder = createEqInBuilder(dslamPortType);
        RangierungBuilder rangierungBuilder = buildRangierung(eqInBuilder, eqOutBuilder);
        createCrossConnections(eqInBuilder);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder);

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class)
                        .withVbz(VBZ))
                .withEndstelleBuilder(endstelleBBuilder)
                .build();

        kunde = new KundeBuilder()
                .withKundeNo(auftragBuilder.get().getKundeNo())
                .withName(CUSTOMER_NAME)
                .build();
    }

    private EquipmentBuilder createEqInBuilder(String dslamPortType) {
        HWBaugruppeBuilder hwBaugruppeBuilder = createHwBaugruppeBuilder();
        hwBaugruppeBuilder.build();
        return getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withSchicht2Protokoll(Schicht2Protokoll.valueOf(dslamPortType))
                .withHwEQN("U03-3-" + PORT_ID);
    }

    private void createCrossConnections(EquipmentBuilder eqInBuilder) {
        // inactive CrossConnection
        getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(eqInBuilder)
                .withValidFrom(DateTools.changeDate(new Date(), Calendar.DATE, -10))
                .withValidTo(new Date())
                .build();

        // active CrossConnection
        getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(eqInBuilder)
                .withValidFrom(new Date())
                .withValidTo(DateTools.getHurricanEndDate())
                .build();
    }

    private EquipmentBuilder createEqOutBuilder() {
        return getBuilder(EquipmentBuilder.class).withUETV(Uebertragungsverfahren.H13)
                .withKvzNummer(KVZ_NR);
    }

    private HWBaugruppeBuilder createHwBaugruppeBuilder() {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withHwTypeName("XDSL_AGB")
                .withName("ADBF");
        bgTyp = bgTypBuilder.build();

        HWDslamBuilder hwDslamBuilder = getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(
                        getBuilder(HVTTechnikBuilder.class)
                                .withHersteller(MANUFACTURER)
                                .withCpsName(MANUFACTURER)
                );
        hwRack = hwDslamBuilder.build();

        return getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(hwDslamBuilder);
    }

    private RangierungBuilder buildRangierung(EquipmentBuilder eqInBuilder, EquipmentBuilder eqOutBuilder) {
        PhysikTypBuilder ptBuilder = getBuilder(PhysikTypBuilder.class)
                .withCpsTransferMethod("ADSL2+");

        return getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withEqOutBuilder(eqOutBuilder)
                .withPhysikTypBuilder(ptBuilder)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class));
    }

}
