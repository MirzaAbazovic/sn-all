/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice;

import de.augustakom.common.tools.lang.BooleanTools;

/**
 * Hilfsklasse fuer FFM-Builder
 */
public class FfmHelper {

    /**
     * Konvertiert einen Boolean-Wert in einen String.
     * @param toConvert
     * @return bei {@code TRUE}: ja; sonst 'nein'
     */
    public static String convertBoolean(Boolean toConvert) {
        return (BooleanTools.nullToFalse(toConvert)) ? "ja" : "nein";
    }

    public static String convertDouble(Double toConvert) {
        return (toConvert != null) ? String.format("%s", toConvert) : null;
    }

}
