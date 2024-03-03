/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2011
 */
package de.augustakom.hurrican.service.cc.impl.command.geoid;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTStandort;


/**
 * Command-Klasse, um die in einem Excel-Sheet angegebenen GeoIDs in Hurrican zu importieren, diese sind speziell fuer
 * VDSL an HVT. In der Tabelle T_GEO_ID_2_TECH_LOCATION wird ein Flag gepflegt, das angibt, ob VDSL am HVT verfügbar
 * ist.
 *
 *
 */
public class ImportGeoIdVDSLExcelCommand extends CommonImportGeoIdFromExcelCommand implements ImportGeoIdFromExcelCommand, IServiceCommand {
    private static final long serialVersionUID = 7828130066564467957L;

    private static final Logger LOGGER = Logger.getLogger(ImportGeoIdVDSLExcelCommand.class);


    /**
     * Erstellt das Mapping zwischen technologischem Standort und der GeoId
     */
    @Override
    protected void createGeoId2TechLocationMappings(GeoIdImportView importView, HVTStandort hvtStd, List<GeoId> geoIds) {
        for (GeoId geoId : geoIds) {
            try {
                GeoId2TechLocation mapping = availabilityService.findGeoId2TechLocation(geoId.getId(), hvtStd.getId());
                if (mapping == null) {
                    mapping = new GeoId2TechLocation();
                    mapping.setGeoId(geoId.getId());
                    mapping.setHvtIdStandort(hvtStd.getId());
                    mapping.setKvzNumber(importView.kvzNumber);
                    mapping.setTalLength(importView.getTalLengthAsLong());
                    mapping.setTalLengthTrusted(false);
                    LOGGER.info("Folgendes GeoId2TechLocation Mapping mit VDSL an HVT Flag wird neu angelegt (GeoID): " + mapping.getGeoId());
                }
                else {
                    LOGGER.info("Folgendes GeoId2TechLocation Mapping mit VDSL an HVT Flag ist bereits vorhanden (GeoID): " + mapping.getGeoId());
                }

                // Verfuegbar seit heute
                mapping.setVdslAnHvtAvailableSince(Calendar.getInstance());
                availabilityService.saveGeoId2TechLocation(mapping, sessionId);
            }
            catch (Exception e) {
                warnings.add(String.format(
                        "Fehler bei der Standortzuordnung der GeoID %s (PLZ: %s; Ort: %s; Strasse: %s; HouseNum: %s %s): %s",
                        geoId.getId(), importView.zipCode, importView.city, importView.street, importView.houseNum,
                        importView.houseNumExt, e.getMessage()));
            }
        }
    }

}
