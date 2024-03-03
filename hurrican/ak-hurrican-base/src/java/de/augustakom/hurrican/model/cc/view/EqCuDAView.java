/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2005 14:21:18
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;

/**
 * Equipment, Kupfer-Doppelader (CuDA)
 *
 *
 */
public class EqCuDAView extends AbstractCCModel implements HvtIdStandortModel {

    private Long hvtIdStandort = null;
    private String uevt = null;
    private String cudaPhysik = null;
    private Integer anzahl = null;
    private String carrier = null;
    private String status = null;
    private String rangSSType = null;

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
     * @return Returns the anzahl.
     */
    public Integer getAnzahl() {
        return anzahl;
    }

    /**
     * @param anzahl The anzahl to set.
     */
    public void setAnzahl(Integer anzahl) {
        this.anzahl = anzahl;
    }

    /**
     * @return Returns the carrier.
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * @param carrier The carrier to set.
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
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
     * @return Returns the hvtIdStandort.
     */
    @Override
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

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
