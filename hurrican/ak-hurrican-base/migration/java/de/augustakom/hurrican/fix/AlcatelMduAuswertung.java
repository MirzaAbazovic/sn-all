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
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * Häufung der Alcatel MDU Ausfälle ermitteln
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode {@link #doCreateExcelSpreadSheet()} enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul> IntelliJ VM Options:
 * produktiv: -ea -Duse.config=production -Doverride.prod.check=true -Dtest.user=migration -Dtest.password=m1gr@ti0n4devel
 * devel: -ea -Duse.config=user.reinerjue -Doverride.prod.check=true -Dtest.user=migration -Dtest.password=m1gr@ti0n4devel
 */

public class AlcatelMduAuswertung extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(AlcatelMduAuswertung.class);

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    class MduDaten {
        public Long hwRackId;
        public String geraetebezeichnung;
        public String mduTyp;
        public String hersteller;
        public String plz;
        public String ort;
        public String strasse;
        public String houseNo;
    }

    class CpsDaten {
        public Long soType;
        public Date datum;
        public byte[] soData;
        public String xmlDaten;
    }

    private CellStyle blackStyle;
    private CellStyle whiteStyle;


    class JdbcHurricanDao extends JdbcDaoSupport {

        public List<MduDaten> getMduDaten(final String preparedDnStatement) {
            final List<MduDaten> mduDaten = new ArrayList<>();
            List<Map<String, Object>> queryResult = getJdbcTemplate().queryForList(preparedDnStatement);
            if (queryResult != null) {
                for (Map<String, Object> values : queryResult) {
                    MduDaten mduSatz = new MduDaten();
                    mduSatz.hwRackId = MapTools.getLong(values, "ID");
                    mduSatz.geraetebezeichnung = MapTools.getString(values, "GERAETEBEZ");
                    mduSatz.mduTyp = MapTools.getString(values, "MDU_TYPE");
                    mduSatz.hersteller = MapTools.getString(values, "CPS_NAME");
                    mduSatz.plz = MapTools.getString(values, "PLZ");
                    mduSatz.ort = MapTools.getString(values, "ORT");
                    mduSatz.strasse = MapTools.getString(values, "STRASSE");
                    mduSatz.houseNo = MapTools.getString(values, "HAUS_NR");
                    mduDaten.add(mduSatz);
                }
            }
            return mduDaten;
        }

        public List<CpsDaten> getCpsDaten(final String preparedDnStatement, MduDaten mduDaten) throws UnsupportedEncodingException {
            final List<CpsDaten> cpsDaten = new ArrayList<>();
            List<Map<String, Object>> queryResult = getJdbcTemplate().queryForList(preparedDnStatement, mduDaten.hwRackId);
            if (queryResult != null) {
                for (Map<String, Object> values : queryResult) {
                    CpsDaten cpsSatz = new CpsDaten();
                    cpsSatz.soType = MapTools.getLong(values, "SERVICE_ORDER_TYPE");
                    cpsSatz.datum = MapTools.getDate(values, "ESTIMATED_EXEC_TIME");
                    cpsSatz.soData = MapTools.getObject(values, "SERVICE_ORDER_DATA", byte[].class);
                    cpsSatz.xmlDaten = new String(cpsSatz.soData, "UTF-8");
                    cpsSatz.xmlDaten = cpsSatz.xmlDaten.replaceAll("\\n|\\r", "");
                    cpsDaten.add(cpsSatz);
                }
            }
            return cpsDaten;
        }
    }

    private String prepareMduDatenStatement() {
        return "SELECT DISTINCT\n"
                + "  r.ID,\n"
                + "  r.GERAETEBEZ,\n"
                + "  mdu.MDU_TYPE,\n"
                + "  t.CPS_NAME,\n"
                + "  g.ORT,\n"
                + "  g.STRASSE,\n"
                + "  g.PLZ,\n"
                + "  g.HAUS_NR\n"
                + "FROM T_HW_RACK r\n"
                + "  INNER JOIN  T_HW_RACK_MDU mdu ON r.ID = mdu.RACK_ID\n"
                + "  INNER JOIN T_HVT_TECHNIK t on r.HW_PRODUCER = t.ID\n\n"
                + "  INNER JOIN T_HVT_STANDORT s on r.HVT_ID_STANDORT = s.HVT_ID_STANDORT\n"
                + "  INNER JOIN T_HVT_GRUPPE g on s.HVT_GRUPPE_ID = g.HVT_GRUPPE_ID\n"
                + "WHERE r.RACK_TYP = 'MDU' AND t.ID = 6\n"
                + "ORDER BY r.GERAETEBEZ";
    }

    private String prepareCpsDatenStatement() {
        return "SELECT\n"
                + "  tx.ID,\n"
                + "  tx.SERVICE_ORDER_TYPE,\n"
                + "  tx.ESTIMATED_EXEC_TIME,\n"
                + "  tx.SERVICE_ORDER_DATA\n"
                + "FROM T_CPS_TX tx\n"
                + "WHERE tx.TX_STATE = 14260 AND tx.SERVICE_ORDER_TYPE IN (14005, 14007) AND tx.HW_RACK_ID = ?\n"
                + "ORDER BY tx.ID";
    }

    private List<MduDaten> prepareMduDaten() {
        final JdbcHurricanDao jdbcHurricanDao = new JdbcHurricanDao();
        jdbcHurricanDao.setDataSource(SessionFactoryUtils.getDataSource(sessionFactory));

        return jdbcHurricanDao.getMduDaten(prepareMduDatenStatement());
    }

    private List<CpsDaten> prepareCpsDaten(MduDaten mduDaten) throws UnsupportedEncodingException {
        final JdbcHurricanDao jdbcHurricanDao = new JdbcHurricanDao();
        jdbcHurricanDao.setDataSource(SessionFactoryUtils.getDataSource(sessionFactory));

        return jdbcHurricanDao.getCpsDaten(prepareCpsDatenStatement(), mduDaten);
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
        XlsPoiTool.setContent(row, rowIndex++, "Geraetebez.");
        XlsPoiTool.setContent(row, rowIndex++, "Hersteller");
        XlsPoiTool.setContent(row, rowIndex++, "Typ");
        XlsPoiTool.setContent(row, rowIndex++, "PLZ");
        XlsPoiTool.setContent(row, rowIndex++, "Stadt");
        XlsPoiTool.setContent(row, rowIndex++, "Strasse");

        for (int i = 0; i < 13; i++) {
            XlsPoiTool.setContent(row, rowIndex++, "------");
            XlsPoiTool.setContent(row, rowIndex++, "SO Typ");
            XlsPoiTool.setContent(row, rowIndex++, "Datum");
            XlsPoiTool.setContent(row, rowIndex++, "Seriennummer");
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
        Matcher m = Pattern.compile(".*<MDU_SERIAL_NO>(.*)</MDU_SERIAL_NO>.*").matcher(xmlDaten);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return "?";
    }

    private String extractOltBezeichner(String xmlDaten) {
        Matcher m = Pattern.compile(".*<OLT_GERAETE_BEZEICHNUNG>(.*)</OLT_GERAETE_BEZEICHNUNG>.*").matcher(xmlDaten);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return "?";
    }

    private void writeRow(Workbook workbook, Sheet sheet, Font blackFont, Font whiteFont,
            MduDaten mduDaten, List<CpsDaten> cpsDaten) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        int rowIndex = 0;
        XlsPoiTool.setContent(row, rowIndex++, mduDaten.geraetebezeichnung);
        XlsPoiTool.setContent(row, rowIndex++, mduDaten.hersteller);
        XlsPoiTool.setContent(row, rowIndex++, mduDaten.mduTyp);
        XlsPoiTool.setContent(row, rowIndex++, mduDaten.plz);
        XlsPoiTool.setContent(row, rowIndex++, mduDaten.ort);
        XlsPoiTool.setContent(row, rowIndex++, StringTools.join(new String[] { mduDaten.strasse, mduDaten.houseNo },
                " ", true));

        for (CpsDaten daten : cpsDaten) {
            XlsPoiTool.setContent(row, rowIndex++, "");
            XlsPoiTool.setContent(row, rowIndex++, mapSoType(daten.soType));
            XlsPoiTool.setContent(row, rowIndex++, df.format(daten.datum));
            XlsPoiTool.setContent(row, rowIndex++, extractSerialNo(daten.xmlDaten));
            XlsPoiTool.setContent(row, rowIndex++, extractOltBezeichner(daten.xmlDaten));
        }

        toggleCellStyle(workbook, blackFont, whiteFont, row);
    }

    private void foreach(Workbook workbook, Sheet sheet, Font blackFont, Font whiteFont, List<MduDaten> mduDaten)
            throws UnsupportedEncodingException {
        for (MduDaten daten : mduDaten) {
            final List<CpsDaten> cpsDaten = prepareCpsDaten(daten);
            if (cpsDaten != null && !cpsDaten.isEmpty()) {
                writeRow(workbook, sheet, blackFont, whiteFont, daten, cpsDaten);
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
        Sheet sheet = workbook.createSheet("Alcatel MDU Betriebszeiten");
        writeHeader(workbook, sheet);

        try {
            foreach(workbook, sheet, blackFont, whiteFont, prepareMduDaten());
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
