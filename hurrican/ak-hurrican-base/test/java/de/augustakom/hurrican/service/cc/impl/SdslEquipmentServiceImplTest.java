/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.14
 */
package de.augustakom.hurrican.service.cc.impl;

import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Function2;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.UNIT)
public class SdslEquipmentServiceImplTest extends BaseTest {

    @InjectMocks
    SdslEquipmentServiceImpl cut = new SdslEquipmentServiceImpl();
    @Mock
    EquipmentDAO equipmentDaoMock;
    @Mock
    HWService hwServiceMock;
    @Mock
    HVTService hvtService;
    @Mock
    RangierungsService rangierungsServiceMock;
    @Mock
    EndstellenService endstellenServiceMock;
    @Mock
    RangierungDAO rangierungDaoMock;
    @Mock
    CCAuftragService auftragServiceMock;
    @Mock
    PhysikService physikServiceMock;
    @Mock
    CCLeistungsService leistungsServiceMock;
    @Mock
    ProduktService produktService;

    private HVTStandort hvtStandort;
    private EquipmentBuilder adslEquipment;
    private EquipmentBuilder sdslEquipment;
    private Rangierung freeAdslRangierung;
    private Rangierung freeSdslRangierung;


    @BeforeMethod
    public void prepareTest() throws FindException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindFreeEquipmentsByStandortSorted() throws Exception {
        HWDslam hwDslam = new HWDslamBuilder()
                .withRandomId()
                .setPersist(false)
                .build();

        HWBaugruppe adslHwBaugruppe = new HWBaugruppeBuilder()
                .withRandomId()
                .setPersist(false)
                .build();
        HWBaugruppe sdslHwBaugruppe = new HWBaugruppeBuilder()
                .withRandomId()
                .setPersist(false)
                .build();

        adslEquipment = new EquipmentBuilder()
                .withRandomId()
                .setPersist(false);
        sdslEquipment = new EquipmentBuilder()
                .withRandomId()
                .setPersist(false);

        freeAdslRangierung = new RangierungBuilder()
                .withFreigegeben(Rangierung.Freigegeben.freigegeben)
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_SDSL_ATM_ALCATEL)
                .withEqInBuilder(adslEquipment)
                .setPersist(false)
                .build();
        freeSdslRangierung = new RangierungBuilder()
                .withFreigegeben(Rangierung.Freigegeben.freigegeben)
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_SDSL_IP_ALCATEL)
                .withEqInBuilder(sdslEquipment)
                .setPersist(false)
                .build();

        hvtStandort = new HVTStandortBuilder()
                .withRandomId()
                .setPersist(false)
                .build();

        when(hwServiceMock.findRacks(hvtStandort.getId(), HWDslam.class, true))
                .thenReturn(Lists.newArrayList(hwDslam));

        when(hwServiceMock.findBaugruppen4Rack(hwDslam.getId()))
                .thenReturn(Lists.newArrayList(sdslHwBaugruppe, adslHwBaugruppe));

        when(equipmentDaoMock.findEquipmentsByBaugruppe(adslHwBaugruppe.getId()))
                .thenReturn(Lists.newArrayList(adslEquipment.get()));
        when(equipmentDaoMock.findEquipmentsByBaugruppe(sdslHwBaugruppe.getId()))
                .thenReturn(Lists.newArrayList(sdslEquipment.get()));

        when(
                rangierungDaoMock.queryByExample(argThat(new RangierungByEqInIdMatcher(adslEquipment.getId())),
                        eq(Rangierung.class))
        )
                .thenReturn(singletonList(freeAdslRangierung));
        when(
                rangierungDaoMock.queryByExample(argThat(new RangierungByEqInIdMatcher(sdslEquipment.getId())),
                        eq(Rangierung.class))
        )
                .thenReturn(singletonList(freeSdslRangierung));

        final List<Long> possiblePhysiktypen = Lists.newArrayList(PhysikTyp.PHYSIKTYP_SDSL_IP_ALCATEL);
        assertEquals(cut.findFreeEquipmentsByStandortSorted(hvtStandort.getId(), possiblePhysiktypen),
                Lists.newArrayList(sdslEquipment.get()));
        assertEquals(cut.findFreeEquipmentsByStandortSorted(hvtStandort.getId() + 1, possiblePhysiktypen),
                Collections.emptyList());
    }

    @Test
    public void testGroupBySchicht2Protokoll() {
        final Equipment atm1 = new EquipmentBuilder().withSchicht2Protokoll(Schicht2Protokoll.ATM).build();
        final Equipment efm2 = new EquipmentBuilder().withSchicht2Protokoll(Schicht2Protokoll.EFM).build();
        final Equipment atm3 = new EquipmentBuilder().withSchicht2Protokoll(null).build();
        final Equipment efm4 = new EquipmentBuilder().withSchicht2Protokoll(Schicht2Protokoll.EFM).build();

        Map<Schicht2Protokoll, List<Equipment>> result = Maps.newHashMap();
        result.put(Schicht2Protokoll.ATM, Lists.newArrayList(atm1, atm3));
        result.put(Schicht2Protokoll.EFM, Lists.newArrayList(efm2, efm4));

        assertEquals(cut.groupBySchicht2Protokoll(Lists.newArrayList(atm1, efm2, atm3, efm4)), result);
    }

    @Test
    public void testFilterBloeckeWithMixedLayer2Protocol() throws Exception {
        final List<Equipment> blockWithoutMixedL2Protocol1 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.ATM).build(),
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(null).build()
        );
        final List<Equipment> blockWithoutMixedL2Protocol2 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.EFM).build(),
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.EFM).build()
        );
        final List<Equipment> blockWithMixedL2Protocol = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.ATM).build(),
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.EFM).build()
        );
        final List<Equipment> blockWithMixedL2Protocol2 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.ATM).build(),
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.EFM).build(),
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.ATM).build()
        );
        final List<List<Equipment>> in = ImmutableList.of(
                blockWithoutMixedL2Protocol1,
                blockWithMixedL2Protocol,
                blockWithoutMixedL2Protocol2,
                blockWithMixedL2Protocol2
        );

        final List<List<Equipment>> result = cut.filterBloeckeWithMixedLayer2Protocol(in);

        assertThat(result, hasSize(2));
        assertThat(result.get(0), equalTo(blockWithoutMixedL2Protocol1));
        assertThat(result.get(1), equalTo(blockWithoutMixedL2Protocol2));
    }

    @Test
    public void testGroup4erBloecke() throws Exception {
        final HWDslamBuilder hwRackBuilder = new HWDslamBuilder()
                .withRandomId()
                .withHwProducerBuilder(new HVTTechnikBuilder().toAlcatel());
        final HWBaugruppeBuilder hwBaugruppeBuilder = new HWBaugruppeBuilder()
                .withRandomId()
                .withRackBuilder(hwRackBuilder);
        final HWDslamBuilder hwRackBuilder2 = new HWDslamBuilder()
                .withRandomId()
                .withHwProducerBuilder(new HVTTechnikBuilder().toHuawei());
        final HWBaugruppeBuilder hwBaugruppeBuilder2 = new HWBaugruppeBuilder()
                .withRandomId()
                .withRackBuilder(hwRackBuilder2);
        final List<Equipment> expectedBlock1 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-1").withBaugruppeBuilder(hwBaugruppeBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-2").withBaugruppeBuilder(hwBaugruppeBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-3").withBaugruppeBuilder(hwBaugruppeBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-4").withBaugruppeBuilder(hwBaugruppeBuilder)
                        .build()
        );
        final List<Equipment> expectedBlock2 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-5")
                        .withBaugruppeBuilder(hwBaugruppeBuilder).build()
        );
        final List<Equipment> expectedBlock3 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-1-4-0").withBaugruppeBuilder(hwBaugruppeBuilder2)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-1-4-1").withBaugruppeBuilder(hwBaugruppeBuilder2)
                        .build()
        );

        final List<Equipment> in = ImmutableList
                .<Equipment>builder()
                .addAll(expectedBlock1)
                .addAll(expectedBlock2)
                .addAll(expectedBlock3)
                .build();

        when(rangierungsServiceMock.getHWRackForEquipment(eq(expectedBlock1.get(0)), any(HWService.class))).thenReturn(
                hwRackBuilder.get());
        when(rangierungsServiceMock.getHWRackForEquipment(eq(expectedBlock2.get(0)), any(HWService.class))).thenReturn(
                hwRackBuilder2.get());
        when(rangierungsServiceMock.getHWRackForEquipment(eq(expectedBlock3.get(0)), any(HWService.class))).thenReturn(
                hwRackBuilder2.get());

        final List<List<Equipment>> result = cut.groupEquipmentsByCorresponding4erBloecke(in);
        assertThat(result, hasSize(3));
        assertThat(result.get(0), contains(expectedBlock1.toArray(new Equipment[4])));
        assertThat(result.get(1), contains(expectedBlock2.toArray(new Equipment[1])));
        assertThat(result.get(2), contains(expectedBlock3.toArray(new Equipment[2])));
    }


    @Test
    public void testGroupEquipmentsByBondingCapableBaugruppe() throws FindException {
        final HWDslamBuilder hwRackBuilder = new HWDslamBuilder()
                .withRandomId()
                .withHwProducerBuilder(new HVTTechnikBuilder().toAlcatel());

        final HWBaugruppeBuilder hwBgBondingCapableBuilder = new HWBaugruppeBuilder()
                .withRandomId()
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder().withBondingCapable(Boolean.TRUE))
                .withRackBuilder(hwRackBuilder);

        final HWBaugruppeBuilder hwBgBondingCapableBuilder2 = new HWBaugruppeBuilder()
                .withRandomId()
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder().withBondingCapable(Boolean.TRUE))
                .withRackBuilder(hwRackBuilder);

        final HWBaugruppeBuilder hwBgNotBondingCapableBuilder = new HWBaugruppeBuilder()
                .withRandomId()
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder().withBondingCapable(Boolean.FALSE))
                .withRackBuilder(hwRackBuilder);

        final List<Equipment> expectedBlock1 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-1").withBaugruppeBuilder(hwBgBondingCapableBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-5").withBaugruppeBuilder(hwBgBondingCapableBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-15").withBaugruppeBuilder(hwBgBondingCapableBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-23").withBaugruppeBuilder(hwBgBondingCapableBuilder)
                        .build());

        final List<Equipment> notInResult = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-1").withBaugruppeBuilder(hwBgNotBondingCapableBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-2").withBaugruppeBuilder(hwBgNotBondingCapableBuilder)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-3").withBaugruppeBuilder(hwBgNotBondingCapableBuilder)
                        .build());

        final List<Equipment> expectedBlock2 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-1").withBaugruppeBuilder(hwBgBondingCapableBuilder2)
                        .build(),
                new EquipmentBuilder().withRandomId().withHwEQN("1-1-4-5").withBaugruppeBuilder(hwBgBondingCapableBuilder2)
                        .build());

        final List<Equipment> in = ImmutableList
                .<Equipment>builder()
                .addAll(expectedBlock1)
                .addAll(expectedBlock2)
                .addAll(notInResult)
                .build();

        when(hwServiceMock.findBaugruppe(eq(hwBgBondingCapableBuilder.get().getId()))).thenReturn(
                hwBgBondingCapableBuilder.get());
        when(hwServiceMock.findBaugruppe(eq(hwBgBondingCapableBuilder2.get().getId()))).thenReturn(
                hwBgBondingCapableBuilder2.get());
        when(hwServiceMock.findBaugruppe(eq(hwBgNotBondingCapableBuilder.get().getId()))).thenReturn(
                hwBgNotBondingCapableBuilder.get());

        final List<List<Equipment>> result = cut.groupEquipmentsByBondingCapableBaugruppe(in);
        assertThat(result, hasSize(2));
        assertTrue(result.stream().filter(i -> i.size() == 4).findFirst().isPresent());
        assertTrue(result.stream().filter(i -> i.size() == 2).findFirst().isPresent());
    }



    @Test
    public void testAssignSdslNdrahtWithAuftragIstRangiert() throws Exception {
        final Date today = new Date();
        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withRandomId()
                .withRandomAuftragId()
                .build();
        final RangierungBuilder rangGueltigBuilderEsB = new RangierungBuilder()
                .withRandomId();
        final Endstelle esB = new EndstelleBuilder()
                .withRandomId()
                .withRangierungBuilder(rangGueltigBuilderEsB)
                .build();

        when(
                this.endstellenServiceMock.findEndstelle4AuftragWithoutExplicitFlush(auftragDaten.getAuftragId(),
                        Endstelle.ENDSTELLEN_TYP_B)
        )
                .thenReturn(esB);
        when(rangierungsServiceMock.findRangierungen(eq(auftragDaten.getAuftragId()), eq(Endstelle.ENDSTELLEN_TYP_B),
                any(Function2.class))).thenReturn(new Rangierung[] { rangGueltigBuilderEsB.get() });
        when(this.auftragServiceMock.findAuftragDatenByEndstelleTx(esB.getId())).thenReturn(auftragDaten);
        final Pair<CCAuftragService.CheckAnzNdrahtResult, Collection<AuftragDaten>> toReturn =
                Pair.create(CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED,
                        (Collection<AuftragDaten>) Lists.newArrayList(auftragDaten));
        when(this.auftragServiceMock.checkAnzahlNdrahtOptionAuftraege(auftragDaten.getAuftragId()))
                .thenReturn(toReturn);
        final AKWarnings akWarnings = cut.assignSdslNdraht(esB.getId(), today);

        assertThat(akWarnings.isNotEmpty(), equalTo(Boolean.TRUE));
    }

    @Test
    public void testAssignSdslNdrahtWithWenigerSdslNdrahtAuftraegeAlsNoetig() throws Exception {
        final Date today = new Date();
        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withRandomId()
                .withRandomAuftragId()
                .build();
        final Endstelle esB = new EndstelleBuilder()
                .withRandomId()
                .build();
        when(this.auftragServiceMock.findAuftragDatenByEndstelleTx(esB.getId())).thenReturn(auftragDaten);
        final Pair<CCAuftragService.CheckAnzNdrahtResult, Collection<AuftragDaten>> toReturn =
                Pair.create(CCAuftragService.CheckAnzNdrahtResult.LESS_THAN_EXPECTED,
                        (Collection<AuftragDaten>) Lists.newArrayList(auftragDaten));
        when(this.auftragServiceMock.checkAnzahlNdrahtOptionAuftraege(auftragDaten.getAuftragId()))
                .thenReturn(toReturn);
        final AKWarnings akWarnings = cut.assignSdslNdraht(esB.getId(), today);

        assertThat(akWarnings.isNotEmpty(), equalTo(Boolean.TRUE));
    }

    @Test
    public void testCheckAuftraegeNichtRangiert() throws Exception {
        final Date today = new Date();
        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withRandomId()
                .withRandomAuftragId()
                .build();

        final RangierungBuilder rangGueltigBuilderEsB = new RangierungBuilder()
                .withRandomId();
        final RangierungBuilder rangUngueltigBuilderEsB = new RangierungBuilder()
                .withRandomId()
                .withGueltigBis(today);
        final Endstelle esB = new EndstelleBuilder()
                .withRandomId()
                .withRangierungBuilder(rangUngueltigBuilderEsB)
                .withRangierungAdditionalBuilder(rangGueltigBuilderEsB)
                .build();

        final RangierungBuilder rangGueltigBuilderEsA = new RangierungBuilder()
                .withRandomId();
        final RangierungBuilder rangUngueltigBuilderEsA = new RangierungBuilder()
                .withRandomId()
                .withGueltigBis(today);
        final Endstelle esA = new EndstelleBuilder()
                .withRandomId()
                .withRangierungBuilder(rangUngueltigBuilderEsA)
                .withRangierungAdditionalBuilder(rangGueltigBuilderEsA)
                .build();

        when(
                endstellenServiceMock.findEndstelle4AuftragWithoutExplicitFlush(auftragDaten.getAuftragId(),
                        Endstelle.ENDSTELLEN_TYP_A)
        )
                .thenReturn(esA);
        when(
                endstellenServiceMock.findEndstelle4AuftragWithoutExplicitFlush(auftragDaten.getAuftragId(),
                        Endstelle.ENDSTELLEN_TYP_B)
        )
                .thenReturn(esB);

        when(
                rangierungsServiceMock.findRangierungen(eq(auftragDaten.getAuftragId()),
                        eq(Endstelle.ENDSTELLEN_TYP_B), any(Function2.class))
        )
                .thenReturn(new Rangierung[] { rangGueltigBuilderEsB.get(), rangUngueltigBuilderEsB.get() });
        when(
                rangierungsServiceMock.findRangierungen(eq(auftragDaten.getAuftragId()),
                        eq(Endstelle.ENDSTELLEN_TYP_A), any(Function2.class))
        )
                .thenReturn(new Rangierung[] { rangGueltigBuilderEsA.get(), rangUngueltigBuilderEsA.get() });

        when(rangierungDaoMock.findById(rangUngueltigBuilderEsB.getId(), Rangierung.class)).thenReturn(
                rangUngueltigBuilderEsB.get());
        when(rangierungDaoMock.findById(rangGueltigBuilderEsA.getId(), Rangierung.class)).thenReturn(
                rangGueltigBuilderEsA.get());
        when(rangierungDaoMock.findById(rangUngueltigBuilderEsA.getId(), Rangierung.class)).thenReturn(
                rangUngueltigBuilderEsA.get());

        final AKWarnings result = cut.checkAuftraegeNichtRangiert(ImmutableList.of(auftragDaten), today);

        assertThat(result.isNotEmpty(), equalTo(Boolean.TRUE));
        assertThat(result.getAKMessages(), hasSize(2));
        assertThat(result.getWarningsAsText(), containsString(rangGueltigBuilderEsB.getId().toString()));
        assertThat(result.getWarningsAsText(), containsString(rangGueltigBuilderEsA.getId().toString()));
    }

    @DataProvider
    Object[][] testCheckAnzahlNdrahtAuftraegeDataProvider() {
        return new Object[][] {
                { CCAuftragService.CheckAnzNdrahtResult.AS_EXPECTED, 0 },
                { CCAuftragService.CheckAnzNdrahtResult.MORE_THAN_EXPECTED, 1 },
                { CCAuftragService.CheckAnzNdrahtResult.LESS_THAN_EXPECTED, 1 },
                { CCAuftragService.CheckAnzNdrahtResult.NO_NDRAHT_CONFIG, 1 },
        };
    }

    @Test(dataProvider = "testCheckAnzahlNdrahtAuftraegeDataProvider")
    public void testCheckAnzahlNdrahtAuftraege(final CCAuftragService.CheckAnzNdrahtResult checkResult,
            final int expNoOfWarnings) throws Exception {
        final long auftragId = 815L;
        final Pair<CCAuftragService.CheckAnzNdrahtResult, Collection<AuftragDaten>> toReturn =
                Pair.create(checkResult, (Collection<AuftragDaten>) Lists.newArrayList(new AuftragDaten()));
        when(this.auftragServiceMock.checkAnzahlNdrahtOptionAuftraege(auftragId)).thenReturn(toReturn);

        final Pair<AKWarnings, Collection<AuftragDaten>> result = cut.checkAnzahlNdrahtAuftraege(auftragId);
        if (expNoOfWarnings == 0) {
            assertThat(result.getFirst().getAKMessages().size(), equalTo(0));
            assertThat(result.getSecond(), not(Matchers.<AuftragDaten>empty()));
        }
        else {
            assertThat(result.getFirst().getAKMessages().size(), equalTo(expNoOfWarnings));
            assertThat(result.getSecond(), Matchers.<AuftragDaten>empty());
        }
    }

    @DataProvider
    Object[][] testfindViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslamDataProvider() {
        return new Object[][] {
                new Object[] { PhysikTyp.PHYSIKTYP_SDSL_IP_ALCATEL, "1-1-5-11", "1-1-5-9", HVTTechnik.ALCATEL },
                new Object[] { PhysikTyp.PHYSIKTYP_SHDSL_IP_ALCATEL, "1-1-5-11", "1-1-5-9", HVTTechnik.ALCATEL },
                new Object[] { PhysikTyp.PHYSIKTYP_SHDSL_HUAWEI, "U02-2-013-17", "U02-2-013-16", HVTTechnik.HUAWEI },
        };
    }

    @Test(dataProvider = "testfindViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslamDataProvider")
    public void findViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslam(final long physikTypId, final String eqn,
            final String eqnPort1, final long hvtTechnikId) throws Exception {
        final HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder().withRandomId();
        final EquipmentBuilder eqInBuilder = new EquipmentBuilder()
                .withRandomId()
                .withBaugruppeBuilder(baugruppeBuilder)
                .withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_SDSL_OUT)
                .withHwEQN(eqn);
        final Equipment firstPortOfBlock = new EquipmentBuilder()
                .withRandomId()
                .withHwEQN(eqnPort1)
                .build();
        final HVTTechnikBuilder hvtTechnikBuilder = new HVTTechnikBuilder()
                .withId(hvtTechnikId);
        final PhysikTypBuilder physikTypBuilder = new PhysikTypBuilder()
                .withId(physikTypId)
                .withHvtTechnikBuilder(hvtTechnikBuilder);
        final Rangierung rang = new RangierungBuilder()
                .withEqInBuilder(eqInBuilder)
                .withPhysikTypBuilder(physikTypBuilder)
                .build();
        final List<Equipment> expectedResult = ImmutableList.of(firstPortOfBlock, firstPortOfBlock, firstPortOfBlock,
                firstPortOfBlock);

        when(rangierungsServiceMock.findEquipment(eqInBuilder.getId())).thenReturn(eqInBuilder.get());
        when(rangierungsServiceMock.findRangierung4Equipment(eqInBuilder.getId())).thenReturn(rang);
        when(physikServiceMock.findPhysikTyp(rang.getPhysikTypId())).thenReturn(physikTypBuilder.get());
        when(equipmentDaoMock.queryByExample(any(Equipment.class), eq(Equipment.class))).thenReturn(
                singletonList(firstPortOfBlock));
        // when(rangierungDaoMock.queryByExample(any(Rangierung.class), eq(Rangierung.class))).thenReturn(
        // singletonList(rang));
        when(rangierungsServiceMock.findEquipments4HWBaugruppe(anyLong())).thenReturn(singletonList(firstPortOfBlock));

        final List<Equipment> result = cut.findViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslam(eqInBuilder.get()
                .getId());
        assertThat(expectedResult, equalTo(result));

        final ArgumentCaptor eqExCaptor = ArgumentCaptor.forClass(Equipment.class);

        verify(equipmentDaoMock, times(4)).queryByExample(eqExCaptor.capture(), eq(Equipment.class));
        assertThat(((Equipment) eqExCaptor.getAllValues().get(0)).getHwBaugruppenId(),
                equalTo(baugruppeBuilder.getId()));
        assertThat(((Equipment) eqExCaptor.getAllValues().get(0)).getHwEQN(), equalTo(firstPortOfBlock.getHwEQN()));
        assertThat(((Equipment) eqExCaptor.getAllValues().get(0)).getGueltigBis(),
                equalTo(DateTools.getHurricanEndDate()));
    }

    @DataProvider
    Object[][] testPortNoOfBlockStartDataProvider() {
        final SdslEquipmentServiceImpl.PortNoOfBlockStartStrategyHuawei cutHuawei =
                SdslEquipmentServiceImpl.PortNoOfBlockStartStrategyHuawei.instance;
        final SdslEquipmentServiceImpl.PortNoOfBlockStartStrategyAlcatel cutAlcatel =
                SdslEquipmentServiceImpl.PortNoOfBlockStartStrategyAlcatel.instance;
        // @formatter:off
        return new Object[][] {
                {"1-1-5-1", 1, cutAlcatel},
                {"1-1-5-7", 5, cutAlcatel},
                {"1-1-5-8", 5, cutAlcatel},
                {"1-1-5-9", 9, cutAlcatel},
                {"1-1-5-09", 9, cutAlcatel},
                {"1-1-5-10", 9, cutAlcatel},
                {"1-1-5-11", 9, cutAlcatel},
                {"1-1-5-12", 9, cutAlcatel},
                {"1-1-5-13", 13, cutAlcatel},
                {"1-1-5-14", 13, cutAlcatel},
                {"U02-2-013-0", 0, cutHuawei},
                {"U02-2-013-00", 0, cutHuawei},
                {"U02-2-013-01", 0, cutHuawei},
                {"U02-2-013-02", 0, cutHuawei},
                {"U02-2-013-03", 0, cutHuawei},
                {"U02-2-013-04", 4, cutHuawei},
                {"U02-2-013-08", 8, cutHuawei},
                {"U02-2-013-10", 8, cutHuawei},
                {"U02-2-013-11", 8, cutHuawei},
        };
        // @formatter:on
    }

    @Test(dataProvider = "testPortNoOfBlockStartDataProvider")
    public void testPortNoOfBlockStart(final String hwEqn, final int expectedResult,
            final SdslEquipmentServiceImpl.PortNrOfBlockStartStrategy cut) {
        final Equipment port = new EquipmentBuilder().withHwEQN(hwEqn).build();
        assertThat(cut.get(port), equalTo(expectedResult));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testPortNoOfBlockStartAlcatelMustNotBeZero() {
        SdslEquipmentServiceImpl.PortNoOfBlockStartStrategyAlcatel.instance.get(new EquipmentBuilder().withHwEQN(
                "1-1-5-0")
                .build());
    }

    public void testFindFirstPortOfBlock() {
        final Equipment equipment = new EquipmentBuilder()
                .withHwEQN("1-1-5-10")
                .build();
        final int blockStartPort = 8;
        final Equipment eqExampleWithLeadingZero = new EquipmentBuilder()
                .withHwEQN("1-1-5-08")
                .build();

        when(
                equipmentDaoMock.queryByExample(HwEqnArgumentMatcher.argThatFromEquipment(eqExampleWithLeadingZero),
                        eq(Equipment.class))
        ).thenReturn(
                ImmutableList.of(eqExampleWithLeadingZero));

        final Equipment result = cut.findPortOfBlock(equipment, blockStartPort,
                Equipment.HWEQNPART_DSLAM_PORT_ALCATEL);

        assertThat(result, equalTo(eqExampleWithLeadingZero));
    }

    @Test
    public void testFindPorts4Assignment() throws Exception {
        final ImmutableList<Equipment> blockEfmSize1 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().build()
        );
        final List<Equipment> blockEfmSize2_1 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().build(),
                new EquipmentBuilder().withRandomId().build()
        );
        final List<Equipment> blockEfmSize2_2 = ImmutableList.of(
                new EquipmentBuilder().withRandomId().build(),
                new EquipmentBuilder().withRandomId().build()
        );
        final List<List<Equipment>> efmBloecke =
                ImmutableList.of(
                        blockEfmSize1,
                        blockEfmSize2_1,
                        blockEfmSize2_2
                );
        final List<List<Equipment>> atmBloecke =
                ImmutableList.of(
                        (List<Equipment>) ImmutableList.of(
                                new EquipmentBuilder().withRandomId().build(),
                                new EquipmentBuilder().withRandomId().build()
                        )
                );
        final Long auftragId = Long.valueOf(1234L);
        final Date today = new Date();

        final List<Equipment> bloecke =
                cut.findPorts4Assignment(efmBloecke, atmBloecke, 2, auftragId, new Date());

        assertThat(bloecke, equalTo(blockEfmSize2_1));
    }

    @Test
    public void testFindPorts4AssignmentOnlyEfmAllowed() throws Exception {
        final List<List<Equipment>> efmBloeckeEmpty = ImmutableList.of(Collections.<Equipment>emptyList());
        final List<List<Equipment>> atmBloeckeThatWouldFit =
                ImmutableList.of(
                        (List<Equipment>) ImmutableList.of(
                                new EquipmentBuilder().withRandomId().build()
                        )
                );
        final Long auftragId = Long.valueOf(1234L);
        final Date today = new Date();

        when(leistungsServiceMock.findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_LAYER2, today))
                .thenReturn(new TechLeistungBuilder().withId(TechLeistung.TECH_LEISTUNG_EFM).build());

        final List<Equipment> bloecke =
                cut.findPorts4Assignment(efmBloeckeEmpty, atmBloeckeThatWouldFit, 1, auftragId, today);

        assertThat(bloecke, Matchers.<Equipment>empty());
    }

    @Test
    public void testFindPorts4AssignmentWithAtmAsFallback() throws Exception {
        final List<List<Equipment>> efmBloeckeEmpty = ImmutableList.of(Collections.<Equipment>emptyList());
        final List<List<Equipment>> atmBloeckeThatWouldFit =
                ImmutableList.of(
                        (List<Equipment>) ImmutableList.of(
                                new EquipmentBuilder().withRandomId().build()
                        )
                );
        final Long auftragId = Long.valueOf(1234L);
        final Date today = new Date();

        when(leistungsServiceMock.findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_LAYER2, today))
                .thenReturn(null);

        final List<Equipment> bloecke =
                cut.findPorts4Assignment(efmBloeckeEmpty, atmBloeckeThatWouldFit, 1, auftragId, today);

        assertThat(bloecke, equalTo(atmBloeckeThatWouldFit.get(0)));
    }

    private static class RangierungByEqInIdMatcher extends BaseMatcher<Equipment> {

        private final long eqInId;

        public RangierungByEqInIdMatcher(final long eqInId) {
            this.eqInId = eqInId;
        }

        @Override
        public boolean matches(final Object item) {
            return (item instanceof Rangierung) && ((Rangierung) item).getEqInId().equals(eqInId);
        }

        @Override
        public void describeTo(final Description description) {
        }
    }

    public static class HwEqnArgumentMatcher extends ArgumentMatcher<Equipment> {
        @Nonnull
        public final String hwEqnToMatch;

        public HwEqnArgumentMatcher(@Nonnull final String hwEqnToMatch) {
            this.hwEqnToMatch = hwEqnToMatch;
        }

        public static Equipment argThatFromEquipment(@Nonnull final Equipment equipment) {
            return argThat(new HwEqnArgumentMatcher(equipment.getHwEQN()));
        }

        @Override
        public boolean matches(@CheckForNull final Object argument) {
            return (argument != null) && (((Equipment) argument).getHwEQN().equals(hwEqnToMatch))
                    || (argument == null && hwEqnToMatch == null);
        }
    }

}
