/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.03.2012 14:08:02
 */
package de.augustakom.hurrican.model.wholesale;

/**
 * Modell-Klasse fuer die Abbildung eines EKP-Rahmenvertrags.
 */
public class WholesaleEkpFrameContract {

    private String ekpId;
    private String ekpFrameContractId;

    public String getEkpId() {
        return ekpId;
    }

    public void setEkpId(String ekpId) {
        this.ekpId = ekpId;
    }

    public String getEkpFrameContractId() {
        return ekpFrameContractId;
    }

    public void setEkpFrameContractId(String ekpFrameContractId) {
        this.ekpFrameContractId = ekpFrameContractId;
    }

}


