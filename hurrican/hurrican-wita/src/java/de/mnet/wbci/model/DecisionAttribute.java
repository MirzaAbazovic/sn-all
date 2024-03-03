/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.model;

/**
 * Enumeration of decision attributes that are checked when evaluating incoming Vorabstimmung.
 */
public enum DecisionAttribute {
    SONSTIGES("Sonstiges", MeldungsCode.SONST),
    VORNAME("Vorname", MeldungsCode.AIFVN),
    NACHNAME("Nachname", MeldungsCode.AIF),
    FIRMEN_NAME("Firmenname", MeldungsCode.AIF),
    FIRMENZUSATZ("Firmenzusatz", MeldungsCode.ZWA),
    KUNDENWUNSCHTERMIN("Kundenwunschtermin", MeldungsCode.NAT),
    AKT_VERTRAGSENDE("Aktuelles Vertragsende", MeldungsCode.NAT),
    PORTIERUNGSZEITFENSTER("Poriterungszeitfenster", MeldungsCode.SONST),
    STR_AEN_WECHSELTERMIN("Urspr. Wechseltermin", MeldungsCode.ZWA),
    PLZ("PLZ", MeldungsCode.ADFPLZ),
    ORT("Ort", MeldungsCode.ADFORT),
    STRASSENNAME("Strasse", MeldungsCode.ADFSTR),
    HAUSNUMMER("Hausnummer", MeldungsCode.ADFHSNR),
    HAUSNUMMERZUSATZ("Hausnummerzusatz", MeldungsCode.ZWA),
    RUFNUMMER("Rufnummer", MeldungsCode.RNG),
    RUFNUMMERN_BLOCK("Rufnummernblock", MeldungsCode.RNG),
    VORNAME_WAI("Vorname (wAI)", MeldungsCode.WAI), // weitere Anschlussinhaber
    NACHNAME_WAI("Nachname (wAI)", MeldungsCode.WAI), // weitere Anschlussinhaber
    FIRMEN_NAME_WAI("Firmenname (wAI)", MeldungsCode.WAI), // weitere Anschlussinhaber
    FIRMENZUSATZ_WAI("Firmenzusatz (wAI)", MeldungsCode.WAI), // weitere Anschlussinhaber
    ALLE_RUFNUMMERN_PORTIEREN("Alle Nummern der Anschlüsse portieren", MeldungsCode.ZWA),
    TECHNOLOGIE("Techn. Ressource", MeldungsCode.UETN_NM);

    private String displayName;
    private MeldungsCode negativeMeldungsCode;

    DecisionAttribute(String displayName, MeldungsCode negativeMeldungsCode) {
        this.displayName = displayName;
        this.negativeMeldungsCode = negativeMeldungsCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MeldungsCode getNegativeMeldungsCode() {
        return negativeMeldungsCode;
    }
}
