/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2011 11:18:16
 */
package de.augustakom.hurrican.service.cc.impl.command.geoid;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Command-Klasse, um die in einem Excel-Sheet angegebenen GeoIDs in Hurrican zu importieren und sie den technischen
 * Standorten zuzuordnen.
 */
public abstract class CommonImportGeoIdFromExcelCommand extends AbstractServiceCommand implements ImportGeoIdFromExcelCommand {

    private static final Logger LOGGER = Logger.getLogger(CommonImportGeoIdFromExcelCommand.class);

    public static final String KEY_SESSION_ID = "session.id";
    public static final String KEY_IMPORT_FILENAME = "import.filename";

    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    protected AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    protected HVTService hvtService;

    protected String fileName;
    protected Long sessionId;

    protected List<Exception> exceptionList = new ArrayList<Exception>();
    protected List<String> successList = new ArrayList<String>();
    protected List<String> warnings = new ArrayList<String>();
    protected Map<String, Object> returnValue = new HashMap<String, Object>();


    protected void init() throws FindException {
        fileName = getPreparedValue(KEY_IMPORT_FILENAME, String.class, false, "File-Name fuer den Import ist nicht definiert!");
        sessionId = getPreparedValue(KEY_SESSION_ID, Long.class, false, "SessionId ist nicht definiert!");

        returnValue.put(AvailabilityService.EXCEPTION, exceptionList);
        returnValue.put(AvailabilityService.SUCCESS, successList);
        returnValue.put(AvailabilityService.WARNING, warnings);
    }

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        init();
        return importGeoIds();
    }

    /**
     * Funktion wird in Import ImportGeoIdKVZExcelCommand und ImportGeoIdVDSLExcelCommand implementiert
     *
     * @param importView {@link GeoIdImportView}
     * @throws StoreException           falls die übergebene Datei nicht existiert
     * @throws FindException            falls GeoId nicht gefunden wird
     * @throws ServiceNotFoundException
     */
    protected void importGeoIdFromRow(GeoIdImportView importView) throws FindException, StoreException, ServiceNotFoundException {
        HVTStandort hvtStd = hvtService.findHVTStandortByBezeichnung(importView.hvtName);
        if (hvtStd == null) {
            throw new FindException(
                    String.format("Der technische Standort '%s' konnte nicht ermittelt werden.", importView.hvtName));
        }

        List<GeoId> geoIds = availabilityService.mapLocationDataToGeoIds(importView.street,
                importView.houseNum,
                importView.houseNumExt,
                importView.zipCode,
                importView.city,
                null);
        if (CollectionTools.isNotEmpty(geoIds)) {
            createGeoId2TechLocationMappings(importView, hvtStd, geoIds);
        }
        else {
            throw new FindException(String.format(
                    "Fuer Lokalität existiert keine GeoId im lokalen Cache! PLZ: %s; Ort: %s; Strasse: %s; HouseNum: %s %s",
                    importView.zipCode, importView.city, importView.street, importView.houseNum, importView.houseNumExt));
        }
    }

    // wird in den konkreten Command Implementierungen umgesetzt
    abstract void createGeoId2TechLocationMappings(GeoIdImportView importView, HVTStandort hvtStd, List<GeoId> geoIds);

    /**
     * importiert Excel-File
     */
    @Override
    public Map<String, Object> importGeoIds() throws StoreException {
        try {
            File source = new File(fileName);
            if (!source.exists()) {
                throw new StoreException("Datei nicht gefunden.");
            }
            Sheet sheet = XlsPoiTool.loadExcelFile(new FileInputStream(source));
            int lastRowNo = sheet.getLastRowNum();

            for (int rowCount = 1; rowCount <= lastRowNo; rowCount++) {
                String hvtName = null;
                try {
                    Row currentRow = sheet.getRow(rowCount);
                    if (currentRow != null) {
                        GeoIdImportView importView = new GeoIdImportView();
                        importView.readData(currentRow);
                        hvtName = importView.hvtName;
                        importGeoIdFromRow(importView);
                        successList.add(String.format("Zeile %s, Datensatz %s erfolgreich importiert", rowCount, hvtName));
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    exceptionList.add(new Exception(
                            String.format("Fehler beim Datensatz %s in Zeile %s: %s", hvtName, rowCount, e.getMessage())));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            exceptionList.add(new Exception(e.getMessage()));
        }
        return returnValue;
    }
}
