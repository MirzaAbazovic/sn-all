/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2004 08:58:58
 */
package de.augustakom.hurrican.model.cc;


/**
 * Ueber dieses Modell wird ein AuftragsImport-Status definiert.
 *
 *
 */
public class AuftragImportStatus extends AbstractCCIDModel {

    /**
     * Auftrag befindet sich im Import (1000).
     */
    public static final Long AUFTRAG_IMPORT = Long.valueOf(1000);
    /**
     * Bonitaet muss geprueft werden (1100)
     */
    public static final Long IMPORT_BONI_PRUEFEN = Long.valueOf(1100);
    /**
     * Import wird storniert, z.B. weil Kunde den Auftrag zurueck gezogen hat (1200).
     */
    public static final Long IMPORT_STORNO = Long.valueOf(1200);
    /**
     * Import wird abgebrochen, da Bonitaet schlecht (1250)
     */
    public static final Long IMPORT_ABBRUCH_BONI = Long.valueOf(1250);
    /**
     * Bonitaet des Kunden ist o.k. (1300)
     */
    public static final Long IMPORT_BONI_OK = Long.valueOf(1300);
    /**
     * Bandbreitenreduzierung fuer den Auftrag bei Kunde angefordert (1350)
     */
    public static final Long IMPORT_BB_REDUZIERUNG_ANGEFORDERT = Long.valueOf(1350);
    /**
     * Auftrageingangsschreiben wurde erstellt (1400)
     */
    public static final Long IMPORT_AE_SCHREIBEN = Long.valueOf(1400);
    /**
     * Auftrag wurde an Erfassung weitergegeben(1500).
     */
    public static final Long IMPORT_ERFASSUNG = Long.valueOf(1500);

    public static final String GET_STATUS_TEXT = "getStatusText";
    private String statusText = null;
    private Boolean importActive = null;

    /**
     * @return Returns the statusText.
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * @param statusText The statusText to set.
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * Gibt an, ob der Import bei dem angegebenen Status als 'aktiv' (true) oder 'inaktiv' (false) angesehen werden
     * soll.
     *
     * @return Returns the importActive.
     */
    public Boolean getImportActive() {
        return importActive;
    }

    /**
     * @param importActive The importActive to set.
     */
    public void setImportActive(Boolean importActive) {
        this.importActive = importActive;
    }
}


