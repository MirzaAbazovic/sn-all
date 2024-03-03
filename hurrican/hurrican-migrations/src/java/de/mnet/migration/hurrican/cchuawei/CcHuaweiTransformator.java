/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.2015
 */
package de.mnet.migration.hurrican.cchuawei;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcHelper;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;

/**
 *
 */
public class CcHuaweiTransformator extends HurricanTransformator<CcHuawei> {

    private static final Logger LOG = Logger.getLogger(CcHuaweiTransformator.class);

    public static class MigMessages extends HurricanMessages {
        final Messages.Message ERROR_UNEXPECTED = new Messages.Message(
                TransformationStatus.ERROR, 0x04L,
                "unexpected error: %s");
    }

    private static final int NIEDERLASSUNG_ID = 0;
    private static final int HW_SCHNITTSTELLE = 1;
    private static final int DSLAM_TYPE = 2;
    private static final int ALTE_CC_VORHANDEN = 3;
    private static final int AUFTRAG_ID = 4;
    private static final int EQUIPMENT_ID = 5;
    private static final int AUFTRAG_STATUS_ID = 6;
    private static final int HW_EQN = 7;
    private static final int MODEL_NUMBER = 8;
    private static final int BAUAUFTRAGSDATUM = 9;
    private static final int CROSS_CONNECTIONS_NEW = 10;
    private static final int CROSS_CONNECTIONS_OLD = 11;

    @SuppressWarnings("squid:S00115")
    private static final MigMessages messages = new MigMessages();

    private static HurricanMigrationStarter migrationStarter;

    @Resource(name = "de.augustakom.hurrican.service.cc.EQCrossConnectionService")
    private EQCrossConnectionService eqCrossConnectionService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    private Long sessionId;

