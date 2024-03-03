package de.augustakom.hurrican.fix;

import java.io.*;
import java.util.*;
import javax.inject.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.testng.annotations.Test;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * Excel Liste fuer alle existierenden ONTs in Hurrican
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link de.augustakom.common.AbstractTransactionalServiceTest#NO_ROLLBACK_TEST}
 * für den Test setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über
 * die System Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul> IntelliJ VM Options:
 * -ea -Duse.config=production -Dtest.user=reinerjue -Dtest.password=********
 */
public class FtthOntQuery4Command extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(FtthOntQuery4Command.class);

    @Inject
    private AuftragTechnikDAO auftragTechnikDao;

    @SuppressWarnings("JpaQlInspection")
    final String hql = "select hwOnt, hwBg, eq from HWOnt hwOnt, HWBaugruppe hwBg, " +
            "Equipment eq \n"
            + " where \n"
            + " hwOnt.id = hwBg.rackId \n"
            + " and hwBg.id = eq.hwBaugruppenId \n"
            + " order by hwOnt.geraeteBez, eq.hwEQN asc \n";

    private enum EntityIndex {
        HwOnt(0),
        HwBg(1),
        Eq(2);

        private int index;

        EntityIndex(int index) {
            this.index = index;
        }

        @SuppressWarnings("unchecked")
        public <T> T getEntity(Object[] entities) {
            if (entities != null && entities.length > index) {
                return (T) entities[index];
            }
            return null;
        }
    }

    @Test(enabled = false)
    public void doQuery() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("alle Hurrican ONTs");
        writeHeader(sheet);

        try {
            final List<Object[]> queryResults = processQuery();
            forEachRow(queryResults, sheet);
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

    private void forEachRow(List<Object[]> queryResults, Sheet sheet) {
        if (queryResults != null && !queryResults.isEmpty()) {
            for (Object[] queryResult : queryResults) {
                final HWOnt hwOnt = EntityIndex.HwOnt.getEntity(queryResult);
                //final HWBaugruppe hwBaugruppe = EntityIndex.HwBg.getEntity(queryResult);
                final Equipment equipment = EntityIndex.Eq.getEntity(queryResult);
                final String source = "hurrican-ws".equals(equipment.getUserW()) ? "Command" : "Migration";
                writeRow(sheet, hwOnt.getGeraeteBez(), equipment.getHwEQN(), source, hwOnt.getFreigabe());
            }
        }
    }

    protected void writeHeader(Sheet sheet) {
        final Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, 0, "ONT-ID");
        XlsPoiTool.setContent(row, 1, "Port-ID");
        XlsPoiTool.setContent(row, 2, "Quelle");
        XlsPoiTool.setContent(row, 3, "Freigabedatum");
    }

    private void writeRow(Sheet sheet, final String ontId, final String portId, final String source,
            final Date freigabe) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        XlsPoiTool.setContent(row, 0, ontId);
        XlsPoiTool.setContent(row, 1, portId);
        XlsPoiTool.setContent(row, 2, source);
        XlsPoiTool.setContent(row, 3, freigabe.toString());
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> processQuery() {
        return ((Hibernate4DAOImpl) auftragTechnikDao).find(hql);
    }
}
