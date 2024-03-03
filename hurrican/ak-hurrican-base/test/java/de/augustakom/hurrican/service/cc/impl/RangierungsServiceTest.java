/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 12:15:50
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.hurrican.model.cc.Rangierung.*;
import static org.testng.Assert.*;

import java.text.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWRackBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.*;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.RangierungsmatrixBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.UEVTBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = { BaseTest.SERVICE })
public class RangierungsServiceTest extends AbstractHurricanBaseServiceTest implements IServiceCallback {

    private static final String HW_EQN = "12345";
    private static final Logger LOGGER = Logger.getLogger(RangierungsServiceTest.class);

    @Autowired
    private RangierungDAO rangierungDAO;

    @Test
    public void testSaveRangierung() throws FindException, StoreException {
        RangierungsService rs = getRangierungsService();
        Rangierung toSave = getBuilder(RangierungBuilder.class).setPersist(false).build();
        toSave.setGueltigVon(null);
        Rangierung rangierung = rs.saveRangierung(toSave, true);
        assertTrue(DateTools.isDateEqual(rangierung.getGueltigVon(), new Date()));
        assertTrue(DateTools.isHurricanEndDate(rangierung.getGueltigBis()));
    }

    public void testSaveRangierungWithHistory() throws StoreException {
        Date today = new Date();
        RangierungsService sut = getRangierungsService();
        Rangierung toSave = getBuilder(RangierungBuilder.class).withBemerkung("asdf").build();
        toSave.setBemerkung("Testbemerkung um Historisierung zu testen");

        Rangierung rangierungNewVersion = sut.saveRangierung(toSave, true);
        rangierungNewVersion = rangierungDAO.findById(rangierungNewVersion.getId(), Rangierung.class);

        Rangierung rangierungHistorized = rangierungDAO.findById(toSave.getId(), Rangierung.class);

        assertTrue(DateTools.isHurricanEndDate(rangierungNewVersion.getGueltigBis()));
        assertTrue(DateTools.isDateEqual(rangierungNewVersion.getGueltigVon(), today));
        assertEquals(rangierungNewVersion.getBemerkung(), toSave.getBemerkung());
        assertTrue(DateTools.isDateEqual(rangierungNewVersion.getGueltigVon(), today));
        assertEquals(rangierungNewVersion.getHistoryFrom(), toSave.getId());

        assertNotEquals(rangierungHistorized.getBemerkung(), toSave.getBemerkung());
        assertTrue(DateTools.isDateEqual(rangierungHistorized.getGueltigBis(), today));

        assertNotEquals(rangierungNewVersion.getId(), rangierungHistorized.getId());
    }

    public void testSaveRangierungWithoutHistory() throws Exception {
        RangierungsService sut = getRangierungsService();
        Rangierung toSave = getBuilder(RangierungBuilder.class).withBemerkung("fdsa").build();
        toSave.setBemerkung("Testbemerkung um Historisierung zu testen");
        toSave.setGueltigVon(DateTools.minusWorkDays(10));
        toSave.setGueltigBis(DateTools.plusWorkDays(20));

        Rangierung rangierungSaved = sut.saveRangierung(toSave, false);
        rangierungSaved = rangierungDAO.findById(rangierungSaved.getId(), Rangierung.class);
        assertEquals(rangierungSaved.getBemerkung(), toSave.getBemerkung());
        assertTrue(DateTools.isDateEqual(rangierungSaved.getGueltigVon(), toSave.getGueltigVon()));
        assertTrue(DateTools.isDateEqual(rangierungSaved.getGueltigBis(), toSave.getGueltigBis()));
        assertEquals(rangierungSaved.getId(), toSave.getId());
    }

    public void testfindEquipmentByBaugruppe() throws FindException {
        Equipment expectedEquipment = getBuilder(EquipmentBuilder.class)
                .withHwEQN(HW_EQN)
                .withRangSSType(Equipment.HW_SCHNITTSTELLE_SDSL_OUT)
                .build();

        Equipment equipment = getRangierungsService().findEquipmentByBaugruppe(expectedEquipment.getHwBaugruppenId(),
                HW_EQN, Equipment.HW_SCHNITTSTELLE_SDSL_OUT);

        assertEquals(equipment, expectedEquipment);
    }

