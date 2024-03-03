/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2012 15:12:50
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Unit-Test fuer die Klasse FindNextHigherDSLAMProfile4DSL18000Command
 */
@Test(groups = { BaseTest.UNIT })
public class FindNextHigherDSLAMProfile4DSL18000CommandTest extends BaseTest {

    @Spy
    private FindNextHigherDSLAMProfile4DSL18000Command cut;
    @Mock
    private DSLAMService dslamService;
    @Mock
    private CCLeistungsService leistungService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private HWService hwService;

    private final Long ccAuftragId = RandomTools.createLong();
    private final Integer maxAttBitRateDown = 1000;
    private final Integer maxAttBitRateUp = 1000;

    private final HWBaugruppenTypBuilder hwBaugruppenTypBuilder = new HWBaugruppenTypBuilder().setPersist(false)
            .withRandomId();
    private final HWBaugruppeBuilder hwBaugruppenBuilder = new HWBaugruppeBuilder().setPersist(false).withRandomId()
            .withBaugruppenTypBuilder(hwBaugruppenTypBuilder);
    private final EquipmentBuilder eqInBuilder = new EquipmentBuilder().setPersist(false).withRandomId()
            .withBaugruppeBuilder(hwBaugruppenBuilder);
    private final Rangierung[] rangierungen = new Rangierung[] { new RangierungBuilder().setPersist(false)
            .withEqInBuilder(eqInBuilder).build() };
    private final Equipment equipment = eqInBuilder.build();
    private final HWBaugruppe hwBaugruppe = hwBaugruppenBuilder.build();

    @BeforeMethod
    void setUp() throws ServiceNotFoundException {
        cut = new FindNextHigherDSLAMProfile4DSL18000Command();
        cut.prepare(FindNextHigherDSLAMProfile4DSL18000Command.CCAUFTRAG_ID, ccAuftragId);
        cut.prepare(FindNextHigherDSLAMProfile4DSL18000Command.MAX_ATTAINABLE_BITRATE_DOWN, maxAttBitRateDown);
        cut.prepare(FindNextHigherDSLAMProfile4DSL18000Command.MAX_ATTAINABLE_BITRATE_UP, maxAttBitRateUp);
        cut.initPreparedValues();
        initMocks(this);

        doReturn(leistungService).when(cut).getCCService(CCLeistungsService.class);
        doReturn(dslamService).when(cut).getCCService(DSLAMService.class);
        doReturn(rangierungsService).when(cut).getCCService(RangierungsService.class);
        doReturn(hwService).when(cut).getCCService(HWService.class);
    }

    @DataProvider(name = "validateIsDSL18000Auftrag_validationFails_DP")
    public Object[][] validateIsDSL18000Auftrag_validationFails_DP() {
        return new Object[][] { { null }, { 17999L }, { 18001L } };
    }

