/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.12.2012 12:08:59
 */
package de.augustakom.hurrican.fix;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.AbstractTransactionalServiceTest;
import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;

/**
 * Fix für fehlerhafte EqVlans für FTTH Hueawei OLTs (Grund war der falsch herangezogene Split aus der HW_EQN).
 * Schritte: <ul> <li>Iterieren über alle Huawei OLTs mit vlanAktivAb != null</li> <li>Iterieren über alle Baugruppen
 * einer OLT</li> <li>Iterieren über alle Aufträge die auf Equipments der Baugruppe rangiert sind</li> <li>EqVlans für
 * den Auftrag neu berechnen und speichern</li> <li>Modify CPS Transaktion senden, wenn die letzte Transaktion Create
 * oder modify war</li> </ul>
 * <p/>
 * Die bearbeiteten Aufträge werden in einer XLS Datei protokolliert.
 * <p/>
 * <b>ACHTUNG</b> Test sollte nur bei Bedarf aktiviert werden!!!<br> Um den Test "scharf" zu schalten sind folgende
 * Schritte notwendig: <ul> <li>Die Test-Gruppe {@link AbstractTransactionalServiceTest#NO_ROLLBACK_TEST} für den Test
 * setzen</li> <li>Für die Testmethode enabled=false entfernen</li> <li>Die gewünschte Umgebung (DB) über die System
 * Property <code>-Duse.config=...</code> setzen</li> <li>Den User und das Pwd. für die Umgebung setzen über die System
 * Properties <code>-Dtest.user=...</code> bzw. <code>-Dtest.password=...</code> </ul>
 *
 *
 */
// TODO: nur lokal bei Bedarf einschalten
// @Test(groups = AbstractTransactionalServiceTest.NO_ROLLBACK_TEST)
public class FixVlan4FtthHuaweiOlt extends AbstractHurricanBaseServiceTest {
    private static final Logger LOGGER = Logger.getLogger(FixVlan4FtthHuaweiOlt.class);

    /**
     * Taifun Aufträge die nicht bearbeitet werden sollen
     */
    private static final Long[] IGNORE_AUFTRAG_NO = new Long[] { Long.valueOf("1860429") };

    /**
     * Maximale Anzahl an Aufträge die an CPS gesendet werden. Bei erreichen dieser Zahl wird die Abarbeitung beendet.
     * <p/>
     * -1 bedeutet kein Limit.
     */
    private static int MAX_SEND_TO_CPS = -1;

    private int cpsTxCount;

    @Autowired
    HWService hwService;

    @Autowired
    EquipmentDAO equipmentDAO;

    @Autowired
    CCAuftragService auftragService;

    @Autowired
    EkpFrameContractService ekpFrameContractService;

    @Autowired
    VlanService vlanService;

    @Autowired
    CPSService cpsService;

    private static final int OLT = 0;
    private static final int TAIFUN_ID = 1;
    private static final int HUR_ID = 2;
    private static final int VLAN_NEU = 3;
    private static final int CPS_GEN = 4;
    private static final int MSG = 5;

    // TODO: nur lokal bei Bedarf einschalten!!!
    @Test(enabled = false)
    public void fixVlans() throws Exception {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        writeHeaderRow(sheet);

        List<HWOlt> olts = hwService.findRacksByType(HWOlt.class);

        try {
            for (HWOlt olt : olts) {
                if (!HVTTechnik.HUAWEI.equals(olt.getHwProducer()) || (olt.getVlanAktivAb() == null)) {
                    continue;
                }
                List<HWBaugruppe> baugruppen4Rack = hwService.findBaugruppen4Rack(olt.getId());
                for (HWBaugruppe bg : baugruppen4Rack) {
                    processOltBg(olt, bg, sheet);
                }
            }
        }
        catch (MaxCpsSendReachedException e) {
            LOGGER.info(String.format(
                    "Maximale Anzahl an CPS Transaktionen erreichet (%d). Verarbeitung wird gestoppt", cpsTxCount));
        }
        finally {
            File xlsFile = new File(InitializeLog4J.getLogDirectory(), getClass().getSimpleName()
                    + "_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xls");
            workbook.write(new FileOutputStream(xlsFile));
            LOGGER.info("XLS File=" + xlsFile.getPath());
        }
    }

