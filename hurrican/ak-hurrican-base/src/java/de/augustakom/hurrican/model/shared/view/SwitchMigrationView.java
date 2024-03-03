/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.07.2011 10:23:07
 */
package de.augustakom.hurrican.model.shared.view;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * View-Modell, um diverse Daten der Switch Migration anzuzeigen.
 */
public class SwitchMigrationView implements CCAuftragModel, Serializable {

    private Long auftragId = null;
    private Long auftragStatusId = null;
    private String auftragStatus = null;
    private String produkt = null;
    private Long prodId = null;
    private String vbz = null;
    private Long billingAuftragId = null;
    private Date baRealisierung = null;
    private Date inbetriebnahme = null;
    private String switchKennung = null;
    private String techLocation = null;

    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getAuftragStatusId() {
        return auftragStatusId;
    }

    public void setAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;
    }

    public String getProdukt() {
        return produkt;
    }

    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getAuftragStatus() {
        return auftragStatus;
    }

    public void setAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public Long getBillingAuftragId() {
        return billingAuftragId;
    }

    public void setBillingAuftragId(Long billingAuftragId) {
        this.billingAuftragId = billingAuftragId;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public Date getBaRealisierung() {
        return baRealisierung;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP2")
    public void setBaRealisierung(Date baRealisierung) {
        this.baRealisierung = baRealisierung;
    }

    public String getSwitchKennung() {
        return switchKennung;
    }

    public void setSwitchKennung(String switchKennung) {
        this.switchKennung = switchKennung;
    }

    public String getTechLocation() {
        return techLocation;
    }

    public void setTechLocation(String techLocation) {
        this.techLocation = techLocation;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP2")
    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

}
