/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2007 09:08:04
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.ICBVorgangStatusModel;


/**
 * View-Modell fuer einen CB-Vorgang.
 *
 *
 */
public class CBVorgangView extends AbstractCCModel implements CCAuftragModel, ICBVorgangStatusModel {

    private static final Logger LOGGER = Logger.getLogger(CBVorgangView.class);

    private Long cbVorgangId = null;
    private String vbz = null;
    private Long auftragId = null;
    private Long prodId = null;
    private String produkt = null;
    private Date submittedAt = null;
    private Date vorgabeMnet = null;
    private Long typ = null;
    private Long status = null;
    private Long hvtStandortId = null;
    private String hvt = null;
    private Boolean returnOk = null;

    /**
     * @return Returns the cbVorgangId.
     */
    public Long getCbVorgangId() {
        return cbVorgangId;
    }

    /**
     * @param cbVorgangId The cbVorgangId to set.
     */
    public void setCbVorgangId(Long cbVorgangId) {
        this.cbVorgangId = cbVorgangId;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param verbindungsBezeichnung The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    /**
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return Returns the produkt.
     */
    public String getProdukt() {
        return produkt;
    }

    /**
     * @param produkt The produkt to set.
     */
    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    /**
     * @return Returns the vorgabeMnet.
     */
    public Date getVorgabeMnet() {
        return vorgabeMnet;
    }

    /**
     * @param vorgabeMnet The vorgabeMnet to set.
     */
    public void setVorgabeMnet(Date vorgabeMnet) {
        this.vorgabeMnet = vorgabeMnet;
    }

    /**
     * @return Returns the typ.
     */
    public Long getTyp() {
        return typ;
    }

    /**
     * @param cbTyp The typ to set.
     */
    public void setTyp(Long typ) {
        this.typ = typ;
    }

    /**
     * @return Returns the status.
     */
    @Override
    public Long getStatus() {
        return status;
    }

    /**
     * @param cbStatus The status to set.
     */
    public void setStatus(Long status) {
        this.status = status;
    }

    /**
     * @return Returns the hvtStandortId.
     */
    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    /**
     * @param hvtStandortId The hvtStandortId to set.
     */
    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
    }

    /**
     * @return Returns the hvt.
     */
    public String getHvt() {
        return hvt;
    }

    /**
     * @param hvt The hvt to set.
     */
    public void setHvt(String hvt) {
        this.hvt = hvt;
    }

    /**
     * @return Returns the returnOk.
     */
    @Override
    public Boolean getReturnOk() {
        return returnOk;
    }

    /**
     * @param returnOk The returnOk to set.
     */
    public void setReturnOk(Boolean returnOk) {
        this.returnOk = returnOk;
    }

    /**
     * @return Returns the submittedAt.
     */
    public Date getSubmittedAt() {
        return submittedAt;
    }

    /**
     * @param submittedAt The submittedAt to set.
     */
    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public boolean hasAutomationErrors() {
        LOGGER.warn("CBVorgangView#hasAutomationErrors is not implemented!");
        return false;
    }

}


