/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2012 14:03:00
 */
package de.augustakom.hurrican.service.cc.impl.command;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * Importiert FTTC KVZ Standort Adressen aus dem "offiziellen" DTAG XLS. Adressen für bestehende KVZs werden
 * aktualisiert, neue KVZs werden angelegt, wenn es für den (Standort, KVZ-Nummer) Equipments gibt.
 *
 *
 */
public class ImportHvtAdresseCommand extends AbstractXlsImportCommand {

    public static final int COL_ONKZ = 0;
    public static final int COL_ASB = 1;
    protected static final int COL_ZUGANGSART = 2;
    protected static final String ZUGANGSART_CUDA = "CuDa";
    protected static final int COL_PLZ_NEU = 4;
    protected static final int COL_ORT_NEU = 5;
    protected static final int COL_STR_HAUSNR_NEU = 6;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Override
    @CcTxRequiresNew
    public Object execute() throws Exception {
        Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");
        SingleRowResult importResult = new SingleRowResult();
        String zugangsArt = XlsPoiTool.getContentAsString(row, COL_ZUGANGSART);
        if (!ZUGANGSART_CUDA.equals(zugangsArt)) {
            importResult.setIgnored(true);
            return importResult;
        }

        String onkz = XlsPoiTool.getContentAsString(row, COL_ONKZ);
        Integer dtagAsb = XlsPoiTool.getContentAsInt(row, COL_ASB);
        if (StringUtils.isEmpty(onkz) || (dtagAsb == null)) {
            throw new ImportException("ONKZ und/oder ASB fehlt");
        }
        if (!onkz.startsWith("0")) {
            onkz = "0" + onkz;
        }
        HVTStandort hvtStandort = hvtService.findHVTStandort(onkz, dtagAsb);
        if (hvtStandort == null) {
            importResult.setIgnored(true);
            return importResult;
        }
        HVTGruppe hvtGruppe = hvtService.findHVTGruppeById(hvtStandort.getHvtGruppeId());
        if (hvtGruppe == null) {
            throw new ImportException("HvtGruppe nicht gefunden");
        }

        String plzNeu = XlsPoiTool.getContentAsString(row, COL_PLZ_NEU);
        String ortNeu = XlsPoiTool.getContentAsString(row, COL_ORT_NEU);
        String strasseHausNrNeu = XlsPoiTool.getContentAsString(row, COL_STR_HAUSNR_NEU);

        HVTGruppe neueAdresse = new HVTGruppe();
        Pair<String, String> strasseHausNrSplit = HVTGruppe.splitDTAGStrasseHausNr(strasseHausNrNeu);
        neueAdresse.setStrasse(strasseHausNrSplit.getFirst());
        neueAdresse.setHausNr(strasseHausNrSplit.getSecond());
        neueAdresse.setPlz(plzNeu);
        neueAdresse.setOrt(ortNeu);

        if (!hvtGruppe.equalsAdresse(neueAdresse)) {
            String diffMsg = " ALT " + hvtGruppe.toStringAdresse() + " NEU " + neueAdresse.toStringAdresse();
            importResult.setRowMessage(diffMsg);
            hvtGruppe.copyAdresse(neueAdresse);
            hvtService.saveHVTGruppe(hvtGruppe);
        }
        return importResult;
    }
}
