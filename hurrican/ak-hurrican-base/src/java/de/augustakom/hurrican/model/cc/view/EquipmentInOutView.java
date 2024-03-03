/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.2007 10:25:20
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * View-Klasse fuer die Kombination von zwei Equipments. <br> Diese View entspricht nich einer Rangierung, sondern wird
 * fuer die Kombination von In- und Out-Equipments vom DSLAM verwendet.
 *
 *
 */
public class EquipmentInOutView extends AbstractCCModel {

    private Long eqIdIn = null;
    private Long eqIdOut = null;
    private String hwEqnIn = null;
    private String hwEqnOut = null;

    /**
     * @return Returns the eqIdIn.
     */
    public Long getEqIdIn() {
        return eqIdIn;
    }

    /**
     * @param eqIdIn The eqIdIn to set.
     */
    public void setEqIdIn(Long eqIdIn) {
        this.eqIdIn = eqIdIn;
    }

    /**
     * @return Returns the eqIdOut.
     */
    public Long getEqIdOut() {
        return eqIdOut;
    }

    /**
     * @param eqIdOut The eqIdOut to set.
     */
    public void setEqIdOut(Long eqIdOut) {
        this.eqIdOut = eqIdOut;
    }

    /**
     * @return Returns the hwEqnIn.
     */
    public String getHwEqnIn() {
        return hwEqnIn;
    }

    /**
     * @param hwEqnIn The hwEqnIn to set.
     */
    public void setHwEqnIn(String hwEqnIn) {
        this.hwEqnIn = hwEqnIn;
    }

    /**
     * @return Returns the hwEqnOut.
     */
    public String getHwEqnOut() {
        return hwEqnOut;
    }

    /**
     * @param hwEqnOut The hwEqnOut to set.
     */
    public void setHwEqnOut(String hwEqnOut) {
        this.hwEqnOut = hwEqnOut;
    }

}


