/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2012 15:24:17
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.hurrican.model.cc.Rangierung.*;
import static org.testng.Assert.*;

import java.text.*;
import java.time.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.PhysikUebernahmeBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.*;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenViewBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * TestNG class for {@link RangierungFreigabeService}
 */
@Test(groups = { BaseTest.SERVICE })
public class RangierungFreigabeServiceTest extends AbstractHurricanBaseServiceTest {

    private RangierungFreigabeService rangierungFreigabeService;
    private RangierungsService rangierungsService;

    /**
     * Initialize the tests
     */
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        rangierungFreigabeService = getCCService(RangierungFreigabeService.class);
        rangierungsService = getCCService(RangierungsService.class);
    }

    private void createRangierungFreigabebereit(Date freigabeDatum) {
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withFreigegeben(Freigegeben.freigegeben)
                .withFreigabeAb(freigabeDatum);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class);

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withProdBuilder(produktBuilder);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withRandomId()
                .withAuftragDatenBuilder(auftragDatenBuilder);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder);

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withRangierungBuilder(rangierungBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);

        endstelleBuilder.build();
        try {
            // Rangierung freigeben
            Rangierung rangierung = rangierungBuilder.get();
            rangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
            rangierungsService.saveRangierung(rangierung, false);
            flushAndClear();
        }
        catch (StoreException e) {
        }
    }

    public void testCreatePhysikFreigabeView() throws StoreException, ParseException, FindException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date testDatum = format.parse("2005-07-01");
        createRangierungFreigabebereit(testDatum);

        Map<Long, List<PhysikFreigebenView>> result = rangierungFreigabeService.createPhysikFreigabeView(testDatum,
                null, false);

        Assert.assertTrue(((result != null) && (!result.isEmpty())), "PhysikFreigabeViews darf nicht leer sein!");
    }

    public void testSaveRangierungFreigabeInfos() throws StoreException, ParseException, FindException {
        Rangierung rangierung = getBuilder(RangierungBuilder.class).build();
        PhysikFreigebenView physikFreigebenView = new PhysikFreigebenView();
        physikFreigebenView.setClarifyInfo("<etwas zu klären>");
        List<PhysikFreigebenView> physikFreigebenViews = new ArrayList<>();
        physikFreigebenViews.add(physikFreigebenView);
        Map<Long, List<PhysikFreigebenView>> rangierungRelationen = new HashMap<>();
        rangierungRelationen.put(rangierung.getId(), physikFreigebenViews);

        rangierungFreigabeService.saveRangierungFreigabeInfos(rangierungRelationen);
    }

    @DataProvider(name = "testFreigebenRangierungDataProvider")
    public Object[][] testFreigebenRangierungDataProvider() {
        //@formatter:off
        return new Object[][] {
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H04, Boolean.TRUE, Uebertragungsverfahren.H13},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H04, Boolean.FALSE, Uebertragungsverfahren.H04},
                { null, Uebertragungsverfahren.H04, Boolean.TRUE, Uebertragungsverfahren.H04},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, null, Boolean.TRUE, null},
                { "ADSL", Uebertragungsverfahren.H04, Boolean.TRUE, Uebertragungsverfahren.H04},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H01, Boolean.TRUE, Uebertragungsverfahren.H01},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H02, Boolean.TRUE, Uebertragungsverfahren.H02},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H03, Boolean.TRUE, Uebertragungsverfahren.H03},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H05, Boolean.TRUE, Uebertragungsverfahren.H05},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H07, Boolean.TRUE, Uebertragungsverfahren.H07},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H08, Boolean.TRUE, Uebertragungsverfahren.H08},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H11, Boolean.TRUE, Uebertragungsverfahren.H11},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H13, Boolean.TRUE, Uebertragungsverfahren.H13},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H16, Boolean.TRUE, Uebertragungsverfahren.H16},
                { RangierungFreigabeServiceImpl.ADSL2PLUS, Uebertragungsverfahren.H18, Boolean.TRUE, Uebertragungsverfahren.H18},
        };
    }

    @Test(dataProvider="testFreigebenRangierungDataProvider")
    public void testFreigebenRangierung(String transferMethod, Uebertragungsverfahren uetv, Boolean freigeben,
            final Uebertragungsverfahren uetvExpected) throws StoreException, FindException {
        EquipmentBuilder eqInBuilder = getBuilder(EquipmentBuilder.class)
            .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_ADSL_OUT)
            .withManualConfiguration(Boolean.TRUE);

        EquipmentBuilder eqOutBuilder = getBuilder(EquipmentBuilder.class).withUETV(uetv);

        PhysikTypBuilder physiktypBuilder = getBuilder(PhysikTypBuilder.class).withCpsTransferMethod(transferMethod);

        //@formatter:off
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withEsId(Rangierung.RANGIERUNG_NOT_ACTIVE)
                .withEqOutBuilder(eqOutBuilder)
                .withFreigegeben(Freigegeben.gesperrt)
                .withPhysikTypBuilder(physiktypBuilder)
                .withFreigabeAb(DateTools.minusWorkDays(10));
        //@formatter:on

        PhysikFreigebenView view = getBuilder(PhysikFreigebenViewBuilder.class)
                .withRangierungBuilder(rangierungBuilder)
                .withFreigeben(freigeben)
                .build();

        List<PhysikFreigebenView> views = new ArrayList<>();
        views.add(view);
        Map<Long, List<PhysikFreigebenView>> relationen = new HashMap<>();
        relationen.put(view.getRangierId(), views);

        rangierungFreigabeService.rangierungenFreigeben(relationen);

        // Check Uetv
        Equipment eqOut = rangierungsService.findEquipment(rangierungBuilder.get().getEqOutId());
        Assert.assertEquals(eqOut.getUetv(), uetvExpected);

        Rangierung rangierung = rangierungBuilder.get();
        if (BooleanTools.nullToFalse(freigeben)) {
            // Check, if Equipment is MANUAL_CONFIGURATION=FALSE
            Equipment eqIn = rangierungsService.findEquipment(rangierungBuilder.get().getEqInId());
            Assert.assertNotNull(eqIn, "EQ-IN not loaded!");
            Assert.assertEquals(eqIn.getManualConfiguration(), Boolean.FALSE, "ManualConfiguration flag of "
                    + "equipment is not set to 'FALSE'");

            // Check if 'rangierung' is released
            assertNull(rangierung.getEsId());
            assertNull(rangierung.getFreigabeAb());
        }
        else {
            // Check if 'rangierung' is NOT released
            assertNotNull(rangierung.getEsId());
            assertNotNull(rangierung.getFreigabeAb());
        }
    }

    @DataProvider(name = "testFreigebenRangierungFTTBPortsDataProvider")
    public Object[][] testFreigebenRangierungFTTBPortsDataProvider() {
        //@formatter:off
        return new Object[][]   {
                {PhysikTyp.PHYSIKTYP_FTTB_VDSL},
                {PhysikTyp.PHYSIKTYP_FTTB_POTS},
                {PhysikTyp.PHYSIKTYP_FTTB_RF}
        };
        //@formatter:on
    }

    @Test(dataProvider = "testFreigebenRangierungFTTBPortsDataProvider")
    public void testFreigebenRangierungVdslOrPots(final Long physikTypId) throws StoreException, FindException {
        //@formatter:off
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withFreigegeben(Freigegeben.gesperrt)
                .withEsId(RandomTools.createLong())
                .withFreigabeAb(new Date())
                .withBemerkung("testbemerkung")
                .withEqOutBuilder(getBuilder(EquipmentBuilder.class))
                .withEqInBuilder(getBuilder(EquipmentBuilder.class)
                        .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2));

        PhysikFreigebenView view = getBuilder(PhysikFreigebenViewBuilder.class)
                .withRangierungBuilder(rangierungBuilder)
                .withFreigeben(Boolean.TRUE)
                .build();
        //@formatter:on

        // nicht mit builder moeglich wegen unique-constraint. hier muss bestehender Wert aus der DB verwendet
        // werden
        Rangierung rangierung = rangierungsService.findRangierung(view.getRangierId());
        rangierung.setPhysikTypId(physikTypId);
        rangierungsService.saveRangierung(rangierung, false);

        Map<Long, List<PhysikFreigebenView>> relationen = new HashMap<>();
        relationen.put(view.getRangierId(), Collections.singletonList(view));
        rangierungFreigabeService.rangierungenFreigeben(relationen);
        // Rangierung rangierung = rangierungsService.findRangierung(view.getRangierId());
        assertNull(rangierung.getEqOutId());
        assertNull(rangierung.getEsId());
        assertNull(rangierung.getFreigabeAb());
        assertNull(rangierung.getBemerkung());
    }

    public void testFreigebenRangierungFtth() throws StoreException, FindException {
        final Date freigabeAb = new Date();
        //@formatter:off
        EquipmentBuilder equipmentBuilder = getBean(EquipmentBuilder.class)
                .withStatus(EqStatus.rang)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withFreigegeben(Freigegeben.gesperrt)
                .withEsId(RandomTools.createLong())
                .withFreigabeAb(freigabeAb)
                .withEqInBuilder(equipmentBuilder);
        PhysikFreigebenView view = getBuilder(PhysikFreigebenViewBuilder.class)
                .withRangierungBuilder(rangierungBuilder)
                .withFreigeben(Boolean.TRUE)
                .build();
        //@formatter:on

        //mit builder nicht moeglich wegen unique-constraint. hier muss bestehender Wert aus der DB verwendet werden
        Rangierung rangierung = rangierungsService.findRangierung(view.getRangierId());
        rangierung.setPhysikTypId(PhysikTyp.PHYSIKTYP_FTTH);
        rangierungsService.saveRangierung(rangierung, false);

        Map<Long, List<PhysikFreigebenView>> relationen = new HashMap<>();
        relationen.put(view.getRangierId(), Collections.singletonList(view));

        rangierungFreigabeService.rangierungenFreigeben(relationen);

        Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
        assertEquals(rangierung.getFreigegeben(), Freigegeben.gesperrt);
        assertTrue(DateTools.compareDates(rangierung.getGueltigBis(), freigabeAb) == 0);
        assertNull(rangierung.getEsId());
        assertNull(rangierung.getFreigabeAb());
        assertEquals(eqIn.getStatus(), EqStatus.locked);
    }

    public void testFreigebenRangierungGewofag() throws StoreException, FindException {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_GEWOFAG)
                .withBreakRangierung(Boolean.TRUE);

        EquipmentBuilder eqInBuilder = getBuilder(EquipmentBuilder.class)
                .withStatus(EqStatus.rang)
                .withHvtStandortBuilder(hvtStandortBuilder);
        EquipmentBuilder eqOutBuilder = getBuilder(EquipmentBuilder.class)
                .withCarrier(TNB.MNET.carrierNameUC)
                .withStatus(EqStatus.rang)
                .withHvtStandortBuilder(hvtStandortBuilder);

        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withEqOutBuilder(eqOutBuilder)
                .withFreigegeben(Freigegeben.freigegeben)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);

        PhysikFreigebenView view = getBuilder(PhysikFreigebenViewBuilder.class)
                .withRangierungBuilder(rangierungBuilder)
                .withFreigeben(Boolean.TRUE)
                .build();

        Map<Long, List<PhysikFreigebenView>> relationen = new HashMap<>();
        relationen.put(view.getRangierId(), Collections.singletonList(view));

        rangierungFreigabeService.rangierungenFreigeben(relationen);

        Rangierung rangierung = rangierungsService.findRangierung(rangierungBuilder.get().getId());
        assertNotNull(rangierung);
        assertTrue(DateTools.isBefore(rangierung.getGueltigBis(), DateTools.getHurricanEndDate()));

        assertEquipmentHasStatusFrei(eqInBuilder);
        assertEquipmentHasStatusFrei(eqOutBuilder);
    }

    private void assertEquipmentHasStatusFrei(EquipmentBuilder eqBuilder) throws FindException {
        Equipment eq = rangierungsService.findEquipment(eqBuilder.get().getId());
        assertNotNull(eq);
        assertEquals(eq.getStatus(), EqStatus.frei);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Rangierung ist nicht der Endstelle zugeordnet\\.")
    public void testRemoveRangierungEndstelleIncorrect() throws Exception {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        endstelle.setRangierId(null);
        flushAndClear();

        assertNull(endstelle.getRangierId(), "rangierungId in endstelle not correct for following test");
        assertNotNull(rangierung.getEsId(), "endstelleId in rangierung not correct for following test");

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Endstelle ist nicht der Rangierung zugeordnet\\.")
    public void testRemoveRangierungRangierungIncorrect() throws Exception {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        rangierung.setEsId(null);
        flushAndClear();

        assertNotNull(endstelle.getRangierId(), "rangierungId in endstelle not correct for following test");
        assertNull(rangierung.getEsId(), "endstelleId in rangierung not correct for following test");

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Rangierung ist nicht freigegeben\\.")
    public void testRemoveRangierungNichtFreigegeben() throws StoreException {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        endstelleBuilder.getRangierungBuilder().withFreigegeben(Freigegeben.in_Aufbau);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();

        rangierung.setBemerkung("test");
        rangierungsService.saveRangierung(rangierung, true);
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Rangierung ist historisiert\\.")
    public void testRemoveRangierungHasHistory() throws StoreException {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        rangierung.setBemerkung("test");
        rangierungsService.saveRangierung(rangierung, true);
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "In dem aktuellen Auftragsstatus darf die Rangierung nicht entfernt werden\\.")
    public void testRemoveRangierungAuftragStatusInvalid() throws StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung(null, null, AuftragStatus.PROJEKTIERUNG_ERLEDIGT, false,
                false);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Zum Auftrag der Endstelle existiert eine Physikübernahme-Protokollierung\\.")
    public void testRemoveRangierungPhysikUebernahmeProtokollierung() throws StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung(null, null, AuftragStatus.ERFASSUNG, false, true);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Carrierbestellung mit Id '[0-9]*' hat bereits eine Leitungsbezeichnung und/oder Vertragsnr\\.")
    public void testRemoveRangierungHasCBWithVtrNr() throws StoreException, StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung(null, "test123", AuftragStatus.AUS_TAIFUN_UEBERNOMMEN,
                false, false);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Carrierbestellung mit Id '[0-9]*' hat bereits eine Leitungsbezeichnung und/oder Vertragsnr\\.")
    public void testRemoveRangierungHasCBWithLbz() throws StoreException, StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung("123test", null, AuftragStatus.ERFASSUNG_SCV, false,
                false);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    @Test(expectedExceptions = StoreException.class,
            expectedExceptionsMessageRegExp = "Für Carrierbestellung mit Id '[0-9]*' existiert bereits eine elektronische TAL-Bestellung\\.")
    public void testRemoveRangierungHasCBVorgang() throws StoreException, StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung(null, null, AuftragStatus.UNDEFINIERT, true, false);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        flushAndClear();

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, null, null);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    public void testRemoveRangierung() throws FindException, StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung(null, null, AuftragStatus.ERFASSUNG, false, false);
        endstelleBuilder.getRangierungBuilder().withLeitungLfdNr(123456);
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        rangierung.setPhysikTypId(PhysikTyp.PHYSIKTYP_FTTH);
        flushAndClear();

        assertEquals(endstelle.getRangierId(), rangierung.getId(), "rangierung not assigned to endstelle");
        assertEquals(rangierung.getEsId(), endstelle.getId(), "endstelle not assigned to rangierung");
        assertEquals(rangierung.getLeitungLfdNr(), Integer.valueOf(123456), "LeitungsLfdNr incorrect");

        rangierungFreigabeService.removeRangierung(endstelle, rangierung, null, null, "Bemerkung!", null);
        flushAndClear();

        assertNull(endstelle.getRangierId(), "a rangierung assigned to endstelle");
        assertNull(rangierung.getEsId(), "an endstelle assigned to rangierung");
        assertNull(rangierung.getFreigabeAb(), "an freigabe Date is set on the rangierung");
        LocalDate today = LocalDate.now();
        assertEquals(rangierung.getGueltigBis().getTime(), today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                "freigabeAb=null, somit Freigabe ab sofort, gueltigBis auf heute");
        assertEquals(rangierung.getBemerkung(), "Bemerkung!", "bemerkung was not set");

        Endstelle resultES = getCCService(EndstellenService.class).findEndstelle(endstelle.getId());
        assertNotNull(resultES, "endstelle missing in Db");
        assertNull(resultES.getRangierId(), "a rangierung assigned to endstelle");

        assertRangierungWithDb(rangierung);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#removeRangierung(de.augustakom.hurrican.model.cc.Endstelle,
     * de.augustakom.hurrican.model.cc.Rangierung, de.augustakom.hurrican.model.cc.Rangierung)
     */
    public void testRemoveRangierungAdd() throws FindException, StoreException {
        EndstelleBuilder endstelleBuilder = generateRangierung(null, null, AuftragStatus.STORNO, false, false);
        endstelleBuilder.getRangierungBuilder().withLeitungLfdNr(123456);
        endstelleBuilder.withRangierungAdditionalBuilder(getBuilder(RangierungBuilder.class).withLeitungLfdNr(7890));
        Endstelle endstelle = endstelleBuilder.get();
        Rangierung rangierung = endstelleBuilder.getRangierungBuilder().get();
        Rangierung rangierungAdd = endstelleBuilder.getRangierungAdditionalBuilder().get();
        flushAndClear();

        assertEquals(endstelle.getRangierId(), rangierung.getId(), "rangierung not assigned to endstelle");
        assertEquals(endstelle.getRangierIdAdditional(), rangierungAdd.getId(),
                "rangierungAdd not assigned to endstelle");
        assertEquals(rangierung.getEsId(), endstelle.getId(), "endstelle not assigned to rangierung");
        assertEquals(rangierungAdd.getEsId(), endstelle.getId(), "endstelle not assigned to rangierungAdd");
        assertEquals(rangierung.getLeitungLfdNr(), Integer.valueOf(123456), "LeitungsLfdNr incorrect");
        assertEquals(rangierungAdd.getLeitungLfdNr(), Integer.valueOf(7890), "LeitungsLfdNr incorrect");

        Date freigabeAb = new java.sql.Date(DateTools.plusWorkDays(5).getTime());
        rangierungFreigabeService
                .removeRangierung(endstelle, rangierung, rangierungAdd, freigabeAb, "Bemerkung!", null);
        flushAndClear();

        assertNull(endstelle.getRangierId(), "a rangierung assigned to endstelle");
        assertEquals(rangierung.getEsId(), RANGIERUNG_NOT_ACTIVE,
                "an endstelle assigned to rangierung, or rangierung is null");
        assertEquals(rangierung.getFreigabeAb(), DateUtils.truncate(freigabeAb, Calendar.DAY_OF_MONTH),
                "freigabeAb is not correct");
        assertEquals(rangierung.getBemerkung(), "Bemerkung!", "bemerkung was not set");

        Endstelle resultES = getCCService(EndstellenService.class).findEndstelle(endstelle.getId());
        assertNotNull(resultES, "endstelle missing in Db");
        assertNull(resultES.getRangierId(), "a rangierung assigned to endstelle");

        assertRangierungWithDb(rangierung);
        assertRangierungWithDb(rangierungAdd);
    }

    /* helper */

    /**
     * generate proper db entities to test {@link RangierungsService#removeRangierung(Endstelle, Rangierung,
     * Rangierung)}
     *
     * @param physikUebernahme
     */
    private EndstelleBuilder generateRangierung(String cbLbz, String cbVtnr, Long auftragStatus,
            boolean withCbVorgang, boolean physikUebernahme) {
        CarrierbestellungBuilder carrierbestellungBuilder = null;
        if ((StringUtils.isNotBlank(cbLbz)) || (StringUtils.isNotBlank(cbVtnr)) || (withCbVorgang)) {
            carrierbestellungBuilder = getBuilder(CarrierbestellungBuilder.class).withLbz(cbLbz).withVtrNr(cbVtnr);
            carrierbestellungBuilder.build();
        }
        if (withCbVorgang) {
            getBuilder(CBVorgangBuilder.class).withCbBuilder(carrierbestellungBuilder).withSubmittedAt(new Date())
                    .withAutomation(Boolean.FALSE).build();
        }

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class);
        auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().withStatusId(auftragStatus);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withAuftragTechnikBuilder(
                auftragTechnikBuilder);
        endstelleBuilder.getRangierungBuilder().withFreigegeben(Freigegeben.freigegeben);
        if (carrierbestellungBuilder != null) {
            endstelleBuilder.withCb2EsBuilder(carrierbestellungBuilder.getCb2EsBuilder());
        }

        if (physikUebernahme) {
            PhysikUebernahmeBuilder physikUebernahmeBuilder = getBuilder(PhysikUebernahmeBuilder.class)
                    .withAuftragABuilder(auftragTechnikBuilder.getAuftragBuilder());
            physikUebernahmeBuilder.build();
        }

        return endstelleBuilder;
    }

    /**
     * Method to assert a expected Rangierung to the actual Rangierung from DB
     */
    private void assertRangierungWithDb(Rangierung expected) throws FindException {
        Rangierung actual = rangierungsService.findRangierung(expected.getId());
        assertNotNull(actual, "rangierung missing in Db");
        assertEquals(actual.getEsId(), expected.getEsId(), "endstelle id incorrect");
        assertEquals(actual.getId(), expected.getId(), "Id incorrect");
        assertEquals(actual.getBemerkung(), expected.getBemerkung(), "Bemerkung incorrect");
        assertTrue(DateUtils.isSameDay(actual.getDateW(), new Date()), "DateW incorrect");
        assertEquals(actual.getEqInId(), expected.getEqInId(), "EqInId incorrect");
        assertEquals(actual.getEqOutId(), expected.getEqOutId(), "EqOutId incorrect");
        assertEquals(actual.getFreigabeAb(), expected.getFreigabeAb(), "FreigabeAb incorrect");
        assertEquals(actual.getFreigegeben(), expected.getFreigegeben(), "Freigegeben incorrect");
        assertEquals(actual.getGueltigBis(), expected.getGueltigBis(), "GueltigBis incorrect");
        assertTrue(DateUtils.isSameDay(actual.getGueltigVon(), expected.getGueltigVon()), "GueltigVon incorrect");
        assertEquals(actual.getHvtIdStandort(), expected.getHvtIdStandort(), "HvtIdStandort incorrect");
        assertEquals(actual.getLeitungLfdNr(), expected.getLeitungLfdNr(), "LeitungLfdNr incorrect");
        assertEquals(actual.getOntId(), expected.getOntId(), "OntId incorrect");
        assertEquals(actual.getPhysikTypId(), expected.getPhysikTypId(), "PhysikTypId incorrect");
        assertEquals(actual.getRangierungsAuftragId(), expected.getRangierungsAuftragId(),
                "RangierungsAuftragId incorrect");
        assertEquals(actual.getUserW(), expected.getUserW(), "UserW incorrect");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRangierungFreigebenIllegalRangierId() throws Exception {
        rangierungFreigabeService.rangierungenFreigeben(null, null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRangierungFreigebenIllegalSessionId() throws Exception {
        rangierungFreigabeService.rangierungenFreigeben(1L, null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRangierungFreigebenException4NotSameLeitungGesamtId() throws Exception {
        Rangierung rangierung = getBuilder(RangierungBuilder.class).withLeitungGesamtId(12345)
                .withBemerkung("Test4LeitungGesamtId Bemerkung").withRandomId().build();
        Rangierung rangierungAdd = getBuilder(RangierungBuilder.class).withLeitungGesamtId(12346)
                .withBemerkung("Test4LeitungGesamtId Bemerkung Add").withRandomId().build();
        flushAndClear();

        rangierungFreigabeService.rangierungenFreigeben(rangierung.getId(), rangierungAdd.getId(), getSessionId());
    }

    @DataProvider
    public Object[][] dataProviderRangierungFreigeben() {
        return new Object[][] {
                { RANGIERUNG_NOT_ACTIVE, null, false, null, null, true },
                { RANGIERUNG_NOT_ACTIVE, date(-1), false, null, null, true },
                { RANGIERUNG_NOT_ACTIVE, date(+1), false, null, null, false },
                { 181918383L, date(-1), false, null, null, false },
                { 181918383L, date(+1), false, null, null, false },

                { RANGIERUNG_NOT_ACTIVE, date(-1), true, null, date(0), false },
                { RANGIERUNG_NOT_ACTIVE, date(+1), true, null, date(0), false },
                { 181918383L, date(-1), true, null, date(0), false },
                { 181918383L, date(+1), true, null, date(0), false },
                { RANGIERUNG_NOT_ACTIVE, date(-1), true, RANGIERUNG_NOT_ACTIVE,
                        date(-1), true },
                { RANGIERUNG_NOT_ACTIVE, date(+1), true, RANGIERUNG_NOT_ACTIVE,
                        date(-1), false },
                { RANGIERUNG_NOT_ACTIVE, date(-1), true, RANGIERUNG_NOT_ACTIVE,
                        date(+1), false },
                { RANGIERUNG_NOT_ACTIVE, date(+1), true, RANGIERUNG_NOT_ACTIVE,
                        date(+1), false },
                { 181918383L, date(-1), true, RANGIERUNG_NOT_ACTIVE, date(-1), false },
                { 181918383L, date(+1), true, RANGIERUNG_NOT_ACTIVE, date(+1), false },
                { RANGIERUNG_NOT_ACTIVE, date(-1), true, 181918383L, date(+1), false },
                { RANGIERUNG_NOT_ACTIVE, date(+1), true, 181918383L, date(-1), false },
                { 181918383L, date(-1), true, 1819234411L, date(-1), false },
                { 181918383L, date(+1), true, 1819234411L, date(-1), false },
        };
    }

    private Date date(int daysFromNow) {
        Date now = new Date();
        return new Date(now.getTime() + (daysFromNow * 24L * 60L * 60L * 1000L));
    }

    @Test(dataProvider = "dataProviderRangierungFreigeben")
    public void testRangierungFreigeben(Long rangEsId, Date freigabeAb, boolean withRangAdd, Long rangEsIdAdd,
            Date freigabeAbAdd, boolean expectedResult) throws Exception {
        Rangierung rangierung = getBuilder(RangierungBuilder.class).withEsId(rangEsId).withFreigabeAb(freigabeAb)
                .withBemerkung("Test Bemerkung").withRandomId().build();
        Rangierung rangierungAdd = new Rangierung();
        if (withRangAdd) {
            rangierungAdd = getBuilder(RangierungBuilder.class).withEsId(rangEsIdAdd).withFreigabeAb(freigabeAbAdd)
                    .withBemerkung("Test Bemerkung Add").withRandomId().build();
        }
        flushAndClear();

        assertEquals(rangierungFreigabeService.rangierungenFreigeben(rangierung.getId(), rangierungAdd.getId(),
                getSessionId()), expectedResult);
        flushAndClear();

        Rangierung result = rangierungsService.findRangierung(rangierung.getId());
        Rangierung resultAdd = rangierungsService.findRangierung(rangierungAdd.getId());

        assertNotNull(result);
        // prüfe: x == null und expectedResult - ^ gibt true wenn eins von beiden checks true ist (xor)
        assertFalse((result.getBemerkung() == null) ^ expectedResult);
        assertFalse((result.getEsId() == null) ^ expectedResult);
        assertFalse((result.getFreigabeAb() == null) ^ expectedResult);
        if (withRangAdd) {
            assertFalse((resultAdd.getBemerkung() == null) ^ expectedResult);
            if (rangEsIdAdd == null) {
                assertNull(resultAdd.getEsId());
            }
            else {
                assertFalse((resultAdd.getEsId() == null) ^ expectedResult);
            }
            assertFalse((resultAdd.getFreigabeAb() == null) ^ expectedResult);
        }
    }

}
