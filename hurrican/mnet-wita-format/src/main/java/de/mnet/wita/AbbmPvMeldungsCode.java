/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2011 13:44:47
 */
package de.mnet.wita;

public enum AbbmPvMeldungsCode {

    // @formatter:off
    WECHSEL_ZURUECKGEZOGEN("0040","Der Auftrag zum Wechsel wurde zurückgezogen bzw. abgebrochen. Ihr Vertrag bleibt unverändert bestehen.");
    // @formatter:on

    public final String meldungsCode;
    public final String meldungsText;

    private AbbmPvMeldungsCode(String meldungsCode, String meldungsText) {
        this.meldungsCode = meldungsCode;
        this.meldungsText = meldungsText;
    }

}
