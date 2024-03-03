/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2009 09:28:06
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIn.isIn;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;
import de.augustakom.hurrican.model.tools.StandortOntOnlyBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;

/**
 * TestNG TestCase fuer <code>HWService</code>
 *
 *
 *
 */
@Test(groups = { "service" })
public class HWServiceTest extends AbstractHurricanBaseServiceTest {
    private static final Logger LOGGER = Logger.getLogger(HWServiceTest.class);
    private HWService sut;

    @BeforeMethod(groups = { "service" })
    public void initService() {
        sut = getCCService(HWService.class);
    }

    public void findBaugruppentypByName() throws Exception {
        final String bgTypName = RandomTools.createPassword(19).toUpperCase();
        getBuilder(HWBaugruppenTypBuilder.class)
                .withName(bgTypName)
                .build();
        flushAndClear();
        assertThat(sut.findBaugruppenTypByName(bgTypName), notNullValue(HWBaugruppenTyp.class));
    }

    public void shouldSaveDslamWithValidType() {
        String validHuaweiDslamType = "MA5603T";
        getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(new HVTTechnikBuilder().toHuawei())
                .withDslamType(validHuaweiDslamType)
                .get();

        flushAndClear();
    }

    public void shouldSaveNonHuaweiDslamWithNoType() {
        getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(new HVTTechnikBuilder().toAlcatel())
                .withDslamType(null)
                .get();

        flushAndClear();
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Exception creating new instance of class.*")
    public void shouldThrowExceptionSavingHuaweiDslamWithInvalidType() {
        getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(new HVTTechnikBuilder().toHuawei())
                .withDslamType("some unknown type")
                .get();

        flushAndClear();
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Exception creating new instance of class.*")
    public void shouldThrowExceptionSavingHuaweiDslamWithNoType() {
        getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(new HVTTechnikBuilder().toHuawei())
                .withDslamType(null)
                .get();

        flushAndClear();
    }

    public void testFindSubrackByHwRackAndModNumber() throws FindException {
        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class);
        HWSubrack subrackExpected = subrackBuilder.build();
        HWRack rack = subrackBuilder.getRackBuilder().get();

        HWSubrack subrackFound = sut.findSubrackByHwRackAndModNumber(rack.getId(), subrackExpected.getModNumber());
        assertEquals(subrackFound, subrackExpected);
    }

    public void testFindBaugruppeByRackSubrackModNumber() throws FindException {
        HWBaugruppe hwBaugruppeExpected = getBuilder(HWBaugruppeBuilder.class).build();
        HWBaugruppe hwBaugruppeFound = sut.findBaugruppe(hwBaugruppeExpected.getRackId(),
                hwBaugruppeExpected.getSubrackId(), hwBaugruppeExpected.getModNumber());
        assertEquals(hwBaugruppeFound, hwBaugruppeExpected);
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.HWServiceImpl#findDslamByIP(java.lang.String)}.
     */
    public void testFindHWDslamByIP() throws FindException {
        HWDslam dslam = getBuilder(HWDslamBuilder.class).get();

        flushAndClear();

        HWDslam result = sut.findDslamByIP(dslam.getIpAdress());
        assertNotNull(result, "DSLAM wurde nicht gefunden!");
        LOGGER.info("DSLAM Rack-ID: " + result.getId());
    }

    public void testFindSubracksForRack() throws FindException {
        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class);
        HWRack rack = subrackBuilder.getRackBuilder().get();
        List<HWSubrack> subracks = new ArrayList<HWSubrack>();
        for (int i = 0; i < 5; ++i) {
            subracks.add(subrackBuilder.withModNumber("1-" + i).build());
        }
        subrackBuilder.withRackBuilder(getBuilder(HWDslamBuilder.class));
        for (int i = 5; i < 10; ++i) {
            subrackBuilder.withModNumber("2-" + i).build();
        }

        flushAndClear();

        List<HWSubrack> result = sut.findSubracksForRack(rack.getId());
        Collections.sort(result, HWSubrack.SUBRACK_COMPARATOR);
        assertEquals(result.size(), subracks.size(), "Unexpected number of subracks returned");
        for (int i = 0; i < subracks.size(); i++) {
            assertEquals(HWSubrack.SUBRACK_COMPARATOR.compare(subracks.get(i), result.get(i)), 0,
                    "Unexpected subrack returned");
        }
    }

    public void testFindSubracksForStandort() throws FindException {
        HWSubrackBuilder subrackBuilder = getBuilder(HWSubrackBuilder.class);
        HVTStandort standort = subrackBuilder.getRackBuilder().getHvtStandortBuilder().get();
        List<HWSubrack> subracks = new ArrayList<HWSubrack>();
        for (int i = 0; i < 5; ++i) {
            subracks.add(subrackBuilder.withModNumber("1-" + i).build());
        }
        subrackBuilder.withRackBuilder(getBuilder(HWDslamBuilder.class));
        for (int i = 5; i < 10; ++i) {
            subrackBuilder.withModNumber("2-" + i).build();
        }

        flushAndClear();

        List<HWSubrack> result = sut.findSubracksForStandort(standort.getId());
        Collections.sort(result, HWSubrack.SUBRACK_COMPARATOR);
        assertEquals(result.size(), subracks.size(), "Unexpected number of subracks returned");
        for (int i = 0; i < subracks.size(); i++) {
            assertEquals(HWSubrack.SUBRACK_COMPARATOR.compare(subracks.get(i), result.get(i)), 0,
                    "Unexpected subrack returned");
        }
    }

    public void testFindHWBaugruppenViews() throws FindException {
        List<HWBaugruppeView> result = sut.findHWBaugruppenViews(Long.valueOf(1));
        assertNotEmpty(result, "Keine Baugruppen-Views gefunden!");
    }

    public void testFindHWOltChildByOlt() throws FindException {
        HWOltBuilder hwOltBuilder = getBuilder(HWOltBuilder.class);
        HWMdu hwMdu1 = getBuilder(HWMduBuilder.class).withHWRackOltBuilder(hwOltBuilder).build();
        HWMdu hwMdu2 = getBuilder(HWMduBuilder.class).withHWRackOltBuilder(hwOltBuilder).build();
        HWDpo hwDpo1 = getBuilder(HWDpoBuilder.class).withHWRackOltBuilder(hwOltBuilder).build();

        List<HWMdu> mdusFound = sut.findHWOltChildByOlt(hwOltBuilder.get().getId(), HWMdu.class);
        List<HWDpo> dposFound = sut.findHWOltChildByOlt(hwOltBuilder.get().getId(), HWDpo.class);
        List<HWOnt> ontsFound = sut.findHWOltChildByOlt(hwOltBuilder.get().getId(), HWOnt.class);
        assertEquals(mdusFound.size(), 2);
        assertTrue(mdusFound.contains(hwMdu1));
        assertTrue(mdusFound.contains(hwMdu2));
        assertEquals(dposFound.size(), 1);
        assertTrue(dposFound.contains(hwDpo1));
        assertTrue(ontsFound.isEmpty());
    }

    public void testFindAllRacksForFtth() throws Exception {
        final HVTStandortBuilder betriebsraumBuilder = getBuilder(HVTStandortBuilder.class);
        final HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withBetriebsraumBuilder(betriebsraumBuilder);
        final HWOlt hwOlt = getBuilder(HWOltBuilder.class)
                .withHvtStandortBuilder(betriebsraumBuilder)
                .build();
        final HWDslamBuilder hwDslamWithGponBuilder = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(betriebsraumBuilder);
        final HWBaugruppenTypBuilder gponBaugruppenTyp = getBuilder(HWBaugruppenTypBuilder.class)
                .withTunneling(HWBaugruppenTyp.Tunneling.VLAN);
        final HWBaugruppe gponBaugruppe = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(gponBaugruppenTyp)
                .withRackBuilder(hwDslamWithGponBuilder)
                .withEingebaut(Boolean.TRUE)
                .build();
        final HWDslam hwDslamNoGpon = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(betriebsraumBuilder)
                .build();

        final List<HWRack> result = sut
                .findAllRacksForFtth(hvtStandortBuilder.get().getBetriebsraumId());

        assertThat(hwOlt, isIn(result));
        assertThat(hwDslamWithGponBuilder.get(), isIn(result));
        assertThat(hwDslamNoGpon, not(isIn(result)));
    }

    public void testFindAllRacksForFtthWithTwoBaugruppen() throws Exception {
        final HVTStandortBuilder betriebsraumBuilder = getBuilder(HVTStandortBuilder.class);
        final HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withBetriebsraumBuilder(betriebsraumBuilder);

        final HWDslamBuilder hwDslamWithGponBuilder = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(betriebsraumBuilder);
        final HWBaugruppenTypBuilder gponBaugruppenTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withTunneling(HWBaugruppenTyp.Tunneling.VLAN);
        final HWBaugruppe gponBaugruppe1 = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(gponBaugruppenTypBuilder)
                .withRackBuilder(hwDslamWithGponBuilder)
                .withEingebaut(Boolean.TRUE)
                .build();
        final HWBaugruppe gponBaugruppe2 = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(gponBaugruppenTypBuilder)
                .withRackBuilder(hwDslamWithGponBuilder)
                .withEingebaut(Boolean.TRUE)
                .build();

        final List<HWRack> result = sut
                .findAllRacksForFtth(hvtStandortBuilder.get().getBetriebsraumId());

        assertThat(result.size(), equalTo(1));
        assertThat(hwDslamWithGponBuilder.get(), isIn(result));
    }

    public void testFindAllRacksForFtthWithTwoGslams() throws Exception {
        final HVTStandortBuilder betriebsraumBuilder = getBuilder(HVTStandortBuilder.class);
        final HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withBetriebsraumBuilder(betriebsraumBuilder);

        final HWBaugruppenTypBuilder gponBaugruppenTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withTunneling(HWBaugruppenTyp.Tunneling.VLAN);
        final HWDslamBuilder hwDslamWithGponBuilder1 = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(betriebsraumBuilder);
        final HWBaugruppe gponBaugruppe1 = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(gponBaugruppenTypBuilder)
                .withRackBuilder(hwDslamWithGponBuilder1)
                .withEingebaut(Boolean.TRUE)
                .build();
        final HWBaugruppe gponBaugruppe2 = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(gponBaugruppenTypBuilder)
                .withRackBuilder(hwDslamWithGponBuilder1)
                .withEingebaut(Boolean.TRUE)
                .build();
        final HWDslamBuilder hwDslamWithGponBuilder2 = getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(betriebsraumBuilder);
        final HWBaugruppe gponBaugruppe3 = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(gponBaugruppenTypBuilder)
                .withRackBuilder(hwDslamWithGponBuilder2)
                .withEingebaut(Boolean.TRUE)
                .build();

        final List<HWRack> result = sut
                .findAllRacksForFtth(hvtStandortBuilder.get().getBetriebsraumId());

        assertThat(result.size(), equalTo(2));
        assertThat(hwDslamWithGponBuilder1.get(), isIn(result));
        assertThat(hwDslamWithGponBuilder2.get(), isIn(result));
    }

    public void testFindNoActiveRackByBezeichnung() throws Exception {
        final StandortOntOnlyBuilder standortOntOnlyBuilder = new StandortOntOnlyBuilder();
        final StandortOntOnlyBuilder.StandortOntOnly standortOntOnly = standortOntOnlyBuilder
                .withOntGueltigVon(DateTools.minusWorkDays(2))
                .withOntGueltigBis(DateTools.minusWorkDays(1))
                .prepare(this, null);
        standortOntOnlyBuilder.build(standortOntOnly);

        HWRack rack = sut.findActiveRackByBezeichnung(standortOntOnly.hwOntBuilder.get().getGeraeteBez());
        assertNull(rack);
    }

    public void testFindActiveRackByBezeichnung() throws Exception {
        final StandortOntOnlyBuilder standortOntOnlyBuilder = new StandortOntOnlyBuilder();
        final StandortOntOnlyBuilder.StandortOntOnly standortOntOnly = standortOntOnlyBuilder.prepare(this, null);
        standortOntOnlyBuilder.build(standortOntOnly);

        HWRack rack = sut.findActiveRackByBezeichnung(standortOntOnly.hwOntBuilder.get().getGeraeteBez());
        assertNotNull(rack);
        assertEquals(rack.getGueltigBis().after(DateTools.minusWorkDays(1)), true);
    }

    @Test(expectedExceptions = FindException.class)
    public void testFindTwoActiveRackByBezeichnung() throws Exception {
        final StandortOntOnlyBuilder standortOntOnlyBuilder = new StandortOntOnlyBuilder();
        final StandortOntOnlyBuilder.StandortOntOnly standortOntOnly = standortOntOnlyBuilder.prepare(this, null);
        standortOntOnlyBuilder.build(standortOntOnly);

        standortOntOnlyBuilder.build(standortOntOnly);

        sut.findActiveRackByBezeichnung(standortOntOnly.hwOntBuilder.get().getGeraeteBez());
    }

}
