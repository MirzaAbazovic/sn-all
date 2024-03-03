/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.13
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * View-Modell um die Such-Ergebnisse fuer WBCI Vorabstimmung-IDs darzustellen.
 */
public class WbciRequestCarrierView extends AbstractHurricanModel implements CCAuftragModel {

    private static final long serialVersionUID = -641625786388688847L;

    private String vorabstimmungsId;
    private String aenderungsId;
    private Long billingOrderNoOrig;
    private Long auftragId;
    private String geschaeftsfallTyp;
    private Date kundenwunschtermin;
    private String abgebenderEkp;
    private String aufnehmenderEkp;
    private String status;

    public String getAenderungsId() {
        return aenderungsId;
    }

    public void setAenderungsId(String aenderungsId) {
        this.aenderungsId = aenderungsId;
    }

    public String getAbgebenderEkp() {
        return abgebenderEkp;
    }

    public void setAbgebenderEkp(String abgebenderEkp) {
        this.abgebenderEkp = abgebenderEkp;
    }

    public String getAufnehmenderEkp() {
        return aufnehmenderEkp;
    }

    public void setAufnehmenderEkp(String aufnehmenderEkp) {
        this.aufnehmenderEkp = aufnehmenderEkp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBillingOrderNoOrig() {
        return billingOrderNoOrig;
    }

    public void setBillingOrderNoOrig(Long billingOrderNoOrig) {
        this.billingOrderNoOrig = billingOrderNoOrig;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public void setGeschaeftsfallTyp(String geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    public Date getKundenwunschtermin() {
        return kundenwunschtermin;
    }

    public void setKundenwunschtermin(Date kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
    }

    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }
}
