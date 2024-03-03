/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.2012 11:31:02
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.dao.cc.fttx.VlanDao;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = UNIT)
public class VlanServiceImplTest extends BaseTest {

    private static final Integer SVLAN_EKP_UNICAST = 409;
    private static final Integer SVLAN_EKP_MULTICAST = 400;
    private static final Integer SVLAN_OLT_UNICAST = 109;
    private static final Integer SVLAN_OLT_MULTICAST = 100;
    private static final Integer SVLAN_MDU = 149;

    private static final Integer CVLAN_MC = 82;
    private static final Integer CVLAN_VOIP = 81;
    private static final Integer CVLAN_HSI = 80;

    private static final String OLT_SLOT = "09";
    private static final Integer A10NSP_NUMBER = 2;
    private static final Integer OLT_NUMMER = 8;

    @InjectMocks
    @Spy
    private VlanServiceImpl cut;
    @Mock
    private HWService hwService;
    @Mock
    private HVTService hvtService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private EkpFrameContractService ekpFrameContractService;
    @Mock
    private VlanDao vlanDao;
    @Mock
    private CCAuftragService auftragService;

    private AuftragDaten auftragDaten;
    private Long auftragId;
    private Long productId;
    private EkpFrameContract ekpFrameContract, ekpFrameContractMnet;
    private LocalDate when;
    private Endstelle endstelle;
    private Rangierung rangierung;
    private Equipment equipment;
    private HWBaugruppe hwBaugruppe;
    private HWMdu hwMdu;
    private HWOlt hwOlt;
    private A10NspPort a10NspPort, a10NspPortMnet;
    private Auftrag2EkpFrameContract auftrag2Ekp;
    private List<EqVlan> oldEqVlans, eqVlans, newEqVlans;

    public static Date today() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date tomorrow() {
        return Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date yesterday() {
        return Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @BeforeMethod
    public void beforeMethod() throws FindException {
        MockitoAnnotations.initMocks(this);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        auftragDaten = new AuftragDatenBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .build();
        auftragId = auftragDaten.getAuftragId();
        productId = auftragDaten.getProdId();

        when = LocalDate.now();
        EkpFrameContractBuilder ekpFrameContractBuilder = new EkpFrameContractBuilder().setPersist(false)
                .addCVlan(new CVlanBuilder().withTyp(HSI).withValue(CVLAN_HSI).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(VOIP).withValue(CVLAN_VOIP).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(MC).withValue(CVLAN_MC).setPersist(false).build());
        ekpFrameContract = ekpFrameContractBuilder.build();
        ekpFrameContractMnet = new EkpFrameContractBuilder()
                .addCVlan(new CVlanBuilder().withTyp(HSI).withValue(CVLAN_HSI).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(VOIP).withValue(CVLAN_VOIP).setPersist(false).build())
                .addCVlan(new CVlanBuilder().withTyp(MC).withValue(CVLAN_MC).setPersist(false).build())
                .setPersist(false).build();
        EquipmentBuilder equipmentBuilder = new EquipmentBuilder().withRandomId().setPersist(false);
        equipment = equipmentBuilder.build();
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId().withEqInBuilder(equipmentBuilder)
                .setPersist(false);
        rangierung = rangierungBuilder.build();
        endstelle = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder).setPersist(false).build();
        hwBaugruppe = new HWBaugruppeBuilder().withBaugruppenTypBuilder(new HWBaugruppenTypBuilder()).setPersist(false)
                .build();
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(false);
        HVTStandort standort = hvtStandortBuilder.build();
        HWOltBuilder oltBuilder = new HWOltBuilder().withRandomId().withOltNr(OLT_NUMMER)
                .withHwProducerBuilder(new HVTTechnikBuilder().withId(HVTTechnik.HUAWEI))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withVlanAktivAb(DateTools.minusWorkDays(5))
                .withRackTyp(HWRack.RACK_TYPE_OLT)
                .setPersist(false);
        hwOlt = oltBuilder.build();
        hwMdu = new HWMduBuilder().withHWRackOltBuilder(oltBuilder).withOltSlot(OLT_SLOT)
                .withRackTyp(HWRack.RACK_TYPE_MDU).setPersist(false).build();
        A10NspBuilder a10NspBuilder = new A10NspBuilder().withNummer(A10NSP_NUMBER).setPersist(false);
        a10NspPort = new A10NspPortBuilder().withA10NspBuilder(a10NspBuilder).withOlts(ImmutableSet.of(hwOlt))
                .setPersist(false)
                .build();
        A10NspBuilder a10NspMnetBuilder = new A10NspBuilder().setPersist(false);
        a10NspPortMnet = new A10NspPortBuilder().withA10NspBuilder(a10NspMnetBuilder).withOlts(ImmutableSet.of(hwOlt))
                .setPersist(false)
                .build();
        Set<CvlanServiceTyp> cvlanServiceTypes = ImmutableSet.of(HSI, VOIP, MC);

        oldEqVlans = ImmutableList.of(
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.HSIPLUS)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withGueltigVon(DateTools.minusWorkDays(5))
                        .withGueltigBis(DateTools.minusWorkDays(3))
                        .build(),
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.VOIP)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withGueltigVon(DateTools.minusWorkDays(5))
                        .withGueltigBis(DateTools.minusWorkDays(3))
                        .build()
        );

