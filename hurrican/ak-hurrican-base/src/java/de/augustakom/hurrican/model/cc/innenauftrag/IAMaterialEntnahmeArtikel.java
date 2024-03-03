/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 13:50:26
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Detail-Modell zu einer Materialentnahme. <br> In diesem Modell ist ein entnommener Artikel zu einer Materialentnahme
 * mit Bezeichnung, Menge etc. definiert.
 *
 *
 */
public class IAMaterialEntnahmeArtikel extends AbstractCCIDModel {

    public static final String MATERIAL_ENTNAHME_ID = "materialEntnahmeId";
    private Long materialEntnahmeId = null;
    public static final String ARTIKEL = "artikel";
    private String artikel = null;
    public static final String MATERIAL_NR = "materialNr";
    private String materialNr = null;
    public static final String ANZAHL = "anzahl";
    private Float anzahl = null;
    public static final String EINZELPREIS = "einzelpreis";
    private Float einzelpreis = null;
    public static final String REMOVED_AT = "removedAt";
    private Date removedAt = null;
    public static final String REMOVED_FROM = "removedFrom";
    private String removedFrom = null;
    public static final String ANLAGEN_BEZEICHNUNG = "anlagenBez";
    private String anlagenBez = null;

    /**
     * @return Returns the anzahl.
     */
    public Float getAnzahl() {
        return this.anzahl;
    }

    /**
     * @param anzahl The anzahl to set.
     */
    public void setAnzahl(Float anzahl) {
        this.anzahl = anzahl;
    }

    /**
     * @return Returns the artikel.
     */
    public String getArtikel() {
        return this.artikel;
    }

    /**
     * @param artikel The artikel to set.
     */
    public void setArtikel(String artikel) {
        this.artikel = artikel;
    }

    /**
     * @return Returns the einzelpreis.
     */
    public Float getEinzelpreis() {
        return this.einzelpreis;
    }

    /**
     * @param einzelpreis The einzelpreis to set.
     */
    public void setEinzelpreis(Float einzelpreis) {
        this.einzelpreis = einzelpreis;
    }

    /**
     * @return Returns the materialEntnahmeId.
     */
    public Long getMaterialEntnahmeId() {
        return this.materialEntnahmeId;
    }

    /**
     * @param materialEntnahmeId The materialEntnahmeId to set.
     */
    public void setMaterialEntnahmeId(Long materialEntnahmeId) {
        this.materialEntnahmeId = materialEntnahmeId;
    }

    /**
     * @return Returns the materialNr.
     */
    public String getMaterialNr() {
        return this.materialNr;
    }

    /**
     * @param materialNr The materialNr to set.
     */
    public void setMaterialNr(String materialNr) {
        this.materialNr = materialNr;
    }

    /**
     * @return Returns the removedAt.
     */
    public Date getRemovedAt() {
        return this.removedAt;
    }

    /**
     * @param removedAt The removedAt to set.
     */
    public void setRemovedAt(Date removedAt) {
        this.removedAt = removedAt;
    }

    /**
     * @return Returns the removedFrom.
     */
    public String getRemovedFrom() {
        return this.removedFrom;
    }

    /**
     * @param removedFrom The removedFrom to set.
     */
    public void setRemovedFrom(String removedFrom) {
        this.removedFrom = removedFrom;
    }

    /**
     * @return the anlagenBez
     */
    public String getAnlagenBez() {
        return anlagenBez;
    }

    /**
     * @param anlagenBez the anlagenBez to set
     */
    public void setAnlagenBez(String anlagenBez) {
        this.anlagenBez = anlagenBez;
    }

}


