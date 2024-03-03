/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2011 13:44:47
 */
package de.mnet.wita;

public enum AbbmMeldungsCode {

    // @formatter:off
    ABLEHNUNG_ABGEBENDER_PROVIDER("0041","Der abgebende Provider hat die Kündigung seines Bestandes abgelehnt."),
    RUFNUMMER_UNBEKANNT("1000", "Die Rufnummer ist unbekannt."),
    ANSCHRIFT_NOT_CORRECT("1001", "Die Anschrift ist nicht bekannt."),
    SCHALTTERMIN_UNZULAESSIG ("1003", "Der gewünschte Schalttermin liegt ausserhalb des zulässigen Zeitrahmens"),
    LEISTUNGSMERKMAL_NOT_POSSIBLE("1009", "Das Leistungsmerkmal ist nicht möglich."),
    AUFTRAG_ALREADY_EXISTS("1012", "Es liegt bereits ein Auftrag für diesen Anschluss vor. Ihr Auftrag kann erst nach Erledigung des noch offenen Auftrags erteilt werden."),
    EXTERNEAUFTRAGNUMMER_NOT_CORRECT("1013","Externe Auftragsnummer ist nicht eindeutig oder nicht korrekt"),
    VERTRAGSNUMMER_NICHT_IM_BESTAND("1015", "Vertragsnummer passt nicht zum Bestand"),
    BESTELLER_NOT_AUTHORIZED("1018", "Der Besteller ist nicht berechtigt für den Kunden zu bestellen"),
    STANDORTANGABEN_B_NOT_CORRECT("1019","Angaben zum Standort B sind nicht plausibel."),
    AUFTRAG_NICHT_BEARBEITBAR("1023", "Ihr Auftrag ist derzeit aus technischen Gründen nicht bearbeitbar. Bitte versuchen Sie es zu einem späteren Zeitpunkt erneut"),
    RUFNUMMER_MISSING("1025", "Es wurden nicht alle Rufnummern des Anschlussbestandes angegeben"),
    RUFNUMMER_NICHT_IN_BESTAND("1028", "Ein oder mehrere der zu portierenden Rufnummern gehören nicht zum Bestand oder sind unbekannt"),
    AUFTRAG_AUSGEFUEHRT_TV_NOT_POSSIBLE("1084", "Der Auftrag wurde  bereits ausgeführt. Eine Terminverschiebung ist nicht möglich."),
    STORNO_EXISTS("1086", "Kundenauftrag ist nicht stornierbar, da Stornierung bereits angestoßen"),
    STORNO_NOT_POSSIBLE("1087", "Eine Stornierung ist nicht mehr möglich."),
    AUFTRAG_AUSGEFUEHRT_STORNO_NOT_POSSIBLE("1089", "Der Auftrag wurde bereits durchgeführt. Eine Stornierung ist nicht mehr möglich"),
    KEIN_WECHSELFAEHIGES_PRODUKT("1100", "Es gibt kein wechselfähiges Produkt im Bestand"),
    PRODUKT_ODER_GESCHAEFTSFALL_NOT_POSSIBLE("1102", "Produkt oder Geschäftsfall ist nicht zulässig."),
    ANBIETERWECHSEL_NUR_MIT_VBL("1106", "Der gwünschte Anbieterwechsel ist nur mit dem Geschäftsfall Verbundleistung möglich."),
    ANBIETERWECHSEL_NUR_MIT_PV("1107", "Der gwünschte Anbieterwechsel ist nur mit dem Geschäftsfall Providerwechsel möglich."),
    SCHALTANGABEN_NOT_CORRECT("1302", "Schaltangaben sind nicht korrekt"),
    ALTERNATIVPRODUKT("1304", "Gewünschtes Produkt nicht möglich. Alternativprodukt bestellbar"),
    STANDORTANGABEN_NOT_SUFFICIENT("1311", "Standortangabe nicht eindeutig oder ausreichend"),
    STORNO_TOO_LATE("1089", "Der Auftrag wurde bereits durchgeführt. Eine Stornierung ist nicht mehr möglich."),
    PROJEKTKENNER_NICHT_BEKANNT("2006", "Projektkenner ist nicht bekannt."),
    RESERVIERUNGSNUMMER_UNGUELTIG("1040", "Reservierungsnummer ist ungültig"),
    RESERVIERUNGSNUMMER_UNGLEICH_WUNSCHTERMIN("1045", "Der Termin zur Reservierungs ID ist ungleich dem Kundenwunschtermin"),
    TV_AUS_BETRIEBLICHEN_GRUENDEN_NICHT_MOEGLICH("1083", "Eine Terminverschiebung ist aus betrieblichen Gründen nicht möglich."),
    ;
    // @formatter:on

    public final String meldungsCode;
    public final String meldungsText;

    private AbbmMeldungsCode(String meldungsCode, String meldungsText) {
        this.meldungsCode = meldungsCode;
        this.meldungsText = meldungsText;
    }

}
