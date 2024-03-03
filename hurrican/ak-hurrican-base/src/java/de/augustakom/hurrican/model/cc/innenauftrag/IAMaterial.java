/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 13:48:13
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell stellt einen Eintrag einer Materialliste dar. <br> Die Materialliste kommt urspruenglich aus SAP. Damit fuer
 * einen Materialentnahmeschein eine Auswahl erfolgen kann, wird diese SAP-Liste in Hurrican importiert.
 *
 *
 */
public class IAMaterial extends AbstractCCIDModel {

    private String artikel = null;
    private String text = null;
    private String materialNr = null;
    private Float einzelpreis = null;

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
     * @return Returns the text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

}


