/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2005 11:57:56
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * View-Klasse fuer die Abbildung von Belegungs-Informationen eines HVTs.
 *
 *
 */
public class HVTBelegungView extends AbstractCCModel implements HvtIdStandortModel {

    private String uevt = null;
    private Long hvtIdStandort = null;
    private String cudaPhysik = null;
    private String rangLeiste1 = null;
    private String rangSSType = null;
    private Integer belegt = null;
    private Integer frei = null;

    /**
     * @return Returns the belegt.
     */
    public Integer getBelegt() {
        return belegt;
    }

    /**
     * @param belegt The belegt to set.
     */
    public void setBelegt(Integer belegt) {
        this.belegt = belegt;
    }

    /**
     * @return Returns the cudaPhysik.
     */
    public String getCudaPhysik() {
        return cudaPhysik;
    }

    /**
     * @param cudaPhysik The cudaPhysik to set.
     */
    public void setCudaPhysik(String cudaPhysik) {
        this.cudaPhysik = cudaPhysik;
    }

    /**
     * @return Returns the frei.
     */
    public Integer getFrei() {
        return frei;
    }

    /**
     * @param frei The frei to set.
     */
    public void setFrei(Integer frei) {
        this.frei = frei;
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
     * @return Returns the rangLeiste1.
     */
    public String getRangLeiste1() {
        return rangLeiste1;
    }

    /**
     * @param rangLeiste1 The rangLeiste1 to set.
     */
    public void setRangLeiste1(String rangLeiste1) {
        this.rangLeiste1 = rangLeiste1;
    }

    /**
     * @return Returns the rangSSType.
     */
    public String getRangSSType() {
        return rangSSType;
    }

    /**
     * @param rangSSType The rangSSType to set.
     */
    public void setRangSSType(String rangSSType) {
        this.rangSSType = rangSSType;
    }

    /**
     * @return Returns the uevt.
     */
    public String getUevt() {
        return uevt;
    }

    /**
     * @param uevt The uevt to set.
     */
    public void setUevt(String uevt) {
        this.uevt = uevt;
    }

}


