/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2004 12:06:20
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell fuer die Abbildung einer Account-Art.
 *
 *
 */
public class AccountArt extends AbstractCCIDModel {

    private Integer liNr = null;
    private String text = null;

    /**
     * @return Returns the liNr.
     */
    public Integer getLiNr() {
        return liNr;
    }

    /**
     * @param liNr The liNr to set.
     */
    public void setLiNr(Integer liNr) {
        this.liNr = liNr;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }
}


