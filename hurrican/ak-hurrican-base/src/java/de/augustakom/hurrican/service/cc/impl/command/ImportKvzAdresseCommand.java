/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2012 14:03:00
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * Importiert FTTC KVZ Standort Adressen aus dem "offiziellen" DTAG XLS. Adressen für bestehende KVZs werden
 * aktualisiert, neue KVZs werden angelegt, wenn es für den (Standort, KVZ-Nummer) Equipments gibt.
 *
 *
 */
public class ImportKvzAdresseCommand extends AbstractXlsImportCommand {

    public static final int COL_ONKZ = 1;
    public static final int COL_ASB = 2;
    public static final int COL_KVZNR = 3;
    protected static final int COL_PLZ = 4;
    protected static final int COL_ORT = 5;
    protected static final int COL_STR_HAUSNR = 6;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Override
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    @CcTxRequiresNew
    public Object execute() throws Exception {
        Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");
        SingleRowResult importResult = new SingleRowResult();
        String onkz = XlsPoiTool.getContentAsString(row, COL_ONKZ);
        Integer dtagAsb = XlsPoiTool.getContentAsInt(row, COL_ASB);
        if (StringUtils.isEmpty(onkz) || (dtagAsb == null)) {
            throw new ImportException("ONKZ und/oder ASB fehlt");
        }
        if (!onkz.startsWith("0")) {
            onkz = "0" + onkz;
        }
        List<HVTStandort> hvtStandorte = hvtService.findHVTStandort4DtagAsb(onkz, dtagAsb,
                HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
        if (hvtStandorte.isEmpty()) {
            importResult.setIgnored(true);
            return importResult;
        }

        String dtagKvzNr = XlsPoiTool.getContentAsString(row, COL_KVZNR);
        if (StringUtils.isEmpty(dtagKvzNr)) {
            throw new ImportException("KVZ Nummer fehlt");
        }
        String kvzNr = KvzAdresse.formatKvzNr(dtagKvzNr);

        KvzAdresse kvzAdresse = null;
        for (HVTStandort hvtStandort : hvtStandorte) {
            kvzAdresse = hvtService.findKvzAdresse(hvtStandort.getId(), kvzNr);
            if (kvzAdresse == null) {
                if (hvtService.findEquipments4Kvz(hvtStandort.getId(), kvzNr).isEmpty()) {
                    // KVZ nicht durch diesen Standort angebunden
                    continue;
                }
                kvzAdresse = new KvzAdresse();
                kvzAdresse.setHvtStandortId(hvtStandort.getId());
                kvzAdresse.setKvzNummer(kvzNr);
            }
            String plz = XlsPoiTool.getContentAsString(row, COL_PLZ);
            String ort = XlsPoiTool.getContentAsString(row, COL_ORT);
            String strasseHausNr = XlsPoiTool.getContentAsString(row, COL_STR_HAUSNR);

            KvzAdresse neueAdresse = new KvzAdresse();
            Pair<String, String> strasseHausNrSplit = KvzAdresse.splitDTAGStrasseHausNr(strasseHausNr);
            neueAdresse.setStrasse(strasseHausNrSplit.getFirst());
            neueAdresse.setHausNr(strasseHausNrSplit.getSecond());
            neueAdresse.setPlz(plz);
            neueAdresse.setOrt(ort);

            if (!kvzAdresse.equalsAdresse(neueAdresse)) {
                String diffMsg = " ALT " + kvzAdresse.toStringAdresse() + " NEU " + neueAdresse.toStringAdresse();
                importResult.setRowMessage(diffMsg);
                kvzAdresse.copyAdresse(neueAdresse);
                hvtService.saveKvzAdresse(kvzAdresse);
            }
            break;
        }
        if (kvzAdresse == null) {
            // es wurde zwar ein Standort gefunden, der KVZ ist aber nicht angeschlossen
            importResult.setIgnored(true);
        }

        return importResult;
    }
}
