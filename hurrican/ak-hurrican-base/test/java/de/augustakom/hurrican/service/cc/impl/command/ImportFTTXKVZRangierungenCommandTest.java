/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2012 16:57:39
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKMessages;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.RangierungsAuftragBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.ImportFTTXKVZRangierungenCommand.Column;

/**
 * Unit Test, der das Command zum Import von FTTC KVZ Rangierungen testet
 */
@Test(groups = { BaseTest.UNIT })
public class ImportFTTXKVZRangierungenCommandTest extends BaseTest {

    @InjectMocks
    @Spy
    private ImportFTTXKVZRangierungenCommand cut;

    @Mock
    private HWService hwService;
    @Mock
    private HVTService hvtService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private PhysikService physikService;
    @Mock
    private RangierungAdminService rangierungAdminService;

    private static final int ANZAHL = 2;

    @BeforeMethod
    public void setUp() {
        cut = new ImportFTTXKVZRangierungenCommand();
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void testModifyRangierungenSetFreigegebenRangierungFehlt() throws Exception {
        final Pair<RangierungsAuftrag, Pair<List<Rangierung>, List<Equipment>>> preparedData = modifyRangierungenSetFreigegebenMock();
        final Equipment ersterPort = preparedData.getSecond().getSecond().get(0);
        when(rangierungsService.findConsecutivePorts(ersterPort, ANZAHL)).thenReturn(
                preparedData.getSecond().getSecond());
        when(rangierungsService.findRangierungen4Equipments(preparedData.getSecond().getSecond())).thenReturn(
                MapUtils.putAll(new HashMap<Long, Rangierung>(), new Map.Entry[] { new DefaultMapEntry(preparedData
                        .getSecond().getFirst().get(0).getEqInId(), preparedData.getSecond().getFirst().get(0)) })
        );
        cut.modifyRangierungenSetFreigegeben(Long.valueOf(1), Freigegeben.freigegeben, ANZAHL, preparedData.getFirst()
                .getId(), ersterPort);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyRangierungenSetFreigegebenRangierungsauftragIstNeu() throws Exception {
        final Pair<RangierungsAuftrag, Pair<List<Rangierung>, List<Equipment>>> preparedData = modifyRangierungenSetFreigegebenMock();
        final Equipment ersterPort = preparedData.getSecond().getSecond().get(0);
        when(rangierungsService.findConsecutivePorts(ersterPort, ANZAHL)).thenReturn(
                preparedData.getSecond().getSecond());
        when(rangierungsService.findRangierungen4Equipments(preparedData.getSecond().getSecond())).thenReturn(
                MapUtils.putAll(new HashMap<Long, Rangierung>(), new Map.Entry[] {
                        new DefaultMapEntry(preparedData.getSecond().getFirst().get(0).getEqInId(), preparedData
                                .getSecond().getFirst().get(0)),
                        new DefaultMapEntry(preparedData.getSecond().getFirst().get(1).getEqInId(), preparedData
                                .getSecond().getFirst().get(1)) })
        );
        cut.modifyRangierungenSetFreigegeben(Long.valueOf(1), Freigegeben.freigegeben, ANZAHL, preparedData.getFirst()
                .getId(), ersterPort);
        verify(rangierungAdminService).releaseRangierungen4RA(eq(preparedData.getFirst().getId()),
                eq(Float.valueOf(0.0f)), any(Long.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyRangierungenSetFreigegebenOhneRangierungsAuftrag() throws Exception {
        final Pair<RangierungsAuftrag, Pair<List<Rangierung>, List<Equipment>>> preparedData = modifyRangierungenSetFreigegebenMock();
        final Equipment ersterPort = preparedData.getSecond().getSecond().get(0);
        when(rangierungsService.findConsecutivePorts(ersterPort, ANZAHL)).thenReturn(
                preparedData.getSecond().getSecond());
        when(rangierungsService.findRangierungen4Equipments(preparedData.getSecond().getSecond())).thenReturn(
                MapUtils.putAll(new HashMap<Long, Rangierung>(), new Map.Entry[] {
                        new DefaultMapEntry(preparedData.getSecond().getFirst().get(0).getEqInId(), preparedData
                                .getSecond().getFirst().get(0)),
                        new DefaultMapEntry(preparedData.getSecond().getFirst().get(1).getEqInId(), preparedData
                                .getSecond().getFirst().get(1)) })
        );
        cut.modifyRangierungenSetFreigegeben(Long.valueOf(1), Freigegeben.freigegeben, ANZAHL, null, ersterPort);
        verify(rangierungAdminService, times(0)).releaseRangierungen4RA(eq(preparedData.getFirst().getId()),
                eq(Float.valueOf(0.0f)), any(Long.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyRangierungenSetFreigegebenFreigegebenIstNichtFreigegeben() throws Exception {
        final Pair<RangierungsAuftrag, Pair<List<Rangierung>, List<Equipment>>> preparedData = modifyRangierungenSetFreigegebenMock();
        final Equipment ersterPort = preparedData.getSecond().getSecond().get(0);
        when(rangierungsService.findConsecutivePorts(ersterPort, ANZAHL)).thenReturn(
                preparedData.getSecond().getSecond());
        when(rangierungsService.findRangierungen4Equipments(preparedData.getSecond().getSecond())).thenReturn(
                MapUtils.putAll(new HashMap<Long, Rangierung>(), new Map.Entry[] {
                        new DefaultMapEntry(preparedData.getSecond().getFirst().get(0).getEqInId(), preparedData
                                .getSecond().getFirst().get(0)),
                        new DefaultMapEntry(preparedData.getSecond().getFirst().get(1).getEqInId(), preparedData
                                .getSecond().getFirst().get(1)) })
        );
        cut.modifyRangierungenSetFreigegeben(Long.valueOf(1), Freigegeben.in_Aufbau, ANZAHL, preparedData.getFirst()
                .getId(), ersterPort);

        assertThat(preparedData.getSecond().getFirst().get(0).getFreigegeben(), equalTo(Freigegeben.in_Aufbau));
        assertThat(preparedData.getSecond().getFirst().get(1).getFreigegeben(), equalTo(Freigegeben.in_Aufbau));
    }

    private Pair<RangierungsAuftrag, Pair<List<Rangierung>, List<Equipment>>> modifyRangierungenSetFreigegebenMock()
            throws Exception {
        final EquipmentBuilder portBuilder1 = new EquipmentBuilder().withHwEQN("1-1-4-01").withId(Long.valueOf(1));
        final EquipmentBuilder portBuilder2 = new EquipmentBuilder().withHwEQN("1-1-4-02").withId(Long.valueOf(2));
        final RangierungsAuftrag rangierungsAuftrag = new RangierungsAuftragBuilder().withRandomId().withAnzahlPorts(2)
                .build();
        final Rangierung rangierung1 = new RangierungBuilder().withRandomId().withEqInBuilder(portBuilder1).build();
        final Rangierung rangierung2 = new RangierungBuilder().withRandomId().withEqInBuilder(portBuilder2).build();

        when(rangierungAdminService.findRA(rangierungsAuftrag.getId())).thenReturn(rangierungsAuftrag);

        return Pair.create(
                rangierungsAuftrag,
                Pair.create(Arrays.asList(rangierung1, rangierung2),
                        Arrays.asList(portBuilder1.get(), portBuilder2.get()))
        );
    }

    @Test
    public void testGetAnzahlSuccess() throws HurricanServiceCommandException {
        doReturn(1).when(cut).getColumnAsInt(any(Row.class), anyInt(), any(String.class));
        final int result = cut.getAnzahl(null);
        assertTrue(result == 1);
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void testGetAnzahlFail() throws HurricanServiceCommandException {
        doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsInt(any(Row.class), anyInt(),
                any(String.class));
        cut.getAnzahl(null);
    }

    @Test
    public void testFindHVTStandortSuccess() throws HurricanServiceCommandException, FindException {
        final HWRack hwRack = new HWMduBuilder().withHvtStandortBuilder(new HVTStandortBuilder().withRandomId()).build();
        when(hvtService.findHVTStandort(any(Long.class))).thenReturn(new HVTStandort());
        final HVTStandort hvtStandort = cut.findHVTStandort(hwRack);
        assertNotNull(hvtStandort);
    }

    @DataProvider
    public Object[][] dataProviderFindHVTStandortFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindHVTStandortFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindHVTStandortFail(final int whichToThrow)
            throws HurricanServiceCommandException, FindException {
        final HWRack hwRack = new HWMduBuilder().withHvtStandortBuilder(new HVTStandortBuilder().withRandomId()).build();
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        anyInt(), any(String.class));
                break;
            case 2:
                when(hvtService.findHVTStandort(hwRack.getHvtIdStandort())).thenThrow(new FindException("Error"));
                break;
            case 3:
                doReturn(new HWRack()).when(cut).findHWRack(any(Row.class));
                when(hvtService.findHVTStandort(hwRack.getHvtIdStandort())).thenReturn(null);
                break;
        }
        cut.findHVTStandort(hwRack);
    }

    @Test
    public void testFindHWRackSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        when(hwService.findRackByBezeichnung(any(String.class))).thenReturn(new HWRack());
        final HWRack hwRack = cut.findHWRack(null);
        assertNotNull(hwRack);
    }

    @DataProvider
    public Object[][] dataProviderFindHWRackFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindHWRackFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindHWRackFail(final int whichToThrow)
            throws HurricanServiceCommandException, FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        anyInt(), any(String.class));
                break;
            case 2:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(hwService.findRackByBezeichnung(any(String.class))).thenThrow(new FindException("Error"));
                break;
            case 3:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(hwService.findRackByBezeichnung(any(String.class))).thenReturn(null);
                break;
        }
        cut.findHWRack(null);
    }

    @Test
    public void testFindHWSubrackSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        when(hwService.findSubrackByHwRackAndModNumber(any(Long.class), any(String.class))).thenReturn(new HWSubrack());
        final HWSubrack hwSubrack = cut.findHWSubrack(null, new HWRack());
        assertNotNull(hwSubrack);
    }

    @DataProvider
    public Object[][] dataProviderFindHWSubrackFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindHWSubrackFail", expectedExceptions = { HurricanServiceCommandException.class,
            FindException.class })
    public void testFindHWSubrackFail(final int whichToThrow)
            throws HurricanServiceCommandException, FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        anyInt(), any(String.class));
                break;
            case 2:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(hwService.findSubrackByHwRackAndModNumber(any(Long.class), any(String.class))).thenThrow(
                        new FindException("Error"));
                break;
            case 3:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(hwService.findSubrackByHwRackAndModNumber(any(Long.class), any(String.class))).thenReturn(null);
                break;
        }
        cut.findHWSubrack(null, new HWRack());
    }

    @Test
    public void testFindHWBaugruppeSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        when(hwService.findBaugruppe(any(Long.class), any(Long.class), any(String.class)))
                .thenReturn(new HWBaugruppe());
        final HWBaugruppe hwBaugruppe = cut.findHWBaugruppe(null, new HWRack(), new HWSubrack());
        assertNotNull(hwBaugruppe);
    }

    @DataProvider
    public Object[][] dataProviderFindHWBaugruppeFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindHWBaugruppeFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindHWBaugruppeFail(final int whichToThrow)
            throws HurricanServiceCommandException, FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        anyInt(), any(String.class));
                break;
            case 2:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(hwService.findBaugruppe(any(Long.class), any(Long.class), any(String.class))).thenThrow(
                        new FindException("Error"));
                break;
            case 3:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(hwService.findBaugruppe(any(Long.class), any(Long.class), any(String.class))).thenReturn(null);
                break;
        }
        cut.findHWBaugruppe(null, new HWRack(), new HWSubrack());
    }

    @DataProvider
    public Object[][] dataProviderFindHWBaugruppeMainFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindHWBaugruppeMainFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindHWBaugruppeMainFail(final int whichFind) throws HurricanServiceCommandException, FindException {
        switch (whichFind) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findHWRack(any(Row.class));
                break;
            case 2:
                doReturn(new HWRack()).when(cut).findHWRack(any(Row.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findHWSubrack(any(Row.class),
                        any(HWRack.class));
                break;
            case 3:
                doReturn(new HWRack()).when(cut).findHWRack(any(Row.class));
                doReturn(new HWSubrack()).when(cut).findHWSubrack(any(Row.class), any(HWRack.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findHWBaugruppe(any(Row.class),
                        any(HWRack.class), any(HWSubrack.class));
                break;
        }
        cut.findHWBaugruppe(null, new HWRack(), new HWSubrack());
    }

    @Test
    public void testFindErsterPortSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("1-1-1-01").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        when(rangierungsService.findEquipmentByBaugruppe(any(Long.class), any(String.class), any(String.class)))
                .thenReturn(new Equipment());
        final Equipment ersterPort = cut.findErsterPort(new HWBaugruppe(), null);
        assertNotNull(ersterPort);
    }

    @DataProvider
    public Object[][] dataProviderFindErsterPortFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindErsterPortFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindErsterPortFail(final int whichToThrow) throws HurricanServiceCommandException,
            FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        anyInt(), any(String.class));
                break;
            case 2:
                doReturn("1-1-1-01").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(rangierungsService.findEquipmentByBaugruppe(any(Long.class), any(String.class), any(String.class)))
                        .thenThrow(
                                new FindException("Error"));
                break;
            case 3:
                doReturn("1-1-1-01").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(rangierungsService.findEquipmentByBaugruppe(any(Long.class), any(String.class), any(String.class)))
                        .thenReturn(null);
                break;
        }
        cut.findErsterPort(new HWBaugruppe(), null);
    }

    @Test
    public void testFindErsterStiftSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("verteiler").when(cut).getColumnAsString(any(Row.class),
                eq(ImportFTTXKVZRangierungenCommand.Column.VERTEILER_START.ordinal()), any(String.class));
        doReturn("leiste").when(cut).getColumnAsString(any(Row.class),
                eq(ImportFTTXKVZRangierungenCommand.Column.LEISTE_START.ordinal()), any(String.class));
        doReturn("stift").when(cut).getColumnAsString(any(Row.class),
                eq(ImportFTTXKVZRangierungenCommand.Column.STIFT_START.ordinal()), any(String.class));
        //@formatter:off
        when(rangierungsService.findEQByVerteilerLeisteStift(any(Long.class), any(String.class), any(String.class),
                any(String.class))).thenReturn(new Equipment());
        //@formatter:on
        final Equipment ersterStift = cut.findErsterStift(Long.valueOf(1), null);
        assertNotNull(ersterStift);
    }

    @DataProvider
    public Object[][] dataProviderFindErsterStiftFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
                { 4 },
                { 5 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindErsterStiftFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindErsterStiftFail(final int whichToThrow)
            throws HurricanServiceCommandException,
            FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.VERTEILER_START.ordinal()), any(String.class));
                break;
            case 2:
                doReturn("verteiler").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.VERTEILER_START.ordinal()), any(String.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.LEISTE_START.ordinal()), any(String.class));
                break;
            case 3:
                doReturn("verteiler").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.VERTEILER_START.ordinal()), any(String.class));
                doReturn("leiste").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.LEISTE_START.ordinal()), any(String.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.STIFT_START.ordinal()), any(String.class));
                break;
            case 4:
                doReturn("verteiler").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.VERTEILER_START.ordinal()), any(String.class));
                doReturn("leiste").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.LEISTE_START.ordinal()), any(String.class));
                doReturn("stift").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.STIFT_START.ordinal()), any(String.class));
                //@formatter:off
                when(rangierungsService.findEQByVerteilerLeisteStift(any(Long.class), any(String.class), any(String.class),
                        any(String.class))).thenThrow(new FindException("Error"));
                //@formatter:on
                break;
            case 5:
                doReturn("verteiler").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.VERTEILER_START.ordinal()), any(String.class));
                doReturn("leiste").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.LEISTE_START.ordinal()), any(String.class));
                doReturn("stift").when(cut).getColumnAsString(any(Row.class),
                        eq(ImportFTTXKVZRangierungenCommand.Column.STIFT_START.ordinal()), any(String.class));
                //@formatter:off
                when(rangierungsService.findEQByVerteilerLeisteStift(any(Long.class), any(String.class), any(String.class),
                        any(String.class))).thenReturn(null);
                //@formatter:on
                break;
        }
        cut.findErsterStift(Long.valueOf(1), null);
    }

    @Test
    public void testCheckPortSuccess() throws HurricanServiceCommandException {
        final Equipment port = new Equipment();
        port.setStatus(EqStatus.frei);
        cut.checkPort(port);
    }

    @DataProvider
    public Object[][] dataProviderCheckPortFail() {
        //@formatter:off
        return new Object[][] {
                { EqStatus.rang },
                { EqStatus.abgebaut },
                { EqStatus.defekt },
                { EqStatus.locked },
                { EqStatus.res },
                { EqStatus.vorb },
                { EqStatus.WEPLA },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderCheckPortFail", expectedExceptions = { HurricanServiceCommandException.class })
    public void testCheckPortFail(final EqStatus status) throws HurricanServiceCommandException {
        final Equipment port = new Equipment();
        port.setStatus(status);
        cut.checkPort(port);
    }

    @Test
    public void testCheckStiftSuccess() throws HurricanServiceCommandException {
        final Equipment port = new Equipment();
        port.setStatus(EqStatus.frei);
        cut.checkStift(port);
    }

    @DataProvider
    public Object[][] dataProviderCheckStiftFail() {
        //@formatter:off
        return new Object[][] {
                { EqStatus.rang },
                { EqStatus.abgebaut },
                { EqStatus.defekt },
                { EqStatus.locked },
                { EqStatus.res },
                { EqStatus.vorb },
                { EqStatus.WEPLA },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderCheckPortFail", expectedExceptions = { HurricanServiceCommandException.class })
    public void testCheckStiftFail(final EqStatus status) throws HurricanServiceCommandException {
        final Equipment port = new Equipment();
        port.setStatus(status);
        cut.checkStift(port);
    }

    @Test
    public void testFindEquipmentsSuccess() throws HurricanServiceCommandException, FindException {
        final Equipment port = new Equipment();
        port.setStatus(EqStatus.frei);
        final Equipment stift = new Equipment();
        stift.setStatus(EqStatus.frei);
        // doReturn(new HWBaugruppe()).when(cut).findHWBaugruppe(any(Row.class), any(HWRack.class));
        // doReturn(port).when(cut).findErsterPort(any(HWBaugruppe.class), any(Row.class));
        doReturn(stift).when(cut).findErsterStift(any(Long.class), any(Row.class));
        when(rangierungsService.findConsecutivePorts(any(Equipment.class), anyInt())).thenReturn(Arrays.asList(port));
        when(rangierungsService.findConsecutiveUEVTStifte(any(Equipment.class), anyInt())).thenReturn(
                Arrays.asList(stift));
        final Pair<List<Equipment>, List<Equipment>> result =
                cut.findAndCheckEquipments(port, null, new HVTStandort(), new HWRack(), 1);
        assertNotNull(result);
    }

    @DataProvider
    public Object[][] dataProviderFindEquipmentsFail() {
        final Equipment ersterPort = new Equipment();
        ersterPort.setStatus(EqStatus.frei);
        final Equipment ersterPortFail = new Equipment();
        ersterPortFail.setStatus(EqStatus.rang);
        final Equipment ersterStiftFail = new Equipment();
        ersterStiftFail.setStatus(EqStatus.rang);
        //@formatter:off
        return new Object[][] {
                { 1, null, null },
                { 2, null, null },
                { 3, null, null },
                { 4, null, null },
                { 5, ersterPort, null },
                { 6, ersterPortFail, null },
                { 6, ersterPort, ersterStiftFail },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindEquipmentsFail", expectedExceptions = {
            HurricanServiceCommandException.class, FindException.class })
    public void testFindEquipmentsFail(final int whichToThrow, final Equipment ersterPort, final Equipment ersterStift)
            throws HurricanServiceCommandException, FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findHWBaugruppe(any(Row.class),
                        any(HWRack.class));
                break;
            case 2:
                // doReturn(new HWBaugruppe()).when(cut).findHWBaugruppe(any(Row.class), any(HWRack.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findErsterPort(any(HWBaugruppe.class),
                        any(Row.class));
                break;
            case 3:
                // doReturn(new HWBaugruppe()).when(cut).findHWBaugruppe(any(Row.class), any(HWRack.class));
                doReturn(ersterPort).when(cut).findErsterPort(any(HWBaugruppe.class), any(Row.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findErsterStift(any(Long.class),
                        any(Row.class));
                break;
            case 4:
                // doReturn(new HWBaugruppe()).when(cut).findHWBaugruppe(any(Row.class), any(HWRack.class));
                // doReturn(ersterPort).when(cut).findErsterPort(any(HWBaugruppe.class), any(Row.class));
                doReturn(ersterPort).when(cut).findErsterStift(any(Long.class), any(Row.class));
                when(rangierungsService.findConsecutivePorts(any(Equipment.class), anyInt())).thenThrow(
                        new FindException("Error"));
                break;
            case 5:
                // doReturn(new HWBaugruppe()).when(cut).findHWBaugruppe(any(Row.class), any(HWRack.class));
                // doReturn(ersterPort).when(cut).findErsterPort(any(HWBaugruppe.class), any(Row.class));
                doReturn(ersterPort).when(cut).findErsterStift(any(Long.class), any(Row.class));
                when(rangierungsService.findConsecutivePorts(any(Equipment.class), anyInt())).thenReturn(
                        Arrays.asList(ersterPort));
                when(rangierungsService.findConsecutiveUEVTStifte(any(Equipment.class), anyInt())).thenThrow(
                        new FindException("Error"));
                break;
            case 6:
                // doReturn(new HWBaugruppe()).when(cut).findHWBaugruppe(any(Row.class), any(HWRack.class));
                // doReturn(ersterPort).when(cut).findErsterPort(any(HWBaugruppe.class), any(Row.class));
                doReturn(ersterPort).when(cut).findErsterStift(any(Long.class), any(Row.class));
                when(rangierungsService.findConsecutivePorts(any(Equipment.class), anyInt())).thenReturn(
                        Arrays.asList(ersterPort));
                when(rangierungsService.findConsecutiveUEVTStifte(any(Equipment.class), anyInt())).thenReturn(
                        Arrays.asList(ersterStift));
                break;
        }
        cut.findAndCheckEquipments(ersterPort, null, new HVTStandort(), new HWRack(), 1);
    }

    @Test
    public void testFindPhysikTypSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("physiktypName").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        when(physikService.findPhysikTypByName(any(String.class))).thenReturn(new PhysikTyp());
        final PhysikTyp physikTyp = cut.findPhysikTyp(null);
        assertNotNull(physikTyp);
    }

    @DataProvider
    public Object[][] dataProviderFindPhysikTypFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
                { 3 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderFindPhysikTypFail", expectedExceptions = { HurricanServiceCommandException.class,
            FindException.class })
    public void testFindPhysikTypFail(final int whichToThrow) throws HurricanServiceCommandException,
            FindException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getColumnAsString(any(Row.class),
                        anyInt(), any(String.class));
                break;
            case 2:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(physikService.findPhysikTypByName(any(String.class))).thenThrow(new FindException("Error"));
                break;
            case 3:
                doReturn("bezeichner").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
                when(physikService.findPhysikTypByName(any(String.class))).thenReturn(null);
                break;
        }
        cut.findPhysikTyp(null);
    }

    @Test
    public void testCreateRangierungsAuftragSuccess() throws HurricanServiceCommandException, FindException,
            StoreException {
        doReturn(new PhysikTyp()).when(cut).findPhysikTyp(any(Row.class));
        final RangierungsAuftrag rangierungsAuftrag = cut.createRangierungsAuftrag(null, null, new HVTStandort(), 1);
        assertNotNull(rangierungsAuftrag);
        verify(rangierungAdminService, times(1)).saveRangierungsAuftrag(any(RangierungsAuftrag.class));
    }

    @DataProvider
    public Object[][] dataProviderCreateRangierungsAuftragFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
        };
        //@formatter:on
    }

    @Test(dataProvider = "dataProviderCreateRangierungsAuftragFail", expectedExceptions = {
            HurricanServiceCommandException.class, StoreException.class })
    public void testCreateRangierungsAuftragFail(final int whichToThrow) throws HurricanServiceCommandException,
            FindException, StoreException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findPhysikTyp(any(Row.class));
                break;
            case 2:
                doReturn(new PhysikTyp()).when(cut).findPhysikTyp(any(Row.class));
                doThrow(new StoreException("Error")).when(rangierungAdminService).saveRangierungsAuftrag(
                        any(RangierungsAuftrag.class));
                break;
        }
        cut.createRangierungsAuftrag(null, null, new HVTStandort(), 1);
    }


    @Test
    public void testFindUETVSuccess() throws HurricanServiceCommandException, FindException {
        doReturn("h18").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        final Uebertragungsverfahren uetv = cut.findUETV(null);
        assertNotNull(uetv);
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void testFindUETVFail() throws HurricanServiceCommandException {
        doReturn("gibts nicht").when(cut).getColumnAsString(any(Row.class), anyInt(), any(String.class));
        cut.findUETV(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateRangierungenSuccess() throws HurricanServiceCommandException, FindException,
            StoreException {
        final Equipment port = new Equipment();
        port.setId(Long.valueOf(1));
        final Equipment stift = new Equipment();
        stift.setId(Long.valueOf(1));

        final RangierungsAuftrag rangierungsAuftrag = new RangierungsAuftragBuilder().withRandomId().build();

        doReturn(Uebertragungsverfahren.H18).when(cut).findUETV(any(Row.class));

        cut.createRangierungen(null,
                new Pair<List<Equipment>, List<Equipment>>(Arrays.asList(port), Arrays.asList(stift)),
                rangierungsAuftrag, Long.valueOf(1));
        verify(rangierungAdminService, times(1)).createRangierungen(eq(rangierungsAuftrag.getId()), isNull(List.class),
                any(List.class), isNull(List.class), any(List.class), eq(Equipment.RANG_SS_2H),
                eq(Uebertragungsverfahren.H18),
                isNull(String.class), any(Long.class));
    }

    @DataProvider
    public Object[][] dataProviderCreateRangierungenFail() {
        //@formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
        };
        //@formatter:on
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "dataProviderCreateRangierungenFail", expectedExceptions = {
            HurricanServiceCommandException.class, StoreException.class })
    public void testCreateRangierungenFail(final int whichToThrow) throws HurricanServiceCommandException,
            FindException, StoreException {
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findUETV(any(Row.class));
                break;
            case 2:
                doReturn(Uebertragungsverfahren.H18).when(cut).findUETV(any(Row.class));
                //@formatter:off
                when(rangierungAdminService.createRangierungen(any(Long.class), any(List.class), any(List.class),
                            any(List.class), any(List.class), any(String.class), any(Uebertragungsverfahren.class),
                            any(String.class), any(Long.class))).thenThrow(new StoreException("Error"));
                //@formatter:on
                break;
        }
        final Equipment port = new Equipment();
        port.setId(Long.valueOf(1));
        final Equipment stift = new Equipment();
        stift.setId(Long.valueOf(1));
        cut.createRangierungen(null,
                new Pair<List<Equipment>, List<Equipment>>(Arrays.asList(port), Arrays.asList(stift)),
                new RangierungsAuftrag(), Long.valueOf(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteSuccess() throws Exception {
        final EquipmentBuilder portBuilder = new EquipmentBuilder().withId(Long.valueOf(1));
        final Equipment port = portBuilder.build();
        final Equipment stift = new Equipment();
        stift.setId(Long.valueOf(1));
        final Row cell = createRow();
        final HVTStandort hvtStandort = new HVTStandortBuilder().withRandomId().build();
        final int anzahl = 1;
        //@formatter:off
        final RangierungsAuftrag rangierungsAuftrag = new RangierungsAuftragBuilder()
                .withRandomId()
                .withAnzahlPorts(anzahl)
                .build();
        //@formatter:on
        final HWRack hwRack = new HWMduBuilder().withRandomId().build();
        final HWBaugruppe hwBaugruppe = new HWBaugruppeBuilder().withRandomId().build();
        //@formatter:off
        final Rangierung rangierungErsterPort = new RangierungBuilder()
                .withRandomId()
                .withEqInBuilder(portBuilder)
                .withRangierungsAuftragId(rangierungsAuftrag.getId())
                .build();
        //@formatter:on
        cut.prepare(ImportFTTXKVZRangierungenCommand.PARAM_IMPORT_ROW, cell);
        cut.prepare(ImportFTTXKVZRangierungenCommand.PARAM_SESSION_ID, Long.valueOf(1));
        cut.prepare(ImportFTTXKVZRangierungenCommand.PARAM_AK_USER, null);

        doReturn(anzahl).when(cut).getAnzahl(any(Row.class));
        doReturn(hvtStandort).when(cut).findHVTStandort(any(HWRack.class));
        doReturn(new Pair<List<Equipment>, List<Equipment>>(Arrays.asList(port), Arrays.asList(stift))).when(cut)
                .findAndCheckEquipments(any(Equipment.class), any(Row.class), any(HVTStandort.class),
                        any(HWRack.class), anyInt());
        doReturn(rangierungsAuftrag).when(cut).createRangierungsAuftrag(any(Row.class), any(AKUser.class),
                any(HVTStandort.class), anyInt());
        doReturn(new AKMessages()).when(cut).createRangierungen(any(Row.class), any(Pair.class),
                any(RangierungsAuftrag.class), any(Long.class));
        doReturn(hwRack).when(cut).findHWRack(any(Row.class));
        doReturn(Freigegeben.freigegeben.toString()).when(cut).getColumnAsString(any(Row.class),
                eq(Column.FREIGABESTATUS.ordinal()), any(String.class));
        doReturn(hwBaugruppe).when(cut).findHWBaugruppe(cell, hwRack);
        doReturn(new Equipment()).when(cut).findErsterPort(hwBaugruppe, cell);

        when(rangierungAdminService.releaseRangierungen4RA(any(Long.class), any(Float.class), any(Long.class)))
                .thenReturn(new RangierungsAuftrag());
        when(rangierungsService.findRangierung4Equipment(any(Long.class), eq(true)))
                .thenReturn(rangierungErsterPort);
        when(rangierungAdminService.findRA(rangierungsAuftrag.getId())).thenReturn(rangierungsAuftrag);
        when(rangierungsService.findRangierungen4Equipments(Arrays.asList(port))).thenReturn(
                MapUtils.putAll(new HashMap<Long, Rangierung>(), new Map.Entry[] { new DefaultMapEntry(
                        rangierungErsterPort.getEqInId(), rangierungErsterPort) })
        );
        when(rangierungsService.findConsecutivePorts(any(Equipment.class), eq(anzahl))).thenReturn(Arrays.asList(port));

        final SingleRowResult importResult = (SingleRowResult) cut.execute();

        assertNotNull(importResult);
        assertTrue(importResult.getWarningCount() == 0);

        verify(cut).findHWRack(cell);
        verify(cut).modifyRangierungenSetFreigegeben(any(Long.class), eq(Freigegeben.freigegeben), eq(anzahl),
                eq(rangierungsAuftrag.getId()), any(Equipment.class));
        verify(rangierungAdminService).releaseRangierungen4RA(eq(rangierungsAuftrag.getId()),
                eq(Float.valueOf(0.0f)),
                any(Long.class));
    }

    private Row createRow() {
        final XSSFWorkbook wb = new XSSFWorkbook();
        final XSSFSheet sheet = wb.createSheet();
        final XSSFRow row = sheet.createRow(0);
        return row;
    }

    @DataProvider
    public Object[][] dataProviderExceuteFail() {
        final Row row = createRow();
        final Long sessionId = Long.valueOf(1);
        final Equipment port = new Equipment();
        port.setId(Long.valueOf(1));
        final Equipment stift = new Equipment();
        stift.setId(Long.valueOf(1));
        final Pair<List<Equipment>, List<Equipment>> equipments = new Pair<List<Equipment>, List<Equipment>>(
                Arrays.asList(port), Arrays.asList(stift));
        //@formatter:off
        return new Object[][] {
                { 0, null, null, null },
                { 0, row,  null, null },
                { 1, row,  sessionId, null },
                { 2, row,  sessionId, null },
                { 3, row,  sessionId, null },
                { 4, row,  sessionId, equipments },
                { 5, row,  sessionId, equipments },
                { 6, row,  sessionId, equipments },
        };
        //@formatter:on
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "dataProviderExceuteFail", expectedExceptions = { FindException.class, StoreException.class,
            HurricanServiceCommandException.class })
    public void testExecuteFail(final int whichToThrow, final Row row, final Long sessionId,
            final Pair<List<Equipment>, List<Equipment>> equipments) throws Exception {
        cut.prepare(ImportFTTXKVZRangierungenCommand.PARAM_IMPORT_ROW, row);
        cut.prepare(ImportFTTXKVZRangierungenCommand.PARAM_SESSION_ID, sessionId);
        cut.prepare(ImportFTTXKVZRangierungenCommand.PARAM_AK_USER, null);
        switch (whichToThrow) {
            case 1:
                doThrow(new HurricanServiceCommandException("Error")).when(cut).getAnzahl(any(Row.class));
                break;
            case 2:
                doReturn(1).when(cut).getAnzahl(any(Row.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findHVTStandort(any(HWRack.class));
                break;
            case 3:
                doReturn(1).when(cut).getAnzahl(any(Row.class));
                doReturn(new HVTStandort()).when(cut).findHVTStandort(any(HWRack.class));
                doThrow(new HurricanServiceCommandException("Error")).when(cut).findAndCheckEquipments(
                        any(Equipment.class), any(Row.class), any(HVTStandort.class), any(HWRack.class), anyInt());
                break;
            case 4:
                doReturn(1).when(cut).getAnzahl(any(Row.class));
                doReturn(new HVTStandort()).when(cut).findHVTStandort(any(HWRack.class));
                doReturn(equipments).when(cut).findAndCheckEquipments(any(Equipment.class), any(Row.class),
                        any(HVTStandort.class), any(HWRack.class), anyInt());
                doReturn(equipments).when(cut).findAndCheckEquipments(any(Equipment.class), any(Row.class),
                        any(HVTStandort.class), any(HWRack.class), anyInt());
                doThrow(new HurricanServiceCommandException("Error")).when(cut).createRangierungsAuftrag(
                        any(Row.class), any(AKUser.class), any(HVTStandort.class), anyInt());
                break;
            case 5:
                doReturn(1).when(cut).getAnzahl(any(Row.class));
                doReturn(new HVTStandort()).when(cut).findHVTStandort(any(HWRack.class));
                doReturn(equipments).when(cut).findAndCheckEquipments(any(Equipment.class), any(Row.class),
                        any(HVTStandort.class), any(HWRack.class), anyInt());
                doReturn(equipments).when(cut).findAndCheckEquipments(any(Equipment.class), any(Row.class),
                        any(HVTStandort.class), any(HWRack.class), anyInt());
                doReturn(new RangierungsAuftrag()).when(cut).createRangierungsAuftrag(any(Row.class),
                        any(AKUser.class), any(HVTStandort.class), anyInt());
                doThrow(new HurricanServiceCommandException("Error")).when(cut).createRangierungen(any(Row.class),
                        any(Pair.class), any(RangierungsAuftrag.class), any(Long.class));
                break;
            case 6:
                doReturn(1).when(cut).getAnzahl(any(Row.class));
                doReturn(new HVTStandort()).when(cut).findHVTStandort(any(HWRack.class));
                doReturn(equipments).when(cut).findAndCheckEquipments(any(Equipment.class), any(Row.class),
                        any(HVTStandort.class), any(HWRack.class), anyInt());
                doReturn(equipments).when(cut).findAndCheckEquipments(any(Equipment.class), any(Row.class),
                        any(HVTStandort.class), any(HWRack.class), anyInt());
                doReturn(new RangierungsAuftrag()).when(cut).createRangierungsAuftrag(any(Row.class),
                        any(AKUser.class), any(HVTStandort.class), anyInt());
                doReturn(new AKMessages()).when(cut).createRangierungen(any(Row.class), any(Pair.class),
                        any(RangierungsAuftrag.class), any(Long.class));
                when(rangierungAdminService.releaseRangierungen4RA(any(Long.class), any(Float.class), any(Long.class)))
                        .thenThrow(new StoreException("Error"));
                break;
        }
        cut.execute();
    }
}


