/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2012 08:49:48
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.thoughtworks.xstream.XStream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
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
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSFTTBData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsEndpointDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsNetworkDevice;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.cc.CPSGetDataService;

/**
 * Service-Tests fuer CPSGetDataService
 */
@Test(groups = { BaseTest.SERVICE })
public class CPSGetDataServiceTest extends AbstractHurricanBaseServiceTest {

    private static final String FTTB_PORT_ID = "00-00-01-04";
    private static final String FTTB_MANUFACTURER = "HUAWEI_FTTB";

    private static final int UPSTREAM = 5000;
    private static final int DOWNSTREAM = 50000;
    private static final String PORT_ID = "portId";
    private static final String ORTSTEIL = "MUC-ARNUL-178";

    private static final String VBZ = "DV123456-2009";

    private static final Long FTTH_TAIFUN_ORDER__NO = Long.valueOf(8888888L);
    private static final String FTTH_PORT_ID = "00-01-02-03";
    private static final String FTTH_PORT_TYP = "POTS";
    private static final String FTTH_ONT_ID = "123";
    private static final String FTTH__CPS_MANUFACTURER = "HUAWEI_FTTH";
    private static final int PROFILE_DOWNSTREAM = 50000;
    private static final int PROFILE_UPSTREAM = 5000;
    private static final Integer PROFILE_TM_DOWN = 123;
    private static final Integer PROFILE_TM_UP = 456;
    private static final String HVT_NAME = "MUC-ARNUL-178";
    private static final String EXPECTED_PORT_ID = "DV123456-2009--BaeckerAG";
    private static final String BG_TYP = "MA5652G";

    private CPSGetDataService sut;

    private HWMdu hwMDUFTTB;
    private HWOnt hwOntFtth;
    private AuftragBuilder auftragBuilderFTTH;
    private EquipmentBuilder eqInBuilderFTTH;

    private DeviceService deviceService;

    @BeforeMethod
    public void setup() {
        sut = getCCService(CPSGetDataService.class);
        hwMDUFTTB = null;
        hwOntFtth = null;
        auftragBuilderFTTH = null;
    }

    public void findOnkzAndAsb() throws FindException {
        final String endstellenTyp = Endstelle.ENDSTELLEN_TYP_B;
        final String onkz = "12345";
        final Integer asb = 12345;
        //@formatter:off
        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOnkz(onkz);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withAsb(asb);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(endstellenTyp)
                .withHvtStandortBuilder(hvtStandortBuilder);
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .build();
        //@formatter:on
        Pair<HVTGruppe, HVTStandort> hvtGruppeStandort = sut.findHvtGruppeAndStandort(auftragTechnik.getAuftragId(),
                endstellenTyp);

        assertNotNull(hvtGruppeStandort);
        assertEquals(hvtGruppeStandort.getFirst().getOnkz(), onkz);
        assertEquals(hvtGruppeStandort.getSecond().getDTAGAsb(), Integer.valueOf(345));
    }

