/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.2006 07:01:37
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Mapping-Klasse fuer die Zuordnung/Konfiguration von Endgeraeten zu einem Produkt.
 *
 *
 */
public class Produkt2EG extends AbstractCCIDModel implements CCProduktModel {

    private Long prodId = null;
    private Long endgeraetId = null;
    private Boolean isDefault = null;
    private Boolean isActive = null;

    /**
     * @return Returns the endgeraetId.
     */
    public Long getEndgeraetId() {
        return this.endgeraetId;
    }

    /**
     * @param endgeraetId The endgeraetId to set.
     */
    public void setEndgeraetId(Long endgeraetId) {
        this.endgeraetId = endgeraetId;
    }

    /**
     * @return Returns the isDefault.
     */
    public Boolean getIsDefault() {
        return this.isDefault;
    }

    /**
     * @param isDefault The isDefault to set.
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return this.prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return Returns the isActive.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive The isActive to set.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}


