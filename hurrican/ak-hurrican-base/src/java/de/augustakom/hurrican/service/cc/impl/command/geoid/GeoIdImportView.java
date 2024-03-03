/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2011 13:29:13
 */
package de.augustakom.hurrican.service.cc.impl.command.geoid;

import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.poi.XlsPoiTool;


/**
 * Hilfsobjekt, um die Daten des Excel-Sheets auszulesen
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "URF_UNREAD_FIELD", justification = "read externally")
public class GeoIdImportView {

    private static final int BEZEICHNUNG = 0;
    private static final int STRASSE = 1;
    private static final int HAUSNUMMER = 2;
    private static final int HAUSNUMMER_ZUSATZ = 3;
    private static final int PLZ = 4;
    private static final int ORT = 5;
    private static final int ONKZ = 6;
    private static final int ASB = 7;
    private static final int KVZ_NUMMER = 8;
    private static final int ENTFERNUNG = 9;

    String hvtName = null;
    String street = null;
    String houseNum = null;
    String houseNumExt = null;
    String zipCode = null;
    String city = null;
    String onkz = null;
    Integer asb = null;
    String kvzNumber = null;
    String distance = null;

    void readData(Row row) {
        hvtName = XlsPoiTool.getContentAsString(row, BEZEICHNUNG);
        street = XlsPoiTool.getContentAsString(row, STRASSE);
        city = XlsPoiTool.getContentAsString(row, ORT);
        zipCode = XlsPoiTool.getContentAsString(row, PLZ);
        houseNum = XlsPoiTool.getContentAsString(row, HAUSNUMMER);
        houseNumExt = XlsPoiTool.getContentAsString(row, HAUSNUMMER_ZUSATZ);
        onkz = XlsPoiTool.getContentAsString(row, ONKZ);
        asb = XlsPoiTool.getContentAsInt(row, ASB);
        kvzNumber = XlsPoiTool.getContentAsString(row, KVZ_NUMMER);
        distance = XlsPoiTool.getContentAsString(row, ENTFERNUNG, "");
    }

    Long getTalLengthAsLong() {
        if (distance == null) {
            return null;
        }
        else {
            final Float distanceInKilometer = Float.valueOf(distance);
            final Float distanceInMeter = distanceInKilometer * 1000;
            return distanceInMeter.longValue();
        }
    }

}
