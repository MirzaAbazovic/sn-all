/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.2012 12:13:01
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.cc.HVTService;

@Test(groups = { BaseTest.UNIT })
public class ImportHvtAdresseCommandTest extends BaseTest {
    @InjectMocks
    @Spy
    private ImportHvtAdresseCommand cut;

    @Mock
    protected HVTService hvtService;

    @BeforeMethod
    public void setUp() {
        cut = new ImportHvtAdresseCommand();
        initMocks(this);
    }

    @Test
    public void testImport() throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        XSSFRow row = sheet.createRow(0);
        row.createCell(ImportHvtAdresseCommand.COL_ONKZ).setCellValue("89");
        row.createCell(ImportHvtAdresseCommand.COL_ASB).setCellValue("40");
        row.createCell(ImportHvtAdresseCommand.COL_ZUGANGSART).setCellValue("CuDa");
        row.createCell(ImportHvtAdresseCommand.COL_PLZ_NEU).setCellValue("81671");
        row.createCell(ImportHvtAdresseCommand.COL_ORT_NEU).setCellValue("München");
        row.createCell(ImportHvtAdresseCommand.COL_STR_HAUSNR_NEU).setCellValue("Führichstr. 1");

        HVTStandort hvtStandort = new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withAsb(Integer.valueOf(40)).get();
        Mockito.when(hvtService.findHVTStandort(Mockito.anyString(), Mockito.anyInt())).thenReturn(hvtStandort);
        HVTGruppe hvtGruppe = new HVTGruppeBuilder().withStrasse("Bad-Schachener-Str").withHausNr("2").withPlz("81671")
                .withOrt("München").withOnkz("089").get();
        Mockito.when(hvtService.findHVTGruppeById(Mockito.anyLong())).thenReturn(hvtGruppe);

        cut.prepare(ImportKvzAdresseCommand.PARAM_IMPORT_ROW, row);
        SingleRowResult result = (SingleRowResult) cut.execute();
        Assert.assertEquals(result.isIgnored(), false);
        verify(hvtService, times(1)).saveHVTGruppe(Mockito.any(HVTGruppe.class));
    }

}


