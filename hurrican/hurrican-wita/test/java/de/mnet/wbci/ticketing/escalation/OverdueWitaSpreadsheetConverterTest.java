/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.04.2014
 */
package de.mnet.wbci.ticketing.escalation;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.mnet.wbci.model.BaseOverdueVo;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.OverdueWitaOrderVO;
import de.mnet.wbci.model.Technologie;

/**
 *
 */
public class OverdueWitaSpreadsheetConverterTest {

    @Test
    public void testGenerateWitaSpreadsheet() throws Exception {
        List<OverdueWitaOrderVO> testDataWita;
        testDataWita = new ArrayList<>();

        OverdueWitaOrderVO vo = new OverdueWitaOrderVO();
        vo.setVaid("ABC");
        vo.setEkpAbg(CarrierCode.DTAG);
        vo.setWechseltermin(Date.from(LocalDate.parse("2014-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        vo.setAuftragNoOrig(456L);
        vo.setAuftragId(789L);
        vo.setmNetTechnologie(Technologie.FTTC);
        vo.addAssignedWitaOrder(new Pair<>("123", "beantwortet, positive Rückmeldung"));
        testDataWita.add(vo);

        XSSFWorkbook result = OverdueWitaSpreadsheetConverter.convert(testDataWita, false);
        assertWbContent(result, vo);
    }

    @Test
    public void testGenerateAbmPvSpreadsheet() throws Exception {
        List<OverdueAbmPvVO> testDataAbmPv = new ArrayList<>();
        OverdueAbmPvVO vo = new OverdueAbmPvVO();
        vo.setVaid("ABC");
        vo.setVertragsnummer("123");
        vo.setEkpAuf(CarrierCode.DTAG);
        vo.setWechseltermin(Date.from(LocalDate.parse("2014-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        vo.setAuftragNoOrig(456L);
        vo.setAuftragId(789L);
        vo.setAkmPvReceived(true);
        vo.setAbbmPvReceived(true);
        testDataAbmPv.add(vo);

        XSSFWorkbook result = OverdueWitaSpreadsheetConverter.convert(testDataAbmPv, true);
        assertWbContent(result, vo);
    }


    private <VO extends BaseOverdueVo> void assertWbContent(XSSFWorkbook wb, VO vo) {
        boolean abmPv = vo instanceof OverdueAbmPvVO;
        Assert.notNull(wb);
        XSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());

        String formattedDate = DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR);
        String expectedSheetHeading;
        String expectedTabName;
        if (abmPv) {
            expectedTabName = String.format("ABM-PVs - %s",
                    DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN));
            expectedSheetHeading = String.format("WBCI-Vorgänge mit fehlenden ABM-PV Meldungen (Stand %s)", formattedDate);
        }
        else {
            expectedTabName = String.format("WITA-Best - %s",
                    DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN));
            expectedSheetHeading = String.format("WBCI-Vorgänge mit fehlerhaften WITA-Bestellungen (Stand %s)", formattedDate);
        }

        int row = 0;
        assertEquals(sheet.getSheetName(), expectedTabName);
        assertEquals(sheet.getRow(row++).getCell(0).getStringCellValue(), expectedSheetHeading);

        XSSFRow header = sheet.getRow(row++);
        int col = 0;
        assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.VA_ID.getText());
        if (abmPv) {
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.EKP_AUF.getText());
        }
        else {
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.EKP_ABG.getText());
        }
        assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.WECHSELTERMIN.getText());
        if (abmPv) {
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.VERTRAGS_NR.getText());
        }
        assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.BILLING_NR.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.HURRICAN_AUFTRAGS_NR.getText());
        if (abmPv) {
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.AKM_PV_ERHALTEN.getText());
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.ABBM_PV_ERHALTEN.getText());
        }
        else {
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.TECHNOLOGIE.getText());
            assertEquals(header.getCell(col++).getStringCellValue(), OverdueWitaSpreadsheetConverter.COLUMN.WITA_DATEN.getText());
        }
        assertNull(header.getCell(col));

        XSSFRow dataRow = sheet.getRow(row);
        col = 0;
        assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getVaid());
        if (abmPv) {
            assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEkpAuf().getITUCarrierCode());
        }
        else {
            assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEkpAbg().getITUCarrierCode());
        }
        assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getWechseltermin(), DateTools.PATTERN_DAY_MONTH_YEAR));
        if (vo instanceof OverdueAbmPvVO) {
            assertEquals(dataRow.getCell(col++).getStringCellValue(), ((OverdueAbmPvVO) vo).getVertragsnummer());
        }
        assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getAuftragNoOrig().toString());
        assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getAuftragId().toString());
        if (vo instanceof OverdueWitaOrderVO) {
            assertEquals(dataRow.getCell(col++).getStringCellValue(), ((OverdueWitaOrderVO) vo).getmNetTechnologie().getWbciTechnologieCode());
            Pair<String, String> el = ((OverdueWitaOrderVO) vo).getAssignedWitaOrders().iterator().next();
            assertEquals(dataRow.getCell(col++).getStringCellValue(), el.getFirst() + " - " + el.getSecond());
        }
        if (vo instanceof OverdueAbmPvVO) {
            assertEquals(dataRow.getCell(col++).getStringCellValue(), ((OverdueAbmPvVO) vo).isAkmPvReceived() ? "ja" : "nein");
            assertEquals(dataRow.getCell(col++).getStringCellValue(), ((OverdueAbmPvVO) vo).isAbbmPvReceived() ? "ja" : "nein");
        }
        assertNull(dataRow.getCell(col));
    }
}