        eqVlans = ImmutableList.of(
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.HSI)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withGueltigVon(DateTools.minusWorkDays(3))
                        .build(),
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.VOIP)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withGueltigVon(DateTools.minusWorkDays(3))
                        .build()
        );
        when(vlanDao.findEqVlans(eq(equipment.getId()), any(Date.class))).thenReturn(eqVlans);
        when(vlanDao.findEqVlansForFuture(eq(equipment.getId()), any(Date.class))).thenReturn(eqVlans);

        newEqVlans = ImmutableList.of(
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.VOIP)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withGueltigVon(tomorrow())
                        .build(),
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(HSI)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withGueltigVon(tomorrow())
                        .build()
        );

        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten);
        doReturn(cvlanServiceTypes).when(cut).getNecessaryCvlanServiceTypes4Auftrag(auftragId, productId,
                ekpFrameContract, HVTStandort.HVT_STANDORT_TYP_FTTB, HVTTechnik.HUAWEI, when);
        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(rangierungsService.findRangierung(any(Long.class))).thenReturn(rangierung);
        when(rangierungsService.findRangierungenTx(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(
                new Rangierung[] { rangierung });
        when(rangierungsService.findEquipment(any(Long.class))).thenReturn(equipment);
        when(hwService.findBaugruppe(any(Long.class))).thenReturn(hwBaugruppe);
        when(hwService.findRackById(null)).thenReturn(hwMdu);
        when(hwService.findRackById(hwOlt.getId())).thenReturn(hwOlt);
        when(hvtService.findHVTStandort(standort.getId())).thenReturn(standort);
        when(ekpFrameContractService.findA10NspPort(ekpFrameContract, hwOlt.getId())).thenReturn(a10NspPort);
        when(ekpFrameContractService.findA10NspPort(ekpFrameContractMnet, hwOlt.getId())).thenReturn(a10NspPortMnet);
        when(ekpFrameContractService.getDefaultEkpFrameContract()).thenReturn(ekpFrameContractMnet);

        auftrag2Ekp = new Auftrag2EkpFrameContractBuilder().withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpFrameContractBuilder)
                .build();
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(auftragId, when)).thenReturn(auftrag2Ekp);

        when(vlanDao.store(any(EqVlan.class))).thenAnswer(new Answer<EqVlan>() {
            @Override
            public EqVlan answer(InvocationOnMock invocation) throws Throwable {
                return (EqVlan) invocation.getArguments()[0];
            }
        });

    }

    @DataProvider(name = "oltChildsForEqVlan")
    public static Object[][] oltChildsForEqVlan() {
        long dpoRackID = 998881L;
        long ontRackID = 998882L;
        long mduRackID = 998883L;
        HWDpo hwDPO = new HWDpoBuilder()
                .withOltSlot(OLT_SLOT)
                .withRackTyp(HWRack.RACK_TYPE_DPO)
                .setPersist(false).build();
        hwDPO.setId(dpoRackID);
        HWOnt hwOnt = new HWOntBuilder()
                .withOltSlot(OLT_SLOT)
                .withRackTyp(HWRack.RACK_TYPE_ONT)
                .setPersist(false).build();
        hwOnt.setId(ontRackID);
        HWMdu hwMdu = new HWMduBuilder()
                .withOltSlot(OLT_SLOT)
                .withRackTyp(HWRack.RACK_TYPE_MDU)
                .setPersist(false).build();
        hwMdu.setId(mduRackID);
        return new Object[][] {
                { hwDPO, dpoRackID },
                { hwOnt, ontRackID },
                { hwMdu, mduRackID },
        };

    }

    @Test(dataProvider = "oltChildsForEqVlan")
    public void calculateEqVlansOltChilds(HWOltChild hwOltChild, long hwOltChildRackId) throws FindException {
        hwOltChild.setOltRackId(hwOlt.getId());
        when(hwService.findRackById(hwOltChildRackId)).thenReturn(hwOltChild);
        hwBaugruppe.setRackId(hwOltChildRackId);
        List<EqVlan> result = cut.calculateEqVlans(ekpFrameContract, auftragId, productId, when);

        verify(hwService).findRackById(hwOlt.getId());
        verify(hwService).findRackById(hwOltChild.getId());
        assertNotEmpty(result);
        assertThat(result.size(), equalTo(3));

        assertCvlan(result, HSI, CVLAN_HSI, SVLAN_EKP_UNICAST, SVLAN_OLT_UNICAST, SVLAN_MDU);
        assertCvlan(result, VOIP, CVLAN_VOIP, SVLAN_EKP_UNICAST, SVLAN_OLT_UNICAST, SVLAN_MDU);
        assertCvlan(result, MC, CVLAN_MC, SVLAN_EKP_MULTICAST, SVLAN_OLT_MULTICAST, SVLAN_MDU);
    }

    private void assertCvlan(List<EqVlan> eqVlans, CvlanServiceTyp cvlanServiceTyp, Integer cvlan, Integer svlanEkp,
            Integer svlanOlt, Integer svlanMdu) {
        EqVlan matchingEqVlan = null;
        for (EqVlan vlan : eqVlans) {
            if (cvlanServiceTyp.equals(vlan.getCvlanTyp())) {
                matchingEqVlan = vlan;
                break;
            }
        }

        if (matchingEqVlan != null) {
            assertThat(matchingEqVlan.getCvlan(), equalTo(cvlan));
            assertThat(matchingEqVlan.getSvlanEkp(), equalTo(svlanEkp));
            assertThat(matchingEqVlan.getSvlanMdu(), equalTo(svlanMdu));
            assertThat(matchingEqVlan.getSvlanOlt(), equalTo(svlanOlt));
            assertThat(matchingEqVlan.getGueltigVon(), equalTo(Date.from(when.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            assertThat(matchingEqVlan.getGueltigBis(), equalTo(DateTools.getHurricanEndDate()));
        }
        else {
            fail(String.format("No EqVlan found for CvlanServiceTyp <%s>", cvlanServiceTyp));
        }
    }

    @Test
    public void testStoreEqVlans() throws Exception {
        // keine Aenderungen, alte Liste sollte zurueckgegeben werden
        List<EqVlan> storedEqVlans = cut.addEqVlans(newEqVlans, null);
        assertThat(eqVlans, equalTo(storedEqVlans));

        // Aenderungen, alte EqVlans historisieren, neue zurueckgeben
        newEqVlans.get(1).setSvlanEkp(SVLAN_EKP_UNICAST);
        List<EqVlan> storedEqVlans2 = cut.addEqVlans(newEqVlans, null);
        assertThat(newEqVlans, equalTo(storedEqVlans2));
        assertThat(eqVlans.get(0).getGueltigBis(), equalTo(newEqVlans.get(0).getGueltigVon()));
    }

    @Test
    public void testStoreEqVlansNoHistory() throws Exception {
        List<EqVlan> eqVlans = new ArrayList<>();
        EquipmentBuilder equipmentBuilder = new EquipmentBuilder().withRandomId().setPersist(false);
        eqVlans.add(new EqVlanBuilder().withCvlanTyp(HSI).withEquipmentBuilder(equipmentBuilder).setPersist(false)
                .build());
        eqVlans.add(new EqVlanBuilder().withCvlanTyp(CvlanServiceTyp.VOIP).withEquipmentBuilder(equipmentBuilder)
                .setPersist(false).build());
        List<EqVlan> storedEqVlans = cut.addEqVlans(eqVlans, null);
        assertThat(eqVlans, equalTo(storedEqVlans));
    }

    @Test(expectedExceptions = { StoreException.class })
    public void testStoreEqVlansDifferentEquipments() throws Exception {
        List<EqVlan> eqVlans = new ArrayList<>();
        eqVlans.add(new EqVlanBuilder().withCvlanTyp(CvlanServiceTyp.VOIP)
                .withEquipmentBuilder(new EquipmentBuilder().withRandomId().setPersist(false)).setPersist(false)
                .build());
        eqVlans.add(new EqVlanBuilder().withCvlanTyp(HSI)
                .withEquipmentBuilder(new EquipmentBuilder().withRandomId().setPersist(false)).setPersist(false)
                .build());
        cut.addEqVlans(eqVlans, null);
    }

    @Test(expectedExceptions = { StoreException.class })
    public void testStoreEqVlansDifferentGueltigVon() throws Exception {
        List<EqVlan> eqVlans = new ArrayList<>();
        EquipmentBuilder equipmentBuilder = new EquipmentBuilder().withRandomId().setPersist(false);
        eqVlans.add(new EqVlanBuilder().withCvlanTyp(HSI).withEquipmentBuilder(equipmentBuilder).setPersist(false)
                .build());
        eqVlans.add(new EqVlanBuilder().withCvlanTyp(CvlanServiceTyp.VOIP).withEquipmentBuilder(equipmentBuilder)
                .withGueltigVon(DateTools.minusWorkDays(35))
                .setPersist(false).build());
        cut.addEqVlans(eqVlans, null);
    }

    public void findEqVlans4Auftrag() throws FindException {
        List<EqVlan> result = cut.findEqVlans4Auftrag(auftragId, when);
        assertThat(result, equalTo(eqVlans));
    }

    @Test(expectedExceptions = { StoreException.class })
    public void validateParams4ChangeHwOltVlanAb_VlanAbOfOltIsNotInFuture() throws StoreException, FindException {
        when(hwService.findRackById(hwOlt.getId())).thenReturn(new HWOltBuilder().withVlanAktivAb(yesterday()).build());
        cut.validateParams4ChangeHwOltVlanAb(hwOlt.getId(), tomorrow());
    }

    @DataProvider(name = "validateParams4ChangeHwOltVlanAb_FailsDataProvider")
    Object[][] validateParams4ChangeHwOltVlanAb_FailsDataProvider() {
        return new Object[][] {
                //@formatter:off
                { RandomTools.createLong(), tomorrow(), null},
                { RandomTools.createLong(), tomorrow(), new HWMdu()},
                { RandomTools.createLong(), tomorrow(), new HWOltBuilder().withVlanAktivAb(yesterday()).build()},
                //@formatter:on
        };
    }

    @Test(expectedExceptions = { StoreException.class }, dataProvider = "validateParams4ChangeHwOltVlanAb_FailsDataProvider")
    public void validateParams4ChangeHwOltVlanAb_Fails(Long oltId, Date vlanAktivAb, HWRack oltToFind)
            throws StoreException,
            FindException {
        when(hwService.findRackById(oltId)).thenReturn(oltToFind);
        cut.validateParams4ChangeHwOltVlanAb(oltId, vlanAktivAb);
    }

    @DataProvider(name = "validateParams4ChangeHwOltVlanAb_SuccessDataProvider")
    Object[][] validateParams4ChangeHwOltVlanAb_SuccessDataProvider() {
        return new Object[][] {
                //@formatter:off
                { today(),    new HWOltBuilder().withVlanAktivAb(null).build()},
                { null,       new HWOltBuilder().withVlanAktivAb(null).build()},
                { tomorrow(), new HWOltBuilder().withVlanAktivAb(null).build()},
                { tomorrow(), new HWOltBuilder().withVlanAktivAb(today()).build()},
                { today(),    new HWOltBuilder().withVlanAktivAb(today()).build()},
                { null,       new HWOltBuilder().withVlanAktivAb(today()).build()},
                { tomorrow(), new HWOltBuilder().withVlanAktivAb(tomorrow()).build()},
                { null,       new HWOltBuilder().withVlanAktivAb(tomorrow()).build()},
                //@formatter:on
        };
    }

    @Test(dataProvider = "validateParams4ChangeHwOltVlanAb_SuccessDataProvider")
    public void validateParams4ChangeHwOltVlanAb_Success(Date vlanAktivAb, HWOlt oltToFind)
            throws StoreException, FindException {
        when(hwService.findRackById(oltToFind.getId())).thenReturn(oltToFind);
        try {
            cut.validateParams4ChangeHwOltVlanAb(oltToFind.getId(), vlanAktivAb);
        }
        catch (Exception e) {
            fail();
        }
    }

    public void cancelEqVlans() throws Exception {
        AuftragAktion aktion = new AuftragAktionBuilder().withRandomId().setPersist(false).build();
        EquipmentBuilder equipmentBuilder = new EquipmentBuilder().withRandomId().setPersist(false);

        List<EqVlan> eqVlansOfEquipment = ImmutableList.of(
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(HSI)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withAuftragAktionsIdRemove(aktion.getId())
                        .build(),
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.VOIP)
                        .withEquipmentBuilder(equipmentBuilder)
                        .withAuftragAktionsIdAdd(aktion.getId())
                        .build()
        );

        doReturn(eqVlansOfEquipment).when(cut).findEqVlans(any(Long.class));

        cut.cancelEqVlans(auftragId, aktion);

        verify(vlanDao).delete(eq(eqVlansOfEquipment.get(1)));
        verify(vlanDao).store(eq(eqVlansOfEquipment.get(0)));
        assertThat(eqVlansOfEquipment.get(0).getGueltigBis(), equalTo(DateTools.getHurricanEndDate()));
    }

    @SuppressWarnings("unchecked")
    public void moveEqVlans() throws Exception {
        when(vlanDao.queryByExample(any(), eq(EqVlan.class))).thenReturn(eqVlans);
        when(vlanDao.findEqVlans(equipment.getId())).thenReturn(
                (List<EqVlan>) CollectionUtils.union(oldEqVlans, eqVlans));

        LocalDate newDate = DateConverterUtils.asLocalDate(tomorrow()).plusDays(1);
        cut.moveEqVlans4Auftrag(auftragId, DateConverterUtils.asLocalDate(eqVlans.get(0).getGueltigVon()), newDate, null);

        assertThat(eqVlans.get(0).getGueltigVon(), equalTo(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertThat(eqVlans.get(1).getGueltigVon(), equalTo(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertThat(oldEqVlans.get(0).getGueltigBis(), equalTo(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertThat(oldEqVlans.get(1).getGueltigBis(), equalTo(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        verify(vlanDao).store(eq(eqVlans.get(0)));
        verify(vlanDao).store(eq(eqVlans.get(1)));
        verify(vlanDao).store(eq(oldEqVlans.get(0)));
        verify(vlanDao).store(eq(oldEqVlans.get(1)));
    }

    public void moveEqVlansDateInFuture() throws Exception {
        when(vlanDao.queryByExample(any(), eq(EqVlan.class))).thenReturn(new ArrayList<EqVlan>());
        when(vlanDao.findEqVlans(equipment.getId())).thenReturn(newEqVlans);
        when(vlanDao.findEqVlans(any(Long.class), any(Date.class))).thenReturn(newEqVlans);

        LocalDate newDate = LocalDate.now().atStartOfDay().toLocalDate();
        cut.moveEqVlans4Auftrag(auftragId, DateConverterUtils.asLocalDate(newEqVlans.get(0).getGueltigVon()), newDate, null);

        assertThat(newEqVlans.get(0).getGueltigVon(), equalTo(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertThat(newEqVlans.get(1).getGueltigVon(), equalTo(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));

    }

    public void calculateAndAddEqVlans_VlanAktivAbIsNotNull() throws Exception {
        LocalDate vlanAktivAb = tomorrow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<EqVlan> eqVlansToCreate = Arrays.asList(new EqVlanBuilder().build());

        doReturn(eqVlansToCreate).when(cut).calculateEqVlans(ekpFrameContract, auftragId, productId, vlanAktivAb);
        doReturn(eqVlansToCreate).when(cut).addEqVlans(eqVlansToCreate, null);

        cut.assignEqVlans(ekpFrameContract, auftragId, productId, vlanAktivAb, null);
        verify(cut).calculateEqVlans(eq(ekpFrameContract), eq(auftragId), eq(productId), eq(vlanAktivAb));
        verify(cut).addEqVlans(eq(eqVlansToCreate), any(AuftragAktion.class));
    }

    public void calculateAndAddEqVlans_NoEqVlanstoCreate() throws Exception {
        LocalDate vlanAktivAb = tomorrow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        doReturn(Collections.emptyList()).when(cut).calculateEqVlans(ekpFrameContract, auftragId, productId,
                vlanAktivAb);
        List<EqVlan> eqVlans = cut.assignEqVlans(ekpFrameContract, auftragId, productId, vlanAktivAb, null);
        assertThat(eqVlans.size(), equalTo(0));
        verify(cut).calculateEqVlans(eq(ekpFrameContract), eq(auftragId), eq(productId), eq(vlanAktivAb));
    }

    public void findBaugruppen4OltAndItsMdusAndDpos() throws Exception {
        HWOlt hwOlt = new HWOltBuilder().withRandomId().build();
        HWMdu hwMdu1 = new HWMduBuilder().withRandomId().build();
        HWMdu hwMdu2 = new HWMduBuilder().withRandomId().build();
        HWDpo hwDpo1 = new HWDpoBuilder().withRandomId().build();
        HWBaugruppe baugruppeMdu1 = new HWBaugruppeBuilder().withRandomId().build();
        HWBaugruppe baugruppeMdu2 = new HWBaugruppeBuilder().withRandomId().build();
        HWBaugruppe baugruppeDpo1 = new HWBaugruppeBuilder().withRandomId().build();
        HWBaugruppe baugruppeOlt1 = new HWBaugruppeBuilder().withRandomId().build();
        HWBaugruppe baugruppeOlt2 = new HWBaugruppeBuilder().withRandomId().build();

        when(hwService.findHWOltChildByOlt(hwOlt.getId(), HWMdu.class)).thenReturn(Arrays.asList(hwMdu1, hwMdu2));
        when(hwService.findHWOltChildByOlt(hwOlt.getId(), HWDpo.class)).thenReturn(Arrays.asList(hwDpo1));
        when(hwService.findBaugruppen4Rack(hwMdu1.getId())).thenReturn(Arrays.asList(baugruppeMdu1));
        when(hwService.findBaugruppen4Rack(hwMdu2.getId())).thenReturn(Arrays.asList(baugruppeMdu2));
        when(hwService.findBaugruppen4Rack(hwDpo1.getId())).thenReturn(Arrays.asList(baugruppeDpo1));
        when(hwService.findBaugruppen4Rack(hwOlt.getId())).thenReturn(Arrays.asList(baugruppeOlt1, baugruppeOlt2));

        List<HWBaugruppe> baugruppenFound = cut.findBaugruppen4OltAndItsMdusAndDpos(hwOlt.getId());

        assertThat(baugruppenFound.size(), equalTo(5));
        assertThat(baugruppenFound, contains(baugruppeMdu1, baugruppeMdu2, baugruppeDpo1, baugruppeOlt1, baugruppeOlt2));

        verify(hwService).findHWOltChildByOlt(hwOlt.getId(), HWMdu.class);
        verify(hwService).findHWOltChildByOlt(hwOlt.getId(), HWDpo.class);
        verify(hwService).findBaugruppen4Rack(hwMdu1.getId());
        verify(hwService).findBaugruppen4Rack(hwMdu2.getId());
        verify(hwService).findBaugruppen4Rack(hwDpo1.getId());
        verify(hwService).findBaugruppen4Rack(hwOlt.getId());
    }

    @DataProvider(name = "changeHwOltVlanAbDataProvider")
    public Object[][] changeHwOltVlanAbDataProvider() {
        return new Object[][] {
                { tomorrow(), null },
                { null, tomorrow() },
        };
    }

    @Test(dataProvider = "changeHwOltVlanAbDataProvider")
    public void changeHwOltVlanAb(Date vlanAktivAb, Date oltsCurrentVlanAktivAb) throws Exception {
        HWOlt oltToFind = new HWOltBuilder().withRandomId().withVlanAktivAb(oltsCurrentVlanAktivAb).build();
        List<HWBaugruppe> baugruppenToFind = Arrays.asList(new HWBaugruppeBuilder().withRandomId().build());
        List<AuftragDaten> auftragDatenToFind = Arrays.asList(new AuftragDatenBuilder().withRandomId()
                .withInbetriebnahme(DateTools.plusWorkDays(-3)).build());

        doReturn(oltToFind).when(cut).validateParams4ChangeHwOltVlanAb(oltToFind.getId(), vlanAktivAb);
        doReturn(baugruppenToFind).when(cut).findBaugruppen4OltAndItsMdusAndDpos(oltToFind.getId());
        when(auftragService.findAktiveAuftragDatenByBaugruppe(baugruppenToFind.get(0).getId())).thenReturn(
                auftragDatenToFind);
        doReturn(new ArrayList<EqVlan>()).when(cut).assignEqVlans(any(EkpFrameContract.class), any(Long.class),
                any(Long.class),
                any(LocalDate.class), any(AuftragAktion.class));
        when(
                ekpFrameContractService.findEkp4AuftragOrDefaultMnet(any(Long.class), any(LocalDate.class),
                        any(Boolean.class))
        ).thenReturn(ekpFrameContract);
        when(hwService.saveHWRack(oltToFind)).thenReturn(oltToFind);

        Pair<HWOlt, Integer> retVal = cut.changeHwOltVlanAb(oltToFind.getId(), vlanAktivAb);
        HWOlt oltFound = retVal.getFirst();
        assertThat(oltFound, equalTo(oltToFind));
        assertThat(oltFound.getVlanAktivAb(), equalTo(oltToFind.getVlanAktivAb()));

        verify(cut).validateParams4ChangeHwOltVlanAb(eq(oltToFind.getId()), eq(vlanAktivAb));
        verify(cut).findBaugruppen4OltAndItsMdusAndDpos(eq(oltToFind.getId()));
        if (vlanAktivAb != null) {
            verify(auftragService).findAktiveAuftragDatenByBaugruppe(eq(baugruppenToFind.get(0).getId()));
            verify(cut).assignEqVlans(eq(ekpFrameContract), eq(auftragDatenToFind.get(0).getAuftragId()),
                    eq(productId),
                    eq(DateConverterUtils.asLocalDate(vlanAktivAb)), any(AuftragAktion.class));
        }
        verify(hwService).saveHWRack(eq(oltToFind));
    }

    @DataProvider(name = "changeHwOltVlanAb_VlanAbDoesNotChangeDataProvider")
    public Object[][] changeHwOltVlanAb_VlanAbDoesNotChangeDataProvider() {
        return new Object[][] {
                { null },
                { tomorrow() },
        };
    }

    @Test(dataProvider = "changeHwOltVlanAb_VlanAbDoesNotChangeDataProvider")
    public void changeHwOltVlanAb_VlanAbDoesNotChange(Date vlanAktivAb) throws Exception {
        HWOlt oltToFind = new HWOltBuilder().withRandomId().withVlanAktivAb(vlanAktivAb).build();

        doReturn(oltToFind).when(cut).validateParams4ChangeHwOltVlanAb(oltToFind.getId(), vlanAktivAb);
        when(hwService.saveHWRack(eq(oltToFind))).thenReturn(oltToFind);

        Pair<HWOlt, Integer> retVal = cut.changeHwOltVlanAb(oltToFind.getId(), vlanAktivAb);
        HWOlt oltFound = retVal.getFirst();
        assertThat(oltFound, equalTo(oltToFind));
        assertThat(oltFound.getVlanAktivAb(), equalTo(oltToFind.getVlanAktivAb()));

        verify(cut).validateParams4ChangeHwOltVlanAb(eq(oltToFind.getId()), eq(vlanAktivAb));
        verify(cut, never()).findBaugruppen4OltAndItsMdusAndDpos(any(Long.class));
        verify(auftragService, never()).findAktiveAuftragDatenByBaugruppe(any(Long.class));
        verify(cut, never()).assignEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class), any(AuftragAktion.class));
        verify(hwService, never()).saveHWRack(eq(oltToFind));
    }

    @SuppressWarnings("unchecked")
    public void assignEqVlansUseDefaultEkpSuccess() throws FindException, StoreException {
        when(ekpFrameContractService.getDefaultEkpFrameContract()).thenReturn(null);
        doReturn(null).when(cut).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class));
        doReturn(null).when(cut).addEqVlans(any(List.class), any(AuftragAktion.class));
        cut.assignEqVlans(ekpFrameContract, Long.valueOf(1), Long.valueOf(1), LocalDate.now(), null);
        verify(cut, times(1)).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class));
        verify(cut, times(1)).addEqVlans(any(List.class), any(AuftragAktion.class));
    }

    @SuppressWarnings("unchecked")
    public void assignEqVlansNoDefaultEkpSuccess() throws FindException, StoreException {
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(any(Long.class), any(LocalDate.class)))
                .thenReturn(new Auftrag2EkpFrameContract());
        doReturn(null).when(cut).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class));
        doReturn(null).when(cut).addEqVlans(any(List.class), any(AuftragAktion.class));
        cut.assignEqVlans(ekpFrameContract, Long.valueOf(1), Long.valueOf(1), LocalDate.now(), null);
        verify(ekpFrameContractService, times(0)).getDefaultEkpFrameContract();
        verify(cut, times(1)).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class));
        verify(cut, times(1)).addEqVlans(any(List.class), any(AuftragAktion.class));
    }

    @SuppressWarnings("unchecked")
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_NONNULL_PARAM_VIOLATION", justification = "null parameter should throw an exception")
    public void assignEqVlansNoDefaultEkpNoEkpFramContract() throws FindException, StoreException {
        try {
            cut.assignEqVlans(null, Long.valueOf(1), Long.valueOf(1), LocalDate.now(), null);
        }
        catch (FindException e) {
            verify(cut, times(0)).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                    any(LocalDate.class));
            verify(cut, times(0)).addEqVlans(any(List.class), any(AuftragAktion.class));
            return;
        }
        fail();
    }

    @SuppressWarnings("unchecked")
    public void assignEqVlansNoDefaultEkpCalculateEqVlansFails() throws FindException, StoreException {
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(any(Long.class), any(LocalDate.class)))
                .thenReturn(new Auftrag2EkpFrameContract());
        doThrow(new FindException()).when(cut).calculateEqVlans(any(EkpFrameContract.class), any(Long.class),
                any(Long.class), any(LocalDate.class));
        try {
            cut.assignEqVlans(ekpFrameContract, Long.valueOf(1), Long.valueOf(1), LocalDate.now(), null);
        }
        catch (FindException e) {
            verify(cut, times(1)).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                    any(LocalDate.class));
            verify(cut, times(0)).addEqVlans(any(List.class), any(AuftragAktion.class));
            return;
        }
        fail();
    }

    @SuppressWarnings("unchecked")
    public void assignEqVlansNoDefaultEkpAddEqVlansFails() throws FindException, StoreException {
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(any(Long.class), any(LocalDate.class)))
                .thenReturn(new Auftrag2EkpFrameContract());
        doReturn(null).when(cut).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class));
        doThrow(new StoreException()).when(cut).addEqVlans(any(List.class), any(AuftragAktion.class));
        try {
            cut.assignEqVlans(ekpFrameContract, Long.valueOf(1), Long.valueOf(1), LocalDate.now(), null);
        }
        catch (StoreException e) {
            verify(cut, times(1)).calculateEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                    any(LocalDate.class));
            verify(cut, times(1)).addEqVlans(any(List.class), any(AuftragAktion.class));
            return;
        }
        fail();
    }

    public void addEqVlansDefineAktionsId() throws FindException, StoreException {
        AuftragAktion aktion = new AuftragAktionBuilder().withRandomId().setPersist(false).build();
        List<EqVlan> differentEqVlans = ImmutableList.of(
                new EqVlanBuilder().setPersist(false).withRandomId()
                        .withCvlanTyp(CvlanServiceTyp.POTS)
                        .withEquipmentBuilder(
                                new EquipmentBuilder().withRandomId().setPersist(false))
                        .withGueltigVon(tomorrow())
                        .build()
        );

        doReturn(eqVlans).when(vlanDao).findEqVlansForFuture(any(Long.class), any(Date.class));
        List<EqVlan> assignedVlans = cut.addEqVlans(differentEqVlans, aktion);

        for (EqVlan oldEqVlan : eqVlans) {
            assertNotNull(oldEqVlan.getAuftragAktionsIdRemove());
        }

        for (EqVlan assignedVlan : assignedVlans) {
            assertNotNull(assignedVlan.getAuftragAktionsIdAdd());
            assertNull(assignedVlan.getAuftragAktionsIdRemove());
        }
    }

    @DataProvider
    Object[][] dataProviderCheckOltVlanAktivAb() {
        HWOlt olt = new HWOlt();
        olt.setId(Long.valueOf(2));
        olt.setRackTyp(HWRack.RACK_TYPE_OLT);
        HWBaugruppe hwBaugruppeOlt = new HWBaugruppe();
        hwBaugruppeOlt.setId(Long.valueOf(2));
        hwBaugruppeOlt.setRackId(olt.getId());
        HWMdu mdu = new HWMdu();

        mdu.setId(Long.valueOf(1));
        mdu.setRackTyp(HWRack.RACK_TYPE_MDU);
        mdu.setOltRackId(olt.getId());
        HWBaugruppe hwBaugruppeMdu = new HWBaugruppe();
        hwBaugruppeMdu.setId(Long.valueOf(1));
        hwBaugruppeMdu.setRackId(mdu.getId());

        HWDpo dpo = new HWDpo();
        dpo.setId(Long.valueOf(3));
        dpo.setRackTyp(HWRack.RACK_TYPE_DPO);
        dpo.setOltRackId(olt.getId());
        HWBaugruppe hwBaugruppeDpo = new HWBaugruppe();
        hwBaugruppeDpo.setId(Long.valueOf(3));
        hwBaugruppeDpo.setRackId(dpo.getId());
        // @formatter:off
        return new Object[][] {
                { hwBaugruppeOlt, null, olt, today().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), false },
                { hwBaugruppeOlt, null, olt, tomorrow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), false },
                { hwBaugruppeOlt, null, olt, yesterday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), true },
                { hwBaugruppeOlt, null, olt, null,  null, true },

                { hwBaugruppeMdu, mdu, olt, today().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), false },
                { hwBaugruppeMdu, mdu, olt, tomorrow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), false },
                { hwBaugruppeMdu, mdu, olt, yesterday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), true },
                { hwBaugruppeMdu, mdu, olt, null,  null, true },

                { hwBaugruppeDpo, dpo, olt, today().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), false },
                { hwBaugruppeDpo, dpo, olt, tomorrow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), false },
                { hwBaugruppeDpo, dpo, olt, yesterday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),  today(), true },
                { hwBaugruppeDpo, dpo, olt, null,  null, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckOltVlanAktivAb")
    public void checkOltVlanAktivAb(HWBaugruppe hwBaugruppe, HWRack mdu, HWOlt olt, LocalDate newValidFrom,
            Date vlanAktivAb, boolean exceptionExpected) throws FindException, StoreException {

        Equipment equipment = new Equipment();
        equipment.setHwBaugruppenId(hwBaugruppe.getId());
        when(hwService.findBaugruppe(eq(hwBaugruppe.getId()))).thenReturn(hwBaugruppe);
        if (mdu != null) {
            when(hwService.findRackById(eq(mdu.getId()))).thenReturn(mdu);
        }
        olt.setVlanAktivAb(vlanAktivAb);
        when(hwService.findRackById(eq(olt.getId()))).thenReturn(olt);

        try {
            cut.checkOltVlanAktivAb(equipment, newValidFrom);
        }
        catch (FindException e) {
            if (!exceptionExpected) {
                e.printStackTrace();
                fail("Test sollte keine Exception werfen!");
            }
            return;
        }
        if (exceptionExpected) {
            fail("Test sollte eine Exception werfen!");
        }
    }

    public void calculateEqVlansDependingOnHwBgTypTunneling() throws Exception {
        final HWDslamBuilder hwDslamBuilder = new HWDslamBuilder().withRandomId();
        when(hwService.findBaugruppe(equipment.getHwBaugruppenId())).thenReturn(
                new HWBaugruppeBuilder()
                        .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder()
                                .withTunneling(HWBaugruppenTyp.Tunneling.CC))
                        .withRackBuilder(hwDslamBuilder)
                        .build()
        );
        when(hwService.findRackById(hwDslamBuilder.get().getId())).thenReturn(hwDslamBuilder.get());
        final List<EqVlan> result = cut.calculateEqVlans(ekpFrameContract, auftragId, productId, when);
        assertThat(result, Matchers.<EqVlan>empty());
    }

    @Test
    public void calculateEqVlansWhenRackIsDslam() throws Exception {
        hwBaugruppe.getHwBaugruppenTyp().setTunneling(HWBaugruppenTyp.Tunneling.VLAN);
        final HWDslam hwDslam = new HWDslamBuilder()
                .withRandomId()
                .withSVlan(1234)
                .build();
        when(hwService.findRackById(hwBaugruppe.getRackId())).thenReturn(hwDslam);

        final List<EqVlan> result = cut.calculateEqVlans(ekpFrameContract, auftragId, productId, when);
        for (final EqVlan eqVlan : result) {
            assertThat(eqVlan.getSvlanOlt(), equalTo(hwDslam.getSvlan()));
            assertThat(eqVlan.getSvlanMdu(), equalTo(99));
        }
    }

    @Test
    public void calculateEqVlansLandshut() throws Exception {
        final HWDslamBuilder hwDslamBuilder = new HWDslamBuilder().withRandomId().withHvtStandortBuilder(new HVTStandortBuilder().withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(false).withHvtGruppeBuilder(new HVTGruppeBuilder().setPersist(false)));
        hwDslamBuilder.getHvtStandortBuilder().getHvtGruppeBuilder().withNiederlassungId(Niederlassung.ID_LANDSHUT);
        when(hwService.findRackById(hwDslamBuilder.get().getId())).thenReturn(hwDslamBuilder.get());
        final List<EqVlan> result = cut.calculateEqVlans(ekpFrameContract, auftragId, productId, when);
        assertEquals(result.size(), 3);
        EqVlan vlan1 = result.get(0);

        assertEquals(vlan1.getCvlanTyp(), CvlanServiceTyp.HSI);
        assertEquals(vlan1.getCvlan(), CVLAN_HSI);
        assertEquals(vlan1.getSvlanEkp(), Integer.valueOf(409));
        assertEquals(vlan1.getSvlanOlt(), Integer.valueOf(109));
        assertEquals(vlan1.getSvlanMdu(), Integer.valueOf(149));

        EqVlan vlan2 = result.get(1);

        assertEquals(vlan2.getCvlanTyp(), CvlanServiceTyp.VOIP);
        assertEquals(vlan2.getCvlan(), CVLAN_VOIP);
        assertEquals(vlan2.getSvlanEkp(), Integer.valueOf(409));
        assertEquals(vlan2.getSvlanOlt(), Integer.valueOf(109));
        assertEquals(vlan2.getSvlanMdu(), Integer.valueOf(149));

        EqVlan vlan3 = result.get(2);

        assertEquals(vlan3.getCvlanTyp(), CvlanServiceTyp.MC);
        assertEquals(vlan3.getCvlan(), CVLAN_MC);
        assertEquals(vlan3.getSvlanEkp(), Integer.valueOf(400));
        assertEquals(vlan3.getSvlanOlt(), Integer.valueOf(100));
        assertEquals(vlan3.getSvlanMdu(), Integer.valueOf(149));
    }

    @Test
    public void svlanEkpDependsOnFrameContract() throws Exception {
        final HWDslamBuilder hwDslamBuilder = new HWDslamBuilder().withRandomId().withHvtStandortBuilder(new HVTStandortBuilder().withStandortTypRefId(
                HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(false).withHvtGruppeBuilder(new HVTGruppeBuilder().setPersist(false)));
        hwDslamBuilder.getHvtStandortBuilder().getHvtGruppeBuilder().withNiederlassungId(Niederlassung.ID_LANDSHUT);
        when(hwService.findRackById(hwDslamBuilder.get().getId())).thenReturn(hwDslamBuilder.get());

        final List<EqVlan> result1 = cut.calculateEqVlans(ekpFrameContract, auftragId, productId, when);
        EqVlan vlan1 = result1.get(0);
        assertEquals(vlan1.getSvlanEkp(), Integer.valueOf(409));

        ekpFrameContract.setSvlanFaktor(0);
        final List<EqVlan> result2 = cut.calculateEqVlans(ekpFrameContract, auftragId, productId, when);
        EqVlan vlan2 = result2.get(0);
        assertEquals(vlan2.getSvlanEkp(), Integer.valueOf(9));

    }

}
