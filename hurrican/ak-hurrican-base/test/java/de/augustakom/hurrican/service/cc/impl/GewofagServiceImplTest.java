/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 12:11:14
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GewofagWohnung;
import de.augustakom.hurrican.model.cc.GewofagWohnungBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HwBaugruppeToPhysikTyp;
import de.augustakom.hurrican.model.tools.StandortHvtBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DatabaseTestTools;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.GewofagService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.SERVICE)
public class GewofagServiceImplTest extends AbstractHurricanBaseServiceTest {

    private class TestProduktePhysikTypen {
        public PhysikTypBuilder physikTypAdsl;
        public PhysikTypBuilder physikTypAb;
        public PhysikTypBuilder physikTypUk0;
        public ProduktBuilder produktDslUndAnalog;
        public ProduktBuilder produktDslUndIsdn;
        public ProduktBuilder produktIsdn;
    }

    private GewofagService gewofagService;
    private RangierungsService rangierungsService;
    private EndstellenService endstellenService;
    private DatabaseTestTools tools;

    @BeforeMethod(groups = BaseTest.SERVICE)
    public void initTests() {
        gewofagService = getCCService(GewofagService.class);
        rangierungsService = getCCService(RangierungsService.class);
        endstellenService = getCCService(EndstellenService.class);
        tools = getCCService(DatabaseTestTools.class);
    }

    public void testFindGewofagWohnung() throws FindException {
        GewofagWohnung gewofagWohnung = getBuilder(GewofagWohnungBuilder.class).build();
        GewofagWohnung foundGewofagWohnung = gewofagService.findGewofagWohnung(gewofagWohnung.getId());
        assertEquals(foundGewofagWohnung, gewofagWohnung);
    }

    public void testSaveGewofagWohnung() throws StoreException, FindException {
        GewofagWohnung gewofagWohnung = getBuilder(GewofagWohnungBuilder.class).setPersist(false).build();
        gewofagService.saveGewofagWohnung(gewofagWohnung);
        assertNotNull(gewofagWohnung.getId());
        GewofagWohnung foundGewofagWohnung = gewofagService.findGewofagWohnung(gewofagWohnung.getId());
        assertEquals(foundGewofagWohnung, gewofagWohnung);
    }

    public void testWohnungsZuordnung() throws StoreException, FindException {
        // Preparation
        StandortHvtBuilder standortHvtBuilder = new StandortHvtBuilder();
        StandortHvtBuilder.StandortHvt standort = standortHvtBuilder.prepare(this, null);
        standortHvtBuilder.build(standort);
        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);
        TestProduktePhysikTypen produktePhysikTypen = createPhysikTypMappings(standort);

        GewofagWohnungBuilder[] gewofagWohnungBuilder = createGewofagWohnungen(standort, geoIdBuilder);

        rangierungErstellen(standort, geoIdBuilder, produktePhysikTypen.produktDslUndIsdn,
                gewofagWohnungBuilder[0].getEquipmentBuilder(), standort.adslInEquipment[0],
                produktePhysikTypen.physikTypAdsl, standort.adslOutEquipment[0], standort.uk0Equipment[1],
                produktePhysikTypen.physikTypUk0);

        EndstelleBuilder endstelleBuilder2 = endstelleErstellen(standort, geoIdBuilder,
                produktePhysikTypen.produktDslUndIsdn);

        // Invocation
        flushAndClear();
        gewofagService.wohnungsZuordnung(endstelleBuilder2.get(), gewofagWohnungBuilder[1].get(), -1L);
        flushAndClear();

        // Asserts
        Long auftragId = endstelleBuilder2.getEndstelleGruppeBuilder().getAuftragTechnikBuilder()
                .getAuftragBuilder().getAuftragDatenBuilder().get().getAuftragId();
        Rangierung[] rangierungen = rangierungsService.findRangierungenTx(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);

        assertNotNull(rangierungen);
        assertEquals(rangierungen.length, 2, "2 Rangierungen erwartet");

        assertEndstelleHasRangierungen(endstelle, rangierungen[0], rangierungen[1]);

