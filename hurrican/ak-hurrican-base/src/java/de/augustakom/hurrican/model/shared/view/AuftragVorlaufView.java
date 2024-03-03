/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2005 09:01:01
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;


/**
 * View-Klasse zur Darstellung von Auftraegen, die zukuenftig realisiert werden.
 *
 *
 */
public class AuftragVorlaufView extends AuftragEndstelleView {

    private String endstellePLZ = null;
    private String endstelleAnschlussart = null;
    private String hvtSchaltung = null;
    private String leitungsart = null;
    private String schnittstelle = null;
    private String anlass = null;
    private Date realisierungstermin = null;
    private Date vorgabeKunde = null;
    private String uebernommenVon = null;

    /**
     * @return Returns the anlass.
     */
    public String getAnlass() {
        return anlass;
    }

    /**
     * @param anlass The anlass to set.
     */
    public void setAnlass(String anlass) {
        this.anlass = anlass;
    }

    /**
     * @return Returns the endstelleAnschlussart.
     */
    public String getEndstelleAnschlussart() {
        return endstelleAnschlussart;
    }

    /**
     * @param endstelleAnschlussart The endstelleAnschlussart to set.
     */
    public void setEndstelleAnschlussart(String endstelleAnschlussart) {
        this.endstelleAnschlussart = endstelleAnschlussart;
    }

    /**
     * @return Returns the endstellePLZ.
     */
    public String getEndstellePLZ() {
        return endstellePLZ;
    }

    /**
     * @param endstellePLZ The endstellePLZ to set.
     */
    public void setEndstellePLZ(String endstellePLZ) {
        this.endstellePLZ = endstellePLZ;
    }

    /**
     * @return Returns the leitungsart.
     */
    public String getLeitungsart() {
        return leitungsart;
    }

    /**
     * @param leitungsart The leitungsart to set.
     */
    public void setLeitungsart(String leitungsart) {
        this.leitungsart = leitungsart;
    }

    /**
     * @return Returns the realisierungstermin.
     */
    public Date getRealisierungstermin() {
        return realisierungstermin;
    }

    /**
     * @param realisierungstermin The realisierungstermin to set.
     */
    public void setRealisierungstermin(Date realisierungstermin) {
        this.realisierungstermin = realisierungstermin;
    }

    /**
     * @return Returns the schnittstelle.
     */
    public String getSchnittstelle() {
        return schnittstelle;
    }

    /**
     * @param schnittstelle The schnittstelle to set.
     */
    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
    }

    /**
     * @return Returns the vorgabeKunde.
     */
    public Date getVorgabeKunde() {
        return vorgabeKunde;
    }

    /**
     * @param vorgabeKunde The vorgabeKunde to set.
     */
    public void setVorgabeKunde(Date vorgabeKunde) {
        this.vorgabeKunde = vorgabeKunde;
    }

    /**
     * @return Returns the hvtSchaltung.
     */
    public String getHvtSchaltung() {
        return hvtSchaltung;
    }

    /**
     * @param hvtSchaltung The hvtSchaltung to set.
     */
    public void setHvtSchaltung(String hvtSchaltung) {
        this.hvtSchaltung = hvtSchaltung;
    }

    /**
     * @return Returns the uebernommenVon.
     */
    public String getUebernommenVon() {
        return uebernommenVon;
    }

    /**
     * @param uebernommenVon The uebernommenVon to set.
     */
    public void setUebernommenVon(String uebernommenVon) {
        this.uebernommenVon = uebernommenVon;
    }
}


