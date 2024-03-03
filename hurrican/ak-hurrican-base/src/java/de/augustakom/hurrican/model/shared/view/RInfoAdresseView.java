/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2007 13:28:03
 */
package de.augustakom.hurrican.model.shared.view;

import de.augustakom.hurrican.model.billing.view.KundeAdresseView;


/**
 * View fuer Rechnungsdaten und Rechnungsadresse.
 *
 *
 */
public class RInfoAdresseView extends KundeAdresseView {

    private String extDebitorNo = null;
    private Long rInfoNoOrig = null;


    /**
     * @return rInfoNoOrig
     */
    public Long getRInfoNoOrig() {
        return rInfoNoOrig;
    }


    /**
     * @param infoNoOrig Festzulegender rInfoNoOrig
     */
    public void setRInfoNoOrig(Long infoNoOrig) {
        rInfoNoOrig = infoNoOrig;
    }


    /**
     * @return extDebitorNo
     */
    public String getExtDebitorNo() {
        return extDebitorNo;
    }


    /**
     * @param extDebitorNo Festzulegender extDebitorNo
     */
    public void setExtDebitorNo(String extDebitorNo) {
        this.extDebitorNo = extDebitorNo;
    }


}


