/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2010 13:00:57
 */

package de.augustakom.hurrican.model.cc.temp;


/**
 * Temporaeres Modell fuer die Anzeige von Auftragsdaten bei einem Bauauftrags-Split.
 *
 *
 */
public class SplitSelectSubOrder {
    private Boolean selected = null;
    private Long auftragId = null;
    private String anschlussart = null;
    private String esBMgmBez = null;
    private String esBHW_EQN = null;
    private String esBLbz = null;
    private String esAMgmBez = null;
    private String esAHW_EQN = null;
    private String esALbz = null;


    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }

    public String getAnschlussart() {
        return anschlussart;
    }

    public void setEsBMgmBez(String esBMgmBez) {
        this.esBMgmBez = esBMgmBez;
    }

    public String getEsBMgmBez() {
        return esBMgmBez;
    }

    public void setEsBHW_EQN(String esBHW_EQN) {
        this.esBHW_EQN = esBHW_EQN;
    }

    public String getEsBHW_EQN() {
        return esBHW_EQN;
    }

    public void setEsBLbz(String esBLbz) {
        this.esBLbz = esBLbz;
    }

    public String getEsBLbz() {
        return esBLbz;
    }

    public void setEsAMgmBez(String esAMgmBez) {
        this.esAMgmBez = esAMgmBez;
    }

    public String getEsAMgmBez() {
        return esAMgmBez;
    }

    public void setEsAHW_EQN(String esAHW_EQN) {
        this.esAHW_EQN = esAHW_EQN;
    }

    public String getEsAHW_EQN() {
        return esAHW_EQN;
    }

    public void setEsALbz(String esALbz) {
        this.esALbz = esALbz;
    }

    public String getEsALbz() {
        return esALbz;
    }

}
