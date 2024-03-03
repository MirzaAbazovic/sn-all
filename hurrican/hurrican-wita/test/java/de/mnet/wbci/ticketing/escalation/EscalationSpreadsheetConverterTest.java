/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.04.2014
 */
package de.mnet.wbci.ticketing.escalation;

import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.*;
import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.EscalationListType.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.EscalationPreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.EscalationPreAgreementVOBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class EscalationSpreadsheetConverterTest {
    List<EscalationPreAgreementVO> testData;

    @BeforeMethod
    public void setUp() throws Exception {
        testData = convertToTestData(EscalationReportGeneratorTest.carrierPreAgreementVOs(), null);
    }

    protected List<EscalationPreAgreementVO> convertToTestData(Object[][] params, CarrierRole mnetRole) {
        List<EscalationPreAgreementVO> result = new ArrayList<>();
        for (Object[] param : params) {
            RequestTyp typ = (RequestTyp) param[0];
            WbciRequestStatus status = (WbciRequestStatus) param[2];
            Integer overDeadlineDays = (Integer) param[3];
            EscalationPreAgreementVO.EscalationType escalationType = (EscalationPreAgreementVO.EscalationType) param[4];
            EscalationPreAgreementVO.EscalationLevel escalationLevel = (EscalationPreAgreementVO.EscalationLevel) param[5];
            Long deadlineDays = (Long) param[6];
            EscalationPreAgreementVOBuilder builder = EscalationReportGeneratorTest.buildBaseVO(new EscalationPreAgreementVOBuilder(), typ, status, overDeadlineDays, null, mnetRole);
            result.add(builder
                    .withEscalationLevel(escalationLevel)
                    .withEscalationType(escalationType)
                    .withDeadlineDays(deadlineDays)
                    .build());
        }
        return result;
    }

    @Test
    public void testConvertCarrierOverviewList() throws Exception {
        XSSFWorkbook wb = EscalationSpreadsheetConverter.convert(testData, new EscalationListConfiguration(CARRIER_OVERVIEW, null, null));
        assertWbContent(wb, null, testData);
    }

    @Test
    public void testConvertDTAG() throws Exception {
        XSSFWorkbook wb = EscalationSpreadsheetConverter.convert(testData, new EscalationListConfiguration(CARRIER_SPECIFIC, CarrierCode.DTAG, null));
        assertWbContent(wb, CarrierCode.DTAG, testData);
    }


    @Test
    public void testConvertInternalDedalineList() throws Exception {
        List<EscalationPreAgreementVO> testDataInternalAUF = convertToTestData(EscalationReportGeneratorTest.internalPreAgreementVOsAUF(), CarrierRole.AUFNEHMEND);
        List<EscalationPreAgreementVO> testDataInternalABG = convertToTestData(EscalationReportGeneratorTest.internalPreAgreementVOsABG(), CarrierRole.ABGEBEND);

        XSSFWorkbook wbAUF = EscalationSpreadsheetConverter.convert(testDataInternalAUF, new EscalationListConfiguration(INTERNAL, null, CarrierRole.AUFNEHMEND));
        XSSFWorkbook wbABG = EscalationSpreadsheetConverter.convert(testDataInternalABG, new EscalationListConfiguration(INTERNAL, null, CarrierRole.ABGEBEND));
        assertInternalWbContent(wbAUF, testDataInternalAUF, CarrierRole.AUFNEHMEND);
        assertInternalWbContent(wbABG, testDataInternalABG, CarrierRole.ABGEBEND);
    }

    private void assertWbContent(XSSFWorkbook wb, CarrierCode partnerEKP, Collection<EscalationPreAgreementVO> testData) {
        boolean overiewSheet = partnerEKP == null;
        Assert.notNull(wb);
        XSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());

        String prefix = overiewSheet ? "" : partnerEKP.getITUCarrierCode().substring(partnerEKP.getITUCarrierCode().indexOf(".") + 1) + "-";
        assertEquals(sheet.getSheetName(), String.format(EscalationSpreadsheetConverter.TITLE.TAB.getText(),
                prefix,
                DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN)));

        int row = 0;
        String formattedDate = DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR);
        String expectedSheetHeading = overiewSheet
                ? String.format(EscalationSpreadsheetConverter.TITLE.SHEET_CARRIER_OVERVIEW.getText(), formattedDate)
                : String.format(EscalationSpreadsheetConverter.TITLE.SHEET_CARRIER.getText(), partnerEKP.getITUCarrierCode(), formattedDate);
        assertEquals(sheet.getRow(row++).getCell(0).getStringCellValue(), expectedSheetHeading);

        XSSFRow header = sheet.getRow(row++);
        int col = 0;
        if (overiewSheet) {
            assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.PARTNER_EKP.getText());
        }
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.ROLE_MNET.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VA_ID.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.GF.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.WECHSELTERMIN.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VORGANG_STATUS.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VORGANG_UPDATED.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.NEXT_MELDUNG.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.ANTWORTFRIST.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VERBL_ANTWORTFRIST.getText());
        assertEquals(header.getCell(col).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.ESKALATIONSLEVEL.getText());

        for (EscalationPreAgreementVO vo : testData) {
            if (vo.getEscalationLevel() != null) {
                XSSFRow dataRow = sheet.getRow(row++);
                col = 0;
                if (vo.getEkpAuf().equals(CarrierCode.MNET)) {
                    if (overiewSheet) {
                        assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEkpAbgITU());
                    }
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), "== Aufnehmend ==>");
                }
                else {
                    if (overiewSheet) {
                        assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEkpAufITU());
                    }
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), "<== Abgebend ==");

                }
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getVaid());
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getGfTypeShortName());
                assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getWechseltermin(), DateTools.PATTERN_DAY_MONTH_YEAR));
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getRequestStatus().getDescription());
                if (vo.getEscalationType().equals(EscalationPreAgreementVO.EscalationType.RUEM_VA_VERSENDET)) {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getRueckmeldeDatum(), DateTools.PATTERN_DAY_MONTH_YEAR));
                }
                else {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getProcessedAt(), DateTools.PATTERN_DAY_MONTH_YEAR));
                }
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEscalationType().getExpectedAction());
                if (EscalationPreAgreementVO.ESC_TYPES_WITH_WECHSEL_TERMIN_DEADLINE.contains(vo.getEscalationType())) {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getDeadlineDays() + " (vor WT)");
                }
                else {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getDeadlineDays().toString());
                }
                assertEquals(dataRow.getCell(col++).getStringCellValue(), String.valueOf(vo.getDaysUntilDeadlinePartner()));
                assertEquals(dataRow.getCell(col).getStringCellValue(), vo.getEscalationLevel().toString());
            }
        }
    }

    private void assertInternalWbContent(XSSFWorkbook wb, Collection<EscalationPreAgreementVO> testData, CarrierRole carrierRole) {
        Assert.notNull(wb);
        XSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());

        assertEquals(sheet.getSheetName(), String.format("Interne-Pruefliste-%s",
                DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN)));

        int row = 0;
        String formattedDate = DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR);
        String expectedSheetHeading = String.format(EscalationSpreadsheetConverter.TITLE.SHEET_INT_OVERVIEW.getText(), carrierRole.name(), formattedDate);
        assertEquals(sheet.getRow(row++).getCell(0).getStringCellValue(), expectedSheetHeading);

        XSSFRow header = sheet.getRow(row++);
        int col = 0;
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.PARTNER_EKP.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.ROLE_MNET.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VA_ID.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.GF.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.WECHSELTERMIN.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VORGANG_STATUS.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VORGANG_UPDATED.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.NEXT_MELDUNG.getText());
        assertEquals(header.getCell(col++).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.ANTWORTFRIST.getText());
        assertEquals(header.getCell(col).getStringCellValue(), EscalationSpreadsheetConverter.COLUMN.VERBL_ANTWORTFRIST.getText());

        for (EscalationPreAgreementVO vo : testData) {
            if (vo.getEscalationLevel() != null) {
                XSSFRow dataRow = sheet.getRow(row++);
                col = 0;
                if (vo.getEkpAuf().equals(CarrierCode.MNET)) {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEkpAbgITU());
                }
                else {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEkpAufITU());
                }
                assertEquals(dataRow.getCell(col++).getStringCellValue(), carrierRole.getName());
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getVaid());
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getGfTypeShortName());
                assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getWechseltermin(), DateTools.PATTERN_DAY_MONTH_YEAR));
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getRequestStatus().getDescription());
                if (vo.getEscalationType().isRuemVa()) {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getRueckmeldeDatum(), DateTools.PATTERN_DAY_MONTH_YEAR));
                }
                else {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), DateTools.formatDate(vo.getProcessedAt(), DateTools.PATTERN_DAY_MONTH_YEAR));
                }
                assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getEscalationType().getExpectedAction());
                if (EscalationPreAgreementVO.ESC_TYPES_WITH_WECHSEL_TERMIN_DEADLINE.contains(vo.getEscalationType())) {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getDeadlineDays() + " (vor WT)");
                }
                else {
                    assertEquals(dataRow.getCell(col++).getStringCellValue(), vo.getDeadlineDays().toString());
                }
                Integer deadlineDays = vo.getDaysUntilDeadlineMnet();
                assertEquals(dataRow.getCell(col++).getStringCellValue(), deadlineDays != null ? deadlineDays.toString() : "");
                assertNull(dataRow.getCell(col));
            }
        }
    }
}
