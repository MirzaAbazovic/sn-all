package de.augustakom.hurrican.fix;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.inject.*;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Fuer existierende ONTs aus Command Excel Liste die Wohnungsbezeichnung ermitteln, und falls vorhanden in die
 * zugeordnete HVT_Wohnung eintragen.
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul> IntelliJ VM Options:
 * produktiv: -ea -Duse.config=production -Doverride.prod.check=true -Dtest.user=migration -Dtest.password=m1gr@ti0n4devel
 * devel: -ea -Duse.config=user.reinerjue -Doverride.prod.check=true -Dtest.user=migration -Dtest.password=m1gr@ti0n4devel
 */

// TODO: Kommentare aus der unteren Zeile entfernen um die Migration zu persistieren (Autorollback=off)
//@Test(groups = AbstractTransactionalServiceTest.NO_ROLLBACK_TEST)
public class MigrateSipLogin extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(MigrateSipLogin.class);

    class MigrationDnBlock {
        Long dn__no;
        Date gueltig_von;
        Date gueltig_bis;
        Long auftrag_id;
        Long billing_auftrag_no;
        String onkz;
        String dnBase;
        String directDial;
        String rangeFrom;
        String rangeTo;
        String anfang;

        @Override
        public String toString() {
            return String
                    .format("[dn_no=%s, gueltig_von=%s, gueltig_bis=%s, auftrag_id=%s, billing_auftrag_no=%s, onkz=%s, " +
                                    "dnBase=%s, directDial=%s, rangeFrom=%s, rangeTo=%s, anfang=%s]",
                            dn__no.toString(), gueltig_von.toString(), gueltig_bis.toString(), auftrag_id.toString(),
                            billing_auftrag_no.toString(), onkz, dnBase, directDial, rangeFrom, rangeTo, anfang);
        }
    }

    class ImsDn {
        String id;
        String mainDn;
        String sipLogin;
        String shadowMainDn;
        String shadowSipLogin;
    }

    class Technik {
        Long dn__no;
        Long auftrag_id;
        Long billing_auftrag_no;
    }

    class DnBlock {

        public DnBlock(Long dn__no, Long order__no) {
            this.dn__no = dn__no;
            this.order__no = order__no;
        }

        Long dn__no;
        Date validFrom;
        Date validTo;
        Long order__no;
        String onkz;
        String dnBase;
        String directDial;
        String rangeFrom;
        String rangeTo;
    }

    class JdbcRufnummerDao extends JdbcDaoSupport {

        public List<DnBlock> getDnBlocks(final String preparedDnStatement) {
            final List<DnBlock> dnBlocks = new ArrayList<>();
            List<Map<String, Object>> queryResult = getJdbcTemplate().queryForList(preparedDnStatement);
            if (queryResult != null) {
                for (Map<String, Object> values : queryResult) {
                    DnBlock blockDn = new DnBlock(MapTools.getLong(values, "DN__NO"),
                            MapTools.getLong(values, "ORDER__NO"));
                    blockDn.validFrom = MapTools.getDate(values, "VALID_FROM");
                    blockDn.validTo = MapTools.getDate(values, "VALID_TO");
                    blockDn.onkz = MapTools.getString(values, "ONKZ");
                    blockDn.dnBase = MapTools.getString(values, "DN_BASE");
                    blockDn.directDial = MapTools.getString(values, "DIRECT_DIAL");
                    blockDn.rangeFrom = MapTools.getString(values, "RANGE_FROM");
                    blockDn.rangeTo = MapTools.getString(values, "RANGE_TO");
                    dnBlocks.add(blockDn);
                }
            }
            return dnBlocks;
        }
    }

    class JdbcTechnikDao extends JdbcDaoSupport {

        public List<Technik> getTechnik(final String preparedDnStatement, final List<Object> params) {
            final List<Technik> technikResources = new ArrayList<>();
            List<Map<String, Object>> queryResult = getJdbcTemplate().queryForList(preparedDnStatement, params.toArray());
            if (queryResult != null) {
                for (Map<String, Object> values : queryResult) {
                    Technik technik = new Technik();
                    technik.dn__no = MapTools.getLong(values, "DN__NO");
                    technik.auftrag_id = MapTools.getLong(values, "AUFTRAG_ID");
                    technik.billing_auftrag_no = MapTools.getLong(values, "PRODAK_ORDER__NO");
                    technikResources.add(technik);
                }
            }
            return technikResources;
        }
    }

    private final int bucketSize = 500;

    @Inject
    private VoIPService voIPService;

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    //TODO: enabled = true um die Migration zu aktivieren
    @Test(enabled = false)
    public void doMigration() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Font whiteFont = createWhitFont(workbook);
        Font blackFont = createBlackFont(workbook);
        Sheet sheet = workbook.createSheet("SIP-Login Migration");
        writeHeader(workbook, sheet);

        try {
            final List<MigrationDnBlock> migrationDnBlocks = prepareMigrationData();
            final Map<String, ImsDn> imsDns = prepareImsData();
            forEachDnBlock(workbook, sheet, blackFont, whiteFont, migrationDnBlocks, imsDns);
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
        sheet.createFreezePane( 0, 1, 0, 1 );
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "ONKZ");
        XlsPoiTool.setContent(row, 1, "DNBase");
        XlsPoiTool.setContent(row, 2, "DirectDial");
        XlsPoiTool.setContent(row, 3, "RangeFrom");
        XlsPoiTool.setContent(row, 4, "RangeTo");
        XlsPoiTool.setContent(row, 5, "GueltigVon");
        XlsPoiTool.setContent(row, 6, "GueltigBis");
        XlsPoiTool.setContent(row, 7, "Billing Auftrag");
        XlsPoiTool.setContent(row, 8, "Hurrican Auftrag");
        XlsPoiTool.setContent(row, 9, "DN__NO");
        XlsPoiTool.setContent(row, 10, "SIP-Login");
        XlsPoiTool.setContent(row, 11, "Hauptrufnummer");
        XlsPoiTool.setContent(row, 12, "Status");
        XlsPoiTool.setContent(row, 13, "Stacktrace");

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

    private void autosizeColumns(Sheet sheet) {
        for (Cell cell : sheet.getRow(0)) {
            sheet.autoSizeColumn(cell.getColumnIndex());
        }

    }

    private void writeRow(Workbook workbook, Sheet sheet, Font blackFont, Font whiteFont,
            MigrationDnBlock migrationDnBlock, ImsDn imsDn, String status, String trace) {
        final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, migrationDnBlock.onkz);
        XlsPoiTool.setContent(row, 1, migrationDnBlock.dnBase);
        XlsPoiTool.setContent(row, 2, (migrationDnBlock.directDial != null) ? migrationDnBlock.directDial : "leer");
        XlsPoiTool.setContent(row, 3, (migrationDnBlock.rangeFrom != null) ? migrationDnBlock.rangeFrom : "leer");
        XlsPoiTool.setContent(row, 4, (migrationDnBlock.rangeTo != null) ? migrationDnBlock.rangeTo : "leer");
        XlsPoiTool.setContent(row, 5, df.format(migrationDnBlock.gueltig_von));
        XlsPoiTool.setContent(row, 6, df.format(migrationDnBlock.gueltig_bis));
        XlsPoiTool.setContent(row, 7, migrationDnBlock.billing_auftrag_no.toString());
        XlsPoiTool.setContent(row, 8, migrationDnBlock.auftrag_id.toString());
        XlsPoiTool.setContent(row, 9, migrationDnBlock.dn__no.toString());
        XlsPoiTool.setContent(row, 10, (imsDn != null)? imsDn.shadowSipLogin: "<nich verfuegbar!>");
        XlsPoiTool.setContent(row, 11, (imsDn != null)? imsDn.shadowMainDn: "<nicht verfuegbar!>");
        XlsPoiTool.setContent(row, 12, status);
        XlsPoiTool.setContent(row, 13, trace);

        toggleCellStyle(workbook, blackFont, whiteFont, row);
    }

    private void toggleCellStyle(Workbook workbook, Font blackFont, Font whiteFont, Row row) {
        CellStyle blackStyle = workbook.createCellStyle();
        blackStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        blackStyle.setFont(blackFont);

        CellStyle whiteStyle = workbook.createCellStyle();
        whiteStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        whiteStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        whiteStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        whiteStyle.setFont(whiteFont);

        int i = 0;
        for (Cell cell : row) {
            if (i % 2 == 0) {
                cell.setCellStyle(blackStyle);
            }
            else {
                cell.setCellStyle(whiteStyle);
            }
            i++;
        }
    }

    private void forEachDnBlock(Workbook workbook, Sheet sheet, Font blackFont, Font whiteFont,
            List<MigrationDnBlock> migrationDnBlocks, Map<String, ImsDn> imsDns) {
        for (MigrationDnBlock migrationDnBlock : migrationDnBlocks) {
            String exceptionTrace = null;
            String status = "FEHLER!";
            ImsDn imsDnMatch = findDnBlock(migrationDnBlock, imsDns);
            try {
                if (imsDnMatch != null) {
                    updateVoipDn(migrationDnBlock, imsDnMatch);
                    status = "OK";
                }
                else {
                    AuftragVoIPDN auftragVoIPDN = voIPService.findByAuftragIDDN(migrationDnBlock.auftrag_id, migrationDnBlock.dn__no);
                    if (auftragVoIPDN != null) {
                        VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(new Date());
                        if (plan != null) {
                            voIPService.createVoIPLoginDaten(plan, null, migrationDnBlock.onkz, migrationDnBlock.dnBase,
                                    migrationDnBlock.anfang != null ? null : migrationDnBlock.rangeFrom, auftragVoIPDN.getSipDomain().getStrValue());
                            imsDnMatch = new ImsDn();
                            imsDnMatch.shadowSipLogin = "<" + plan.getSipLogin() + "> (generiert!)";
                            imsDnMatch.shadowMainDn = "<" + plan.getSipHauptrufnummer() + "> (generiert!)";
                            status = "OK";
                        }
                    }
                }
            }
            catch (Exception e) {
                StringBuilder builder = new StringBuilder();
                Throwable cause = e;
                while (cause != null) {
                    if (cause.getMessage() != null) {
                        if (builder.length() > 0) {
                            builder.append("\n");
                        }
                        builder.append(cause.getMessage());
                    }
                    cause = cause.getCause();
                }
                exceptionTrace = builder.toString();
                LOGGER.error(String.format("Update DN Block %s %s fehlgeschlagen.\n%s", migrationDnBlock.onkz,
                        migrationDnBlock.dnBase, exceptionTrace), e);
            }
            writeRow(workbook, sheet, blackFont, whiteFont, migrationDnBlock, imsDnMatch, status, exceptionTrace);
        }
    }

    private void updateVoipDn(MigrationDnBlock migrationDnBlock, ImsDn imsDnMatch) throws FindException,
            StoreException {
        AuftragVoIPDN auftragVoIPDN = voIPService.findByAuftragIDDN(migrationDnBlock.auftrag_id,
                migrationDnBlock.dn__no);

        VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(new Date());
        if (plan != null) {
            if (StringUtils.isNotBlank(plan.getSipLogin())) {
                imsDnMatch.shadowSipLogin = "<" + plan.getSipLogin() + "> (Bestand!)";
            }
            else {
                plan.setSipLogin(imsDnMatch.sipLogin);
                imsDnMatch.shadowSipLogin = imsDnMatch.sipLogin;
            }
            if (StringUtils.isNotBlank(plan.getSipHauptrufnummer())) {
                imsDnMatch.shadowMainDn = "<" + plan.getSipHauptrufnummer() + "> (Bestand!)";
            }
            else {
                plan.setSipHauptrufnummer(imsDnMatch.mainDn);
                imsDnMatch.shadowMainDn = imsDnMatch.mainDn;
            }
        }
        voIPService.saveAuftragVoIPDN(auftragVoIPDN);
    }

    private ImsDn findDnBlock(MigrationDnBlock migrationDnBlock, Map<String, ImsDn> imsDns) {
        for (String identifier : imsDns.keySet()) {
            String directDial = (migrationDnBlock.anfang != null)? migrationDnBlock.anfang : migrationDnBlock.directDial;
            if (identifier.startsWith(migrationDnBlock.onkz + migrationDnBlock.dnBase + directDial)) {
                ImsDn imsDn = imsDns.get(identifier);
                if (imsDn.sipLogin == null || !imsDn.sipLogin.contains("@maxi")) {
                    return imsDn;
                }
            }
        }
        return null;
    }

    private Map<String, ImsDn> prepareImsData() throws IOException {
        final Pattern identifier = Pattern.compile("<identifier>49([0-9]+)</identifier>");
        final Pattern mainDn = Pattern.compile("<privateUserId>(\\+[0-9]*)</privateUserId>");
        final Pattern login = Pattern.compile("<privateUserId>(\\+[0-9]*@[^0-9]*)</privateUserId>");

        final Map<String, ImsDn> imsDns = new HashMap<>();
        ImsDn imsDn = null;

        try(BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("user.home")
                + "\\Hurrican", "IMS-Rufnummernliste.txt")))) {
            String line = reader.readLine();
            while (line != null) {
                Matcher identifierMatcher = identifier.matcher(line);
                Matcher mainDnMatcher = mainDn.matcher(line);
                Matcher loginMatcher = login.matcher(line);
                if (identifierMatcher.matches()) {
                    if (imsDn != null) {
                        imsDns.put(imsDn.id, imsDn);
                    }
                    imsDn = new ImsDn();
                    imsDn.id = "0" + identifierMatcher.group(1);
                }
                else if (mainDnMatcher.matches()) {
                    if (imsDn == null)
                        throw new IllegalStateException("Hauptrufnummer ohne <identifier>!");
                    imsDn.mainDn = mainDnMatcher.group(1);
                }
                else if (loginMatcher.matches()) {
                    if (imsDn == null)
                        throw new IllegalStateException("SIP-Login ohne <identifier>!");
                    imsDn.sipLogin = loginMatcher.group(1);
                }
                line = reader.readLine();
            }
            if (imsDn != null) {
                imsDns.put(imsDn.id, imsDn);
            }
        }
        return imsDns;
    }

    private List<MigrationDnBlock> prepareMigrationData() {
        final JdbcRufnummerDao jdbcRufnummerDao = new JdbcRufnummerDao();
        jdbcRufnummerDao.setDataSource(SessionFactoryUtils.getDataSource(sessionFactory));

        final JdbcTechnikDao jdbcTechnikDao = new JdbcTechnikDao();
        jdbcTechnikDao.setDataSource(SessionFactoryUtils.getDataSource(sessionFactory));

        List<DnBlock> dnBlocks = jdbcRufnummerDao.getDnBlocks(prepareDnBlocksStatement());
        final List<List<DnBlock>> buckets = new ArrayList<>();
        for (int i = 0; i < dnBlocks.size(); i = i + bucketSize) {
            buckets.add(dnBlocks.subList(i, (dnBlocks.size() < i + bucketSize) ? dnBlocks.size(): i + bucketSize));
        }

        final List<MigrationDnBlock> migrationDnBlocks = new ArrayList<>();
        final String technikStatement = prepareTechnikStatement();
        int index = 0;
        for (List<DnBlock> bucket : buckets) {
            LOGGER.info(String.format("Processing bucket [index=%s]", index++));
            final List<Object> preparedTechnikParams = prepareTechnikParams(bucket, bucketSize);
            List<Technik> technikResources = jdbcTechnikDao.getTechnik(technikStatement, preparedTechnikParams);
            convergeBucketsWithTechnik(migrationDnBlocks, bucket, technikResources);
        }
        return migrationDnBlocks;
    }

    private void convergeBucketsWithTechnik(final List<MigrationDnBlock> migrationDnBlocks, final List<DnBlock> bucket,
            final List<Technik> technikResources) {
        for (DnBlock dnBlock : bucket) {
            Technik toRemove = null;
            for (Technik technik : technikResources) {
                if (dnBlock.dn__no.equals(technik.dn__no)
                    && dnBlock.order__no.equals(technik.billing_auftrag_no)) {
                    AuftragVoIPDN auftragVoIPDN = null;
                    try { auftragVoIPDN = voIPService.findByAuftragIDDN(technik.auftrag_id, technik.dn__no); }
                    catch (FindException e) { /* nothing to do */ }

                    MigrationDnBlock imsBlockDn = new MigrationDnBlock();
                    imsBlockDn.dn__no = technik.dn__no;
                    imsBlockDn.gueltig_von = dnBlock.validFrom;
                    imsBlockDn.gueltig_bis = dnBlock.validTo;
                    imsBlockDn.auftrag_id = technik.auftrag_id;
                    imsBlockDn.billing_auftrag_no = technik.billing_auftrag_no;
                    imsBlockDn.onkz = dnBlock.onkz;
                    imsBlockDn.dnBase = dnBlock.dnBase;
                    imsBlockDn.directDial = dnBlock.directDial;
                    imsBlockDn.rangeFrom = dnBlock.rangeFrom;
                    imsBlockDn.rangeTo = dnBlock.rangeTo;
                    if (auftragVoIPDN != null) {
                        VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(new Date());
                        if (plan != null) {
                            for (VoipDnBlock block : plan.getVoipDnBlocks()) {
                                if (block.getZentrale()) {
                                    imsBlockDn.anfang = block.getAnfang();
                                    break;
                                }
                            }
                        }
                    }
                    migrationDnBlocks.add(imsBlockDn);
                    LOGGER.info(imsBlockDn.toString());
                    toRemove = technik;
                    break;
                }
            }
            if (toRemove != null) {
                technikResources.remove(toRemove);
            }
        }
    }

    private List<Object> prepareTechnikParams(List<DnBlock> bucket, int bucketSize) {
        final ArrayList<Object> params = new ArrayList<>();
        for (DnBlock dnBlock : bucket) {
            params.add(dnBlock.dn__no);
            --bucketSize;
        }
        for (; bucketSize > 0; bucketSize--) {
            params.add(null);
        }
        return params;
    }

    private String prepareDnBlocksStatement() {
        return "SELECT d.DN__NO, d.VALID_FROM, d.VALID_TO, d.ORDER__NO, d.RANGE_FROM, d.RANGE_TO, d.ONKZ, d.DN_BASE, "
                + " d.DIRECT_DIAL FROM DN d "
                + " WHERE SYSDATE between d.VALID_FROM and d.VALID_TO " + " and RANGE_FROM is not null "
                + " AND ORDER__NO IS NOT NULL ";
    }

    private String prepareTechnikStatement() {
        StringBuilder sql = new StringBuilder();
                sql.append("select avdn.dn__no, ad.prodak_order__no, ad.auftrag_id from T_AUFTRAG_VOIP_DN avdn, \n");
                sql.append("  t_auftrag_technik at, t_auftrag_daten ad, t_endstelle es, t_rangierung r, t_equipment e \n");
                sql.append("  where avdn.auftrag_id = at.auftrag_id \n");
                sql.append("  and at.gueltig_bis = TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS') \n");
                sql.append("  and at.auftrag_id = ad.auftrag_id \n");
                sql.append("  and ad.gueltig_bis = TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS') \n");
                sql.append("  and ad.status_id not in (3400, 1150) \n");
                sql.append("  and ad.status_id < 9800 \n");
                sql.append("  and at.AT_2_ES_ID = es.ES_GRUPPE \n");
                sql.append("  and ((es.rangier_id_additional is not null and es.rangier_id_additional = r.rangier_id \n");
                sql.append("  and r.eq_in_id is not null and e.eq_id = r.eq_in_id and e.switch = 'MUC06') \n");
                sql.append("  or (es.rangier_id is not null and es.rangier_id = r.rangier_id \n");
                sql.append("  and r.eq_in_id is not null and e.eq_id = r.eq_in_id and e.switch = 'MUC06')) \n");
                sql.append("  and avdn.dn__no in (");
        for (int i = 1; i <= bucketSize; i++) {
            if (i < bucketSize) sql.append("?,"); else sql.append("?");
        }
        sql.append(')');
        return sql.toString();
    }
}
