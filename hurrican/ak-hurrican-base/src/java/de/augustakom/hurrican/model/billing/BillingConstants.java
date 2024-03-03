/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.07.2004 13:24:10
 */
package de.augustakom.hurrican.model.billing;


/**
 * Interface definiert Konstanten, die innerhalb des Billing-Systems (der DB) verwendet werden.
 *
 *
 */
public interface BillingConstants {

    /**
     * Sprachbezeichnung fuer Sprache 'deutsch'.
     */
    public static final String LANG_CODE_GERMAN = "german";

    /**
     * Hist-Status Wert 'aktuell'
     */
    public static final String HIST_STATUS_AKT = "AKT";

    /**
     * Hist-Status Wert 'alt'
     */
    public static final String HIST_STATUS_ALT = "ALT";

    /**
     * Hist-Status Wert 'ung'
     */
    public static final String HIST_STATUS_UNG = "UNG";

    /**
     * Hist-Status Wert 'neu'
     */
    public static final String HIST_STATUS_NEU = "NEU";

    /**
     * Auftragstyp 'KUEND'.
     */
    public static final String ATYP_KUEND = "KUEND";

    /**
     * Hist-Last Wert = 1 / bedeutet true
     */
    public static final Integer HIST_LAST_TRUE = 1;

    /**
     * Wert um anzuegeben, dass die Verbindungsdaten eines Auftrags nach Rechnungsstellung geloescht werden sollen. <br>
     * (Konstante wird in Felder AUFTRAG__KOMBI.KUNDENVRB_DATEN und AUFTRAG__SCV.KUNDENVRB_DATEN verwendet.)
     */
    public static final String DELETE_CALLS = "loeschen";

}


