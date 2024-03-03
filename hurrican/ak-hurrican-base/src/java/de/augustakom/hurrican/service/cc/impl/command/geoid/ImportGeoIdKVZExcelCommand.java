/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2011
 */
package de.augustakom.hurrican.service.cc.impl.command.geoid;

import java.util.*;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Command-Klasse, um die in einem Excel-Sheet angegebenen GeoIDs in Hurrican zu importieren und sie den technischen
 * Standorten zuzuordnen.
 */
public class ImportGeoIdKVZExcelCommand extends CommonImportGeoIdFromExcelCommand implements ImportGeoIdFromExcelCommand, IServiceCommand {

    private static final long serialVersionUID = 221970160068642758L;

    /**
     * Erstellt das Mapping zwischen technologischem Standort und der GeoId
     */
    @Override
    protected void createGeoId2TechLocationMappings(GeoIdImportView importView, HVTStandort hvtStd, List<GeoId> geoIds) {
        for (GeoId geoId : geoIds) {
            try {
                GeoId2TechLocation existingMapping = availabilityService.findGeoId2TechLocation(geoId.getId(), hvtStd.getId());
                if (existingMapping != null) {
                    throw new StoreException(String.format("Mapping von GeoId %s / techn. Standort %s ist bereits vorhanden!",
                            geoId.getId(), hvtStd.getId()));
                }

                GeoId2TechLocation geoId2TechLocation = new GeoId2TechLocation();
                geoId2TechLocation.setGeoId(geoId.getId());
                geoId2TechLocation.setHvtIdStandort(hvtStd.getId());
                geoId2TechLocation.setKvzNumber(importView.kvzNumber);
                geoId2TechLocation.setTalLength(importView.getTalLengthAsLong());
                geoId2TechLocation.setVdslAnHvtAvailableSince(null);
                geoId2TechLocation.setTalLengthTrusted(false);

                availabilityService.saveGeoId2TechLocation(geoId2TechLocation, sessionId);
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