    @Test(expectedExceptions = { FindException.class })
    public void findOnkzAndAsbNoEndstelle() throws FindException {
        final String onkz = "12345";
        final Integer asb = 12345;
        //@formatter:off
        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOnkz(onkz);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withAsb(asb);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder);
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .build();
        //@formatter:on
        sut.findHvtGruppeAndStandort(auftragTechnik.getAuftragId(), Endstelle.ENDSTELLEN_TYP_A);
    }

    @Test(expectedExceptions = { FindException.class })
    public void findOnkzAndAsbNoHvtGruppe() throws FindException {
        final String endstellenTyp = Endstelle.ENDSTELLEN_TYP_B;
        final Integer asb = 12345;
        //@formatter:off
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(null)
                .withAsb(asb);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(endstellenTyp)
                .withHvtStandortBuilder(hvtStandortBuilder);
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .build();
        //@formatter:on
        sut.findHvtGruppeAndStandort(auftragTechnik.getAuftragId(), endstellenTyp);
    }

    @Test(expectedExceptions = { FindException.class })
    public void findOnkzAndAsbInvalidEsType() throws FindException {
        final String endstellenTyp = Endstelle.ENDSTELLEN_TYP_B;
        final String onkz = "12345";
        final Integer asb = 12345;
        //@formatter:off
        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOnkz(onkz);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder)
                .withAsb(asb);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(endstellenTyp)
                .withHvtStandortBuilder(hvtStandortBuilder);
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .build();
        //@formatter:on
        sut.findHvtGruppeAndStandort(auftragTechnik.getAuftragId(), "asdf");
    }

    public void getFttxDataFttb() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class);
        AuftragBuilder auftragBuilderFTTB = getBuilder(AuftragBuilder.class);
        //@formatter:off
        auftragBuilderFTTB.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilderFTTB)
                .withProdBuilder(produktBuilder);
        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("MUC-ARNUL-178");
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder);
        EquipmentBuilder eqOutBuilder = createEqOutBuilderFTTB();
        EquipmentBuilder eqInBuilderFTTB = createEqInBuilderFTTB();
        RangierungBuilder rangierungBuilder = buildRangierungFTTB(eqInBuilderFTTB, eqOutBuilder);
        EndstelleBuilder endstelleBBuilder = createEndstelleBBuilder(hvtStandortBuilder, rangierungBuilder);
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(eqInBuilderFTTB).withCvlanTyp(VOIP).build();
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(eqInBuilderFTTB).withCvlanTyp(IAD).build();
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(eqInBuilderFTTB).withGueltigBis(Date.from(LocalDate.now().minusDays(0)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())).build();
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(eqInBuilderFTTB).withGueltigVon(Date.from(LocalDate.now().plusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())).build();
        DSLAMProfileBuilder dslamProfileBuilderAct = createDslamProfileBuilder();
        getBuilder(Auftrag2DSLAMProfileBuilder.class)
            .withAuftragBuilder(auftragBuilderFTTB)
            .withDSLAMProfileBuilder(dslamProfileBuilderAct)
            .build();
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
            .withAuftragBuilder(auftragBuilderFTTB)
            .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class))
            .withEndstelleBuilder(endstelleBBuilder)
            .build();
        //@formatter:on

        CPSFTTBData fttbData = sut.getFttbData4Wholesale(auftragTechnik.getAuftragId(), PORT_ID, new Date());
        assertNotNull(fttbData);

        String gpon = createExpectedGpon();

        assertEquals(fttbData.getManufacturer(), FTTB_MANUFACTURER, "Manufacturer not as expected.");
        assertEquals(fttbData.getMduGeraeteBezeichnung(), hwMDUFTTB.getGeraeteBez(),
                "Rack.Geraetebezeichnung not as expected.");
        assertEquals(fttbData.getMduTyp(), hwMDUFTTB.getMduType(), "BaugruppenTyp not as expected.");
        assertEquals(fttbData.getSerialNo(), hwMDUFTTB.getSerialNo(), "MDU serial no not as expected.");
        assertEquals(fttbData.getGponPort(), gpon, "MDU.OLTGponPort not as expected.");
        assertEquals(fttbData.getBaugruppenPort(), eqInBuilderFTTB.get().getHwEQN(), "Baugruppen Port not as expected.");
        assertEquals(fttbData.getPortTyp(), eqInBuilderFTTB.get().getHwSchnittstelle(), "Port Typ not as expected.");
        assertEquals(fttbData.getUpstream(), "" + UPSTREAM);
        assertEquals(fttbData.getDownstream(), "" + DOWNSTREAM);
        assertEquals(fttbData.getPortId(), PORT_ID);
        assertEquals(fttbData.getMduStandort(), ORTSTEIL);
        assertNull(fttbData.getShelf());
        assertNull(fttbData.getFrame());
        assertNull(fttbData.getGponId());
        assertThat(fttbData.getVlansNotNull().size(), equalTo(2));
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        String xml = xstream.toXML(fttbData).replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "");
        // XStream gibt "__" statt "_" hier im Test aus. CPS XML funktioniert ;)
        assertThat(
                xml,
                new BaseMatcher<String>() {
                    @Override
                    public boolean matches(Object item) {
                        String s = (String) item;
                        return !s.toLowerCase().contains("<oltrackid>");
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("darf das transiente tag 'oltTrackId' nicht enthalten");
                    }
                }
        );
        assertThat(
                xml,
                containsString("<VLANS>"));
        assertThat(
                xml,
                containsString("<VLAN><SERVICE>IAD</SERVICE><TYPE>UNICAST</TYPE><CVLAN>40</CVLAN><SVLAN>102</SVLAN><SVLAN__BACKBONE>101</SVLAN__BACKBONE></VLAN>"));
        assertThat(
                xml,
                containsString("<VLAN><SERVICE>VOIP</SERVICE><TYPE>UNICAST</TYPE><CVLAN>40</CVLAN><SVLAN>102</SVLAN><SVLAN__BACKBONE>101</SVLAN__BACKBONE></VLAN>"));
    }

    private String createExpectedGpon() {
        List<String> gponParts = new ArrayList<String>();
        gponParts.add(hwMDUFTTB.getOltFrame());
        gponParts.add(hwMDUFTTB.getOltSubrack());
        gponParts.add(hwMDUFTTB.getOltSlot());
        gponParts.add(hwMDUFTTB.getOltGPONPort());
        gponParts.add(hwMDUFTTB.getOltGPONId());
        String gpon = StringTools.join(gponParts.toArray(new String[gponParts.size()]), "-", true);
        return gpon;
    }

    private DSLAMProfileBuilder createDslamProfileBuilder() {
        //@formatter:off
        return getBuilder(DSLAMProfileBuilder.class)
                .withRandomId()
                .withBandwidth(DOWNSTREAM, UPSTREAM);
        //@formatter:on
    }

    private EndstelleBuilder createEndstelleBBuilder(HVTStandortBuilder hvtStandortBuilder,
            RangierungBuilder rangierungBuilder) {
        //@formatter:off
        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withRangierungBuilder(rangierungBuilder);
      //@formatter:on
        return endstelleBBuilder;
    }

    private EquipmentBuilder createEqInBuilderFTTB() {
        HWBaugruppeBuilder hwBaugruppeBuilder = createHwBaugruppeBuilderFTTB();
        return getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(hwBaugruppeBuilder).withHwEQN(FTTB_PORT_ID);
    }

    private EquipmentBuilder createEqOutBuilderFTTB() {
        return getBuilder(EquipmentBuilder.class).withUETV(Uebertragungsverfahren.H13);
    }

    private HWBaugruppeBuilder createHwBaugruppeBuilderFTTB() {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class).withHwTypeName("FTTB_AGB")
                .withName("FTTB");

        //@formatter:off
        HWOltBuilder hwRackOltBuilder = getBuilder(HWOltBuilder.class)
                .withHwProducerBuilder(getBuilder(HVTTechnikBuilder.class)
                        .withHersteller(FTTB_MANUFACTURER)
                        .withCpsName(FTTB_MANUFACTURER));
        //@formatter:on
        HWMduBuilder hwRackBuilder = getBuilder(HWMduBuilder.class).withHWRackOltBuilder(hwRackOltBuilder)
                .withHwProducerBuilder(
                        getBuilder(HVTTechnikBuilder.class).withHersteller(FTTB_MANUFACTURER).withCpsName(
                                FTTB_MANUFACTURER)
                );
        hwMDUFTTB = hwRackBuilder.build();
        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withBaugruppenTypBuilder(
                bgTypBuilder).withRackBuilder(hwRackBuilder);

        return hwBaugruppeBuilder;
    }

    private RangierungBuilder buildRangierungFTTB(EquipmentBuilder eqInBuilder, EquipmentBuilder eqOutBuilder) {
        PhysikTypBuilder ptBuilder = getBuilder(PhysikTypBuilder.class);

        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class).withEqInBuilder(eqInBuilder)
                .withEqOutBuilder(eqOutBuilder).withPhysikTypBuilder(ptBuilder)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class));
        return rangierungBuilder;
    }

    public void testExecuteFTTH() throws Exception {
        final Long auftragId = buildFTTH();

        CpsAccessDevice accessDevice = sut.getFtthData(auftragId, new Date(), false, EXPECTED_PORT_ID);

        assertNotNull(accessDevice, "No FTTH data defined!");

        CpsItem item = accessDevice.getItems().get(0);
        CpsEndpointDevice endpointDevice = item.getEndpointDevice();
        CpsNetworkDevice networkDevice = item.getNetworkDevice();

        assertEquals(endpointDevice.getHardwareModel(), HWOnt.ONT_TYPE_O123T);
        assertEquals(endpointDevice.getManufacturer(), FTTH__CPS_MANUFACTURER);
        assertEquals(endpointDevice.getPort(), FTTH_PORT_ID);
        assertEquals(endpointDevice.getPortType(), FTTH_PORT_TYP);
        assertEquals(endpointDevice.getPortName(), EXPECTED_PORT_ID);
        assertEquals(endpointDevice.getTargetMarginDown(), PROFILE_TM_DOWN.toString());
        assertEquals(endpointDevice.getTargetMarginUp(), PROFILE_TM_UP.toString());
        assertEquals(endpointDevice.getSerialNo(), hwOntFtth.getSerialNo());
        assertEquals(networkDevice.getPort(), hwOntFtth.getGponPort());
    }

    /**
     * Generiert die fuer FTTH notwendige Daten-Struktur
     */
    private Long buildFTTH() throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withCpsDSLProduct(Boolean.TRUE);

        auftragBuilderFTTH = getBuilder(AuftragBuilder.class);
        auftragBuilderFTTH.getAuftragDatenBuilder().withAuftragNoOrig(FTTH_TAIFUN_ORDER__NO)
                .withAuftragBuilder(auftragBuilderFTTH).withProdBuilder(produktBuilder);

        eqInBuilderFTTH = createEqInBuilderFTTH();
        RangierungBuilder rangierungBuilder = buildRangierungFTTH(eqInBuilderFTTH);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class).withOrtsteil(HVT_NAME);

        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
                .withHvtGruppeBuilder(hvtGruppeBuilder);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withRangierungBuilder(rangierungBuilder);

        DSLAMProfileBuilder dslamProfileBuilderAct = getBuilder(DSLAMProfileBuilder.class).withRandomId()
                .withBandwidth(PROFILE_DOWNSTREAM, PROFILE_UPSTREAM)
                .withTmDown(PROFILE_TM_DOWN)
                .withTmUp(PROFILE_TM_UP);

        getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTH)
                .withDSLAMProfileBuilder(dslamProfileBuilderAct)
                .build();

        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilderFTTH)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class).withVbz(VBZ))
                .withEndstelleBuilder(endstelleBBuilder)
                .build();

        return auftragTechnik.getAuftragId();
    }

    private EquipmentBuilder createEqInBuilderFTTH() {
        HWBaugruppeBuilder hwBaugruppeBuilder = createHwBaugruppeBuilderFTTH().withModNumber(BG_TYP);
        hwBaugruppeBuilder.build();
        return getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(hwBaugruppeBuilder).withHwEQN(FTTH_PORT_ID);
    }

    private HWBaugruppeBuilder createHwBaugruppeBuilderFTTH() {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withHwSchnittstelleName(FTTH_PORT_TYP)
                .withHwTypeName("FTTH_AGB")
                .withName("FTTH");
        bgTypBuilder.build();

        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH);

        HWOntBuilder hwOntBuilder = getBuilder(HWOntBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withHwProducerBuilder(getBuilder(HVTTechnikBuilder.class).withCpsName(FTTH__CPS_MANUFACTURER))
                .withRackTyp(HWRack.RACK_TYPE_ONT);
        hwOntFtth = hwOntBuilder.build();

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
