package de.augustakom.hurrican.fix;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.inject.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.testng.annotations.Test;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * Häufung der Alcatel MDU Ausfälle auf Basis der Seriennummer ermitteln
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul> IntelliJ VM Options:
 * produktiv: -ea -Duse.config=production -Doverride.prod.check=true -Dtest.user=migration
 * -Dtest.password=m1gr@ti0n4devel devel: -ea -Duse.config=user.reinerjue -Doverride.prod.check=false
 * -Dtest.user=migration -Dtest.password=m1gr@ti0n4devel
 */

public class AlcatelSnAuswertung extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(AlcatelSnAuswertung.class);

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    class CpsDaten {
        public Long soType;
        public Date datum;
        public byte[] soData;
        public String xmlDaten;
        public String geraetebezeichnung;
        public String standort;
        public String serialNo;
        public String olt;
    }

    private CellStyle blackStyle;
    private CellStyle whiteStyle;


    class JdbcHurricanDao extends JdbcDaoSupport {

        public List<CpsDaten> getCpsDaten(final String preparedDnStatement) throws UnsupportedEncodingException {
            final List<CpsDaten> cpsDaten = new ArrayList<>();
            List<Map<String, Object>> queryResult = getJdbcTemplate().queryForList(preparedDnStatement);
            if (queryResult != null) {
                for (Map<String, Object> values : queryResult) {
                    CpsDaten cpsSatz = new CpsDaten();
                    cpsSatz.soType = MapTools.getLong(values, "SERVICE_ORDER_TYPE");
                    cpsSatz.datum = MapTools.getDate(values, "ESTIMATED_EXEC_TIME");
                    cpsSatz.soData = MapTools.getObject(values, "SERVICE_ORDER_DATA", byte[].class);
                    cpsSatz.xmlDaten = new String(cpsSatz.soData, "UTF-8");
                    cpsSatz.xmlDaten = cpsSatz.xmlDaten.replaceAll("\\n|\\r", "");
                    cpsSatz.serialNo = extractSerialNo(cpsSatz.xmlDaten);
                    cpsSatz.olt = extractOltBezeichner(cpsSatz.xmlDaten);
                    cpsSatz.geraetebezeichnung = extractGeraetebezeichnung(cpsSatz.xmlDaten);
                    cpsSatz.standort = extractStandort(cpsSatz.xmlDaten);
                    cpsDaten.add(cpsSatz);
                }
            }
            return cpsDaten;
        }
    }

    private String prepareCpsDatenStatement() {
        return "SELECT tx.SERVICE_ORDER_TYPE, tx.ESTIMATED_EXEC_TIME, tx.SERVICE_ORDER_DATA from T_CPS_TX tx\n"
                + "  INNER JOIN T_HW_RACK r on tx.HW_RACK_ID = r.ID\n"
                + "  INNER JOIN T_HVT_TECHNIK tech on r.HW_PRODUCER = tech.ID\n"
                + "WHERE r.RACK_TYP = 'MDU' AND tech.ID = 6 AND tx.TX_STATE = 14260 \n"
                + "      AND tx.SERVICE_ORDER_TYPE IN (14005, 14007)\n"
                + "ORDER BY tx.ESTIMATED_EXEC_TIME";
    }

    private List<CpsDaten> prepareCpsDaten() throws UnsupportedEncodingException {
        final JdbcHurricanDao jdbcHurricanDao = new JdbcHurricanDao();
        jdbcHurricanDao.setDataSource(SessionFactoryUtils.getDataSource(sessionFactory));

        return jdbcHurricanDao.getCpsDaten(prepareCpsDatenStatement());
    }

    private Font createWhitFont(Workbook workbook) {
        Font whiteFont = workbook.createFont();
        whiteFont.setColor(HSSFColor.WHITE.index);
        return whiteFont;
    }

    private Font createBlackFont(Workbook workbook) {
        Font blackFont = workbook.createFont();
        blackFont.setColor(HSSFColor.BLACK.index);
        return blackFont;
    }

    private void writeHeader(Workbook workbook, Sheet sheet) {
        sheet.createFreezePane(0, 1, 0, 1);
        final Row row = sheet.createRow(0);
        int rowIndex = 0;
        XlsPoiTool.setContent(row, rowIndex++, "Seriennummer");

        for (int i = 0; i < 13; i++) {
            XlsPoiTool.setContent(row, rowIndex++, "------");
            XlsPoiTool.setContent(row, rowIndex++, "SO Typ");
            XlsPoiTool.setContent(row, rowIndex++, "Datum");
            XlsPoiTool.setContent(row, rowIndex++, "Geraetebez.");
            XlsPoiTool.setContent(row, rowIndex++, "Standort");
            XlsPoiTool.setContent(row, rowIndex++, "OLT");
        }

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(HSSFColor.WHITE.index);
        cellStyle.setFont(headerFont);
        for (Cell cell : row) {
            cell.setCellStyle(cellStyle);
        }
    }

    private void toggleCellStyle(Workbook workbook, Font blackFont, Font whiteFont, Row row) {
        if (blackStyle == null) {
            blackStyle = workbook.createCellStyle();
            blackStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            blackStyle.setFont(blackFont);
        }

        if (whiteStyle == null) {
            whiteStyle = workbook.createCellStyle();
            whiteStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            whiteStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            whiteStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            whiteStyle.setFont(whiteFont);
        }

        for (int i = 0; i < 71; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            if (i % 2 == 0) {
                cell.setCellStyle(blackStyle);
            }
            else {
                cell.setCellStyle(whiteStyle);
            }
        }
    }

    private void autosizeColumns(Sheet sheet) {
        for (Cell cell : sheet.getRow(0)) {
            sheet.autoSizeColumn(cell.getColumnIndex());
        }
    }

    private String mapSoType(Long soType) {
        switch (soType.intValue()) {
            case 14005:
                return "MDU-INIT";
            case 14007:
                return "MDU-UPDATE";
            default:
                return "?";
        }
    }

    private String extractSerialNo(String xmlDaten) {
        return extractTag(xmlDaten, "MDU_SERIAL_NO");
    }

    private String extractOltBezeichner(String xmlDaten) {
        return extractTag(xmlDaten, "OLT_GERAETE_BEZEICHNUNG");
    }

    private String extractGeraetebezeichnung(String xmlDaten) {
        return extractTag(xmlDaten, "MDU_GERAETE_BEZEICHNUNG");
    }

    private String extractStandort(String xmlDaten) {
        return extractTag(xmlDaten, "MDU_STANDORT");
    }

    private String extractTag(String xmlDaten, String tagId) {
        Matcher m = Pattern.compile(String.format(".*<%s>(.*)</%s>.*", tagId, tagId)).matcher(xmlDaten);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return "?";
    }

    private void writeRow(Workbook workbook, Sheet sheet, Font blackFont, Font whiteFont,
            String serialNo, List<CpsDaten> cpsDaten) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        int rowIndex = 0;
        XlsPoiTool.setContent(row, rowIndex++, serialNo);

        for (CpsDaten daten : cpsDaten) {
            XlsPoiTool.setContent(row, rowIndex++, "");
            XlsPoiTool.setContent(row, rowIndex++, mapSoType(daten.soType));
            XlsPoiTool.setContent(row, rowIndex++, df.format(daten.datum));
            XlsPoiTool.setContent(row, rowIndex++, daten.geraetebezeichnung);
            XlsPoiTool.setContent(row, rowIndex++, daten.standort);
            XlsPoiTool.setContent(row, rowIndex++, daten.olt);
        }

        toggleCellStyle(workbook, blackFont, whiteFont, row);
    }

    private Map<String, List<CpsDaten>> foreach(List<CpsDaten> cpsDaten)
            throws UnsupportedEncodingException {
        Map<String, List<CpsDaten>> sorted = new HashMap<>();
        List<CpsDaten> items;
        for (CpsDaten daten : cpsDaten) {
            items = sorted.get(daten.serialNo);
            if (items == null) {
                items = new ArrayList<>();
                items.add(daten);
                sorted.put(daten.serialNo, items);
            }
            else {
                items.add(daten);
            }
        }
        return sorted;
    }

    private void foreach(Workbook workbook, Sheet sheet, Font blackFont, Font whiteFont, Map<String, List<CpsDaten>> sorted)
            throws UnsupportedEncodingException {
        for (String serialNo : sorted.keySet()) {
            final List<CpsDaten> cpsDaten = sorted.get(serialNo);
            if (cpsDaten != null && !cpsDaten.isEmpty()) {
                writeRow(workbook, sheet, blackFont, whiteFont, serialNo, cpsDaten);
                if (sheet.getLastRowNum() % 100 == 0) {
                    LOGGER.info("Fortschritt in Zeilen: " + sheet.getLastRowNum());
                }
            }
        }
    }

    //enabled = true um die Migration zu aktivieren
    @Test(enabled = false)
    public void doCreateExcelSpreadSheet() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Font whiteFont = createWhitFont(workbook);
        Font blackFont = createBlackFont(workbook);
        Sheet sheet = workbook.createSheet("Alcatel Austausch pro Seriennummer");
        writeHeader(workbook, sheet);

        try {
            foreach(workbook, sheet, blackFont, whiteFont, foreach(prepareCpsDaten()));
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage(), e);
        }
        finally {
            autosizeColumns(sheet);
            File xlsFile = new File(System.getProperty("user.home") + "\\Hurrican", getClass().getSimpleName()
                    + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
            workbook.write(new FileOutputStream(xlsFile));
            LOGGER.info("XLS File=" + xlsFile.getPath());
        }
    }
}
