/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2012 16:29:29
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * Command Impl. um eine FTTX KVZ Adresse (genau eine Zeile des Imports) zu importieren. Läuft in einer eigenen
 * Transaktion.
 *
 *
 */
public class ImportFTTXKvzAdresseRowCommand extends AbstractXlsImportCommand {

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Override
    @CcTxRequiresNew
    public Object execute() throws Exception {
        Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");
        SingleRowResult importResult = new SingleRowResult();
        int col = 0;
        String hvtName = XlsPoiTool.getContentAsString(row, col++);
        if (StringUtils.isEmpty(hvtName)) {
            throw new ImportException("HVT-Name der Gruppe fehlt");
        }

        HVTGruppe hvtGruppe = hvtService.findHVTGruppeByBezeichnung(hvtName);
        if (hvtGruppe == null) {
            throw new ImportException("kein HVTStandort(bzw. HVTGruppe) mit Bezeichnung '" + hvtName + "' gefunden");
        }

        HVTStandort hvtStandort = null;
        List<HVTStandort> existingStandorte = hvtService.findHVTStandorte4Gruppe(hvtGruppe.getId(), true);
        for (HVTStandort existingStandort : existingStandorte) {
            if (existingStandort.getStandortTypRefId().equals(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                hvtStandort = existingStandort;
                break;
            }
            else {
                throw new ImportException("vorhandene Standort für  Gruppe '" + hvtGruppe.getOrtsteil() + " mit ASB '"
                        + existingStandort.getAsb() + "' hat falschen StandortTyp");
            }
        }
        if (hvtStandort == null) {
            throw new ImportException("kein HVTStandort für HVTGruppe mit Bezeichnung '" + hvtName + "' gefunden");
        }

        String kvzNummerRaw = XlsPoiTool.getContentAsString(row, col++);
        if (StringUtils.isEmpty(kvzNummerRaw)) {
            throw new ImportException("KVZ-Nummer muss angegeben werden");
        }
        String kvzNummer = KvzAdresse.formatKvzNr(kvzNummerRaw);
        KvzAdresse existingKvzAdresse = hvtService.findKvzAdresse(hvtStandort.getId(), kvzNummer);
        if (existingKvzAdresse != null) {
            importResult.addWarning("KVZ Adresse bereits vorhanden");
            return importResult;
        }

        KvzAdresse kvzAdresse = new KvzAdresse();
        kvzAdresse.setHvtStandortId(hvtStandort.getId());
        kvzAdresse.setKvzNummer(kvzNummer);
        kvzAdresse.setStrasse(XlsPoiTool.getContentAsString(row, col++));
        kvzAdresse.setHausNr(XlsPoiTool.getContentAsString(row, col++));
        kvzAdresse.setPlz(XlsPoiTool.getContentAsString(row, col++));
        kvzAdresse.setOrt(XlsPoiTool.getContentAsString(row, col++));
        if (StringUtils.isEmpty(kvzAdresse.getStrasse()) || StringUtils.isEmpty(kvzAdresse.getHausNr())
                || StringUtils.isEmpty(kvzAdresse.getPlz()) || StringUtils.isEmpty(kvzAdresse.getOrt())) {
            throw new ImportException("Adresse muss vollständig angegeben werden");
        }

        hvtService.saveKvzAdresse(kvzAdresse);
        return importResult;
    }

}