    @SuppressWarnings("squid:S00115")
    private static final List<MigrationResult> results = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/cchuawei/cchuawei-migration.xml").finish();
        createHuaweiMigrationXls();
    }

    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }

    /**
     * Ablauf: - Prefix zur Netmask ermitteln - falls IP ein Prefix besitzt --> vergleichen - Typen: T_EG_IP -->
     * IPV4_with_prefixlength, T_EG_ROUTING --> IPV4_prefix
     */
    @Override
    public TransformationResult transform(CcHuawei row) {
        String message = String.format("Calculate CC for Auftrag %s(ID) and Equipment %s(ID)",
                row.auftragId, row.equipmentId);
        messages.prepare(message);
        LOG.info(message);

        calculateCrossConnections(row);

        TargetIdList updatedIds = new TargetIdList(new SourceTargetId("Equipment ID", row.equipmentId));
        return messages.evaluate(updatedIds);
    }

    private void calculateCrossConnections(CcHuawei row) {
        List<EQCrossConnection> crossConnectionsToSet = new ArrayList<>();
        List<EQCrossConnection> crossConnectionsToDeactivate = new ArrayList<>();
        Exception exc = null;
        try {
            Equipment equipment = rangierungsService.findEquipment(row.equipmentId);
            Produkt produkt = produktService.findProdukt4Auftrag(row.auftragId);
            eqCrossConnectionService.defineDefaultCrossConnections4Port(equipment, row.auftragId, getChangeDate(row),
                    produkt.getIsVierDraht(), sessionId, crossConnectionsToSet, crossConnectionsToDeactivate);

        }
        catch (Exception e) {
            messages.ERROR_UNEXPECTED.add(e.getMessage());
            exc = e;
        }
        if(exc != null || !(crossConnectionsToSet.isEmpty() && crossConnectionsToDeactivate.isEmpty())) {
            String infoMessage = String.format("CC for Auftrag %s(ID) and Equipment %s(ID) calculated successfully. "
                            + "CCs (ID) created: %s; CCs (ID) deleted: %s!", row.auftragId, row.equipmentId,
                    getCCIds(crossConnectionsToSet), getCCIds(crossConnectionsToDeactivate));
            messages.INFO.add(infoMessage);
            LOG.info(infoMessage);
            results.add(new MigrationResult(row, exc, crossConnectionsToSet, crossConnectionsToDeactivate));
        }
    }

    private String getCCIds(List<EQCrossConnection> crossConnections) {
        if (crossConnections.isEmpty()) {
            return "no crossconnections";
        }
        return
                crossConnections
                        .stream()
                        .map(cc -> cc.getId().toString())
                        .collect(Collectors.joining(","));
    }

    private static final class MigrationResult {

        final CcHuawei row;
        final Exception exc;
        final List<EQCrossConnection> crossConnectionsToSet;
        final List<EQCrossConnection> crossConnectionsToDeactivate;

        private MigrationResult(CcHuawei row, Exception exc, List<EQCrossConnection> crossConnectionsToSet,
                List<EQCrossConnection> crossConnectionsToDeactivate) {
            this.row = row;
            this.exc = exc;
            this.crossConnectionsToSet = crossConnectionsToSet;
            this.crossConnectionsToDeactivate = crossConnectionsToDeactivate;
        }

        @Override
        public String toString() {
            return "MigrationResult{" +
                    "Record=" + row +
                    ", \nNEU=" + crossConnectionsToSet +
                    ", \nALT=" + crossConnectionsToDeactivate +
                    (exc != null ? ", \nexc=" + exc : "") +
                    '}';
        }

        public CcHuawei getRow() {
            return row;
        }
    }

    private Date getChangeDate(CcHuawei row) {
        Long auftragStatusId = row.auftragStatusId;
        if (auftragStatusId < AuftragStatus.IN_BETRIEB) {
            return row.bauauftragsdatum;
        }
        else {
            return new Date();
        }
    }

    private static void createHuaweiMigrationXls() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        writeHeaderRow(sheet);

        Comparator<MigrationResult> byNiederlassung =
                (m1, m2) -> m1.getRow().niederlassungId.compareTo(m2.getRow().niederlassungId);
        Comparator<MigrationResult> byHwSchnittstelle =
                (m1, m2) -> m1.getRow().hwSchnittstelle.compareTo(m2.getRow().hwSchnittstelle);
        Comparator<MigrationResult> byAuftragId =
                (m1, m2) -> m1.getRow().auftragId.compareTo(m2.getRow().auftragId);

        LOG.info("--------------------------------------------------------------------------");
        results
                .stream()
                .sorted(byNiederlassung.thenComparing(
                                byHwSchnittstelle.thenComparing(
                                        byAuftragId
                                )
                        )
                )
                .forEach(migrationResult -> writeRow(sheet, migrationResult));
        LOG.info("--------------------------------------------------------------------------");
        File xlsFile = new File(System.getProperty("user.dir"), "CcHuaweiMigration"
                + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
        workbook.write(new FileOutputStream(xlsFile));
        LOG.info("XLS File=" + xlsFile.getCanonicalPath());
    }

    private static void writeHeaderRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, NIEDERLASSUNG_ID, "NIEDERLASSUNG_ID");
        XlsPoiTool.setContent(row, HW_SCHNITTSTELLE, "HW_SCHNITTSTELLE");
        XlsPoiTool.setContent(row, DSLAM_TYPE, "DSLAM_TYPE");
        XlsPoiTool.setContent(row, ALTE_CC_VORHANDEN, "ALTE_CC_VORHANDEN");
        XlsPoiTool.setContent(row, AUFTRAG_ID, "AUFTRAG_ID");
        XlsPoiTool.setContent(row, EQUIPMENT_ID, "EQUIPMENT_ID");
        XlsPoiTool.setContent(row, AUFTRAG_STATUS_ID, "AUFTRAG_STATUS_ID");
        XlsPoiTool.setContent(row, HW_EQN, "HW_EQN");
        XlsPoiTool.setContent(row, MODEL_NUMBER, "MODEL_NUMBER");
        XlsPoiTool.setContent(row, BAUAUFTRAGSDATUM, "BAUAUFTRAGSDATUM");
        XlsPoiTool.setContent(row, CROSS_CONNECTIONS_NEW, "CROSS_CONNECTIONS_NEW");
        XlsPoiTool.setContent(row, CROSS_CONNECTIONS_OLD, "CROSS_CONNECTIONS_OLD");
    }

    private static void writeRow(Sheet sheet, MigrationResult migrationResult) {
        LOG.info(migrationResult);
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        CcHuawei ccHuawei = migrationResult.getRow();
        List<EQCrossConnection> crossConnectionsToSet = migrationResult.crossConnectionsToSet;
        List<EQCrossConnection> crossConnectionsToDeactivate = migrationResult.crossConnectionsToDeactivate;
        XlsPoiTool.setContent(row, NIEDERLASSUNG_ID, CcHelper.getNlType(ccHuawei.niederlassungId).toString());
        XlsPoiTool.setContent(row, HW_SCHNITTSTELLE, ccHuawei.hwSchnittstelle);
        XlsPoiTool.setContent(row, DSLAM_TYPE, ccHuawei.dslamType);
        XlsPoiTool.setContent(row, ALTE_CC_VORHANDEN, crossConnectionsToDeactivate.isEmpty() ? "nein" : "ja");
        XlsPoiTool.setContent(row, AUFTRAG_ID, ccHuawei.auftragId);
        XlsPoiTool.setContent(row, EQUIPMENT_ID, ccHuawei.equipmentId);
        XlsPoiTool.setContent(row, AUFTRAG_STATUS_ID, ccHuawei.auftragStatusId);
        XlsPoiTool.setContent(row, HW_EQN, ccHuawei.hwEqn);
        XlsPoiTool.setContent(row, MODEL_NUMBER, ccHuawei.modelNumber);
        XlsPoiTool.setContent(row, BAUAUFTRAGSDATUM, ccHuawei.bauauftragsdatum);
        XlsPoiTool.setContent(row, CROSS_CONNECTIONS_NEW, crossConnectionsToSet.toString());
        XlsPoiTool.setContent(row, CROSS_CONNECTIONS_OLD,
                crossConnectionsToDeactivate.isEmpty() ? "" : crossConnectionsToDeactivate.toString());
    }

}
