/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.regex.*;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.cc.impl.HvtUmzugServiceImpl;

/**
 * Command-Klasse fuer den Import eines XLS-Files von der DTAG. <br/>
 * vorgegebene Spalten: <br/>
 * <ul>
 *     <li>Spalte 8 (= I): alter UEVT Stift</li>
 *     <li>Spalte 17 (= R): Leitungsbezeichnung</li>
 * </ul>
 */
public class ImportHvtUmzugDetailsCommand extends AbstractXlsImportCommand {

    public static final String PARAM_HVT_UMZUG = "HVT_UMZUG";

    public static boolean uevtStiftIsValid(String uevtStift) {
        final Pattern pattern = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$");
        return pattern.matcher(uevtStift).find();
    }

    @Override
    public Object execute() throws Exception {
        final HvtUmzug hvtUmzug = getPreparedValue(PARAM_HVT_UMZUG, HvtUmzug.class, false, "Zugehörige HvtUmzug ist nicht gesetzt");
        final Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");

        SingleRowResult importResult = new XlsImportResultView.SingleRowResult();

        final String uevtStiftAlt = XlsPoiTool.getContentAsString(row, HvtUmzugServiceImpl.COL_UEVT_STIFT_ALT).trim();
        if (!uevtStiftIsValid(uevtStiftAlt)) {
            importResult.addWarning(String.format("UEVT-Stift '%s' entspricht nicht dem erwarteten Format", uevtStiftAlt));
            importResult.setIgnored(true);
            return importResult;
        }

        final String lbz = XlsPoiTool.getContentAsString(row, HvtUmzugServiceImpl.COL_CARRIER_ID).trim();

        final HvtUmzugDetail detail = new HvtUmzugDetail();
        detail.setUevtStiftAlt(uevtStiftAlt);
        detail.setLbz(lbz);
        hvtUmzug.addHvtUmzugDetail(detail);

        return importResult;
    }
}
