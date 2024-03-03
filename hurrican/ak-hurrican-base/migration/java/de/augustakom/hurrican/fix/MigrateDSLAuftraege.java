package de.augustakom.hurrican.fix;

import java.io.*;
import java.util.*;
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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.HWSwitchService;
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
public class MigrateDSLAuftraege extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(MigrateDSLAuftraege.class);

    private static final Long DSL_VOIP = 480L;


    @Inject
    private CCAuftragService ccAuftragService;

    @Inject
    private VoIPService voipService;

    @Inject
    private HWSwitchService hwSwitchService;

    @Inject
    private BAService baService;

    //TODO: enabled = true um die Migration zu aktivieren
    @Test(enabled = false)
    public void doMigration() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Temp\\Hurrican\\SQL\\Migration DSL VoIP Desaster"
                + "\\Auftraege_Migration-Teil4.xlsx");
        XSSFWorkbook workbookIn = new XSSFWorkbook(fileInputStream);
        XSSFSheet worksheetIn = workbookIn.getSheet("Tabelle1");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("DSL Migration");
        writeHeader(workbook, sheet);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        try {
            Long techAuftragId;
            int rowIndex = 1;
            do {
                Row row = worksheetIn.getRow(rowIndex);
                techAuftragId = null;
                if (row != null) {
                    Cell cellTechAuftragId = row.getCell(2);
                    if (cellTechAuftragId != null && cellTechAuftragId.getCellType() != Cell.CELL_TYPE_BLANK) {
                        try {
                            techAuftragId = Double.valueOf(cellTechAuftragId.getNumericCellValue()).longValue();
                        }
                        catch (Exception e) {
                            LOGGER.error(String.format("Fuer die Zeile %s konnte keine technische Auftrags ID ermittelt "
                                    + "werden!", rowIndex + 1, cellTechAuftragId.getStringCellValue()));
                        }
                        Pair<AKWarning, AKWarnings> result = each(techAuftragId);
                        writeRow(sheet, style, techAuftragId, result.getSecond(), result.getFirst());
                    }
                }
                rowIndex++;
            } while (techAuftragId != null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            fileInputStream.close();
            autosizeColumns(sheet);
            File xlsFile = new File(System.getProperty("user.home") + "\\Hurrican", getClass().getSimpleName()
                    + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
            workbook.write(new FileOutputStream(xlsFile));
            LOGGER.info("XLS File=" + xlsFile.getPath());
        }
    }

    private void writeRow(Sheet sheet, CellStyle style, Long techAuftragId, AKWarnings warnings, AKWarning error) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.setRowStyle(style);
        XlsPoiTool.setContent(row, 0, techAuftragId.toString());
        XlsPoiTool.setContent(row, 1, warnings.getWarningsAsText());
        XlsPoiTool.setContent(row, 2, (error != null) ? error.getWarning() : "");
        row.getCell(1).setCellStyle(style);
        row.getCell(2).setCellStyle(style);
    }

    private void autosizeColumns(Sheet sheet) {
        for (Cell cell : sheet.getRow(0)) {
            sheet.autoSizeColumn(cell.getColumnIndex());
        }
    }

    private void writeHeader(Workbook workbook, Sheet sheet) {
        sheet.createFreezePane( 0, 1, 0, 1 );
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "techn. Auftrag ID");
        XlsPoiTool.setContent(row, 1, "Warnungen");
        XlsPoiTool.setContent(row, 2, "Fehler");

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

    private Pair<AKWarning, AKWarnings> each(Long techAuftragId) {
        AKWarnings warnings = new AKWarnings();
        if (techAuftragId != null) {
            try {
                if (auftragAktiv(warnings, techAuftragId)) {
                    createAuftragVoIP(warnings, techAuftragId);
                    updateHwSwitch(warnings, techAuftragId);
                    updateProdukt(warnings, techAuftragId);
                    processBauauftrag(warnings, techAuftragId);
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
                String exceptionTrace = builder.toString();
                LOGGER.error(e.getMessage(), e);
                return Pair.create(new AKWarning(null, String.format("Migration des techn. Auftrages %s ist fehlgeschlagen! \n\r"
                        + "%s", techAuftragId, exceptionTrace)), warnings);
            }
        }
        return Pair.create(null, warnings);
    }

    private boolean auftragAktiv(AKWarnings warnings, Long techAuftragId) throws FindException {
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(techAuftragId);
        if (!auftragDaten.isAuftragActive()) {
            warnings.addAKWarning(new AKWarning(null, "Der Auftrag ist nicht aktiv!"));
            return false;
        }
        return true;
    }

    private void processBauauftrag(AKWarnings warnings, Long techAuftragId) throws FindException, StoreException {
        Verlauf aktiverBauauftrag = baService.findActVerlauf4Auftrag(techAuftragId, false);
        if (aktiverBauauftrag != null) {
            boolean sendMail = false;
            if (DateTools.isTodayOrNextWorkDay(aktiverBauauftrag.getRealisierungstermin())) {
                sendMail = true;
            }
            AKWarnings warningsStorno = baService.verlaufStornieren(aktiverBauauftrag.getId(), sendMail, getSessionId());
            if ((warningsStorno != null) && warningsStorno.isNotEmpty()) {
                warnings.addAKWarnings(warningsStorno);
                LOGGER.info(String.format("Der Storno des aktiven Bauauftrages hat folgende Warnungen erzeugt: %s!",
                        warnings.getWarningsAsText()));
            }
            Pair<Verlauf, AKWarnings> result = baService.createVerlauf(new CreateVerlaufParameter(
                    techAuftragId,
                    aktiverBauauftrag.getRealisierungstermin(),
                    aktiverBauauftrag.getAnlass(),
                    aktiverBauauftrag.getInstallationRefId(),
                    false,
                    getSessionId(),
                    aktiverBauauftrag.getSubAuftragsIds()));
            if (result == null || result.getFirst() == null) {
                throw new StoreException("Anlage des neuen Bauauftrages ist fehlgeschlagen!");
            }
            if (result != null && result.getSecond()!= null && result.getSecond().isNotEmpty()) {
                warnings.addAKWarnings(result.getSecond());
                LOGGER.info(String.format("Der Storno des aktiven Bauauftrages hat folgende Warnungen erzeugt: %s!",
                        result.getSecond().getWarningsAsText()));
            }
        }
        else {
            warnings.addAKWarning(new AKWarning(null, "Kein Bauauftrag erstellt!"));
            List<Verlauf> bauauftraege = baService.findVerlaeufe4Auftrag(techAuftragId);
            if (bauauftraege != null && !bauauftraege.isEmpty()) {
                for (Verlauf bauauftrag : bauauftraege) {
                    if (VerlaufStatus.VERLAUF_ABGESCHLOSSEN.equals(bauauftrag.getVerlaufStatusId())
                            || VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN.equals(bauauftrag.getVerlaufStatusId())) {
                        String message = String.format("Der technische Auftrag %s hat bereits abgeschlossene Bauauftraege!",
                                techAuftragId);
                        warnings.addAKWarning(new AKWarning(null, message));
                        LOGGER.info(message);
                    }
                }
            }

        }
    }

    private void updateProdukt(AKWarnings warnings, Long techAuftragId) throws FindException, StoreException {
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(techAuftragId);
        if (!DSL_VOIP.equals(auftragDaten.getProdId())) {
            auftragDaten.setProdId(DSL_VOIP);
            ccAuftragService.saveAuftragDaten(auftragDaten, false);
        }
        else {
            warnings.addAKWarning(new AKWarning(null, "Das Produkt ist bereits 'DSL VoIP'!"));
        }
    }

    private void updateHwSwitch(AKWarnings warnings, Long techAuftragId) throws FindException, StoreException {
        AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(techAuftragId);
        if (auftragTechnik.getHwSwitch() == null) {
            auftragTechnik.setHwSwitch(hwSwitchService.findSwitchByName("MUC06"));
            ccAuftragService.saveAuftragTechnik(auftragTechnik, false);
        }
        else {
            warnings.addAKWarning(new AKWarning(null, "Der Switch ist bereits gesetzt!"));
        }
    }

    private void createAuftragVoIP(AKWarnings warnings, Long techAuftragId) throws StoreException, FindException {
        AuftragVoIP auftragVoIP = voipService.findVoIP4Auftrag(techAuftragId);
        if (auftragVoIP == null) {
            auftragVoIP = new AuftragVoIP();
            auftragVoIP.setAuftragId(techAuftragId);
            auftragVoIP.setIsActive(Boolean.TRUE);
            auftragVoIP.setUserW("migration");
            auftragVoIP.setGueltigVon(new Date());
            auftragVoIP.setGueltigBis(DateTools.getHurricanEndDate());
            voipService.saveAuftragVoIP(auftragVoIP, false, getSessionId());
        }
        else {
            warnings.addAKWarning(new AKWarning(null, "Die AuftragVoIP Entitaet exisitiert bereits!"));
        }
    }
}
