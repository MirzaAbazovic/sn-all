/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 11:33:45
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOltChildBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.HWRackBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.Produkt2CvlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.TechLeistung2CvlanBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = SERVICE)
public class VlanServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.VlanService")
    private VlanService sut;

    private EkpFrameContract buildEkpFrameContract() {
        A10NspBuilder a10NspBuilder = getBuilder(A10NspBuilder.class).withNummer(99);
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withKindOfUseProduct("F")
                .withKindOfUseType("E").withRandomUniqueCode();
        A10NspPort a10NspPort = getBuilder(A10NspPortBuilder.class).withA10NspBuilder(a10NspBuilder)
                .withVbzBuilder(vbzBuilder)
                .build();

        return getBuilder(EkpFrameContractBuilder.class)
                .addCVlan(new CVlanBuilder().withTyp(HSI).build())
                .addCVlan(new CVlanBuilder().withTyp(VOIP).build())
                .addCVlan(new CVlanBuilder().withTyp(MC).build())
                .addCVlan(new CVlanBuilder().withTyp(POTS).build())
                .addCVlan(new CVlanBuilder().withTyp(VOD).build())
                .addA10NspPort(a10NspPort, Boolean.TRUE)
                .build();
    }

    @Test
    public void testGetNecessaryCvlanServiceTypes4AuftragWithoutTechLs() throws FindException {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class);
        getBuilder(Produkt2CvlanBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withCvlanServiceType(HSI)
                .build();
        getBuilder(Produkt2CvlanBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withCvlanServiceType(MC)
                .withIsMandatory(Boolean.FALSE)
                .build();

        Set<CvlanServiceTyp> result = sut.getNecessaryCvlanServiceTypes4Auftrag(
                Long.MAX_VALUE,
                prodBuilder.get().getId(),
                buildEkpFrameContract(),
                HVTStandort.HVT_STANDORT_TYP_FTTB,
                HVTTechnik.HUAWEI,
                LocalDate.now());
        assertNotEmpty(result);
        assertThat(result.size(), equalTo(1));
        assertThat(result, contains(CvlanServiceTyp.HSI));
    }

    @Test
    public void testGetNecessaryCvlanServiceTypes4AuftragWithTechLs() throws FindException {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Pair<ProduktBuilder, AuftragBuilder> produktAndAuftragId = prepareData4CvlanServiceTypesTest(yesterday);

        Set<CvlanServiceTyp> result = sut.getNecessaryCvlanServiceTypes4Auftrag(
                produktAndAuftragId.getSecond().get().getId(),
                produktAndAuftragId.getFirst().get().getId(),
                buildEkpFrameContract(),
                HVTStandort.HVT_STANDORT_TYP_FTTB,
                HVTTechnik.HUAWEI,
                yesterday);
        assertNotEmpty(result);
        assertThat(result.size(), equalTo(3));
        assertThat(result, containsInAnyOrder(HSI, VOIP, MC));
    }

    @Test
    public void testGetNecessaryCvlanServiceTypes4AuftragWithTechLsAndRemovedVoip() throws FindException {
        // VOIP ist mandatory beim Produkt
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Pair<ProduktBuilder, AuftragBuilder> produktAndAuftrag = prepareData4CvlanServiceTypesTest(yesterday);

        TechLeistungBuilder techLsVoipBuilder = getBuilder(TechLeistungBuilder.class);
        getBuilder(TechLeistung2CvlanBuilder.class).withTechLeistungBuilder(techLsVoipBuilder).withCvlanServiceType(VOIP).withRemoveLogic(Boolean.TRUE).build();

        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragBuilder(produktAndAuftrag.getSecond())
                .withTechleistungBuilder(techLsVoipBuilder).withAktivVon(Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant())).build();

        Set<CvlanServiceTyp> result = sut.getNecessaryCvlanServiceTypes4Auftrag(
                produktAndAuftrag.getSecond().get().getId(),
                produktAndAuftrag.getFirst().get().getId(),
                buildEkpFrameContract(),
                HVTStandort.HVT_STANDORT_TYP_FTTB,
                HVTTechnik.HUAWEI,
                yesterday);
        assertNotEmpty(result);
        assertThat(result.size(), equalTo(2));
        assertThat(result, containsInAnyOrder(HSI, MC));
    }

    @Test
    public void testGetNecessaryCvlanServiceTypes4AuftragWithTechLsAndRemovedMc() throws FindException {
        // MC is optional beim Produkt und wird durch eine Leistung mandatory und durch eine weitere Leistung wieder entfernt.
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Pair<ProduktBuilder, AuftragBuilder> produktAndAuftrag = prepareData4CvlanServiceTypesTest(yesterday);

        TechLeistungBuilder techLsMcBuilder = getBuilder(TechLeistungBuilder.class);
        getBuilder(TechLeistung2CvlanBuilder.class).withTechLeistungBuilder(techLsMcBuilder).withCvlanServiceType(MC).withRemoveLogic(Boolean.TRUE).build();

        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragBuilder(produktAndAuftrag.getSecond())
                .withTechleistungBuilder(techLsMcBuilder).withAktivVon(Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant())).build();

        Set<CvlanServiceTyp> result = sut.getNecessaryCvlanServiceTypes4Auftrag(
                produktAndAuftrag.getSecond().get().getId(),
                produktAndAuftrag.getFirst().get().getId(),
                buildEkpFrameContract(),
                HVTStandort.HVT_STANDORT_TYP_FTTB,
                HVTTechnik.HUAWEI,
                yesterday);
        assertNotEmpty(result);
        assertThat(result.size(), equalTo(2));
        assertThat(result, containsInAnyOrder(HSI, VOIP));
    }

    private Pair<ProduktBuilder, AuftragBuilder> prepareData4CvlanServiceTypesTest(LocalDate refDate) {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class);

        // techn. Leistung mit CVLANs MC+VOD
        TechLeistungBuilder techLsBuilder = getBuilder(TechLeistungBuilder.class);
        getBuilder(TechLeistung2CvlanBuilder.class).withTechLeistungBuilder(techLsBuilder).withCvlanServiceType(MC).build();
        getBuilder(TechLeistung2CvlanBuilder.class).withTechLeistungBuilder(techLsBuilder).withCvlanServiceType(VOD).build();

        // techn. Leistung mit CVLAN POTS
        TechLeistungBuilder techLsPotsBuilder = getBuilder(TechLeistungBuilder.class);
        getBuilder(TechLeistung2CvlanBuilder.class).withTechLeistungBuilder(techLsPotsBuilder).withCvlanServiceType(POTS).build();

        // techn. Leistung mit CVLAN IAD
        TechLeistungBuilder techLsIadBuilder = getBuilder(TechLeistungBuilder.class);
        getBuilder(TechLeistung2CvlanBuilder.class).withTechLeistungBuilder(techLsIadBuilder).withCvlanServiceType(IAD).build();

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(auftragBuilder).withProdBuilder(prodBuilder);
        // Leistung mit CVLANs MC+VOD ist aktiv (MC als Result erwartet; VOD nicht, da nicht fuer das Produkt konfiguriert)
        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragBuilder(auftragBuilder).withTechleistungBuilder(techLsBuilder)
                .withAktivVon(Date.from(refDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).build();
        // Leistung mit CVLAN IAD ist aktiv, aber nicht dem EKP-Rahmenvertrag zugeordnet
        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragBuilder(auftragBuilder).withTechleistungBuilder(techLsIadBuilder)
                .withAktivVon(Date.from(refDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).build();
        // Leistung mit CVLAN POTS ist nicht mehr aktiv
        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragBuilder(auftragBuilder).withTechleistungBuilder(techLsPotsBuilder)
                .withAktivVon(Date.from(refDate.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAktivBis(Date.from(refDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).build();

        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(HSI).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(VOIP).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(MC).withIsMandatory(Boolean.FALSE).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(POTS).withIsMandatory(Boolean.FALSE).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(IAD).withIsMandatory(Boolean.FALSE).build();

        return new Pair<>(prodBuilder, auftragBuilder);
    }

    @Test
    public void testFindEqVlan() {
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class);
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder).build();
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder).withCvlanTyp(VOIP).build();
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder).withCvlanTyp(IAD).build();

        // sollten nicht gefunden werden ...
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigBis(DateTools.minusWorkDays(1)).build();
        getBuilder(EqVlanBuilder.class).withEquipmentBuilder(getBuilder(EquipmentBuilder.class)).build();

        List<EqVlan> eqVlans = sut.findEqVlans(equipmentBuilder.get().getId(), new Date());
        assertThat(eqVlans.size(), equalTo(3));
    }

    @Test
    public void testAddEqVlanWithExistingFutureEqVlan() throws Exception {
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class);
        Date gueltigVon = DateTools.plusWorkDays(5);
        ArrayList<EqVlan> futureEqVlans = new ArrayList<>();
        futureEqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(gueltigVon)
                .withCvlanTyp(HSI).build());
        futureEqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(gueltigVon)
                .withCvlanTyp(VOIP).build());
        futureEqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(gueltigVon)
                .withCvlanTyp(IAD).build());

        ArrayList<EqVlan> eqVlans = new ArrayList<>();
        Date now = new Date();
        eqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(now)
                .withCvlanTyp(HSI)
                .withSvlanEkp(1).setPersist(false).build());
        eqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(now)
                .withCvlanTyp(VOIP)
                .withSvlanEkp(1).setPersist(false).build());
        eqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(now)
                .withCvlanTyp(IAD)
                .withSvlanEkp(1).setPersist(false).build());

        List<EqVlan> storedEqVlans = sut.addEqVlans(eqVlans, null);
        assertThat(storedEqVlans.size(), equalTo(eqVlans.size()));
        for (int i = 0; i < storedEqVlans.size(); i++) {
            assertThat(storedEqVlans.get(i), equalTo(eqVlans.get(i)));
        }

        List<EqVlan> equipmentEqVlans = sut.findEqVlans(equipmentBuilder.get().getId());
        assertThat(equipmentEqVlans.size(), equalTo(eqVlans.size() + futureEqVlans.size()));

        Map<Long, EqVlan> futureEqVlanMap = CollectionTools.toMap(futureEqVlans, AbstractCCIDModel::getId);

        for (EqVlan equipmentEqVlan : equipmentEqVlans) {
            EqVlan eqVlan = futureEqVlanMap.get(equipmentEqVlan.getId());
            if (eqVlan != null) {
                assertTrue(eqVlan.equalsAll(equipmentEqVlan));
                assertThat(equipmentEqVlan.getGueltigBis(), equalTo(now));
            }
        }
    }

    @Test
    public void testAddEqVlanWithExistingEqualFutureEqVlan() throws Exception {
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class);
        Date gueltigVon = DateTools.plusWorkDays(5);
        ArrayList<EqVlan> futureEqVlans = new ArrayList<>();
        futureEqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(gueltigVon)
                .withCvlanTyp(HSI).build());
        futureEqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(gueltigVon)
                .withCvlanTyp(VOIP).build());
        futureEqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(gueltigVon)
                .withCvlanTyp(IAD).build());

        ArrayList<EqVlan> eqVlans = new ArrayList<>();
        Date now = new Date();
        eqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(now)
                .withCvlanTyp(HSI)
                .setPersist(false).build());
        eqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(now)
                .withCvlanTyp(VOIP)
                .setPersist(false).build());
        eqVlans.add(getBuilder(EqVlanBuilder.class).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(now)
                .withCvlanTyp(IAD)
                .setPersist(false).build());

        List<EqVlan> storedEqVlans = sut.addEqVlans(eqVlans, null);
        assertThat(storedEqVlans.size(), equalTo(eqVlans.size()));
        for (int i = 0; i < storedEqVlans.size(); i++) {
            assertThat(storedEqVlans.get(i), equalTo(eqVlans.get(i)));
        }

        List<EqVlan> equipmentEqVlans = sut.findEqVlans(equipmentBuilder.get().getId());
        assertThat(equipmentEqVlans.size(), equalTo(eqVlans.size() + futureEqVlans.size()));

        Map<Long, EqVlan> futureEqVlanMap = CollectionTools.toMap(futureEqVlans, AbstractCCIDModel::getId);

        for (EqVlan equipmentEqVlan : equipmentEqVlans) {
            EqVlan eqVlan = futureEqVlanMap.get(equipmentEqVlan.getId());
            if (eqVlan != null) {
                assertTrue(eqVlan.equalsAll(equipmentEqVlan));
                assertThat(equipmentEqVlan.getGueltigBis(), equalTo(now));
            }
        }

    }

    private Long createOltChildAuftrag(Date vlanAktivAb, ProduktBuilder prodBuilder, Long standortTyp) {
        // OLT Standort
        HVTStandortBuilder oltStandortBuilder = getBuilder(HVTStandortBuilder.class).withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTX_BR);
        HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class)
                .withVlanAktivAb(vlanAktivAb)
                .withHwProducerBuilder(getBuilder(HVTTechnikBuilder.class).toHuawei())
                .withHvtStandortBuilder(oltStandortBuilder);

        // MDU bzw. Ont Standort und Auftrag
        HVTStandortBuilder oltChildStandortBuilder = getBuilder(HVTStandortBuilder.class).withStandortTypRefId(standortTyp);

        HWOltChildBuilder oltChildBuilder;
        if (HVTStandort.HVT_STANDORT_TYP_FTTB.equals(standortTyp)) {
            oltChildBuilder = getBuilder(HWMduBuilder.class);
        }
        else if (HVTStandort.HVT_STANDORT_TYP_FTTB_H.equals(standortTyp)) {
            oltChildBuilder = getBuilder(HWDpoBuilder.class);
        }
        else {
            oltChildBuilder = getBuilder(HWOntBuilder.class);
        }
        oltChildBuilder.withHWRackOltBuilder(oltBuilder);
        oltChildBuilder.withOltSlot("7");
        oltChildBuilder.withHvtStandortBuilder(oltChildStandortBuilder);
        HWBaugruppeBuilder oltChildBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withRackBuilder(oltChildBuilder);
        EquipmentBuilder oltChildEqBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(oltChildBaugruppeBuilder);
        RangierungBuilder oltChildRangBuilder = getBuilder(RangierungBuilder.class).withEqInBuilder(oltChildEqBuilder);
        EndstelleBuilder oltChildEndstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleTyp(
                Endstelle.ENDSTELLEN_TYP_B).withRangierungBuilder(oltChildRangBuilder);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(auftragBuilder).withProdBuilder(prodBuilder)
                .withStatusId(AuftragStatus.IN_BETRIEB).build();
        return getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder)
                .withEndstelleBuilder(oltChildEndstelleBuilder).build().getAuftragId();
    }

    private Long createOltAuftrag(Date vlanAktivAb, ProduktBuilder prodBuilder) {
        HVTStandortBuilder oltStandortBuilder = getBuilder(HVTStandortBuilder.class).withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTX_BR);
        HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class)
                .withVlanAktivAb(vlanAktivAb)
                .withHwProducerBuilder(getBuilder(HVTTechnikBuilder.class).toHuawei())
                .withHvtStandortBuilder(oltStandortBuilder);
        HWBaugruppeBuilder oltBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withRackBuilder(oltBuilder);
        HVTStandortBuilder ftthStandortBuilder = getBuilder(HVTStandortBuilder.class).withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTH);
        EquipmentBuilder oltEqBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(oltBaugruppeBuilder)
                .withHvtStandortBuilder(ftthStandortBuilder);
        RangierungBuilder oltRangBuilder = getBuilder(RangierungBuilder.class).withEqInBuilder(oltEqBuilder);
        EndstelleBuilder oltEndstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleTyp(
                Endstelle.ENDSTELLEN_TYP_B).withRangierungBuilder(oltRangBuilder);

        AuftragBuilder oltAuftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(oltAuftragBuilder).withProdBuilder(prodBuilder)
                .withStatusId(AuftragStatus.IN_BETRIEB).build();
        return getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(oltAuftragBuilder)
                .withEndstelleBuilder(oltEndstelleBuilder).build().getAuftragId();
    }

    private ProduktBuilder createProduktBuilder() {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class);
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(HSI).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(POTS).build();
        return prodBuilder;
    }

    @Test
    public void testCalculateEqVlansOltWithoutVlanAktivDate() throws Exception {
        ProduktBuilder prodBuilder = createProduktBuilder();
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(), createOltAuftrag(null, prodBuilder),
                prodBuilder.get().getId(), LocalDate.now());
        assertThat(eqVlans.size(), equalTo(0));
    }

    @Test
    public void testCalculateEqVlansOltWithVlanAktivDateInPast() throws Exception {
        ProduktBuilder prodBuilder = createProduktBuilder();

        LocalDate when = LocalDate.now();
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(),
                createOltAuftrag(DateTools.minusWorkDays(1), prodBuilder), prodBuilder.get().getId(), when);
        assertThat(eqVlans.size(), Matchers.greaterThan(0));
        assertThat(eqVlans.get(0).getGueltigVon(), equalTo(Date.from(when.atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    @Test
    public void testCalculateEqVlansOltWithVlanAktivDateInFuture() throws Exception {
        ProduktBuilder prodBuilder = createProduktBuilder();

        LocalDate when = LocalDate.now();
        Date vlanAktivAb = DateTools.plusWorkDays(1);
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(),
                createOltAuftrag(vlanAktivAb, prodBuilder), prodBuilder.get().getId(), when);
        assertThat(eqVlans.size(), Matchers.greaterThan(0));
        assertThat(eqVlans.get(0).getGueltigVon(),
                equalTo(Date.from(vlanAktivAb.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    @Test
    public void testCalculateEqVlansOltWithoutProduct2Cvlans() throws Exception {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class);

        LocalDate when = LocalDate.now();
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(),
                createOltAuftrag(DateTools.minusWorkDays(1), prodBuilder), prodBuilder.get().getId(), when);
        assertThat(eqVlans.size(), equalTo(0));
    }

    @Test
    public void testCalculateEqVlansMduWithVlanAktivDateInFuture() throws Exception {
        ProduktBuilder prodBuilder = createProduktBuilder();

        LocalDate when = LocalDate.now();
        Date vlanAktivAb = DateTools.plusWorkDays(1);
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(),
                createOltChildAuftrag(vlanAktivAb, prodBuilder, HVTStandort.HVT_STANDORT_TYP_FTTB), prodBuilder.get().getId(), when);
        assertThat(eqVlans.size(), Matchers.greaterThan(0));
        assertThat(eqVlans.get(0).getGueltigVon(),
                equalTo(Date.from(vlanAktivAb.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    @Test
    public void testCalculateEqVlansOntWithVlanAktivDateInFuture() throws Exception {
        ProduktBuilder prodBuilder = createProduktBuilder();

        LocalDate when = LocalDate.now();
        Date vlanAktivAb = DateTools.plusWorkDays(1);
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(),
                createOltChildAuftrag(vlanAktivAb, prodBuilder, HVTStandort.HVT_STANDORT_TYP_FTTH), prodBuilder.get().getId(), when);
        assertThat(eqVlans.size(), Matchers.greaterThan(0));
        assertThat(eqVlans.get(0).getGueltigVon(),
                equalTo(Date.from(vlanAktivAb.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    @Test
    public void testCalculateEqVlansDpoWithVlanAktivDateInFuture() throws Exception {
        ProduktBuilder prodBuilder = createProduktBuilder();

        LocalDate when = LocalDate.now();
        Date vlanAktivAb = DateTools.plusWorkDays(1);
        List<EqVlan> eqVlans = sut.calculateEqVlans(buildEkpFrameContract(),
                createOltChildAuftrag(vlanAktivAb, prodBuilder, HVTStandort.HVT_STANDORT_TYP_FTTB_H), prodBuilder.get().getId(), when);
        assertThat(eqVlans.size(), Matchers.greaterThan(0));
        assertThat(eqVlans.get(0).getGueltigVon(),
                equalTo(Date.from(vlanAktivAb.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    public void testChangeHwOltVlanAb() throws Exception {
        final Date vlanAktivAb = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class);
        HWMduBuilder mduBuilder = getBuilder(HWMduBuilder.class).withHWRackOltBuilder(oltBuilder);
        List<AuftragDaten> auftraege = createAuftragForRack(oltBuilder, vlanAktivAb, AuftragStatus.IN_BETRIEB,
                AuftragStatus.AUFTRAG_GEKUENDIGT);
        auftraege.addAll(createAuftragForRack(mduBuilder, vlanAktivAb, AuftragStatus.IN_BETRIEB,
                AuftragStatus.KUENDIGUNG,
                AuftragStatus.IN_BETRIEB));
        final Long oltId = oltBuilder.get().getId();

        Pair<HWOlt, Integer> retVal = sut.changeHwOltVlanAb(oltId, vlanAktivAb);
        HWOlt hwOltWithVlanAb = retVal.getFirst();

        assertThat(hwOltWithVlanAb, notNullValue());
        assertThat(hwOltWithVlanAb.getId(), equalTo(oltId));
        assertTrue(DateTools.isDateEqual(hwOltWithVlanAb.getVlanAktivAb(), vlanAktivAb));
        for (AuftragDaten auftrag : auftraege) {
            Date when = vlanAktivAb.after(auftrag.getInbetriebnahme()) ? vlanAktivAb : auftrag.getInbetriebnahme();
            List<EqVlan> eqVlans = sut.findEqVlans4Auftrag(auftrag.getAuftragId(),
                    DateConverterUtils.asLocalDate(when));
            if (auftrag.getStatusId() >= AuftragStatus.AUFTRAG_GEKUENDIGT) {
                assertThat(eqVlans.size(), equalTo(0));
            }
            else {
                assertThat(eqVlans.size(), Matchers.greaterThan(0));
                assertThat(eqVlans.get(0).getGueltigVon(), equalTo(when));
            }
        }
    }

    private List<AuftragDaten> createAuftragForRack(HWRackBuilder<?, ?> rack, Date vlanAktivAb, Long... auftragStatuss) {
        HWBaugruppeBuilder baugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withRackBuilder(rack);

        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class);
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(HSI).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder).withCvlanServiceType(VOIP).build();

        ProduktBuilder prodBuilder2 = getBuilder(ProduktBuilder.class);
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder2).withCvlanServiceType(HSI).build();
        getBuilder(Produkt2CvlanBuilder.class).withProduktBuilder(prodBuilder2).withCvlanServiceType(POTS).build();

        List<AuftragDaten> auftraege = new ArrayList<>();
        for (Long auftragStatus : auftragStatuss) {
            EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(
                    baugruppeBuilder);
            RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class).withEqInBuilder(equipmentBuilder);
            EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withRangierungBuilder(
                    rangierungBuilder);
            // Sonderfall gekündigt; rangierung.es_id = -1; weiteren Auftrag erstellen ... Port umhängen simulieren
            if (auftragStatus.equals(AuftragStatus.KUENDIGUNG)) {
                EndstelleBuilder endstelleBuilder2 = getBuilder(EndstelleBuilder.class).withRangierungBuilder(
                        rangierungBuilder);
                AuftragBuilder auftragBuilder2 = getBuilder(AuftragBuilder.class);
                getBuilder(AuftragTechnikBuilder.class)
                        .withAuftragBuilder(auftragBuilder2)
                        .withEndstelleBuilder(endstelleBuilder2);
                auftragBuilder2
                        .getAuftragDatenBuilder()
                        .withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG)
                        .withInbetriebnahme(Date.from(DateConverterUtils.asLocalDate(vlanAktivAb).plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        .withProdBuilder(prodBuilder2);
                auftragBuilder2.build();
                auftraege.add(auftragBuilder2.getAuftragDatenBuilder().get());

                rangierungBuilder.withEndstelleBuilder(getBuilder(EndstelleBuilder.class).withId(
                        Rangierung.RANGIERUNG_NOT_ACTIVE));
            }
            AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
            getBuilder(AuftragTechnikBuilder.class)
                    .withAuftragBuilder(auftragBuilder)
                    .withEndstelleBuilder(endstelleBuilder);
            auftragBuilder.getAuftragDatenBuilder().withStatusId(auftragStatus).withProdBuilder(prodBuilder);
            auftragBuilder.build();
            auftraege.add(auftragBuilder.getAuftragDatenBuilder().get());
        }
        return auftraege;
    }

}
