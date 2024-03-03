package de.mnet.wbci.ticketing.escalation;

import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.*;

import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.EscalationPreAgreementVO;


public class EscalationSpreadsheetConverter extends AbstractSpreadsheetConverter {

    public static XSSFWorkbook convert(List<EscalationPreAgreementVO> escalations, EscalationListConfiguration configuration) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();

        Map<STYLE, CellStyle> styles = createStyles(wb);

        Sheet sheet = wb.createSheet(getTabName(configuration));
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        //title row
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(styles.get(STYLE.TITLE));
        String formattedDate = DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR);
        switch (configuration.getType()) {
            case CARRIER_OVERVIEW:
                titleCell.setCellValue(String.format(TITLE.SHEET_CARRIER_OVERVIEW.getText(), formattedDate));
                sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$K$1"));
                break;
            case CARRIER_SPECIFIC:
                titleCell.setCellValue(String.format(TITLE.SHEET_CARRIER.getText(), configuration.getPartnerEKP().getITUCarrierCode(), formattedDate));
                sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$J$1"));
                break;
            case INTERNAL:
                titleCell.setCellValue(String.format(TITLE.SHEET_INT_OVERVIEW.getText(), configuration.getMnetRole().name(), formattedDate));
                sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$J$1"));
                break;
            default:
                break;
        }

        //header row
        Row headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints(40);
        Cell headerCell;
        Iterator<COLUMN> it = COLUMN.getIterator(configuration);
        for (int i = 0; it.hasNext(); i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(it.next().getText());
            headerCell.setCellStyle(styles.get(STYLE.HEADER));
        }

        //rownum for content
        int rownum = 2;

        //create freeze pane
        sheet.createFreezePane(0, rownum);

        for (EscalationPreAgreementVO vo : escalations) {
            if (vo.getEscalationLevel() != null) {
                CarrierRole mnetRole = CarrierRole.lookupMNetCarrierRoleByCarrierCode(vo.getEkpAuf(), vo.getEkpAbg());
                Row row = sheet.createRow(rownum++);

                it = COLUMN.getIterator(configuration);
                for (int i = 0; it.hasNext(); i++) {
                    Cell cell = row.createCell(i);
                    switch (it.next()) {
                        case PARTNER_EKP:
                            String partner = mnetRole.equals(CarrierRole.AUFNEHMEND) ? vo.getEkpAbgITU() : vo.getEkpAufITU();
                            cell.setCellValue(partner);
                            break;
                        case ROLE_MNET:
                            cell.setCellValue(mnetRole.getName());
                            break;
                        case VA_ID:
                            cell.setCellValue(vo.getVaid());
                            break;
                        case GF:
                            cell.setCellValue(vo.getGfTypeShortName());
                            break;
                        case WECHSELTERMIN:
                            Date date = (vo.getWechseltermin() != null) ? vo.getWechseltermin() : vo.getVorgabeDatum();
                            cell.setCellValue(DateTools.formatDate(date, DateTools.PATTERN_DAY_MONTH_YEAR));
                            break;
                        case VORGANG_STATUS:
                            cell.setCellValue(vo.getEscalationStatusDescription());
                            break;
                        case VORGANG_UPDATED:
                            Date updated = (vo.getEscalationType().isRuemVa())
                                    ? vo.getRueckmeldeDatum()
                                    : vo.getProcessedAt();
                            cell.setCellValue(DateTools.formatDate(updated, DateTools.PATTERN_DAY_MONTH_YEAR));
                            break;
                        case NEXT_MELDUNG:
                            cell.setCellValue(vo.getEscalationType().getExpectedAction());
                            break;
                        case ANTWORTFRIST:
                            cell.setCellValue(vo.getDeadlineDaysStringValue());
                            break;
                        case VERBL_ANTWORTFRIST:
                            Integer deadlineDays = EscalationHelper.determineDeadlineDays(configuration.getType(), vo);
                            cell.setCellValue(deadlineDays != null ? deadlineDays.toString() : null);
                            break;
                        case ESKALATIONSLEVEL:
                            cell.setCellValue(vo.getEscalationLevel().name());
                            switch (vo.getEscalationLevel()) {
                                case LEVEL_2:
                                    cell.setCellStyle(styles.get(STYLE.ORANGE));
                                    break;
                                case LEVEL_3:
                                    cell.setCellStyle(styles.get(STYLE.RED));
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //finally set column widths, the width is measured in units of 1/256th of a character width
        it = COLUMN.getIterator(configuration);
        for (int i = 0; it.hasNext(); i++) {
            sheet.setColumnWidth(i, it.next().getWidth() * 256);  //x characters wide
        }

        return wb;
    }

    private static String getTabName(EscalationListConfiguration configuration) {
        String prefix = "";
        String name = TITLE.TAB.getText();
        if (EscalationListType.CARRIER_SPECIFIC.equals(configuration.getType()) && configuration.getPartnerEKP() != null) {
            String ituCarrierCode = configuration.getPartnerEKP().getITUCarrierCode();
            prefix = ituCarrierCode.substring(ituCarrierCode.indexOf('.') + 1) + "-";
        }
        if (EscalationListType.INTERNAL.equals(configuration.getType())) {
            prefix = "Interne-";
            name = TITLE.TAB_INTERN.getText();
        }
        return String.format(name, prefix, DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN));
    }

    protected static enum TITLE {
        TAB("%sEskalationen-%s"),
        TAB_INTERN("%sPruefliste-%s"),
        SHEET_INT_OVERVIEW("Interne Prüfliste für WBCI-Nachrichten - %s (Stand %s)"),
        SHEET_CARRIER_OVERVIEW("Eskalationsübersicht für alle Carrier (Stand %s)"),
        SHEET_CARRIER("Eskalationsübersicht für den Carrier '%s' (Stand %s)");
        private final String text;

        private TITLE(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    protected static enum COLUMN {
        PARTNER_EKP("Partner-EKP", 12),
        ROLE_MNET("Rolle M-Net", 20),
        VA_ID("Vorabstimmungs-ID", 25),
        GF("Geschäftsfall", 15),
        WECHSELTERMIN("Wechseltermin / KWT", 16),
        VORGANG_STATUS("Vorgang-Status", 25),
        VORGANG_UPDATED("Vorgang-Zeitstempel", 15),
        NEXT_MELDUNG("Nächstmögliche Meldungen", 17),
        ANTWORTFRIST("Antwortfrist [Arbeitstage]", 13),
        VERBL_ANTWORTFRIST("Verbl. Anwortzeit [Arbeitstage]", 13),
        ESKALATIONSLEVEL("Eskalationslevel", 15);

        private final String text;
        private final int width;

        private COLUMN(String text, int width) {
            this.text = text;
            //check max with of excel column
            assert (width <= 255);
            this.width = width;
        }

        public static Iterator<COLUMN> getIterator(EscalationListConfiguration configuration) {
            List<COLUMN> columns = new ArrayList<>(Arrays.asList(values()));
            if (EscalationListType.CARRIER_SPECIFIC.equals(configuration.getType())) {
                columns.remove(PARTNER_EKP);
            }
            if (EscalationListType.INTERNAL.equals(configuration.getType())) {
                columns.remove(ESKALATIONSLEVEL);
            }
            return columns.iterator();
        }

        public String getText() {
            return text;
        }

        public int getWidth() {
            return width;
        }
    }

}
