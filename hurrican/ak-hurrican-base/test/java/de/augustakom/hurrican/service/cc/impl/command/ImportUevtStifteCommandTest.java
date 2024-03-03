/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2012 16:08:29
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;

/**
 * Unit Test fuer das {@link ImportUevtStifteCommand}
 */
@Test(groups = BaseTest.UNIT)
public class ImportUevtStifteCommandTest extends BaseTest {

    @Spy
    private ImportUevtStifteCommand cut = new ImportUevtStifteCommand();

    @Mock
    private HVTService hvtServiceMock;
    @Mock
    private HVTToolService hvtToolServiceMock;

    @BeforeMethod
    public void setup() {
        initMocks(this);
        cut.setHvtService(hvtServiceMock);
        cut.setHvtToolService(hvtToolServiceMock);
    }

    @Test
    public void testImportUevtStifteEquipmentMissing() throws FindException, StoreException, ValidationException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        XSSFRow row = sheet.createRow(0);
        row.createCell(3).setCellValue("A3");
        cut.prepare(ImportUevtStifteCommand.PARAM_IMPORT_ROW, row);
        cut.prepare(ImportUevtStifteCommand.PARAM_SESSION_ID, -1L);
        doReturn(new UEVT()).when(cut).findUevt(any(String.class), any(String.class));
        doNothing().when(cut).checkUevtLeiste(any(Long.class), any(String.class));
        when(hvtToolServiceMock.fillUevt(any(Long.class), any(String.class), any(String.class),
                any(Integer.class), any(IServiceCallback.class), eq(true), anyInt(), any(Long.class)))
                .thenReturn(null);
        SingleRowResult importResult = cut.importUevtStifte();
        assertTrue(importResult.getWarningCount() == 1);
    }

    @DataProvider
    public Object[][] dataProviderFindUevt() {
        HVTStandort hvtStandort = new HVTStandort();
        UEVT uevt = new UEVT();
        // @formatter:off
        return new Object[][] {
                { null,  null, true },
                { hvtStandort,  null, true },
                { null,  uevt, true },
                { hvtStandort,  uevt, false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFindUevt")
    public void testfindUevt(HVTStandort hvtStandort, UEVT uevt, boolean exceptionExpected) throws FindException {
        when(hvtServiceMock.findHVTStandortByBezeichnung(any(String.class))).thenReturn(hvtStandort);
        when(hvtServiceMock.findUEVT(any(Long.class), any(String.class))).thenReturn(uevt);
        try {
            cut.findUevt("StandortBeliebig", "UevtBeliebig");
        }
        catch (FindException e) {
            if (!exceptionExpected) {
                fail();
            }
            return;
        }
        if (exceptionExpected) {
            fail();
        }
    }

    @DataProvider
    public Object[][] dataProviderCheckUevtLeiste() {
        final EquipmentBelegungView noMatch = new EquipmentBelegungView();
        final EquipmentBelegungView match = new EquipmentBelegungView();
        final String leiste = "27K1";
        match.setLeiste1(leiste);
        // @formatter:off
        return new Object[][] {
                { leiste,  null, true, true },
                { leiste,  Collections.emptyList(), false, false },
                { leiste,  Collections.singletonList(noMatch), false, false },
                { leiste,  Collections.singletonList(match), false, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckUevtLeiste")
    public void testCheckUevtLeiste(String leiste, List<EquipmentBelegungView> equipmentBelegungen,
            boolean throwException, boolean exceptionExpected) throws FindException {
        when(hvtToolServiceMock.findEquipmentBelegung(any(Long.class))).thenReturn(equipmentBelegungen);
        if (throwException) {
            when(hvtToolServiceMock.findEquipmentBelegung(any(Long.class))).thenThrow(new FindException());
        }
        try {
            cut.checkUevtLeiste(1L, leiste);
        }
        catch (FindException e) {
            if (!exceptionExpected) {
                fail();
            }
            return;
        }
        if (exceptionExpected) {
            fail();
        }
    }

}
