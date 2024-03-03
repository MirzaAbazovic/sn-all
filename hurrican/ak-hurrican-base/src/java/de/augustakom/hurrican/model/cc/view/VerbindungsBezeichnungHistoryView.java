/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2005 17:15:03
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * View-Klasse fuer die Anzeige einer VerbindungsBezeichnung-History.
 *
 *
 */
public class VerbindungsBezeichnungHistoryView extends AbstractCCModel implements CCAuftragModel {

    private Long vbzId = null;
    private Long auftragId = null;
    private String vbz = null;
    private String produkt = null;
    private String statusText = null;
    private String endstelleB = null;
    private String endstelleBName = null;
    private Date inbetriebnahme = null;
    private Date kuendigung = null;
    private Date vorgabeSCV = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#setAuftragId(java.lang.Integer)
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#getAuftragId()
     */
    public Long getAuftragId() {
        return auftragId;
    }

    public String getEndstelleB() {
        return endstelleB;
    }

    public void setEndstelleB(String endstelleB) {
        this.endstelleB = endstelleB;
    }

    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    public Date getKuendigung() {
        return kuendigung;
    }

    public void setKuendigung(Date kuendigung) {
        this.kuendigung = kuendigung;
    }

    public String getProdukt() {
        return produkt;
    }

    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public Long getVbzId() {
        return vbzId;
    }

    public void setVbzId(Long vbzId) {
        this.vbzId = vbzId;
    }

    public String getEndstelleBName() {
        return endstelleBName;
    }

    public void setEndstelleBName(String endstelleBName) {
        this.endstelleBName = endstelleBName;
    }

    public Date getVorgabeSCV() {
        return vorgabeSCV;
    }

    public void setVorgabeSCV(Date vorgabeSCV) {
        this.vorgabeSCV = vorgabeSCV;
    }

}