        assertRangiert(rangierungen[0], new EquipmentBuilder[] { gewofagWohnungBuilder[1].getEquipmentBuilder() }, null,
                standort.adslInEquipment, new int[] { 0 });
        assertRangiert(rangierungen[1], standort.adslOutEquipment, new int[] { 0 }, standort.uk0Equipment,
                new int[] { 1 });
    }

    public void testWohnungsZuordnungUseExisting() throws StoreException, FindException {
        // Preparation
        StandortHvtBuilder standortHvtBuilder = new StandortHvtBuilder();
        StandortHvtBuilder.StandortHvt standort = standortHvtBuilder.prepare(this, null);
        standortHvtBuilder.build(standort);
        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);
        TestProduktePhysikTypen produktePhysikTypen = createPhysikTypMappings(standort);

        GewofagWohnungBuilder[] gewofagWohnungBuilder = createGewofagWohnungen(standort, geoIdBuilder);

        EndstelleBuilder endstelleBuilder1 = rangierungErstellen(standort, geoIdBuilder,
                produktePhysikTypen.produktDslUndAnalog, gewofagWohnungBuilder[2].getEquipmentBuilder(),
                standort.adslInEquipment[3], produktePhysikTypen.physikTypAdsl, standort.adslOutEquipment[3],
                standort.abEquipment[1], produktePhysikTypen.physikTypAb);
        setEndstelleFrei(endstelleBuilder1);

        EndstelleBuilder endstelleBuilder2 = endstelleErstellen(standort, geoIdBuilder,
                produktePhysikTypen.produktDslUndAnalog);

        // Invocation
        flushAndClear();
        gewofagService.wohnungsZuordnung(endstelleBuilder2.get(), gewofagWohnungBuilder[2].get(), -1L); // out: 885208,
        // in: 885250
        flushAndClear();

        // Asserts
        Long auftragId = endstelleBuilder2.getEndstelleGruppeBuilder().getAuftragTechnikBuilder()
                .getAuftragBuilder().getAuftragDatenBuilder().get().getAuftragId();
        Rangierung[] rangierungen = rangierungsService.findRangierungenTx(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);

        assertNotNull(rangierungen);
        assertEquals(rangierungen.length, 2, "2 Rangierungen erwartet");

        assertEndstelleHasRangierungen(endstelle, rangierungen[0], rangierungen[1]);

        assertRangiertExact(rangierungen[0], gewofagWohnungBuilder[2].getEquipmentBuilder(),
                standort.adslInEquipment[3]);
        assertRangiertExact(rangierungen[1], standort.adslOutEquipment[3], standort.abEquipment[1]);
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.GewofagServiceImpl#wohnungsZuordnung(de.augustakom.hurrican.model.cc.Endstelle,
     * GewofagWohnung, Long)} .
     */
    public void testWohnungsZuordnungCreateNew() throws StoreException, FindException {
        // Preparation
        StandortHvtBuilder standortHvtBuilder = new StandortHvtBuilder();
        StandortHvtBuilder.StandortHvt standort = standortHvtBuilder.prepare(this, null);
        standortHvtBuilder.build(standort);
        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);
        TestProduktePhysikTypen produktePhysikTypen = createPhysikTypMappings(standort);

        GewofagWohnungBuilder[] gewofagWohnungBuilder = createGewofagWohnungen(standort, geoIdBuilder);

        EndstelleBuilder endstelleBuilder1 = rangierungErstellen(standort, geoIdBuilder,
                produktePhysikTypen.produktDslUndAnalog, gewofagWohnungBuilder[2].getEquipmentBuilder(),
                standort.adslInEquipment[0], produktePhysikTypen.physikTypAdsl, standort.adslOutEquipment[0],
                standort.abEquipment[1], produktePhysikTypen.physikTypAb);
        setEndstelleFrei(endstelleBuilder1);

        EndstelleBuilder endstelleBuilder2 = endstelleErstellen(standort, geoIdBuilder,
                produktePhysikTypen.produktDslUndIsdn);

        // Invocation
        flushAndClear();
        gewofagService.wohnungsZuordnung(endstelleBuilder2.get(), gewofagWohnungBuilder[2].get(), -1L); // out: 885208,
        // in: 885250
        flushAndClear();

        // Asserts
        Long auftragId = endstelleBuilder2.getEndstelleGruppeBuilder().getAuftragTechnikBuilder()
                .getAuftragBuilder().getAuftragDatenBuilder().get().getAuftragId();
        Rangierung[] rangierungen = rangierungsService.findRangierungenTx(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        Rangierung[] oldRangierungen = rangierungsService.findRangierungenTx(endstelleBuilder1
                .getEndstelleGruppeBuilder().getAuftragTechnikBuilder().getAuftragBuilder().getAuftragDatenBuilder()
                .get().getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);

        assertNotNull(rangierungen);
        assertEquals(rangierungen.length, 2, "2 Rangierungen erwartet");

        assertEndstelleHasRangierungen(endstelle, rangierungen[0], rangierungen[1]);

        assertRangiert(rangierungen[0], new EquipmentBuilder[] { gewofagWohnungBuilder[2].getEquipmentBuilder() }, null,
                standort.adslInEquipment, new int[] { 0 });
        assertRangiert(rangierungen[1], standort.adslOutEquipment, new int[] { 0 }, standort.uk0Equipment, null);

        assertEquals(oldRangierungen[0].getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE);
        assertEquals(oldRangierungen[1].getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE);
        assertEquals(rangierungen[0].getHistoryFrom(), oldRangierungen[0].getId());
        assertEquals(rangierungen[0].getHistoryCount(), Integer.valueOf(1));
    }

    /**
     * Ueberprueft ob die Rangierungen der Endstelle korrekt zugeordnet sind
     */
    private void assertEndstelleHasRangierungen(Endstelle endstelle, Rangierung rangierung1, Rangierung rangierung2) {
        assertEquals(rangierung1.getEsId(), endstelle.getId());
        assertEquals(rangierung2.getEsId(), endstelle.getId());
        assertEquals(endstelle.getRangierId(), rangierung1.getId());
        assertEquals(endstelle.getRangierIdAdditional(), rangierung2.getId());
    }

    /**
     * Ueberprueft ob die beiden Equipments an der Rangierung haengen und rangiert sind
     */
    private void assertRangiert(Rangierung rangierung, EquipmentBuilder[] eqOuts, int[] badEqOuts,
            EquipmentBuilder[] eqIns, int[] badEqIns) throws FindException {
        int eqOutIndex = -1;
        for (int i = 0; (i < eqOuts.length) && (eqOutIndex < 0); ++i) {
            if (!ArrayUtils.contains(badEqOuts, i)) {
                if (rangierung.getEqOutId().equals(eqOuts[i].get().getId())) {
                    eqOutIndex = i;
                }
            }
        }
        assertTrue(eqOutIndex >= 0, "eqOut not in list of expected eqOuts");
        int eqInIndex = -1;
        for (int i = 0; (i < eqIns.length) && (eqInIndex < 0); ++i) {
            if (!ArrayUtils.contains(badEqIns, i)) {
                if (rangierung.getEqInId().equals(eqIns[i].get().getId())) {
                    eqInIndex = i;
                }
            }
        }
        assertTrue(eqInIndex >= 0, "eqIn not in list of expected eqIns");
        assertRangiertExact(rangierung, eqOuts[eqOutIndex], eqIns[eqInIndex]);
    }

    /**
     * Ueberprueft ob die beiden Equipments an der Rangierung haengen und rangiert sind
     */
    private void assertRangiertExact(Rangierung rangierung, EquipmentBuilder eqOut, EquipmentBuilder eqIn)
            throws FindException {
        assertEquals(rangierung.getFreigegeben(), Freigegeben.freigegeben);
        assertEquals(rangierung.getGueltigBis(), DateTools.getHurricanEndDate());
        assertEquals(rangierung.getEqOutId(), eqOut.get().getId());
        assertEquals(rangierung.getEqInId(), eqIn.get().getId());
        if (rangierung.getEquipmentOut() == null) {
            rangierungsService.loadEquipments(rangierung);
        }
        assertEquals(rangierung.getEquipmentIn().getStatus(), EqStatus.rang);
        assertEquals(rangierung.getEquipmentOut().getStatus(), EqStatus.rang);
    }

    /**
     * Erstellt eine Endstelle ohne Rangierungen
     */
    private EndstelleBuilder endstelleErstellen(StandortHvtBuilder.StandortHvt standort, GeoIdBuilder geoIdBuilder,
            ProduktBuilder produkt) {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(standort.standort).withRangierungBuilder(null)
                .withRangierungAdditionalBuilder(null);
        endstelleBuilder.getEndstelleGruppeBuilder().withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class));

        AuftragDatenBuilder auftragDatenBuilder2 = endstelleBuilder.getEndstelleGruppeBuilder()
                .getAuftragTechnikBuilder().getAuftragBuilder().getAuftragDatenBuilder();
        auftragDatenBuilder2.withProdBuilder(produkt);

        endstelleBuilder.get();
        return endstelleBuilder;
    }

    /**
     * Setzt die Endstelle auf Frei
     */
    private void setEndstelleFrei(EndstelleBuilder endstelleBuilder) {
        endstelleBuilder.getRangierungBuilder().get().setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
        tools.save(endstelleBuilder.getRangierungBuilder().get());

        endstelleBuilder.getRangierungAdditionalBuilder().get().setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
        tools.save(endstelleBuilder.getRangierungAdditionalBuilder().get());
    }

    /**
     * Erstellt eine Endstelle mit aktiven Rangierungen mit den gegebenen Ports.
     */
    private EndstelleBuilder rangierungErstellen(StandortHvtBuilder.StandortHvt standort, GeoIdBuilder geoIdBuilder,
            ProduktBuilder produkt, EquipmentBuilder eqOut1, EquipmentBuilder eqIn1,
            PhysikTypBuilder physikTypBuilder1, EquipmentBuilder eqOut2, EquipmentBuilder eqIn2,
            PhysikTypBuilder physikTypBuilder2) {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(standort.standort)
                .withRangierungAdditionalBuilder(
                        getBuilder(RangierungBuilder.class).withEqOutBuilder(eqOut2).withEqInBuilder(eqIn2)
                                .withPhysikTypBuilder(physikTypBuilder2).withRandomLeitungGesamtId()
                                .withLeitungLfdNr(Integer.valueOf(2))
                );
        endstelleBuilder.getRangierungBuilder().withEqOutBuilder(eqOut1).withEqInBuilder(eqIn1)
                .withPhysikTypBuilder(physikTypBuilder1)
                .withLeitungGesamtId(endstelleBuilder.getRangierungAdditionalBuilder().getLeitungGesamtId())
                .withLeitungLfdNr(Integer.valueOf(1));
        endstelleBuilder.getEndstelleGruppeBuilder().withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class));

        setEquipmentRangiert(eqOut1, EqStatus.rang);
        setEquipmentRangiert(eqIn1, EqStatus.rang);
        setEquipmentRangiert(eqOut2, EqStatus.rang);
        setEquipmentRangiert(eqIn2, EqStatus.rang);

        AuftragDatenBuilder auftragDatenBuilder1 = endstelleBuilder.getEndstelleGruppeBuilder()
                .getAuftragTechnikBuilder().getAuftragBuilder().getAuftragDatenBuilder();
        auftragDatenBuilder1.withProdBuilder(produkt);

        endstelleBuilder.get();
        return endstelleBuilder;
    }

    private void setEquipmentRangiert(EquipmentBuilder equipmentBuilder, EqStatus status) {
        equipmentBuilder.get().setStatus(status);
        tools.save(equipmentBuilder.get());
    }

    /**
     * Soviele Wohnungen und Wohnungsports wie ADSL Ports vorhanden sind erstellen
     */
    private GewofagWohnungBuilder[] createGewofagWohnungen(StandortHvtBuilder.StandortHvt standort, GeoIdBuilder geoIdBuilder) {
        int num = standort.adslBaugruppe.getHwBaugruppenTypBuilder().get().getPortCount();
        GewofagWohnungBuilder[] gewofagWohnungBuilder = new GewofagWohnungBuilder[num];
        for (int i = 0; i < num; ++i) {
            gewofagWohnungBuilder[i] = getBuilder(GewofagWohnungBuilder.class).withEtage(String.format("%d. OG", i))
                    .withName(String.format("999.%03d", i)).withGeoIdBuilder(geoIdBuilder);
            gewofagWohnungBuilder[i].getEquipmentBuilder().withBaugruppeBuilder(null).withHwEQN(null)
                    .withRangSSType(null).withHwSchnittstelle(null).withRangVerteiler("2").withRangBucht("2")
                    .withRangLeiste1(String.format("%d", (i / 2) + 1)).withRangStift1(String.format("%d", (i + 1) % 8))
                    .withCarrier("GEWOFAG");
            gewofagWohnungBuilder[i].getEquipmentBuilder().withHvtStandortBuilder(standort.standort);
            gewofagWohnungBuilder[i].get();
        }
        return gewofagWohnungBuilder;
    }

    /**
     * Erstellt Mappings, welcher PhysikTyp durch welche Baugruppe realisiert werden kann
     */
    private TestProduktePhysikTypen createPhysikTypMappings(StandortHvtBuilder.StandortHvt standort) {
        TestProduktePhysikTypen produktePhysikTypen = new TestProduktePhysikTypen();

        produktePhysikTypen.physikTypAb = getBuilder(PhysikTypBuilder.class).withRandomId()
                .withNameRandomSuffix("Test-PhysikTyp-Phone")
                .withHvtTechnikBuilder(standort.abBaugruppe.getHwBaugruppenTypBuilder().getHvtTechnikBuilder());
        produktePhysikTypen.physikTypUk0 = getBuilder(PhysikTypBuilder.class).withRandomId()
                .withNameRandomSuffix("Test-PhysikTyp-Uk0")
                .withHvtTechnikBuilder(standort.uk0Baugruppe.getHwBaugruppenTypBuilder().getHvtTechnikBuilder());
        produktePhysikTypen.physikTypAdsl = getBuilder(PhysikTypBuilder.class).withRandomId()
                .withNameRandomSuffix("Test-PhysikTyp-ADSL")
                .withHvtTechnikBuilder(standort.adslBaugruppe.getHwBaugruppenTypBuilder().getHvtTechnikBuilder())
                .withPtGroup(PhysikTyp.PT_GROUP_DATA);

        produktePhysikTypen.produktDslUndAnalog = getBuilder(ProduktBuilder.class);
        produktePhysikTypen.produktDslUndIsdn = getBuilder(ProduktBuilder.class);
        produktePhysikTypen.produktIsdn = getBuilder(ProduktBuilder.class);

        getBuilder(Produkt2PhysikTypBuilder.class).withRandomId()
                .withProduktBuilder(produktePhysikTypen.produktDslUndAnalog)
                .withPhysikTypBuilder(produktePhysikTypen.physikTypAdsl)
                .withPhysikTypAdditionalBuilder(produktePhysikTypen.physikTypAb).get();
        getBuilder(Produkt2PhysikTypBuilder.class).withRandomId()
                .withProduktBuilder(produktePhysikTypen.produktDslUndIsdn)
                .withPhysikTypBuilder(produktePhysikTypen.physikTypAdsl)
                .withPhysikTypAdditionalBuilder(produktePhysikTypen.physikTypUk0).get();
        getBuilder(Produkt2PhysikTypBuilder.class).withRandomId().withProduktBuilder(produktePhysikTypen.produktIsdn)
                .withPhysikTypBuilder(produktePhysikTypen.physikTypUk0).get();

        HwBaugruppeToPhysikTyp bg2pt = new HwBaugruppeToPhysikTyp();
        bg2pt.setHwBaugruppenTypId(standort.adslBaugruppe.getHwBaugruppenTypBuilder().get().getId());
        bg2pt.setPhysikTypId(produktePhysikTypen.physikTypAdsl.get().getId());
        tools.save(bg2pt);

        bg2pt = new HwBaugruppeToPhysikTyp();
        bg2pt.setHwBaugruppenTypId(standort.abBaugruppe.getHwBaugruppenTypBuilder().get().getId());
        bg2pt.setPhysikTypId(produktePhysikTypen.physikTypAb.get().getId());
        tools.save(bg2pt);

        bg2pt = new HwBaugruppeToPhysikTyp();
        bg2pt.setHwBaugruppenTypId(standort.uk0Baugruppe.getHwBaugruppenTypBuilder().get().getId());
        bg2pt.setPhysikTypId(produktePhysikTypen.physikTypUk0.get().getId());
        tools.save(bg2pt);

        return produktePhysikTypen;
    }
}
