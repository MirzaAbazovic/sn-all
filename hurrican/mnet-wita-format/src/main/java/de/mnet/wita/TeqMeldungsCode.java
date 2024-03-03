/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2011 13:44:47
 */
package de.mnet.wita;

/**
 * {@link Enum} to represent possible TeqMeldung
 */
public enum TeqMeldungsCode {
    OK("OK", "OK"),
    VERSION_CHECK_FAILED("0993", "Versionsnummer fehlerhaft"),
    XML_VALIDATION_FAILED("0995", "Datenstruktur ungültig"),
    PRODUKTBEZEICHNER_NOT_OK("0987", "Ein angegebener Produktbezeichner ist ungültig"),
    UNEXPECTED_ERROR("0999", "Technischer Fehler");

    public final String meldungsCode;
    public final String meldungsText;

    private TeqMeldungsCode(String meldungsCode, String meldungsText) {
        this.meldungsCode = meldungsCode;
        this.meldungsText = meldungsText;
    }
}
