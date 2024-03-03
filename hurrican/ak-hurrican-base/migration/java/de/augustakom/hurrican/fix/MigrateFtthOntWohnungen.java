package de.augustakom.hurrican.fix;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.Test;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;

/**
 * Fuer existierende ONTs aus Command Excel Liste die Wohnungsbezeichnung ermitteln, und falls vorhanden in die
 * zugeordnete HVT_Wohnung eintragen.
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul> IntelliJ VM Options:
 * -ea -Duse.config=production -Dtest.user=reinerjue -Dtest.password=********
 */

// TODO: nur lokal bei Bedarf einschalten
//@Test(groups = AbstractTransactionalServiceTest.NO_ROLLBACK_TEST)
//@Test
public class MigrateFtthOntWohnungen extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(MigrateFtthOntWohnungen.class);

    //TODO: Vollstaendigen Importpfad konfigurieren!
    private static final String IMPORT_FILE = "C:\\Temp\\ANFs\\ANF-219\\ONT Wohnungen\\Command ONTs mit Wohnung.xls";

    private static final int ONT_ID_COLUMN = 4; // 0 basiert!
    private static final int RAUM_COLUMN = 3; // 0 basiert!

    @SuppressWarnings("JpaQlInspection")
    final String hql = "select hvtRaum from HVTRaum hvtRaum \n"
            + " where hvtRaum.raum LIKE '%Migration: Raumbzeichnung nicht bekannt>' \n";

    @Inject
    private HWService hwService;

    @Inject
    private HVTService hvtService;

    @Resource(name = "cc.hibernateTxManager")
    private PlatformTransactionManager tm;

    @Inject
    private AuftragTechnikDAO auftragTechnikDao;

    @Test(enabled = false)
    public void doMigration() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheetSuccess = workbook.createSheet("Erfolgreich");
        Sheet sheetErrors = workbook.createSheet("Fehler!");
        Sheet sheetMissing = workbook.createSheet("Offen!");
        writeHeaderSuccess(sheetSuccess);
        writeHeaderErrors(sheetErrors);
        writeHeaderMissing(sheetMissing);

        try {
            File importFile = new File(IMPORT_FILE);
            forEachRow(loadExcelFile(readData(importFile), 0), sheetSuccess, sheetErrors);
            checkMissing(sheetMissing);
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage(), e);
        }
        finally {
            File xlsFile = new File(System.getProperty("user.home"), getClass().getSimpleName()
                    + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
            workbook.write(new FileOutputStream(xlsFile));
            LOGGER.info("XLS File=" + xlsFile.getPath());
        }
    }

    @SuppressWarnings("unchecked")
    private void checkMissing(Sheet sheetMissing) {
        List<HVTRaum> missingApartments = ((Hibernate4DAOImpl) auftragTechnikDao).find(hql);
        if (missingApartments != null && !missingApartments.isEmpty()) {
            for (HVTRaum missingApartment : missingApartments) {
                writeRowMissing(sheetMissing, missingApartment.getId(), missingApartment.getRaum());
            }
        }

    }

    protected void writeHeaderSuccess(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "ONT-ID");
        XlsPoiTool.setContent(row, 1, "Wohnung");
    }

    protected void writeHeaderErrors(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "Zeile");
        XlsPoiTool.setContent(row, 1, "ONT-ID");
        XlsPoiTool.setContent(row, 2, "Wohnung");
        XlsPoiTool.setContent(row, 3, "Fehlerbeschreibung");
    }

    protected void writeHeaderMissing(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "HVT-Raum-ID");
        XlsPoiTool.setContent(row, 1, "Wohnung");
    }

    private void writeRowSuccess(Sheet sheet, final String ontId, final String wohnung) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, ontId);
        XlsPoiTool.setContent(row, 1, wohnung);
    }

    private void writeRowErrors(Sheet sheet, int rowIndex, final String ontId, final String wohnung,
            final String message) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, Integer.valueOf(rowIndex + 1).toString());
        XlsPoiTool.setContent(row, 1, (ontId != null) ? ontId : "NULL");
        XlsPoiTool.setContent(row, 2, (wohnung != null) ? wohnung : "NULL");
        XlsPoiTool.setContent(row, 3, message);
    }

    private void writeRowMissing(Sheet sheet, final Long id, final String wohnung) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, id.toString());
        XlsPoiTool.setContent(row, 1, wohnung);
    }

    private void forEachRow(Sheet sheetIn, Sheet sheetSuccess, Sheet sheetErrors) {
        int maxRows = sheetIn.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= maxRows; rowIndex++) {
            Row row = sheetIn.getRow(rowIndex);
            if (XlsPoiTool.isEmpty(row, 0)) {
                // skip empty rows ...
                continue;
            }
            String ontId = XlsPoiTool.getContentAsString(row, ONT_ID_COLUMN);
            String wohnung = XlsPoiTool.getContentAsString(row, RAUM_COLUMN);
            try {
                processRowTransactional(ontId, wohnung);
                writeRowSuccess(sheetSuccess, ontId, wohnung);
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
                LOGGER.error(String.format("Fehler in Zeile %d! Grund: %s", rowIndex, builder.toString()), e);
                writeRowErrors(sheetErrors, rowIndex, ontId, wohnung, builder.toString());
            }
        }
    }

    private void processRowTransactional(final String ontId, final String wohnung) {
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    processRow(ontId, wohnung);
                }
                catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void processRow(final String ontId, final String wohnung)
            throws FindException, StoreException, ValidationException {
        if (StringUtils.isBlank(wohnung)) {
            throw new FindException("Wohnung fehlt, Aktualisierung abgebrochen!");
        }
        Pair<HWRack, HVTRaum> hwPair = findHvtRaum4OntId(ontId);
        HVTRaum hvtRaum = hwPair.getSecond();
        HWRack hwOnt = hwPair.getFirst();
        if (hwOnt != null) {
            boolean createHvtRaum = false;
            if (hvtRaum == null) {
                createHvtRaum = true;
                hvtRaum = new HVTRaum();
                hvtRaum.setHvtIdStandort(hwOnt.getHvtIdStandort());
            }
            else {
                if (!String.format("<%s Migration: Raumbzeichnung nicht bekannt>", ontId).equals(hvtRaum.getRaum())) {
                    throw new FindException(String.format("'%s' wird nicht ueberschrieben!", hvtRaum.getRaum()));
                }
            }
            hvtRaum.setRaum(wohnung);
            HVTRaum hvtRaumSaved = hvtService.saveHVTRaum(hvtRaum);
            if (createHvtRaum) {
                hwOnt.setHvtRaumId(hvtRaumSaved.getId());
                hwService.saveHWRack(hwOnt);
            }
        }
    }

    private Pair<HWRack, HVTRaum> findHvtRaum4OntId(String ontId) throws FindException {
        if (StringUtils.isBlank(ontId)) {
            throw new FindException("ONT-ID ist nicht gesetzt!");
        }
        HWRack hwOnt = hwService.findRackByBezeichnung(ontId);
        if (hwOnt == null) {
            throw new FindException("Keine ONT in Hurrican verfuegbar!");
        }
        if (hwOnt.getHvtRaumId() == null) {
            return new Pair<>(hwOnt, null);
        }
        HVTRaum hvtRaum = hvtService.findHVTRaum(hwOnt.getHvtRaumId());
        return new Pair<>(hwOnt, hvtRaum);
    }

    private Sheet loadExcelFile(byte[] xlsData, int sheetNumber) throws ImportException {
        try {
            return XlsPoiTool.loadExcelFile(xlsData, sheetNumber);
        }
        catch (Exception e) {
            throw new ImportException(String.format("Fehler beim Laden der XLS Daten! Grund: %s", e.getMessage()), e);
        }
    }

    private byte[] readData(File importFile) throws ImportException {
        try {
            return FileTools.convertToByteArray(importFile);
        }
        catch (Exception e) {
            throw new ImportException(String.format("Fehler beim Lesen der Importdatei! Grund: %s", e.getMessage()), e);
        }
    }
}