    private void processOltBg(HWOlt olt, HWBaugruppe bg, Sheet sheet) throws Exception {
        List<AuftragDaten> auftragDatenList = auftragService.findAuftragDatenByBaugruppe(bg.getId());
        for (AuftragDaten auftragDaten : auftragDatenList) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Date adStartFrom = auftragDaten.getInbetriebnahme() != null ? auftragDaten.getInbetriebnahme()
                    : auftragDaten.getVorgabeSCV();
            if (adStartFrom == null) {
                writeRow(row, olt, auftragDaten, false, false, "Auftrag hat weder Inbetriebname, noch VorgabeSCV Datum");
                continue;
            }
            final Date calcDt = olt.getVlanAktivAb().after(adStartFrom) ? olt.getVlanAktivAb()
                    : adStartFrom;
            LocalDate calcDate = Instant.ofEpochMilli(calcDt.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

            EkpFrameContract ekpFrameContract = ekpFrameContractService.findEkp4AuftragOrDefaultMnet(
                    auftragDaten.getAuftragId(), calcDate, true);
            if (ekpFrameContract == null) {
                throw new StoreException("EKP not found!");
            }

            List<EqVlan> oldEqVlans = vlanService.findEqVlans4Auftrag(auftragDaten.getAuftragId(), calcDate);
            List<EqVlan> newEqVlans = vlanService.assignEqVlans(ekpFrameContract, auftragDaten.getAuftragId(),
                    auftragDaten.getProdId(), calcDate, null);
            Map<CvlanServiceTyp, EqVlan> oldEqVlanMap = CollectionMapConverter.convert2Map(oldEqVlans, "getCvlanTyp",
                    null);

            boolean eqVlanChanged = false;
            for (EqVlan newVlan : newEqVlans) {
                EqVlan old = oldEqVlanMap.get(newVlan.getCvlanTyp());
                if (!newVlan.equalsAll(old)) {
                    eqVlanChanged = true;
                    break;
                }
            }
            if (eqVlanChanged) {
                if (ArrayUtils.contains(IGNORE_AUFTRAG_NO, auftragDaten.getAuftragNoOrig())) {
                    writeRow(row, olt, auftragDaten, true, false, "Auftrag in Ignore Liste enthalten");
                }
                else if (isLastCPSTransactionCreateOrModify(auftragDaten)) {
                    String cpsMsg = sendCpsTransaction(auftragDaten);
                    writeRow(row, olt, auftragDaten, eqVlanChanged, cpsMsg == null, cpsMsg);
                    cpsTxCount++;
                    if ((MAX_SEND_TO_CPS != -1) && (cpsTxCount >= MAX_SEND_TO_CPS)) {
                        throw new MaxCpsSendReachedException();
                    }
                }
                else {
                    writeRow(row, olt, auftragDaten, true, false,
                            "Auftrag hat keine CPS Trans. oder letzte Trans. war DELETE_SUB");
                }
            }
            else {
                writeRow(row, olt, auftragDaten, false, false, "EqVlans sind unverändert");
            }
        }
    }

    private boolean isLastCPSTransactionCreateOrModify(AuftragDaten auftragDaten) throws Exception {
        List<CPSTransaction> cpsTranss = cpsService.findSuccessfulCPSTransaction4TechOrder(auftragDaten.getAuftragId());
        if (!cpsTranss.isEmpty()) {
            CPSTransaction last = cpsTranss.get(cpsTranss.size() - 1);
            if (!last.isCancelSubscriber() && last.isSubscriberType()) {
                return true;
            }
        }
        return false;
    }

    private String sendCpsTransaction(AuftragDaten auftragDaten) throws Exception {
        CPSProvisioningAllowed cpsProvisioningAllowed = cpsService.isCPSProvisioningAllowed(
                auftragDaten.getAuftragId(), null, false, false, true);
        if (!cpsProvisioningAllowed.isProvisioningAllowed()) {
            return cpsProvisioningAllowed.getNoCPSProvisioningReason();
        }

        CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(
                new CreateCPSTransactionParameter(auftragDaten.getAuftragId(), null,
                        CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                        CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                        CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, new Date(), null, null, null, null, false, false,
                        getSessionId())
        );

        if ((cpsTxResult.getCpsTransactions() != null) && (cpsTxResult.getCpsTransactions().size() == 1)) {
            cpsService.sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), getSessionId());
        }
        return cpsTxResult.getWarnings().getWarningsAsText();
    }

    protected void writeHeaderRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        XlsPoiTool.setContent(row, OLT, "OLT");
        XlsPoiTool.setContent(row, TAIFUN_ID, "Taifun-Nr.");
        XlsPoiTool.setContent(row, HUR_ID, "Hurrican-Nr");
        XlsPoiTool.setContent(row, VLAN_NEU, "EqVlans NEU");
        XlsPoiTool.setContent(row, CPS_GEN, "CPS Trans. generiert");
        XlsPoiTool.setContent(row, MSG, "Bemerkung");
    }

    private void writeRow(Row row, HWOlt olt, AuftragDaten auftragDaten, Boolean eqVlanChanged, Boolean cpsGenerated,
            String msg) {
        XlsPoiTool.setContent(row, OLT, olt.getGeraeteBez());
        XlsPoiTool.setContent(row, TAIFUN_ID, auftragDaten.getAuftragNoOrig().toString());
        XlsPoiTool.setContent(row, HUR_ID, auftragDaten.getAuftragId().toString());
        XlsPoiTool.setContent(row, VLAN_NEU, eqVlanChanged.toString());
        XlsPoiTool.setContent(row, CPS_GEN, cpsGenerated.toString());
        if (StringUtils.isNotBlank(msg)) {
            XlsPoiTool.setContent(row, MSG, msg);
        }
        LOGGER.info(String.format("%s %7d/%7d %b %b %s%n", olt.getGeraeteBez(), auftragDaten.getAuftragNoOrig(),
                auftragDaten.getAuftragId(), eqVlanChanged, cpsGenerated, msg));
    }

    static class MaxCpsSendReachedException extends Exception {
    }
}
