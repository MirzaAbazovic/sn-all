/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 17:49:57
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;

/**
 * Command, um DTAG Stifte fuer einen KVZ-Standort zu importieren.
 */
public class ImportUevtStifteCommand extends AbstractXlsImportCommand {

    /**
     * Anzahl bestellter Stifte ist immer fix
     */
    private static final int ANZAHL_CUDA = 100;
    private static final Logger LOGGER = Logger.getLogger(ImportUevtStifteCommand.class);
    private static final int UEVT_STIFTE_START_COLUMN = 0;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTToolService")
    private HVTToolService hvtToolService;
    private ConfirmCallback confirmCallback = new ConfirmCallback();

    @Override
    @CcTxRequiresNew
    public Object execute() throws Exception {
        return importUevtStifte();
    }

    SingleRowResult importUevtStifte() throws FindException, StoreException, ValidationException {
        Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");
        Long sessionId = getPreparedValue(PARAM_SESSION_ID, Long.class, false, "Keine SessionId übergeben");
        SingleRowResult importResult = new SingleRowResult();

        String hvtName = XlsPoiTool.getContentAsString(row, UEVT_STIFTE_START_COLUMN);
        String uevtBezeichner = XlsPoiTool.getContentAsString(row, UEVT_STIFTE_START_COLUMN + 1);
        String leiste = String.format("%02d", XlsPoiTool.getContentAsInt(row, UEVT_STIFTE_START_COLUMN + 2));
        String kvzNummerRaw = XlsPoiTool.getContentAsString(row, UEVT_STIFTE_START_COLUMN + 3);
        String kvzNummer = KvzAdresse.formatKvzNr(kvzNummerRaw);

        UEVT uevt = findUevt(hvtName, uevtBezeichner);
        checkUevtLeiste(uevt.getId(), leiste);

        Date now = new Date();
        HVTBestellung hvtBestellung = new HVTBestellung();
        hvtBestellung.setUevtId(uevt.getId());
        hvtBestellung.setAngebotDatum(now);
        hvtBestellung.setBestelldatum(now);
        hvtBestellung.setPhysiktyp(HVTBestellung.PHYSIKTYP_H);  // bestellte Stifte am KVZ sind immer hochbit-ratig!
        hvtBestellung.setAnzahlCuDA(ANZAHL_CUDA);
        hvtToolService.saveHVTBestellung(hvtBestellung);

        List<Equipment> result = hvtToolService.fillUevt(hvtBestellung.getId(), leiste, kvzNummer, 1,
                confirmCallback, true, ANZAHL_CUDA, sessionId);

        hvtBestellung.setBereitgestellt(now);
        hvtToolService.saveHVTBestellung(hvtBestellung);

        if ((result == null) || (result.size() != ANZAHL_CUDA)) {
            importResult.addWarning(String.format(
                    "Die Anzahl erstellter UEVT Stifte %d weicht von der erwarteten %d ab!", (result == null) ? 0
                            : result.size(), ANZAHL_CUDA
            ));
        }
        return importResult;
    }

    void checkUevtLeiste(Long uevtId, String leiste) throws FindException {
        List<EquipmentBelegungView> equipmentBelegungen;
        try {
            equipmentBelegungen = hvtToolService.findEquipmentBelegung(uevtId);
            for (EquipmentBelegungView equipmentBelegungView : equipmentBelegungen) {
                if (StringUtils.equals(leiste, equipmentBelegungView.getLeiste1())) {
                    throw new FindException(String.format("Die angegebene Leiste '%s' ist bereits angelegt!", leiste));
                }
            }
        }
        catch (FindException e) {
            // Unterdrueckt die wenig aussagekraeftige Fehlermeldung aus dem
            // Service (Unerwarteter Fehler)
            throw new FindException(
                    String.format("Für die Uevt ID '%d' sind keine Stift Belegungen verfügbar!", uevtId));
        }
    }

    UEVT findUevt(String hvtName, String uevtBezeichner) throws FindException {
        HVTStandort hvtStandort = null;
        UEVT uevt = null;
        try {
            hvtStandort = hvtService.findHVTStandortByBezeichnung(hvtName);
            if (hvtStandort != null) {
                uevt = hvtService.findUEVT(hvtStandort.getId(), uevtBezeichner);
            }
        }
        catch (FindException e) {
            LOGGER.info(e.getMessage());
            // Unterdrueckt die wenig aussagekraeftige Fehlermeldung aus dem Service (Unerwarteter Fehler)
        }

        if (hvtStandort == null) {
            throw new FindException(String.format("Für den HVT Namen '%s' ist kein Standort verfügbar!", hvtName));
        }

        if (uevt == null) {
            throw new FindException(String.format(
                    "Für den HVT Namen '%s' und den UEVT Bezeichner '%s' ist kein UEVT verfügbar!", hvtName,
                    uevtBezeichner));
        }
        return uevt;
    }

    public void setHvtToolService(HVTToolService hvtToolService) {
        this.hvtToolService = hvtToolService;
    }

    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    private static class ConfirmCallback implements IServiceCallback {
        @Override
        public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
            if (callbackAction == HVTToolService.CALLBACK_CONFIRM) {
                return Boolean.TRUE;
            }
            return null;
        }
    }

}