    public void testfindEquipmentByBaugruppeWithNull() throws FindException {
        Equipment expectedEquipment = getBuilder(EquipmentBuilder.class)
                .withHwEQN(HW_EQN)
                .withRangSSType(Equipment.HW_SCHNITTSTELLE_SDSL_OUT)
                .build();

        Equipment equipment = getRangierungsService().findEquipmentByBaugruppe(null,
                HW_EQN, Equipment.HW_SCHNITTSTELLE_SDSL_OUT);

        assertEquals(equipment, expectedEquipment);
    }

    public void testIsPortInADslam() throws FindException {
        assertTrue(getRangierungsService().isPortInADslam(
                getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(
                        getBuilder(HWBaugruppeBuilder.class).withRackBuilder(getBuilder(HWDslamBuilder.class)))
                        .build()
        ));
    }

    public void testIsPortInNotInADslam() throws FindException {
        assertFalse(getRangierungsService().isPortInADslam(
                getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(
                        getBuilder(HWBaugruppeBuilder.class).withRackBuilder(getBuilder(HWDluBuilder.class)))
                        .build()
        ));
    }

    public void testFindMatrix() throws FindException {
        UEVTBuilder uevtBuilder = getBuilder(UEVTBuilder.class)
                .withUevt("0000")
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class));

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withAnschlussart("test");

        Produkt2PhysikTypBuilder p2ptBuilder = getBuilder(Produkt2PhysikTypBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withPhysikTypBuilder(getBuilder(PhysikTypBuilder.class));

        Integer highPrio = 10;
        Integer midPrio = 5;

        getBuilder(RangierungsmatrixBuilder.class)
                .withUevtBuilder(uevtBuilder)
                .withProduktBuilder(produktBuilder)
                .withProdukt2PhysikTypBuilder(p2ptBuilder)
                .withPriority(highPrio)
                .build();

        getBuilder(RangierungsmatrixBuilder.class)
                .withUevtBuilder(uevtBuilder)
                .withProduktBuilder(produktBuilder)
                .withProdukt2PhysikTypBuilder(p2ptBuilder)
                .withPriority(midPrio)
                .build();

        getBuilder(RangierungsmatrixBuilder.class)
                .withUevtBuilder(uevtBuilder)
                .withProduktBuilder(produktBuilder)
                .withProdukt2PhysikTypBuilder(p2ptBuilder)
                .withPriority(null)
                .build();

        RangierungsmatrixQuery query = new RangierungsmatrixQuery();
        query.setProduktId(produktBuilder.get().getProdId());
        query.setUevtId(uevtBuilder.get().getId());

        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        List<Rangierungsmatrix> result = rangierungsService.findMatrix(query);
        assertNotEmpty(result, "Matrix not found!");
        assertEquals(result.size(), 3, "Count of Matrix not ok!");
        assertEquals(result.get(0).getPriority(), highPrio, "Sorting not OK!");
        assertEquals(result.get(1).getPriority(), midPrio, "Sorting not OK!");
        assertEquals(result.get(2).getPriority(), null, "Sorting not OK!");
    }


    public void testFindMatrixWithProdukt2PhysiktypPrio() throws FindException {
        HVTStandortBuilder hvtStdBuilder = getBuilder(HVTStandortBuilder.class);

        UEVTBuilder uevtBuilder = getBuilder(UEVTBuilder.class)
                .withUevt("0000")
                .withHvtStandortBuilder(hvtStdBuilder);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withAnschlussart("test");

        Produkt2PhysikTypBuilder p2ptHighPrio = getBuilder(Produkt2PhysikTypBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withPhysikTypBuilder(getBuilder(PhysikTypBuilder.class))
                .withPriority(10L);

        Produkt2PhysikTypBuilder p2ptHighPrioRangierMatrix = getBuilder(Produkt2PhysikTypBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withPhysikTypBuilder(getBuilder(PhysikTypBuilder.class))
                .withPriority(5L);

        Produkt2PhysikTypBuilder p2ptNoPrio = getBuilder(Produkt2PhysikTypBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withPhysikTypBuilder(getBuilder(PhysikTypBuilder.class))
                .withPriority(null);

        getBuilder(RangierungsmatrixBuilder.class)
                .withUevtBuilder(uevtBuilder)
                .withProduktBuilder(produktBuilder)
                .withProdukt2PhysikTypBuilder(p2ptHighPrio)
                .build();

        getBuilder(RangierungsmatrixBuilder.class)
                .withUevtBuilder(uevtBuilder)
                .withProduktBuilder(produktBuilder)
                .withProdukt2PhysikTypBuilder(p2ptHighPrioRangierMatrix)
                .withPriority(100)   // Prio auf Rangierungsmatrix soll Vorrang vor Prio auf Produkt2Physiktyp haben!
                .build();

        getBuilder(RangierungsmatrixBuilder.class)
                .withUevtBuilder(uevtBuilder)
                .withProduktBuilder(produktBuilder)
                .withProdukt2PhysikTypBuilder(p2ptNoPrio)
                .build();

        RangierungsmatrixQuery query = new RangierungsmatrixQuery();
        query.setProduktId(produktBuilder.get().getProdId());
        query.setUevtId(uevtBuilder.get().getId());
        query.setHvtIdStandort(hvtStdBuilder.get().getId());

        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        List<Rangierungsmatrix> result = rangierungsService.findMatrix(query);
        assertNotEmpty(result, "Matrix not found!");
        assertEquals(result.size(), 3, "Count of Matrix not ok!");
        assertEquals(result.get(0).getProdukt2PhysikTypId(), p2ptHighPrioRangierMatrix.get().getId(), "Sorting not OK!");  // mid prio zuerst, da Prio von Rangierungsmatrix Vorrang hat!
        assertEquals(result.get(1).getProdukt2PhysikTypId(), p2ptHighPrio.get().getId(), "Sorting not OK!");
        assertEquals(result.get(2).getProdukt2PhysikTypId(), p2ptNoPrio.get().getId(), "Sorting not OK!");
    }


    public void testChangeEqOut() throws Exception {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleGruppeBuilder(
                getBuilder(AuftragTechnik2EndstelleBuilder.class)).withHvtStandortBuilder(hvtStandortBuilder)
                .withoutRangierung();
        EndstelleBuilder endstelleDestBuilder = getBuilder(EndstelleBuilder.class).withEndstelleGruppeBuilder(
                getBuilder(AuftragTechnik2EndstelleBuilder.class)).withHvtStandortBuilder(hvtStandortBuilder)
                .withoutRangierung();

        HVTTechnikBuilder hwProducerBuilder = getBuilder(HVTTechnikBuilder.class).toAlcatel();
        HWDslamBuilder rackBuilder = getBuilder(HWDslamBuilder.class).withHvtStandortBuilder(hvtStandortBuilder)
                .withHwProducerBuilder(hwProducerBuilder);
        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class).withRackBuilder(rackBuilder);
        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withRackBuilder(rackBuilder)
                .withSubrackBuilder(subrackBuilder);
        EquipmentBuilder eqInBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(hwBaugruppeBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder);
        EquipmentBuilder eqInDestBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(hwBaugruppeBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder);
        Rangierung rangSrc = getBuilder(RangierungBuilder.class)
                .withFreigegeben(Freigegeben.freigegeben)
                .withEndstelleBuilder(endstelleBuilder)
                .withEqInBuilder(eqInBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .build();

        Endstelle endstelle = endstelleBuilder.build();
        endstelle.setRangierId(rangSrc.getId());

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withRandomId();
        getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(auftragBuilder).withStatusId(AuftragStatus.IN_BETRIEB)
                .build();
        getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder)
                .withEndstelleBuilder(endstelleBuilder).build();

        Rangierung rangDest = getBuilder(RangierungBuilder.class).withFreigegeben(Freigegeben.freigegeben)
                .withEndstelleBuilder(endstelleDestBuilder).withEqInBuilder(eqInDestBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder).build();

        Reference changeReason = getBuilder(ReferenceBuilder.class)
                .withIntValue(9)
                .withRandomId()
                .build();

        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        List<Rangierung> result = rangierungsService.changeEqOut(changeReason, endstelle, rangSrc, null, rangDest,
                null, -1L);
        assertNotEmpty(result, "No result generated!");
        assertEquals(result.size(), 4);
        assertNotSame(endstelle.getRangierId(), rangSrc.getId(),
                "Endstelle hat weiterhin gleiche Rangierung zugeordnet!");
        assertNotNull(endstelle.getRangierId());

        for (Rangierung rangierung : result) {
            if (NumberTools.equal(rangierung.getHistoryFrom(), rangSrc.getId())) {
                assertEquals(Freigegeben.getFreigegeben(changeReason.getIntValue()), rangierung.getFreigegeben());
            }
            else if (NumberTools.equal(rangierung.getHistoryFrom(), rangDest.getId())) {
                assertEquals(rangierung.getEsId(), endstelle.getId());
                assertEquals(endstelle.getRangierId(), rangierung.getId());
            }
        }
    }

    @Test(enabled = false)
    public void testAssignRangierung2ES() throws FindException, StoreException {
        Long esId = 248831L;
        Rangierung result = getRangierungsService().assignRangierung2ES(esId, null, null);
        assertNotNull(result, "Rangierung konnte der Endstelle " + esId + " nicht zugeordnet werden!");

        LOGGER.debug("ID der zugeordneten Rangierung: " + result.getId());
    }

    /**
     * Test fuer die Methode RangierungsService#findFreieRangierung(RangierungQuery)
     */
    public void testFindFreieRangierung() throws FindException {
        Rangierung rangierung = createHvtWithUevtAndRangierung("#'012'#'", 1, Uebertragungsverfahren.N01).getSecond();

        RangierungQuery query = new RangierungQuery();
        query.setHvtStandortId(rangierung.getHvtIdStandort());

        Rangierung result = getRangierungsService().findFreieRangierung(query, null, false, null, null, null, null);

        assertNotNull(result, "Es wurde keine freie Rangierung gefunden!!!");
        assertEquals(result.getId(), rangierung.getId());
    }

    public void testFindFreieRangierungClusterNoAndUevtFound() throws Exception {
        Rangierung rangierung = createHvtWithUevtAndRangierung("#'123#'", 1, Uebertragungsverfahren.N01).getSecond();

        RangierungQuery rq = new RangierungQuery();
        rq.setHvtStandortId(rangierung.getHvtIdStandort());

        Rangierung result = getRangierungsService().findFreieRangierung(rq, null, false, null, 1,
                Uebertragungsverfahren.N01, null);
        assertNotNull(result);
        assertEquals(result.getId(), rangierung.getId());
    }

    public void testFindFreieRangierungClusterNoNotFound() throws Exception {
        Rangierung rangierung = createHvtWithUevtAndRangierung("#'234#'", 1, Uebertragungsverfahren.N01).getSecond();
        getBuilder(UEVTBuilder.class).withUevt("#'345'#").withHvtIdStandort(rangierung.getHvtIdStandort()).build();
        flushAndClear();

        RangierungQuery rq = new RangierungQuery();
        rq.setHvtStandortId(rangierung.getHvtIdStandort());

        Rangierung result = getRangierungsService().findFreieRangierung(rq, null, false, null, 2,
                Uebertragungsverfahren.N01, null);
        assertNull(result);
    }

    public void testFindFreieRangierungClusterNoNotFoundDueToDiffernetHvt() throws Exception {
        createHvtWithUevtAndRangierung("#'456#'", 1, Uebertragungsverfahren.N01).getSecond();
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        getBuilder(UEVTBuilder.class).withUevt("#'456'#").withHvtIdStandort(hvtStandort.getHvtIdStandort()).build();
        flushAndClear();

        RangierungQuery rq = new RangierungQuery();
        rq.setHvtStandortId(hvtStandort.getHvtIdStandort());

        Rangierung result = getRangierungsService().findFreieRangierung(rq, null, false, null, 1,
                Uebertragungsverfahren.N01, null);
        assertNull(result);
    }

    public void testFindFreieRangierungClusterNoUETVFound() throws Exception {
        Rangierung rangierung = createHvtWithUevtAndRangierung("#'123#'", 1, Uebertragungsverfahren.H01).getSecond();

        RangierungQuery rq = new RangierungQuery();
        rq.setHvtStandortId(rangierung.getHvtIdStandort());

        Rangierung result = getRangierungsService().findFreieRangierung(rq, null, false, null, 1,
                Uebertragungsverfahren.H01, null);
        assertNotNull(result);
        assertEquals(result.getId(), rangierung.getId());
    }

    public void testFindFreieRangierungUETVNotFound() throws Exception {
        Rangierung rangierung = createHvtWithUevtAndRangierung("#'234#'", 1, Uebertragungsverfahren.H01).getSecond();

        RangierungQuery rq = new RangierungQuery();
        rq.setHvtStandortId(rangierung.getHvtIdStandort());

        Rangierung result = getRangierungsService().findFreieRangierung(rq, null, false, null, 1,
                Uebertragungsverfahren.H08, null);
        assertNull(result);
    }

    /**
     * Test fuer die Methode RangierungsService#rangierungFreigeben(Integer, int)
     */
    @Test(enabled = false)
    public void testRangierungFreigeben() {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Long esId = 144763L;
            Endstelle es = esSrv.findEndstelle(esId);
            getRangierungsService().rangierungFreigabebereit(es, new Date());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode RangierungsService#createMatrix(Long, List, List)
     */
    public void testCreateMatrix() {
        try {
            List<Long> uevtIds = new ArrayList<>();
            uevtIds.add(1L);
            List<Long> prodIds = new ArrayList<>();
            prodIds.add(3L);
            prodIds.add(9L);
            prodIds.add(12L);

            getRangierungsService().createMatrix(getSessionId(), uevtIds, prodIds, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode RangierungsService#physikAenderung(int, Long, Long, IServiceCallback)
     */
    @Test(enabled = false)
    public void physikAenderung() {
        try {
            Long strategy = PhysikaenderungsTyp.STRATEGY_BANDBREITENAENDERUNG;
            Long aIdSrc = 136292L;
            Long aIdDest = 143815L;

            AKWarnings warnings = getRangierungsService().physikAenderung(
                    strategy, aIdSrc, aIdDest, this, getSessionId());

            if ((warnings != null) && warnings.isNotEmpty()) {
                LOGGER.debug("******************** WARNINGS");
                LOGGER.debug(warnings.getWarningsAsText());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode RangierungsService#findFreieRangierungen(RangierungQuery, boolean, Long, Long)
     */
    @Test
    public void testFindFreieRangierungen() throws Exception {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class).withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTB);
        HVTTechnikBuilder hwProducerBuilder = getBuilder(HVTTechnikBuilder.class).toHuawei();
        HWMduBuilder rackBuilder = getBuilder(HWMduBuilder.class).withHvtStandortBuilder(hvtStandortBuilder)
                .withHwProducerBuilder(hwProducerBuilder);
        PhysikTypBuilder physikTypBuilder = getBuilder(PhysikTypBuilder.class).withHvtTechnikBuilder(hwProducerBuilder);
        Rangierung rang50 = rang(hvtStandortBuilder, physikTypBuilder, rackBuilder, Bandwidth.create(50000, null));
        Rangierung rang100 = rang(hvtStandortBuilder, physikTypBuilder, rackBuilder, Bandwidth.create(100000, null));
        Rangierung rang100_4 = rang(hvtStandortBuilder, physikTypBuilder, rackBuilder, Bandwidth.create(100000, 4000));
        Rangierung rang100_5 = rang(hvtStandortBuilder, physikTypBuilder, rackBuilder, Bandwidth.create(100000, 5000));
        Rangierung rangNoBandwidth = rang(hvtStandortBuilder, physikTypBuilder, rackBuilder, null);

        RangierungQuery query = new RangierungQuery();
        query.setHvtStandortId(hvtStandortBuilder.get().getId());
        query.setPhysikTypId(physikTypBuilder.getId());
        query.setIncludeFreigabebereit(Boolean.TRUE);

        List<Rangierung> result = getRangierungsService().findFreieRangierungen(query, true, null, null);
        MatcherAssert.assertThat(result, Matchers.contains(rang50, rang100_4, rang100_5, rang100, rangNoBandwidth));

        // bandwith limit
        result = getRangierungsService().findFreieRangierungen(query, true, null, Bandwidth.create(100000, 5000));
        MatcherAssert.assertThat(result, Matchers.contains(rang100_5, rang100, rangNoBandwidth));
    }

    Rangierung rang(HVTStandortBuilder hvtStandortBuilder, PhysikTypBuilder physikTypBuilder, HWRackBuilder rackBuilder, Bandwidth bandwidth) {
        HWBaugruppenTypBuilder baugruppenTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withMaxBandwidth(bandwidth);
        HWBaugruppeBuilder baugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(rackBuilder)
                .withBaugruppenTypBuilder(baugruppenTypBuilder);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(baugruppeBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder);
        return getBuilder(RangierungBuilder.class)
                .withFreigegeben(Freigegeben.freigegeben)
                .withEsId(RANGIERUNG_NOT_ACTIVE)
                .withEqInBuilder(equipmentBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withPhysikTypBuilder(physikTypBuilder)
                .build();
    }

    @Test(enabled = false)
    public void testFindRangierungen() {
        try {
            Rangierung[] result =
                    getRangierungsService().findRangierungen(204738L, Endstelle.ENDSTELLEN_TYP_B);

            if ((result == null) || (result.length == 0)) {
                fail("Keine Rangierungen gefunden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test-Case fuer <code>RangierungsService.findEquipment(...)</code>
     */
    @Test(enabled = false)
    public void testFindEquipment() {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);
            Equipment result = rs.findEquipment(589L, "Ü03/1-304-03", "*-OUT");
            assertNotNull(result, "Equipment konnte nicht gefunden werden!");

            LOGGER.debug("Equipment: " + result.getId() + " - " + result.getHwEQN());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test(enabled = false)
    public void testGetHwEQNPart() {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);
            Equipment dslamEq = rs.findEquipment(86023L); // HW_EQN: U02-3-000-63
            String slotPort = dslamEq.getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT_AND_PORT);
            LOGGER.debug("DSLAM Slot/Port: " + slotPort);
            assertEquals("Slot/Port not expected!", "000-63", slotPort);
            String slot = dslamEq.getHwEQNPart(Equipment.HWEQNPART_DSLAM_SLOT);
            LOGGER.debug("DSLAM Slot: " + slot);
            assertEquals("Slot not expected!", "000", slot);
            String port = dslamEq.getHwEQNPart(Equipment.HWEQNPART_DSLAM_PORT);
            LOGGER.debug("DSLAM Port: " + port);
            assertEquals("Port not expected!", "63", port);

            Equipment ewsdEq = rs.findEquipment(30792L); // HW_EQN: 0020-00-01-06
            String ewsdPort = ewsdEq.getHwEQNPart(Equipment.HWEQNPART_EWSD_PORT);
            LOGGER.debug("EWSD Port: " + ewsdPort);
            assertEquals("EWSD port not expected!", "06", ewsdPort);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testBreakAndDeactivateRangierung() throws StoreException {
        Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(getBuilder(EquipmentBuilder.class))
                .withEqOutBuilder(getBuilder(EquipmentBuilder.class))
                .build();

        List<Rangierung> result = getRangierungsService().breakAndDeactivateRangierung(Arrays.asList(rangierung), true,
                null);
        assertNotEmpty(result, "Keine Rangierungen zurueck erhalten!");
        assertEquals(result.size(), 1);
        assertTrue(NumberTools.notEqual(rangierung.getId(), result.get(0).getId()));
        assertTrue(DateTools.isDateBefore(result.get(0).getGueltigBis(), DateTools.getHurricanEndDate()));
        assertEquals(result.get(0).getFreigegeben(), Freigegeben.deactivated);
    }

    public void testBreakRangierung() throws StoreException, FindException, ValidationException {
        EquipmentBuilder eqInBuilder = getBuilder(EquipmentBuilder.class).withStatus(EqStatus.locked);
        EquipmentBuilder eqOutBuilder = getBuilder(EquipmentBuilder.class).withStatus(EqStatus.rang);
        Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withEqOutBuilder(eqOutBuilder)
                .build();

        getRangierungsService().breakRangierung(rangierung, true, true, true);

        Equipment eqIn = getRangierungsService().findEquipment(eqInBuilder.get().getId());
        assertNotNull(eqIn);
        assertEquals(eqIn.getStatus(), EqStatus.locked);

        Equipment eqOut = getRangierungsService().findEquipment(eqOutBuilder.get().getId());
        assertNotNull(eqOut);
        assertEquals(eqOut.getStatus(), EqStatus.frei);
    }

    @DataProvider(name = "dataProviderFindEQByLeiste")
    public Object[][] dataProviderFindEQByLeiste() {
        return new Object[][] {
                { null, new String[] { } },
                { "P03", new String[] { "02" } },
                { "P03", new String[] { "01", "02", "03" } },
        };
    }

    /**
     * @see de.augustakom.hurrican.service.cc.RangierungsService#findEQByLeiste(java.lang.Long, java.lang.String)
     */
    @Test(dataProvider = "dataProviderFindEQByLeiste")
    public void testFindEQByLeiste(String leiste, String[] stifte) throws FindException {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);
        HVTStandort hvtStandort = hvtStandortBuilder.get();
        if (leiste != null) {
            for (String stift : stifte) {
                getBuilder(EquipmentBuilder.class).withHvtStandortBuilder(hvtStandortBuilder)
                        .withRangLeiste1(leiste).withRangStift1(stift).build();
            }
        }
        flushAndClear();

        List<Equipment> results = getRangierungsService().findEQByLeiste(hvtStandort.getId(), leiste);
        assertNotNull(results, "null is not a correct result (should be empty list)");
        if (leiste == null) {
            assertTrue(results.isEmpty(), "list should be empty");
        }
        else {
            assertEquals(results.size(), stifte.length, "size should be equal");
            boolean inList = false;
            for (String stift : stifte) {
                for (Equipment result : results) {
                    assertEquals(result.getRangLeiste1(), leiste, "leiste incorrect");
                    if (stift.equals(result.getRangStift1())) {
                        inList = true;
                        break;
                    }
                }
                assertTrue(inList, "stift '" + stift + "' should be in result list");
            }
        }
    }

    private RangierungsService getRangierungsService() {
        return getCCService(RangierungsService.class);
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceCallback#doServiceCallback(java.lang.Object, int, java.util.Map)
     */
    @Override
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
        LOGGER.debug(".... callback");
        return null;
    }

    /* helper */

    public void testAttachRangierung2Endstelle() throws StoreException, ValidationException {
        Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class))
                .withRandomId()
                .build();

        Endstelle endstelle = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class))
                .build();

        getRangierungsService().attachRangierung2Endstelle(rangierung, null, endstelle);

        assertEquals(endstelle.getRangierId(), rangierung.getId());
        assertEquals(rangierung.getEsId(), endstelle.getId());
        assertEquals(endstelle.getHvtIdStandort(), rangierung.getHvtIdStandort());
    }

    @Test(expectedExceptions = StoreException.class)
    public void testAttachRangierung2EndstelleIncorrect() throws StoreException, ValidationException {
        getRangierungsService().attachRangierung2Endstelle(null, null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBreakRangierungUsingNewEqStatusIllegalRangierung() throws StoreException {
        getRangierungsService().breakRangierungUsingNewEqStatus(null, null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBreakRangierungUsingNewEqStatusIllegalEqStatus() throws Exception {
        getRangierungsService().breakRangierungUsingNewEqStatus(new Rangierung(), null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBreakRangierungUsingNewEqStatusIllegalSessionId() throws Exception {
        getRangierungsService().breakRangierungUsingNewEqStatus(new Rangierung(), EqStatus.frei, null);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testBreakRangierungUsingNewEqStatusNoUserFound() throws Exception {
        getRangierungsService().breakRangierungUsingNewEqStatus(new Rangierung(), EqStatus.frei, 1L);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testBreakRangierungUsingNewEqStatusWithRangierungIsNotFrei1() throws Exception {
        Rangierung rangierung = getBuilder(RangierungBuilder.class).withEsId(RANGIERUNG_NOT_ACTIVE)
                .withFreigegeben(Freigegeben.freigegeben)
                .withBemerkung("Test4RangierungIsNotFrei Bemerkung").withRandomId().build();
        flushAndClear();

        getRangierungsService().breakRangierungUsingNewEqStatus(rangierung, EqStatus.frei, getSessionId());
    }

    public void testBreakRangierungUsingNewEqStatus() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d");

        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class).withEsId(null)
                .withBemerkung("Test Bemerkung").withRandomId();
        rangierungBuilder.getEqInBuilder().withStatus(EqStatus.rang);
        rangierungBuilder.getEqOutBuilder().withStatus(EqStatus.rang);
        flushAndClear();

        Rangierung result = getRangierungsService().breakRangierungUsingNewEqStatus(rangierungBuilder.build(),
                EqStatus.frei, getSessionId());
        assertNotNull(result);
        assertNotNull(result.getEqInId());
        assertNotNull(result.getEqOutId());
        assertEquals(sdf.format(result.getGueltigBis()), sdf.format(DateTools.getActualSQLDate()));
        flushAndClear();

        Rangierung rangierung = getRangierungsService().findRangierung(rangierungBuilder.get().getId());
        assertNotNull(rangierung);
        assertNotNull(rangierung.getEqInId());
        assertNotNull(rangierung.getEqOutId());
        assertEquals(sdf.format(rangierung.getGueltigBis()), sdf.format(DateTools.getActualSQLDate()));

        for (Long eqId : Arrays.asList(rangierung.getEqInId(), rangierung.getEqOutId())) {
            Equipment eq = getRangierungsService().findEquipment(eqId);
            assertNotNull(eq);
            assertEquals(eq.getStatus(), EqStatus.frei);
        }
    }

    public void testFindUevt() throws Exception {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);

        UEVT uevt = getBuilder(UEVTBuilder.class).withUevt("#'0123'#").withHvtStandortBuilder(hvtStandortBuilder)
                .build();

        Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withEqOutBuilder(getBuilder(EquipmentBuilder.class).withRangVerteiler("#'0123'#"))
                .build();
        flushAndClear();

        UEVT result = getRangierungsService().findUevt(rangierung);
        assertNotNull(result);
        assertEquals(result.getId(), uevt.getId());
        assertEquals(result.getHvtIdStandort(), uevt.getHvtIdStandort());
        assertEquals(result.getRackId(), uevt.getRackId());
        assertEquals(result.getUevt(), uevt.getUevt());
        assertEquals(result.getBezeichnung(), uevt.getBezeichnung());
        assertEquals(result.getProjektierung(), uevt.getProjektierung());
        assertEquals(result.getSchwellwert(), uevt.getSchwellwert());
    }

    public void testFindUevtWrongHvt() throws Exception {
        Pair<UEVT, Rangierung> uevtAndRangierung = createHvtWithUevtAndRangierung("#'567'#", 567, Uebertragungsverfahren.N01);

        UEVT result = getRangierungsService().findUevt(uevtAndRangierung.getSecond());
        assertNotNull(result);

        UEVT uevt = uevtAndRangierung.getFirst();
        assertEquals(result.getId(), uevt.getId());
        assertEquals(result.getHvtIdStandort(), uevt.getHvtIdStandort());
        assertEquals(result.getRackId(), uevt.getRackId());
        assertEquals(result.getUevt(), uevt.getUevt());
        assertEquals(result.getBezeichnung(), uevt.getBezeichnung());
        assertEquals(result.getProjektierung(), uevt.getProjektierung());
        assertEquals(result.getSchwellwert(), uevt.getSchwellwert());
    }

    private Pair<UEVT, Rangierung> createHvtWithUevtAndRangierung(String uevtName, Integer uevtClusterNo,
            Uebertragungsverfahren uetv) {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);

        UEVT uevt = getBuilder(UEVTBuilder.class).withUevt(uevtName).withHvtStandortBuilder(hvtStandortBuilder).build();

        Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withEqOutBuilder(
                        getBuilder(EquipmentBuilder.class).withRangVerteiler(uevtName).withUETV(uetv)
                                .withUevtClusterNo(uevtClusterNo)
                )
                .build();
        flushAndClear();

        assertEquals(uevt.getHvtIdStandort(), rangierung.getHvtIdStandort());

        return Pair.create(uevt, rangierung);
    }

    public void testDeleteMatrix() throws Exception {
        Rangierungsmatrix rangierungsmatrix = getBuilder(RangierungsmatrixBuilder.class)
                .withProduktBuilder(getBuilder(ProduktBuilder.class))
                .withUevtBuilder(getBuilder(UEVTBuilder.class))
                .withProdukt2PhysikTypBuilder(getBuilder(Produkt2PhysikTypBuilder.class))
                .build();
        getRangierungsService().deleteMatrix(rangierungsmatrix, getSessionId());
        Rangierungsmatrix rangmatrixDeleted = rangierungDAO
                .findById(rangierungsmatrix.getId(), Rangierungsmatrix.class);
        assertTrue(DateTools.isDateEqual(rangmatrixDeleted.getGueltigBis(), new Date()));
    }
}
