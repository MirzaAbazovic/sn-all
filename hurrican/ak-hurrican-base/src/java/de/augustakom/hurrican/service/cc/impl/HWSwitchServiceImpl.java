/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 15:38:18
 */
package de.augustakom.hurrican.service.cc.impl;

import static java.util.stream.Collectors.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.exceptions.UpdateException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.HWSwitchDAO;
import de.augustakom.hurrican.dao.cc.SwitchMigrationLogDao;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.SwitchMigrationLog;
import de.augustakom.hurrican.model.cc.SwitchMigrationLogAuftrag;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;

/**
 * Standardimplementierung von {@link HWSwitchService}.
 */
@CcTxRequired
public class HWSwitchServiceImpl extends DefaultCCService implements HWSwitchService {

    private static final Logger LOGGER = Logger.getLogger(HWServiceImpl.class);

    @Resource
    private HWSwitchDAO hwSwitchDao;

    @Resource
    private SwitchMigrationLogDao switchMigrationLogDao;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.VoIPService")
    private VoIPService voipService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;

    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    @Override
    public HWSwitch findSwitchByName(String name) {
        return hwSwitchDao.findSwitchByName(name);
    }

    @Override
    public List<HWSwitch> findAllSwitches() {
        return hwSwitchDao.findAllSwitches();
    }

    /**
     * @param hwSwitchDao The hwSwitchDao to set.
     */
    public void setHwSwitchDao(HWSwitchDAO hwSwitchDao) {
        this.hwSwitchDao = hwSwitchDao;
    }

    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    @Override
    public List<HWSwitch> findSwitchesByType(HWSwitchType... types) {
        return hwSwitchDao.findSwitchesByType(types);
    }

    @Override
    public byte[] moveOrdersToSwitch(List<SwitchMigrationView> ordersToMigrate, HWSwitch sourceSwitch, HWSwitch destinationSwitch,
            Long sessionId, Date execDate) throws ServiceNotFoundException, IOException {
        final SwitchMigrationLog migrationLog = createMigrationLog(sourceSwitch, destinationSwitch, sessionId, execDate);
        for (SwitchMigrationView view : ordersToMigrate) {
            moveOrderToSwitch(view, migrationLog, sessionId);
        }
        return createMigrationLogXls(migrationLog);
    }

    @Override
    public SwitchMigrationLog createMigrationLog(HWSwitch sourceSwitch, HWSwitch destinationSwitch, Long sessionId, Date execDate)
            throws ServiceNotFoundException, IOException {
        LOGGER.info(String.format("Moving orders to the switch '%s.'", destinationSwitch.getName()));

        HWSwitchService txProxy = getCCService(HWSwitchService.class);

        SwitchMigrationLog migrationLog = new SwitchMigrationLog();
        migrationLog.setBearbeiter(getLoginNameSilent(sessionId));
        migrationLog.setCpsExecTime(execDate);
        migrationLog.setMigrateTime(new Date());
        migrationLog.setNewSwitch(destinationSwitch);
        migrationLog.setOldSwitch(sourceSwitch);
        migrationLog = txProxy.storeMigrationLogNewTx(migrationLog);
        return migrationLog;
    }

