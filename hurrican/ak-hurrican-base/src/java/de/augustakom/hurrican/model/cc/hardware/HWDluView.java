/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2009 11:38:18
 */
package de.augustakom.hurrican.model.cc.hardware;

import de.augustakom.hurrican.model.base.AbstractHurricanModel;


/**
 * View-Klasse fuer die Abbildung einer DLU.
 *
 *
 */
public class HWDluView extends AbstractHurricanModel {

    private Long baugruppenId = null;
    private String bgTyp = null;
    private String dluNumber = null;
    private String modNumber = null;
    private Long hvtIdStandort = null;
    private String hwSchnittstelle = null;

    /**
     * @return Returns the baugruppenId.
     */
    public Long getBaugruppenId() {
        return baugruppenId;
    }

    /**
     * @param baugruppenId The baugruppenId to set.
     */
    public void setBaugruppenId(Long baugruppenId) {
        this.baugruppenId = baugruppenId;
    }

    /**
     * @return Returns the bgTyp.
     */
    public String getBgTyp() {
        return bgTyp;
    }

    /**
     * @param bgTyp The bgTyp to set.
     */
    public void setBgTyp(String bgTyp) {
        this.bgTyp = bgTyp;
    }

    /**
     * @return Returns the dluNumber.
     */
    public String getDluNumber() {
        return dluNumber;
    }

    /**
     * @param dluNumber The dluNumber to set.
     */
    public void setDluNumber(String dluNumber) {
        this.dluNumber = dluNumber;
    }

    /**
     * @return Returns the modNumber.
     */
    public String getModNumber() {
        return modNumber;
    }

    /**
     * @param modNumber The modNumber to set.
     */
    public void setModNumber(String modNumber) {
        this.modNumber = modNumber;
    }

    /**
     * @return Returns the hvtIdStandort.
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * @param hvtIdStandort The hvtIdStandort to set.
     */
    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    /**
     * @return Returns the hwSchnittstelle.
     */
    public String getHwSchnittstelle() {
        return hwSchnittstelle;
    }

    /**
     * @param hwSchnittstelle The hwSchnittstelle to set.
     */
    public void setHwSchnittstelle(String hwSchnittstelle) {
        this.hwSchnittstelle = hwSchnittstelle;
    }

}


