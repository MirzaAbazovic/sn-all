/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 13:26:05
 */
package de.augustakom.hurrican.model.shared.view;


/**
 * View-Modell, um Auftrags- und Carrier-Daten darzustellen. (Die Informationen werden ueber Billing- und CC-Services
 * zusammengetragen.)
 *
 *
 */
public class AuftragCarrierView extends AuftragDatenView {

    private static final long serialVersionUID = 7571312473451628849L;

    private String lbz = null;
    private String otherLbz = null;
    private String vtrNr = null;
    private String carrierRefNr = null;
    private String esTyp = null;

    public String getLbz() {
        return lbz;
    }

    public void setLbz(String lbz) {
        this.lbz = lbz;
    }

    public String getOtherLbz() {
        return otherLbz;
    }

    public void setOtherLbz(String otherLbz) {
        this.otherLbz = otherLbz;
    }

    public String getVtrNr() {
        return vtrNr;
    }

    public void setVtrNr(String vtrNr) {
        this.vtrNr = vtrNr;
    }

    public String getCarrierRefNr() {
        return carrierRefNr;
    }

    public void setCarrierRefNr(String carrierRefNr) {
        this.carrierRefNr = carrierRefNr;
    }

    public String getEsTyp() {
        return esTyp;
    }

    public void setEsTyp(String esTyp) {
        this.esTyp = esTyp;

    }
}


