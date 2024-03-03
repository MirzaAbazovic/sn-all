/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2006 17:47:26
 */
package de.augustakom.hurrican.model.shared.view;

import de.augustakom.hurrican.model.shared.AbstractSharedModel;


/**
 * Hilfsklasse fuer den Auftragsmonitor. <br> In diesem Modell wird die ermittelte (gemappte) Hurrican Produkt-ID zu
 * einem Billing-Auftrag gespeichert.
 *
 *
 */
public class Billing2HurricanProdMapping extends AbstractSharedModel {

    private Long auftragNoOrig = null;
    private Long oldAuftragNoOrig = null;
    private String oeName = null;
    private Long oeNoOrig = null;
    private Integer bundleOrderNo = null;
    /**
     * Hurrican Prokukt-Id
     */
    private Long prodId = null;

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return this.auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the oeName.
     */
    public String getOeName() {
        return this.oeName;
    }

    /**
     * @param oeName The oeName to set.
     */
    public void setOeName(String oeName) {
        this.oeName = oeName;
    }

    /**
     * @return Returns the oeNoOrig.
     */
    public Long getOeNoOrig() {
        return this.oeNoOrig;
    }

    /**
     * @param oeNoOrig The oeNoOrig to set.
     */
    public void setOeNoOrig(Long oeNoOrig) {
        this.oeNoOrig = oeNoOrig;
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
     * @return Returns the bundleOrderNo.
     */
    public Integer getBundleOrderNo() {
        return this.bundleOrderNo;
    }

    /**
     * @param bundleOrderNo The bundleOrderNo to set.
     */
    public void setBundleOrderNo(Integer bundleOrderNo) {
        this.bundleOrderNo = bundleOrderNo;
    }

    /**
     * @return Returns the oldAuftragNoOrig.
     */
    public Long getOldAuftragNoOrig() {
        return this.oldAuftragNoOrig;
    }

    /**
     * @param oldAuftragNoOrig The oldAuftragNoOrig to set.
     */
    public void setOldAuftragNoOrig(Long oldAuftragNoOrig) {
        this.oldAuftragNoOrig = oldAuftragNoOrig;
    }

}


