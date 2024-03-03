/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2005 10:28:55
 */
package de.augustakom.hurrican.gui.tools.carrier;


/**
 * Modell-Klasse, um syntaktisch falsche Carrierbestellungen abzubilden.
 *
 *
 */
public class CarrierLbzCheckModel {

    private Long cbId = null;
    private String lbz = null;
    private String vtrNr = null;
    private String auftragIds = null;
    private String vbzs = null;
    private String bemerkung = null;

    public String getAuftragIds() {
        return auftragIds;
    }

    public void setAuftragIds(String auftragIds) {
        this.auftragIds = auftragIds;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Long getCbId() {
        return cbId;
    }

    public void setCbId(Long cbId) {
        this.cbId = cbId;
    }

    public String getLbz() {
        return lbz;
    }

    public void setLbz(String lbz) {
        this.lbz = lbz;
    }

    public String getVtrNr() {
        return vtrNr;
    }

    public void setVtrNr(String vtrNr) {
        this.vtrNr = vtrNr;
    }

    public String getVbzs() {
        return vbzs;
    }

    public void setVbzs(String vbzs) {
        this.vbzs = vbzs;
    }

}