    @Override
    public void moveOrderToSwitch(SwitchMigrationView orderToMigrate, SwitchMigrationLog migrationLog, Long sessionId)
            throws ServiceNotFoundException, IOException {
        LOGGER.info(String.format("Moving order %s to the switch '%s.'", orderToMigrate.getAuftragId(), migrationLog.getNewSwitch().getName()));
        List<SwitchMigrationLogAuftrag> successfullyMigrated = new ArrayList<>();

        HWSwitchService txProxy = getCCService(HWSwitchService.class);

        List<Long> auftragIds = Collections.singletonList(orderToMigrate.getAuftragId());
        Map<Long, String> endgeraet2SwitchWarnings = checkAuftraegeForCertifiedSwitches(auftragIds, migrationLog.getNewSwitch())
                .stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        SwitchMigrationLogAuftrag log4Order = new SwitchMigrationLogAuftrag();
        log4Order.setAuftragId(orderToMigrate.getAuftragId());
        log4Order.setSwitchMigrationLogId(migrationLog.getId());
        log4Order.addMessage(endgeraet2SwitchWarnings.get(orderToMigrate.getAuftragId()));
        try {
            txProxy.moveOrderToSwitchNewTx(orderToMigrate.getAuftragId(), migrationLog.getNewSwitch());
            log4Order.setMigrated(true);
            successfullyMigrated.add(txProxy.storeMigrationLogAuftragNewTx(log4Order));
        }
        catch (Exception e) {
            LOGGER.error(String.format("change Switch for auftrag '%s' failed", orderToMigrate.getAuftragId()), e);
            log4Order.addMessage(String.format("Fehler beim Auftrag umschreiben: '%s'", e.getMessage()));
            txProxy.storeMigrationLogAuftragNewTx(log4Order);
        }

        for (SwitchMigrationLogAuftrag migrationLogAuftrag : successfullyMigrated) {
            try {
                Pair<Either<Long, Boolean>, AKWarnings> cpsTxIdAndWarnings = txProxy.updateCPSNewTx(migrationLogAuftrag.getAuftragId(), sessionId, migrationLog.getCpsExecTime());
                if(cpsTxIdAndWarnings.getFirst().isLeft()) {
                    migrationLogAuftrag.setCpsTxId(cpsTxIdAndWarnings.getFirst().getLeft());
                    migrationLogAuftrag.setCpsTxRequired(true);
                } else {
                    migrationLogAuftrag.setCpsTxRequired(cpsTxIdAndWarnings.getFirst().getRight());
                }
                migrationLogAuftrag.addMessage(cpsTxIdAndWarnings.getSecond().getWarningsAsText());
            }
            catch (Exception e) {
                LOGGER.error(String.format("send CPS TX for Switch changed failed for Auftrag %s", migrationLogAuftrag.getAuftragId()), e);
                migrationLogAuftrag.addMessage(String.format("Fehler beim Erstellen der CPS-TX: '%s'", e.getMessage()));
            }
            txProxy.storeMigrationLogAuftragNewTx(migrationLogAuftrag);
        }
    }

    public static final int ROW_AUFTRAG_NR = 0;
    public static final int ROW_BILLING_AUFTRAG_NR = 1;
    public static final int ROW_MIGRATED = 2;
    public static final int ROW_CPS_TX_REQUIRED = 3;
    public static final int ROW_CPS_TX_ID = 4;
    public static final int ROW_MESSAGE = 5;

