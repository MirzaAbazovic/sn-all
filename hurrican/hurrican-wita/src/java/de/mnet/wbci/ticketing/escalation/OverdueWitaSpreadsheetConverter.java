package de.mnet.wbci.ticketing.escalation;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.mnet.wbci.model.BaseOverdueVo;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.OverdueWitaOrderVO;

/**
 * Converter class to generate an excel sheet from a {@link Collection} of any {@link BaseOverdueVo};
 */
public class OverdueWitaSpreadsheetConverter extends AbstractSpreadsheetConverter {


    public static <VO extends BaseOverdueVo> XSSFWorkbook convert(Collection<VO> overdueVOs, boolean abmPv) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();

        Map<STYLE, CellStyle> styles = createStyles(wb);

        Sheet sheet = wb.createSheet(String.format("%s - %s",
                (abmPv ? TITLE.TAB_ABMPV.getText() : TITLE.TAB_WITA.getText()),
                DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN)));
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
        if (abmPv) {
            titleCell.setCellValue(String.format(TITLE.SHEET_ABMPV.getText(), formattedDate));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$H$1"));
        }
        else {
            titleCell.setCellValue(String.format(TITLE.SHEET_WITA.getText(), formattedDate));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$G$1"));
        }

        //header row
        Row headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints(40);
        Cell headerCell;
        Iterator<COLUMN> it = COLUMN.getIterator(abmPv);
        for (int i = 0; it.hasNext(); i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(it.next().getText());
            headerCell.setCellStyle(styles.get(STYLE.HEADER));
        }

        //rownum for content
        int rownum = 2;

        //create freeze pane
        sheet.createFreezePane(0, rownum);

        for (VO vo : overdueVOs) {
            Row row = sheet.createRow(rownum++);

            it = COLUMN.getIterator(abmPv);
            for (int i = 0; it.hasNext(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(styles.get(STYLE.LINE));
                switch (it.next()) {
                    case VA_ID:
                        cell.setCellValue(vo.getVaid());
                        break;
                    case EKP_AUF:
                        String ekpAuf = vo.getEkpAuf() != null ? vo.getEkpAuf().getITUCarrierCode() : null;
                        cell.setCellValue(ekpAuf);
                        break;
                    case EKP_ABG:
                        String ekpAbg = vo.getEkpAbg() != null ? vo.getEkpAbg().getITUCarrierCode() : null;
                        cell.setCellValue(ekpAbg);
                        break;
                    case WECHSELTERMIN:
                        cell.setCellValue(vo.getFormattedWechseltermin());
                        break;
                    case VERTRAGS_NR:
                        cell.setCellValue((vo instanceof OverdueAbmPvVO) ? ((OverdueAbmPvVO) vo).getVertragsnummer() : null);
                        break;
                    case BILLING_NR:
                        cell.setCellValue(vo.getAuftragNoOrig() != null ? vo.getAuftragNoOrig().toString() : null);
                        break;
                    case HURRICAN_AUFTRAGS_NR:
                        cell.setCellValue(vo.getAuftragId() != null ? vo.getAuftragId().toString() : null);
                        break;
                    case AKM_PV_ERHALTEN:
                        Boolean akmpv = (vo instanceof OverdueAbmPvVO) ? ((OverdueAbmPvVO) vo).isAkmPvReceived() : null;
                        cell.setCellValue(akmpv == null ? null : (akmpv ? "ja" : "nein"));
                        break;
                    case ABBM_PV_ERHALTEN:
                        Boolean abbmpv = (vo instanceof OverdueAbmPvVO) ? ((OverdueAbmPvVO) vo).isAbbmPvReceived() : null;
                        cell.setCellValue(abbmpv == null ? null : (abbmpv ? "ja" : "nein"));
                        break;
                    case TECHNOLOGIE:
                        String technologie = (vo instanceof OverdueWitaOrderVO) ? ((OverdueWitaOrderVO) vo).getmNetTechnologie().getWbciTechnologieCode() : null;
                        cell.setCellValue(technologie);
                        break;
                    case WITA_DATEN:
                        String value = (vo instanceof OverdueWitaOrderVO) ? format(((OverdueWitaOrderVO) vo).getAssignedWitaOrders()) : null;
                        cell.setCellValue(value);
                        cell.setCellStyle(styles.get(STYLE.MULTILINE));
                        break;
                    default:
                        break;
                }
            }
        }

        //finally set column widths, the width is measured in units of 1/256th of a character width
        it = COLUMN.getIterator(abmPv);
        for (int i = 0; it.hasNext(); i++) {
            sheet.setColumnWidth(i, it.next().getWidth() * 256);  //x characters wide
        }

        return wb;
    }

    private static String format(List<Pair<String, String>> assignedWitaOrders) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(assignedWitaOrders)) {
            int i = 0;
            for (Pair<String, String> p : assignedWitaOrders) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(p.getFirst()).append(" - ").append(p.getSecond());
                i++;
            }
        }
        return sb.toString();
    }

    protected static enum TITLE {
        TAB_ABMPV("ABM-PVs"),
        TAB_WITA("WITA-Best"),
        SHEET_ABMPV("WBCI-Vorgänge mit fehlenden ABM-PV Meldungen (Stand %s)"),
        SHEET_WITA("WBCI-Vorgänge mit fehlerhaften WITA-Bestellungen (Stand %s)");
        private final String text;

        private TITLE(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    protected static enum COLUMN {
        VA_ID("Vorabstimmungs-ID", 25),
        EKP_AUF("EKPauf", 12),
        EKP_ABG("EKPabg", 12),
        WECHSELTERMIN("Wechseltermin", 15),
        VERTRAGS_NR("Vertragsnummer", 16),
        BILLING_NR("Billing Auftrags-Nr.", 14),
        HURRICAN_AUFTRAGS_NR("Tech. Auftrags-Nr.", 13),
        AKM_PV_ERHALTEN("AKM-PV erhalten", 10),
        ABBM_PV_ERHALTEN("ABBM-PV erhalten", 10),
        TECHNOLOGIE("M-Net Technologie", 14),
        WITA_DATEN("WITA-Bestelldaten", 60);

        private final String text;
        private final int width;

        private COLUMN(String text, int width) {
            this.text = text;
            //check max with of excel column
            assert (width <= 255);
            this.width = width;
        }

        public static Iterator<COLUMN> getIterator(boolean abmPv) {
            List<COLUMN> columns;
            if (abmPv) {
                columns = new ArrayList<>(Arrays.asList(
                        VA_ID, EKP_AUF, WECHSELTERMIN, VERTRAGS_NR, BILLING_NR, HURRICAN_AUFTRAGS_NR, AKM_PV_ERHALTEN, ABBM_PV_ERHALTEN
                ));
            }
            else {
                columns = new ArrayList<>(Arrays.asList(
                        VA_ID, EKP_ABG, WECHSELTERMIN, BILLING_NR, HURRICAN_AUFTRAGS_NR, TECHNOLOGIE, WITA_DATEN
                ));
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
