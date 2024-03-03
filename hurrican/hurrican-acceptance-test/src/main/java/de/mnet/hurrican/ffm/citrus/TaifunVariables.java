/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.ffm.citrus;

/**
 * Captures all Taifun variables, like AuftragsNr and CustomerNumber
 */
public final class TaifunVariables {

    public static final String ENDKUNDE_NACHNAME = "endkunde.nachname";
    public static final String ENDKUNDE_VORNAME = "endkunde.vorname";

    public static final String STANDORT_STRASSE = "standort.strasse";
    public static final String STANDORT_HNR = "standort.hnr";
    public static final String STANDORT_HNR_ZUSATZ = "standort.hnr.zusatz";
    public static final String STANDORT_PLZ = "standort.plz";
    public static final String STANDORT_ORT = "standort.ort";
    public static final String STANDORT_GEOID = "standort.geo.id";

    public static final String RUFNUMMER_ONKZ = "rufnummer.onkz.%s";
    public static final String RUFNUMMER_DNBASE = "rufnummer.dnbase.%s";
    public static final String RUFNUMMER_DIRECT_DIAL = "rufnummer.direct.dial.%s";
    public static final String RUFNUMMER_RANGE_FROM = "rufnummer.range.from.%s";
    public static final String RUFNUMMER_RANGE_TO = "rufnummer.range.to.%s";

    public static final String CUSTOMER_NO = "customer.no";
    public static final String ORDER_NO = "order.no";

    public static final String DEVICE_TYPE = "device.type";
    public static final String DEVICE_MANUFACTURER = "device.manufacturer";
    public static final String DEVICE_MODEL = "device.model";
    public static final String DEVICE_SERIAL_NO = "device.serial.no";

    public static final String DEVICE_FB_CWMP_ID = "device.fritzbox.cwmpId";

    /**
     * Prevent instantiation.
     */
    private TaifunVariables() {
    }

}
