package de.augustakom.hurrican.fix;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.testng.annotations.Test;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.DNLeistungDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HWSwitchService;

/**
 * Fuer IMS Telefonanlagen ändert sich die Formatierung der kundenindividuellen Sperren neues Format: Lfdnr[2]-CC-LAC-DN
 * Beispiele:
 * <pre>
 * - 01-0 bzw. 01-0049 (nur CC)
 * - 01-0049-89 (CC und LAC)
 * - 01-0049-89-123456 (alles)
 * </pre>
 * Für IMS Anlagen müssen deshalb die bestehenden Werte der Spalte LEISTUNG_PARAMETER in der Tabelle T_LEISTUNG_DN in
 * das neue Format migriert werden.
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul> IntelliJ VM Options:
 * produktiv: -ea -Duse.config=production -Doverride.prod.check=true -Dtest.user=migration
 * -Dtest.password=m1gr@ti0n4devel devel: -ea -Duse.config=user.koegelma -Doverride.prod.check=true
 * -Dtest.user=migration -Dtest.password=m1gr@ti0n4devel
 */

@Test
public class KundenindivSperren extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(de.augustakom.hurrican.fix.KundenindivSperren.class);
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    @SuppressWarnings("JpaQlInspection")
    String hql =
            "select leist from de.augustakom.hurrican.model.cc.dn.Leistung2DN leist where leist.leistung4DnId >= '31' and leist.leistung4DnId <= '34' and leist.leistungParameter is not null " +
                    "and (leist.scvKuendigung >= sysdate or leist.scvKuendigung is null)";
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWSwitchService")
    private HWSwitchService switchService;
    @Inject
    private DNLeistungDAO dnLeistungDao;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;

    //TODO: enabled = true um die Ausführung zu aktivieren
    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void doMigration() throws IOException {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheetSuccess = workbook.createSheet("Migration_KundenIndivSperren");
        Sheet sheetError = workbook.createSheet("Fehlerausgabe");
        writeHeaderSuccess(sheetSuccess);
        writeHeaderError(sheetError);

        try {
            final List<Leistung2DN> leistungList = getLeistungList();
            for (Leistung2DN leistung : leistungList) {
                final Rufnummer billingRufnummer = getBillingRufnummer(leistung.getDnNo());
                if (billingRufnummer == null) {
                    writeRowError(sheetError, leistung.getLeistungParameter(), null, null, "DN_NO " + leistung.getDnNo() + ", LFDNR " + leistung.getId() + " keine Billing Rufnummer vorhanden");
                    continue;
                }
                final List<CCAuftragIDsView> ccAuftragIDsViews = getAuftraege4AuftragNoOrig(billingRufnummer.getAuftragNoOrig());
                if (ccAuftragIDsViews == null) {
                    writeRowError(sheetError, leistung.getLeistungParameter(), null, null, "DN_NO " + leistung.getDnNo() + ", LFDNR " + leistung.getId() +
                            " und AuftragNoOrig " + billingRufnummer.getAuftragNoOrig() + " keine Hurrican-Aufträge vorhanden");
                    continue;
                }
                for (CCAuftragIDsView ccAuftragIDView : ccAuftragIDsViews) {
                    if (AuftragStatus.AUFTRAG_GEKUENDIGT >= ccAuftragIDView.getAuftragStatusId()) {
                        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(ccAuftragIDView.getAuftragId());
                        if (auftragDaten.getKuendigung() != null && (auftragDaten.getKuendigung().compareTo(new Date()) == -1)) {
                            continue;
                        }
                    }
                    final HWSwitch hwSwitch = ccAuftragService.getSwitchKennung4Auftrag(ccAuftragIDView.getAuftragId());
                    if (hwSwitch == null) {
                        continue;
                    }
                    if (HWSwitchType.isImsOrNsp(hwSwitch.getType())) {
                        prepareAndWriteNewParameter(sheetSuccess, leistung, ccAuftragIDView);
                        //break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage(), e);
        }
        finally {
            File dir = new File(System.getProperty("user.home") + File.separator + "Hurrican");
            Files.createDirectories(dir.toPath());
            File xlsFile = new File(dir, getClass().getSimpleName()
                    + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
            workbook.write(new FileOutputStream(xlsFile));
            LOGGER.info("XLS File=" + xlsFile.getPath());
        }
    }

    private void prepareAndWriteNewParameter(Sheet sheetSuccess, Leistung2DN leistung, CCAuftragIDsView ccAuftragIDView) {
        String sheetNewParameter;
        String sheetSqlStatement;

        sheetNewParameter = parseLeistungParameter(leistung.getLeistungParameter());
        if (sheetNewParameter != null) {
            sheetSqlStatement = getUpdateStatement(sheetNewParameter, leistung.getId().toString(), leistung.getDnNo().toString());
        }
        else {
            sheetNewParameter = sheetSqlStatement = "";
        }

        String sperrenArt = getSperrenArt(leistung);

        String auftragsInfo = "Auftrag-ID: " + ccAuftragIDView.getAuftragId() +
                ", AuftragNoOrig: " + ccAuftragIDView.getAuftragNoOrig() +
                ", Auftrag-Status: " + ccAuftragIDView.getAuftragStatusText() +
                ", DN_NO: " + leistung.getDnNo() +
                ", T_Leistung_Dn_Lfdnr: " + leistung.getId().toString() +
                ", Art der Sperre: " + sperrenArt;

        writeRowSuccess(sheetSuccess, leistung.getLeistungParameter(), sheetNewParameter, sheetSqlStatement, auftragsInfo);
    }

    private String getSperrenArt(Leistung2DN leistung) {
        String sperrenArt;
        switch (leistung.getLeistung4DnId().toString()) {
            case "31":
                sperrenArt = "Sperre individuell; abgehend erlaubt (Whitelist)";
                break;
            case "32":
                sperrenArt = "Sperre individuell; abgehend gesperrt (Blacklist)";
                break;
            case "33":
                sperrenArt = "Sperre individuell; kommend erlaubt (Whitelist)";
                break;
            case "34":
                sperrenArt = "Sperre individuell; kommend gesperrt (Blacklist)";
                break;
            default:
                sperrenArt = "";
                break;
        }
        return sperrenArt;
    }

    private List<CCAuftragIDsView> getAuftraege4AuftragNoOrig(Long auftragNoOrig) {
        try {
            return (ccAuftragService.findAufragIdAndVbz4AuftragNoOrig(auftragNoOrig));
        }
        catch (FindException e) {
            LOGGER.info("zu AuftragNoOrig = " + auftragNoOrig + "wurde keine AuftragID gefunden");
        }
        return null;

    }

    private Rufnummer getBillingRufnummer(Long dnNo) {

        try {
            return (rufnummerService.findDN(dnNo));
        }
        catch (FindException e) {
            LOGGER.info("zu dnNo = " + dnNo + "wurde in Billing keine Rufnummer gefunden");
        }
        return null;

    }

    private String parseLeistungParameter(String sheetOldParameter) {

        String singleNumberList[] = sheetOldParameter.split("&");
        StringBuilder sheetNewParameter = new StringBuilder();
        for (String number : singleNumberList) {
            String lfdnr = number.substring(0, 2);
            String formatedNumber = parseNumber(number.substring(3));
            if (formatedNumber == null) {
                return null;
            }
            if (sheetNewParameter.toString().isEmpty()) {
                sheetNewParameter.append(lfdnr + "-" + formatedNumber);
            }
            else {
                sheetNewParameter.append("&" + lfdnr + "-" + formatedNumber);
            }
        }
        return sheetNewParameter.toString();
    }

    protected void writeHeaderSuccess(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "alte Parameter");
        XlsPoiTool.setContent(row, 1, "neue Parameter");
        XlsPoiTool.setContent(row, 2, "SQL Update Statement");
        XlsPoiTool.setContent(row, 3, "Auftragsinfo");
    }

    protected void writeHeaderError(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "alte Parameter");
        XlsPoiTool.setContent(row, 1, "neue Parameter");
        XlsPoiTool.setContent(row, 2, "SQL Update Statement");
        XlsPoiTool.setContent(row, 3, "Fehlerbeschreibung");
    }

    private void writeRowSuccess(Sheet sheet, final String oldParameter, final String newParameter, final String sqlUpdStatement, final String auftragsInfo) {

        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, oldParameter);
        XlsPoiTool.setContent(row, 1, newParameter);
        XlsPoiTool.setContent(row, 2, sqlUpdStatement);
        XlsPoiTool.setContent(row, 3, auftragsInfo);
    }

    private void writeRowError(Sheet sheet, final String oldParameter, final String newParameter, final String sqlUpdStatement, final String errorDescription) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, oldParameter);
        XlsPoiTool.setContent(row, 1, newParameter);
        XlsPoiTool.setContent(row, 2, sqlUpdStatement);
        XlsPoiTool.setContent(row, 3, errorDescription);
    }

    private List<Leistung2DN> getLeistungList() {
        return ((Hibernate4DAOImpl) dnLeistungDao).find(hql);
    }

    private String parseNumber(String number) {
        String numberToParse = number;
        String internationalFormat;
        boolean hasPrefix = number.endsWith("*");

        try {
            if (hasPrefix) {
                numberToParse = StringUtils.stripToEmpty(numberToParse.replace("*", ""));
            }
            if (!numberToParse.matches("\\d+")) {
                return null;
            }

            if ("00".equals(numberToParse.substring(0, 2)) && (numberToParse.length() == 4 || numberToParse.length() == 5)) {
                internationalFormat = numberToParse.substring(2, numberToParse.length());
            }
            else {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(numberToParse, "DE");
                internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).substring(1);
                internationalFormat = internationalFormat.replaceFirst(" ", "-").replaceFirst(" ", "-").replaceFirst(" ", "-").replaceAll(" ", "");
            }

            return internationalFormat;
        }
        catch (NumberParseException e) {
            return null;
        }
    }

    private String getUpdateStatement(String newParameter, String lfdnr, String dn_no) {
        String parameter = newParameter.replaceAll("&", "'||chr(38)||'");
        return "update T_Leistung_DN set Leistung_parameter = '" + parameter + "' where lfdnr = '" + lfdnr + "' and" +
                " dn_no = '" + dn_no + "';";
    }
}