    @Override
    public byte[] createMigrationLogXls(SwitchMigrationLog migrationLog) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet uebersicht = workbook.createSheet("Übersicht");
        uebersicht.setColumnWidth(0, 20 * 256);
        uebersicht.setColumnWidth(1, 20 * 256);
        writeMigrationRow(uebersicht, "Alter Switch", migrationLog.getOldSwitch().getName());
        writeMigrationRow(uebersicht, "Neuer Switch", migrationLog.getNewSwitch().getName());
        writeMigrationRow(uebersicht, "CPS Ausführung zu", DateTools.formatDate(migrationLog.getCpsExecTime(), DateTools.PATTERN_DATE_TIME));
        writeMigrationRow(uebersicht, "Ausgeführt am", DateTools.formatDate(migrationLog.getMigrateTime(), DateTools.PATTERN_DATE_TIME));
        writeMigrationRow(uebersicht, "Beendet am", DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME));
        writeMigrationRow(uebersicht, "Bearbeiter", migrationLog.getBearbeiter());

        Sheet auftragSheet = workbook.createSheet("Aufträge");
        Row titleRow = auftragSheet.createRow(0);
        titleRow.createCell(ROW_AUFTRAG_NR).setCellValue("Techn. AuftragNr.");
        titleRow.createCell(ROW_BILLING_AUFTRAG_NR).setCellValue("Billing AuftragNr.");
        titleRow.createCell(ROW_MIGRATED).setCellValue("Auftrag umgeschrieben");
        titleRow.createCell(ROW_CPS_TX_REQUIRED).setCellValue("CPS TX erforderlich");
        titleRow.createCell(ROW_CPS_TX_ID).setCellValue("CPS TX-ID");
        titleRow.createCell(ROW_MESSAGE).setCellValue("Meldungen");

        auftragSheet.setColumnWidth(ROW_AUFTRAG_NR, 20 * 256);
        auftragSheet.setColumnWidth(ROW_BILLING_AUFTRAG_NR, 20 * 256);
        auftragSheet.setColumnWidth(ROW_MIGRATED, 22 * 256);
        auftragSheet.setColumnWidth(ROW_CPS_TX_REQUIRED, 20 * 256);
        auftragSheet.setColumnWidth(ROW_CPS_TX_ID, 10 * 256);
        auftragSheet.setColumnWidth(ROW_MESSAGE, 120 * 256);

        List<SwitchMigrationLogAuftrag> logDetails = switchMigrationLogDao.findByProperty(SwitchMigrationLogAuftrag.class,
                SwitchMigrationLogAuftrag.SWITCH_MIGRATION_LOG_ID, migrationLog.getId());
        logDetails.stream()
                .forEach(logAuftrag -> writeDetailLog(auftragSheet, logAuftrag));
        return XlsPoiTool.writeWorkbook(workbook);
    }

    private void writeMigrationRow(Sheet sheet, String title, String value) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue(title);
        row.createCell(1).setCellValue(value);
    }

    private void writeDetailLog(Sheet sheet, SwitchMigrationLogAuftrag detail) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(ROW_AUFTRAG_NR).setCellValue(detail.getAuftragId());
        try {
            Long auftragNoOrig = ccAuftragService.findAuftragDatenByAuftragId(detail.getAuftragId()).getAuftragNoOrig();
            if (auftragNoOrig != null) {
                row.createCell(ROW_BILLING_AUFTRAG_NR).setCellValue(auftragNoOrig);
            }
        }
        catch (FindException e) {
            LOGGER.error("failed to read auftragDaten for id=" + detail.getAuftragId(), e);
        }
        row.createCell(ROW_MIGRATED).setCellValue(detail.isMigrated());
        row.createCell(ROW_CPS_TX_REQUIRED).setCellValue(detail.isCpsTxRequired());
        if(detail.getCpsTxId() != null) {
            row.createCell(ROW_CPS_TX_ID).setCellValue(detail.getCpsTxId());
        }
        row.createCell(ROW_MESSAGE).setCellValue(detail.getMessage());
    }

    @Override
    public List<Pair<Long, String>> checkAuftraegeForCertifiedSwitches(final List<Long> auftragIds, final HWSwitch zielSwitch) {
        return auftragIds.stream()
                .map(id -> Pair.create(id, getEgConfigs4Auftrag(id).stream()
                        .filter(egConf -> egIsNotCertifiedForSwitch(egConf, zielSwitch))
                        .collect(toList())))
                .filter(idWithEgConf -> !idWithEgConf.getSecond().isEmpty())
                .map(idWithEgConfig -> {
                    final Long id = idWithEgConfig.getFirst();
                    final List<EGConfig> egConfigs = idWithEgConfig.getSecond();
                    final String warning = egNotCertifiedForSwitchWarning(id, zielSwitch, egConfigs);
                    return Pair.create(id, warning);
                })
                .collect(Collectors.toList());
    }

    @Override
    public AKWarnings checkAuftraegeForCertifiedSwitchesAsWarnings(final List<Long> auftragIds, final HWSwitch zielSwitch) {
        final AKWarnings akWarnings = new AKWarnings();
        checkAuftraegeForCertifiedSwitches(auftragIds, zielSwitch).stream()
                .forEach(p -> akWarnings.addAKWarningNotNull(p.getFirst(), p.getSecond()));
        return akWarnings;
    }

    @Override
    public Either<String, String> updateHwSwitchBasedComponents(Long auftragId, CalculatedSwitch4VoipAuftrag switchInitialState)
            throws FindException {
        Either<HWSwitch, String> newSwitchOrError = applyRules4NewSwitch(auftragId, switchInitialState);
        if (newSwitchOrError != null) {
            if (newSwitchOrError.isRight()) {
                return Either.right(newSwitchOrError.getRight());
            }
            else if (newSwitchOrError.getLeft() != null) {
                HWSwitch newSwitch = newSwitchOrError.getLeft();
                ccAuftragService.updateSwitchForAuftrag(auftragId, newSwitch);
                return Either.left(String.format("Switchkennung des Auftrags wurde auf \"%s\" geändert.",
                        newSwitch.getName()));
            }
        }
        return Either.left(null);
    }

    /**
     * Wendet Regeln an, um abzuschaetzen, ob der Switch fuer den Auftrag umgeschrieben werden muss.
     * @return null wenn kein VoIP Auftrag, Either.left == null wenn keine Aenderung auf dem Auftrag notwendig,
     * Either.left != null wenn Auftrag auf neuen Switch umgeschrieben werden soll, Either.right != null wenn
     * Fehler mit Meldung
     */
    Either<HWSwitch, String> applyRules4NewSwitch(Long auftragId, CalculatedSwitch4VoipAuftrag switchInitialState) throws FindException {
        final Either<CalculatedSwitch4VoipAuftrag, Boolean> newSwitchOrNoVoip =
                ccAuftragService.calculateSwitch4VoipAuftrag(auftragId);
        if(newSwitchOrNoVoip.isRight()) {
            return null; // kein VoIP
        }

        final HWSwitch newSwitch = newSwitchOrNoVoip.getLeft().calculatedHwSwitch;
        final boolean isNewOverride = newSwitchOrNoVoip.getLeft().isOverride;

        if (newSwitch == null) {
            if (switchInitialState.isOverride) {
                return Either.right("Switch zum umschreiben des Auftrags ist nicht konfiguriert!");
            }
        }
        else {
            if (isNewOverride || switchInitialState.isOverride) {
                final HWSwitch currentSwitch = ccAuftragService.getSwitchKennung4Auftrag(auftragId);
                return (ObjectUtils.equals(currentSwitch, newSwitch)) ? Either.left(null) : Either.left(newSwitch);
            }
        }
        return Either.left(null);
    }


    @CcTxRequiresNew
    @Override
    public SwitchMigrationLog storeMigrationLogNewTx(SwitchMigrationLog migrationLog) {
        return switchMigrationLogDao.store(migrationLog);
    }

    @CcTxRequiresNew
    @Override
    public SwitchMigrationLogAuftrag storeMigrationLogAuftragNewTx(SwitchMigrationLogAuftrag migrationLogAuftrag) {
        return switchMigrationLogDao.store(migrationLogAuftrag);
    }

    @CcTxRequiresNew
    @Override
    public void moveOrderToSwitchNewTx(Long auftragId, HWSwitch destinationSwitch)
            throws ServiceNotFoundException, FindException, UpdateException {
        ccAuftragService.updateSwitchForAuftrag(auftragId, destinationSwitch);
        voipService.migrateSipDomainOfVoipDNs(auftragId, true, new CalculatedSipDomain4VoipAuftrag());
    }

    @CcTxRequiresNew
    @Override
    public Pair<Either<Long, Boolean>, AKWarnings> updateCPSNewTx(Long auftragId, Long sessionId, Date execDate) throws UpdateException, FindException {
        if (execDate == null || DateTools.isDateBefore(execDate, new Date())) {
            throw new UpdateException("CPS execution date have to be in the future!");
        }
        AKWarnings warnings4Order = new AKWarnings();
        Long cpsTxId = null;
        Boolean cpsTxRequired = Boolean.TRUE;
        Verlauf lastVerlauf4Auftrag = baService.findLastVerlauf4Auftrag(auftragId, false);
        if (lastVerlauf4Auftrag == null) {
            warnings4Order.addAKWarning(auftragId, "Kein Bauauftrag gefunden, Switch Änderung wird NICHT an CPS gesendet");
        }
        // Bauauftraegen mit Anlass "Kuendigung" und "Änderung" darf die Migration nicht
        // abgebrochen werden. Die Pruefung auf den Realisierungstermin ist
        // nur beim Anlass "Neuschaltung" , "Anbieterwechsel_TKG46_Neuschaltung" und "Anschlussübernahme" relevant.
        else if (!lastVerlauf4Auftrag.isNeuschaltungOrAnschlussuebernahme()
                || DateTools.isDateBeforeOrEqual(lastVerlauf4Auftrag.getRealisierungstermin(), new Date())) {
            try {
                CPSTransactionExt example = new CPSTransactionExt();
                example.setAuftragId(auftragId);
                final Optional<CPSTransactionExt> latestCPS = cpsService.findCPSTransaction(example)
                        .stream()
                        .filter(CPSTransaction::isSubscriberType)                  // all CPS subscriber transactions
                        .sorted((a, b) -> Long.compare(a.getId(), b.getId()) * -1) //DESC order
                        .findFirst();

                // check if an provisioning is existent
                if (latestCPS.isPresent() && (latestCPS.get().isCreateSubscriber() || latestCPS.get().isModifySubscriber())) {
                    //create new CPS transaction
                    CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(
                            new CreateCPSTransactionParameter(auftragId, null, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                                    CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                                    CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, execDate, null, null, null, null, Boolean.FALSE,
                                    Boolean.TRUE, sessionId));
                    if (cpsTxResult.getWarnings() != null && cpsTxResult.getWarnings().isNotEmpty()) {
                        warnings4Order.addAKWarnings(cpsTxResult.getWarnings());
                    }
                    if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                        CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
                        cpsTxId = cpsTx.getId();
                        cpsService.sendCPSTx2CPS(cpsTx, sessionId);
                    }
                }
                else {
                    warnings4Order.addAKWarning(auftragId, "Keine CPS Transaktion für Auftrag gefunden, "
                            + "Switch Änderung wird NICHT an CPS gesendet");
                }
            }
            catch (Exception e) {
                warnings4Order.addAKWarning(e, e.getMessage());
            }
        } else {
            warnings4Order.addAKWarning(auftragId, "Realisierungstermin für Bauauftrag liegt in der Zukunft, keine CPS Transaktion notwendig");
            cpsTxRequired = Boolean.FALSE;
        }
        return Pair.create(cpsTxId != null ? Either.left(cpsTxId) : Either.right(cpsTxRequired), warnings4Order);
    }


    private boolean egIsNotCertifiedForSwitch(final EGConfig egConf, final HWSwitch targetSwitch) {
        if (egConf == null || egConf.getEgType() == null) {
            return false;
        }

        List<HWSwitch> certifiedSwitches = egConf.getEgType().getOrderedCertifiedSwitches();
        return certifiedSwitches == null || !certifiedSwitches.contains(targetSwitch);
    }

    private List<EGConfig> getEgConfigs4Auftrag(final long auftragId) {
        try {
            return endgeraeteService.findEgConfigs4Auftrag(auftragId);
        }
        catch (FindException e) {
            throw new RuntimeException(e);  // NOSONAR squid:S00112 ; RuntimeEx needed for work with stream
        }
    }

    private String egNotCertifiedForSwitchWarning(final long auftragId, final HWSwitch zielSwitch, final List<EGConfig> egConfigs) {
        final String egErrorMsgs = egConfigs.stream()
                .map(egc -> egc.getEgType().getHersteller() + ": " + egc.getEgType().getModell() + "\n")
                .reduce("", String::concat);

        return egErrorMsgs.isEmpty()
                ? null
                : String.format("Folgende Endgeräte auf dem Auftrag %s sind nicht für den Switch %s zertifiziert: ",
                        auftragId, zielSwitch.getName()) + egErrorMsgs;
    }

    public void setSwitchMigrationLogDao(SwitchMigrationLogDao switchMigrationLogDao) {
        this.switchMigrationLogDao = switchMigrationLogDao;
    }
}