    @Test(dataProvider = "validateIsDSL18000Auftrag_validationFails_DP", expectedExceptions = { FindException.class })
    public void validateIsDSL18000Auftrag_validationFails(Long longValue) throws Exception {
        TechLeistung tl = new TechLeistungBuilder().withLongValue(longValue).build();
        when(leistungService.findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM), eq(false)))
                .thenReturn(tl);
        cut.validateIsDSL18000Auftrag(ccAuftragId);
        verify(leistungService, times(1)).findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM),
                eq(false));
    }

    public void validateIsDSL18000Auftrag_Success() throws Exception {
        TechLeistung tl = new TechLeistungBuilder().withLongValue(18000L).build();
        when(leistungService.findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM), eq(false)))
                .thenReturn(tl);
        cut.validateIsDSL18000Auftrag(ccAuftragId);
        verify(leistungService, times(1)).findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM),
                eq(false));
    }

    @DataProvider(name = "validateIsDSL18000AuftragDP")
    public Object[][] validateIsDSL18000AuftragDP() {
        return new Object[][] { { new TechLeistungBuilder().setPersist(false).withLongValue(17999L).build(), true },
                { new TechLeistungBuilder().setPersist(false).withLongValue(18001L).build(), true }, { null, true },
                { new TechLeistungBuilder().setPersist(false).withLongValue(18000L).build(), false } };
    }

    @Test(dataProvider = "validateIsDSL18000AuftragDP")
    public void validateIsDSL18000Auftrag(TechLeistung techLs, boolean expectsFindExc) throws FindException,
            ServiceNotFoundException {
        when(leistungService.findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM), eq(false)))
                .thenReturn(techLs);
        boolean findExc = false;
        try {
            cut.validateIsDSL18000Auftrag(ccAuftragId);
        }
        catch (FindException e) {
            findExc = true;
        }
        verify(leistungService, times(1)).findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM),
                eq(false));
        assertEquals(findExc, expectsFindExc);
    }

    @DataProvider(name = "findUETVsDP")
    public Object[][] findUETVsDP() {
        // @formatter:off
        return new Object[][] {
                { "asdf",                      true,  Arrays.asList("asdf")},
                { DSLAMProfile.UETV_ADSL,      true,  Arrays.asList(DSLAMProfile.UETV_ADSL)},
                { DSLAMProfile.UETV_H04,       true,  Arrays.asList(DSLAMProfile.UETV_H04)},
                { DSLAMProfile.UETV_ADSL2PLUS, false, Arrays.asList(DSLAMProfile.UETV_ADSL2PLUS, DSLAMProfile.UETV_H13)},
                { DSLAMProfile.UETV_H13,       false, Arrays.asList(DSLAMProfile.UETV_ADSL2PLUS, DSLAMProfile.UETV_H13)}
        };
        // @formatter:on
    }

    @Test(dataProvider = "findUETVsDP")
    public void findUETVs(String uetv, boolean forceADSL1, List<String> uetvsExp) throws FindException,
            ServiceNotFoundException {
        DSLAMProfile dslamProfile = new DSLAMProfileBuilder().setPersist(false).withUetv(uetv)
                .withForceADSL1(forceADSL1).build();
        when(dslamService.findDSLAMProfile4Auftrag(eq(ccAuftragId), any(Date.class), eq(false))).thenReturn(
                dslamProfile);
        List<String> uetvsFound = cut.findUETVs();
        assertEquals(uetvsFound.size(), uetvsExp.size());
        assertTrue(uetvsFound.containsAll(uetvsExp));
        verify(dslamService, times(1)).findDSLAMProfile4Auftrag(eq(ccAuftragId), any(Date.class), eq(false));
    }

    @DataProvider(name = "filterForBestFittingProfileDP")
    public Object[][] filterForBestFittingProfileDP() {
        // @formatter:off
        return new Object[][] {
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(999, 999).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1001, 1001).build()),
                    Bandwidth.create(1001, 1001),
                    Bandwidth.create(1001, 1001),
                    Bandwidth.create(1001, 1001)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(999, 999).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1000, 1000).build()),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(1000, 1000).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1001, 1001).build()),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(1001, 1000).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1000, 1000).build()),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(1000, 1001).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1000, 1000).build()),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1001)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(999, 1000).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1000, 1000).build()),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(1000, 999).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1000, 1000).build()),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000),
                    Bandwidth.create(1000, 1000)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(8000, 1280).build(),
                                  new DSLAMProfileBuilder().withBandwidth(19000, 1300).build(),
                                  new DSLAMProfileBuilder().withBandwidth(22000, 1500).build()),
                    Bandwidth.create(18460, 1297),
                    Bandwidth.create(18460, 1297),
                    Bandwidth.create(19000, 1300)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(8000, 1280).build(),
                                  new DSLAMProfileBuilder().withBandwidth(1152, 128).build(),
                                  new DSLAMProfileBuilder().withBandwidth(19000, 1300).build(),
                                  new DSLAMProfileBuilder().withBandwidth(22000, 1500).build()),
                    Bandwidth.create(546, 100),
                    Bandwidth.create(546, 100),
                    Bandwidth.create(1152, 128)},
                {   Arrays.asList(new DSLAMProfileBuilder().withBandwidth(8000, 640).build(),
                                  new DSLAMProfileBuilder().withBandwidth(16000, 1280).build(),
                                  new DSLAMProfileBuilder().withBandwidth(16000, 640).build(),
                                  new DSLAMProfileBuilder().withBandwidth(22000, 1500).build()),
                    Bandwidth.create(20000, 1280),
                    Bandwidth.create(14582, 1300),
                    Bandwidth.create(16000, 1280)},
                { Collections.emptyList(),
                        Bandwidth.create(1000, 1000),
                        Bandwidth.create(1000, 1000),
                        Bandwidth.create(1000, 1000)},
        };
        // @formatter:on
    }

    @Test(dataProvider = "filterForBestFittingProfileDP")
    public void filterForBestFittingProfile(List<DSLAMProfile> profiles, Bandwidth upDownCurrentProfile,
            Bandwidth upDown, Bandwidth upDownExpected) throws FindException,
            ServiceNotFoundException {
        cut.actProfile = new DSLAMProfileBuilder().withBandwidth(upDownCurrentProfile)
                .setPersist(false).build();
        cut.maxAttainableBitrateDown = upDown.getDownstream();
        cut.maxAttainableBitrateUp = upDown.getUpstream();
        ReflectionTestUtils.setField(cut, "profiles", profiles);
        DSLAMProfile bestFittingProfile = cut.filterForBestFittingProfile();
        assertEquals(bestFittingProfile.getBandwidth(), upDownExpected);
    }

    public void findProfiles() throws ServiceNotFoundException, FindException, ServiceCommandException {
        final Long baugrTypId = RandomTools.createLong();
        final boolean fastpath = true;
        final List<String> uetvs = Collections.emptyList();
        final List<DSLAMProfile> dslamProfilesExp = Arrays.asList(new DSLAMProfileBuilder().build());
        ReflectionTestUtils.setField(cut, "actProfile", new DSLAMProfileBuilder().setPersist(false)
                .withBaugruppenTypId(baugrTypId).withFastpath(fastpath).build());
        ReflectionTestUtils.setField(cut, "uetvs", uetvs);
        when(dslamService.findDSLAMProfiles(eq(baugrTypId), eq(fastpath), eq(uetvs))).thenReturn(dslamProfilesExp);

        assertTrue(dslamProfilesExp == cut.findProfiles());

        verify(dslamService).findDSLAMProfiles(eq(baugrTypId), eq(fastpath), eq(uetvs));
    }

    public void findProfiles_baugruppenTypIdNotSet() throws FindException, ServiceCommandException, ServiceNotFoundException {
        final Long baugrTypId = null;
        final Long baugrTypIdExp = RandomTools.createLong();
        final boolean fastpath = true;
        final List<String> uetvs = Collections.emptyList();
        final List<DSLAMProfile> dslamProfilesExp = Arrays.asList(new DSLAMProfileBuilder().build());
        ReflectionTestUtils.setField(cut, "actProfile", new DSLAMProfileBuilder().setPersist(false)
                .withBaugruppenTypId(baugrTypId).withFastpath(fastpath).build());
        ReflectionTestUtils.setField(cut, "uetvs", uetvs);
        doReturn(baugrTypIdExp).when(cut).findBaugruppenTypId();
        when(dslamService.findDSLAMProfiles(eq(baugrTypIdExp), eq(fastpath), eq(uetvs))).thenReturn(dslamProfilesExp);
        cut.findProfiles();
        verify(cut, times(1)).findBaugruppenTypId();
        verify(dslamService, times(1)).findDSLAMProfiles(
                eq(baugrTypIdExp), eq(fastpath), eq(uetvs));
    }

    @DataProvider
    public Object[][] initPreparedValues_Exception_DP() {
        Integer randomInt = RandomTools.createInteger();
        return new Object[][] { { null, randomInt, randomInt }, { randomInt, null, randomInt },
                { randomInt, randomInt, null }, { null, null, randomInt }, { null, randomInt, null }, { null, null, null }, };
    }

    @Test(dataProvider = "initPreparedValues_Exception_DP", expectedExceptions = { RuntimeException.class })
    public void initPreparedValues_Excpetion(Long ccAuftragId, Integer bitrateUp, Integer bitrateDown)
            throws Exception {
        cut.prepare(FindNextHigherDSLAMProfile4DSL18000Command.CCAUFTRAG_ID, ccAuftragId);
        cut.prepare(FindNextHigherDSLAMProfile4DSL18000Command.MAX_ATTAINABLE_BITRATE_UP, bitrateUp);
        cut.prepare(FindNextHigherDSLAMProfile4DSL18000Command.MAX_ATTAINABLE_BITRATE_DOWN, bitrateDown);
        cut.execute();
    }

    public void execute() throws Exception {
        final List<String> uetvs = new ArrayList<String>(0);
        final List<DSLAMProfile> profiles = new ArrayList<DSLAMProfile>(0);
        final DSLAMProfile newProfile = new DSLAMProfile();

        doNothing().when(cut).initPreparedValues();
        doNothing().when(cut).validateIsDSL18000Auftrag(ccAuftragId);
        doReturn(uetvs).when(cut).findUETVs();
        doReturn(profiles).when(cut).findProfiles();
        doReturn(newProfile).when(cut).filterForBestFittingProfile();

        assertEquals(newProfile, cut.execute());
    }

    public void findBaugruppenTypId() throws FindException, ServiceCommandException, ServiceNotFoundException {
        when(rangierungsService.findRangierungenTx(eq(ccAuftragId), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(
                rangierungen);
        when(rangierungsService.findEquipment(eq(rangierungen[0].getEqInId()))).thenReturn(equipment);
        when(hwService.findBaugruppe(eq(equipment.getHwBaugruppenId()))).thenReturn(hwBaugruppe);
        assertEquals(hwBaugruppe.getHwBaugruppenTyp().getId(), cut.findBaugruppenTypId());
        verify(rangierungsService, times(1)).findRangierungenTx(eq(ccAuftragId), eq(Endstelle.ENDSTELLEN_TYP_B));
        verify(rangierungsService, times(1)).findEquipment(eq(rangierungen[0].getEqInId()));
        verify(hwService, times(1)).findBaugruppe(eq(equipment.getHwBaugruppenId()));
    }
}
