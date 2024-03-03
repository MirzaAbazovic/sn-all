/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2012 08:30:19
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static de.augustakom.hurrican.service.cc.impl.command.ImportFTTXBaugruppenCommand.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class ImportFTTXBaugruppenCommandTest extends BaseTest {

    @InjectMocks
    @Spy
    private ImportFTTXBaugruppenCommand cut;

    @Mock
    private HWService hwService;
    @Mock
    private RangierungsService rangierungService;

    private static final String DEFAULT_MOD_NUMBER = "R1/S1-LT01";

    @BeforeMethod
    public void setUp() {
        cut = new ImportFTTXBaugruppenCommand();
        initMocks(this);
    }

    @Test(dataProvider = "executeCreateBaugruppeSuccessDataProvider", expectedExceptions = { HurricanServiceCommandException.class })
    public void executeFailsBecauseDataAlreadyExists(Row row) throws Exception {
        cut.prepare(ImportFTTXBaugruppenCommand.PARAM_IMPORT_ROW, row);
        HWRack hwRack = createDefaultHwRack();
        HWSubrack hwSubrack = createDefaultHwSubrack();
        HWBaugruppenTyp hwBaugruppenTyp = createDefaultHwBaugruppenTyp();
        when(hwService.findRackByBezeichnung(any(String.class))).thenReturn(hwRack);
        when(hwService.findSubrackByHwRackAndModNumber(any(Long.class), any(String.class))).thenReturn(hwSubrack);
        when(hwService.findBaugruppenTypByName(any(String.class))).thenReturn(hwBaugruppenTyp);
        doNothing().when(cut).createPorts(any(Row.class), any(HWRack.class), any(HWBaugruppe.class));

        when(hwService.findBaugruppe(any(Long.class), any(Long.class), any(String.class)))
                .thenReturn(new HWBaugruppe());

        cut.execute();
    }

    @DataProvider(name = "executeFailsBecauseOfMissingInputDataDataProvider")
    public Object[][] executeFailsBecauseOfMissingInputDataDataProvider() throws Exception {
        Sheet sheet = sheetTestData();
        return new Object[][] {
                { sheet.getRow(4) },
                { sheet.getRow(5) },
                { sheet.getRow(6) },
                { sheet.getRow(7) },
                { sheet.getRow(8) },
                { sheet.getRow(9) },
                { sheet.getRow(10) },
        };
    }

    @Test(dataProvider = "executeFailsBecauseOfMissingInputDataDataProvider", expectedExceptions = { HurricanServiceCommandException.class })
    public void executeFailsBecauseOfMissingInputData(Row row) throws Exception {
        cut.prepare(ImportFTTXBaugruppenCommand.PARAM_IMPORT_ROW, row);
        HWRack hwRack = createDefaultHwRack();
        HWSubrack hwSubrack = createDefaultHwSubrack();
        HWBaugruppenTyp hwBaugruppenTyp = createDefaultHwBaugruppenTyp();
        when(hwService.findRackByBezeichnung(isNull(String.class))).thenReturn(null);
        when(hwService.findRackByBezeichnung(isNotNull(String.class))).thenReturn(hwRack);
        when(hwService.findSubrackByHwRackAndModNumber(isNotNull(Long.class), isNull(String.class)))
                .thenReturn(null);
        when(hwService.findSubrackByHwRackAndModNumber(isNotNull(Long.class), isNotNull(String.class))).thenReturn(
                hwSubrack);
        when(hwService.findBaugruppenTypByName(isNull(String.class))).thenReturn(null);
        when(hwService.findBaugruppenTypByName(isNotNull(String.class))).thenReturn(hwBaugruppenTyp);
        when(hwService.saveHWBaugruppe(any(HWBaugruppe.class))).thenReturn(
                new HWBaugruppeBuilder().withModNumber(DEFAULT_MOD_NUMBER)
                        .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder()).build()
        );
        cut.execute();
    }

    @DataProvider(name = "executeCreateBaugruppeSuccessDataProvider")
    public Object[][] executeCreateBaugruppeSuccessDataProvider() throws Exception {
        Sheet sheet = sheetTestData();
        return new Object[][] {
                { sheet.getRow(2) },
                { sheet.getRow(3) },
        };
    }

    private Sheet sheetTestData() throws Exception {
        File testDataFile = new File(Thread.currentThread().getContextClassLoader().getResource("BaugruppenImportTestData.xls").getFile());
        byte[] testDataAsBytes = FileTools.convertToByteArray(testDataFile);
        return XlsPoiTool.loadExcelSheet(testDataAsBytes);
    }

    @Test(dataProvider = "executeCreateBaugruppeSuccessDataProvider")
    public void executeCreateBaugruppeSuccess(Row row) throws Exception {
        cut.prepare(ImportFTTXBaugruppenCommand.PARAM_IMPORT_ROW, row);
        final String columnRack = XlsPoiTool.getContentAsString(row, COLUMN_RACK);
        final String columnSubrackModNumber = XlsPoiTool.getContentAsString(row, COLUMN_SUBRACK_MOD_NR);
        final String columnTyp = XlsPoiTool.getContentAsString(row, COLUMN_TYP);
        final String columnModNr = XlsPoiTool.getContentAsString(row, COLUMN_MODUL_NR);
        HWRack hwRack = createDefaultHwRack();
        HWSubrack hwSubrack = createDefaultHwSubrack();
        HWBaugruppenTyp hwBaugruppenTyp = createDefaultHwBaugruppenTyp();
        when(hwService.findRackByBezeichnung(eq(columnRack))).thenReturn(hwRack);
        when(hwService.findSubrackByHwRackAndModNumber(eq(hwRack.getId()), eq(columnSubrackModNumber))).thenReturn(
                hwSubrack);
        when(hwService.findBaugruppenTypByName(eq(columnTyp))).thenReturn(hwBaugruppenTyp);
        doNothing().when(cut).createPorts(eq(row), eq(hwRack), any(HWBaugruppe.class));

        cut.execute();

        verify(hwService).findRackByBezeichnung(eq(columnRack));
        verify(hwService).findSubrackByHwRackAndModNumber(eq(hwRack.getId()), eq(columnSubrackModNumber));
        verify(hwService).findBaugruppenTypByName(eq(columnTyp));

        ArgumentCaptor<HWBaugruppe> hwBaugruppeCaptor = ArgumentCaptor.forClass(HWBaugruppe.class);
        verify(hwService).saveHWBaugruppe(hwBaugruppeCaptor.capture());
        HWBaugruppe savedBaugruppe = hwBaugruppeCaptor.getValue();
        assertTrue(savedBaugruppe.getEingebaut());
        assertNull(savedBaugruppe.getBemerkung());
        assertNull(savedBaugruppe.getInventarNr());
        assertEquals(savedBaugruppe.getHwBaugruppenTyp(), hwBaugruppenTyp);
        assertEquals(savedBaugruppe.getModNumber(), columnModNr);
    }

    private HWRack createDefaultHwRack() {
        return new HWDslamBuilder().withRandomId().build();
    }

    private HWSubrack createDefaultHwSubrack() {
        return new HWSubrackBuilder().withRandomId().build();
    }

    private HWBaugruppenTyp createDefaultHwBaugruppenTyp() {
        return new HWBaugruppenTypBuilder().withRandomId().build();
    }

    @Test(dataProvider = "executeCreateBaugruppeSuccessDataProvider")
    public void executeCreatePortsSuccessful(Row row) throws Exception {
        final String rangVerteiler = XlsPoiTool.getContentAsString(row, COLUMN_RANG_VERTEILER_OUT);
        final String rangBucht = XlsPoiTool.getContentAsString(row, COLUMN_RANG_BUCHT_OUT);
        final String rackGeraeteBezeichnung = XlsPoiTool.getContentAsString(row, COLUMN_RACK);
        final int rangStift1StartValue = XlsPoiTool.getContentAsInt(row, COLUMN_RANG_STIFT_1_OUT);

        cut.prepare(ImportFTTXBaugruppenCommand.PARAM_IMPORT_ROW, row);
        HWRack hwRack = createDefaultHwRack();
        HWBaugruppe hwBaugruppe = createHwBaugruppe();
        final int expectedNumberOfEquipmentsSaved = hwBaugruppe.getHwBaugruppenTyp().getPortCount();
        final String expectedHwEqnBase = cut.buildHwEqnBase(hwBaugruppe);

        when(hwService.findRackByBezeichnung(eq(rackGeraeteBezeichnung))).thenReturn(hwRack);
        doReturn(hwBaugruppe).when(cut).createBaugruppe(eq(row), eq(hwRack));

        cut.execute();

        ArgumentCaptor<Equipment> equipmentCaptor = ArgumentCaptor.forClass(Equipment.class);
        verify(rangierungService, times(expectedNumberOfEquipmentsSaved)).saveEquipment(equipmentCaptor.capture());
        List<Equipment> savedEquipments = equipmentCaptor.getAllValues();

        int counter = 0;
        for (Equipment equipment : savedEquipments) {
            counter = assertEquipmentAttributes(rangVerteiler, rangBucht, rangStift1StartValue, hwRack, hwBaugruppe,
                    expectedHwEqnBase, counter, equipment);
        }
    }

    private int assertEquipmentAttributes(String rangVerteiler, String rangBucht, int rangStift1StartValue,
            HWRack hwRack, HWBaugruppe hwBaugruppe, String expectedHwEqnBase,
            int counter, Equipment equipment) {
        assertNull(equipment.getCarrier());
        assertNull(equipment.getKvzNummer());
        assertEquals(equipment.getHvtIdStandort(), hwRack.getHvtIdStandort());
        assertEquals(equipment.getRangVerteiler(), rangVerteiler);
        assertEquals(equipment.getRangBucht(), rangBucht);
        assertEquals(equipment.getHwBaugruppenId(), hwBaugruppe.getId());
        assertEquals(equipment.getRangStift1(), String.valueOf(rangStift1StartValue + counter));
        counter++;
        assertTrue(equipment.getHwEQN().startsWith(expectedHwEqnBase));
        String hwEqnSuffix = equipment.getHwEQN().substring(expectedHwEqnBase.length(), equipment.getHwEQN().length());
        assertEquals(hwEqnSuffix, ((counter < 10) ? ("0" + counter) : ("" + counter)));
        assertEquals(equipment.getStatus(), EqStatus.frei);
        assertEquals(equipment.getHwSchnittstelle(), hwBaugruppe.getHwBaugruppenTyp().getHwSchnittstelleName());
        assertEquals(equipment.getRangSSType(), hwBaugruppe.getHwBaugruppenTyp().getHwSchnittstelleName());
        return counter;
    }

    private HWBaugruppe createHwBaugruppe() {
        return new HWBaugruppeBuilder().withModNumber(DEFAULT_MOD_NUMBER)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder()).withRandomId().build();
    }

    @DataProvider(name = "buildHwEqnBaseSuccessfulDataProvider")
    public Object[][] buildHwEqnBaseSuccessfulDataProvider() {
        return new Object[][] {
                { "R1/S1-LT01", "1-1-1-" },
                { "R1/S1-LT1", "1-1-1-" },
                { "R01/S01-LT1", "1-1-1-" },
                { "R01/S01-LT01", "1-1-1-" },
                { "R5/S3-LT07", "5-3-7-" },
                { "R5/S3-LT07", "5-3-7-" },
                { "R11/S12-LT13", "11-12-13-" },
        };
    }

    @Test(dataProvider = "buildHwEqnBaseSuccessfulDataProvider")
    public void buildHwEqnBaseSuccessful(String modNumber, String hweqnBaseExpected) throws Exception {
        HWBaugruppe hwBaugruppe = new HWBaugruppeBuilder().withModNumber(modNumber).build();
        String hweqnBase = cut.buildHwEqnBase(hwBaugruppe);
        assertEquals(hweqnBase, hweqnBaseExpected);
    }
}
