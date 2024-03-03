/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 14:39:51
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell zur Abbildung einer moeglichen Produkt-Aenderung.
 *
 *
 */
public class Produkt2Produkt extends AbstractCCIDModel {

    private Long prodIdSrc = null;
    private Long prodIdDest = null;
    private Long physikaenderungsTyp = null;
    private Long chainId = null;
    private String description = null;

    /**
     * @return Returns the chainId.
     */
    public Long getChainId() {
        return chainId;
    }

    /**
     * @param chainId The chainId to set.
     */
    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return Returns the physikaenderungsTyp.
     */
    public Long getPhysikaenderungsTyp() {
        return physikaenderungsTyp;
    }

    /**
     * @param physikaenderungsTyp The physikaenderungsTyp to set.
     */
    public void setPhysikaenderungsTyp(Long physikaenderungsTyp) {
        this.physikaenderungsTyp = physikaenderungsTyp;
    }

    /**
     * @return Returns the prodIdDest.
     */
    public Long getProdIdDest() {
        return prodIdDest;
    }

    /**
     * @param prodIdDest The prodIdDest to set.
     */
    public void setProdIdDest(Long prodIdDest) {
        this.prodIdDest = prodIdDest;
    }

    /**
     * @return Returns the prodIdSrc.
     */
    public Long getProdIdSrc() {
        return prodIdSrc;
    }

    /**
     * @param prodIdSrc The prodIdSrc to set.
     */
    public void setProdIdSrc(Long prodIdSrc) {
        this.prodIdSrc = prodIdSrc;
    }

}


