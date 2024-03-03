/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2008 10:10:04
 */
package de.augustakom.hurrican.model.billing.history;


/**
 * Modell für die ChangeBillSpec-Tabelle. Enthaelt die zukuenftigen Aenderungen der BillSpec-Objekte.
 *
 *
 */
public class ChangeBillSpec extends AbstractChangeModel {

    private Long billSpecNo = null;
    private Long adresseNo = null;

    /**
     * @return adresseNo
     */
    public Long getAdresseNo() {
        return adresseNo;
    }

    /**
     * @param adresseNo Festzulegender adresseNo
     */
    public void setAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
    }

    /**
     * @return billSpecNo
     */
    public Long getBillSpecNo() {
        return billSpecNo;
    }

    /**
     * @param billSpecNo Festzulegender billSpecNo
     */
    public void setBillSpecNo(Long billSpecNo) {
        this.billSpecNo = billSpecNo;
    }

}
