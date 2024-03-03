/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 10:30:44
 */
package de.augustakom.hurrican.model.cc.gui;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell bildet das Mapping zwischen einer GUI-Definition und einer (nahezu beliebigen) Referenz ab. <br> <br> Der Wert
 * von <code>referenzHerkunft</code> muss immer einem Klassennamen entsprechen.
 *
 *
 */
public class GUIMapping extends AbstractCCIDModel {

    /**
     * Konstante fuer Referenz-ID, die eine beliebige ID darstellen soll.
     */
    public static final Long REFERENZ_ID_ALL = Long.valueOf(-99);

    private Long guiDefinitionId = null;
    private Long referenzId = null;
    private String referenzHerkunft = null;

    /**
     * @return Returns the guiDefinitionId.
     */
    public Long getGuiDefinitionId() {
        return guiDefinitionId;
    }

    /**
     * @param guiDefinitionId The guiDefinitionId to set.
     */
    public void setGuiDefinitionId(Long guiDefinitionId) {
        this.guiDefinitionId = guiDefinitionId;
    }

    /**
     * @return Returns the referenzHerkunft.
     */
    public String getReferenzHerkunft() {
        return referenzHerkunft;
    }

    /**
     * @param referenzHerkunft The referenzHerkunft to set.
     */
    public void setReferenzHerkunft(String referenzHerkunft) {
        this.referenzHerkunft = referenzHerkunft;
    }

    /**
     * @return Returns the referenzId.
     */
    public Long getReferenzId() {
        return referenzId;
    }

    /**
     * @param referenzId The referenzId to set.
     */
    public void setReferenzId(Long referenzId) {
        this.referenzId = referenzId;
    }
}


