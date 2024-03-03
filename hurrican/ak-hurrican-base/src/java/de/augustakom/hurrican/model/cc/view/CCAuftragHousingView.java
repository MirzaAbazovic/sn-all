/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2011 10:52:08
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * View fuer die Darstellung von Objekten des Typs <code>AuftragHousingView</code>
 *
 *
 */
public class CCAuftragHousingView extends AbstractCCModel implements CCAuftragModel {

    private Long auftragId;
    private Long kundeNo;
    private Long auftragHousingId;
    private Long transponderId;
    private String customerFirstName;
    private String customerLastName;

    public static CCAuftragHousingView createAuftragHousingView(Transponder transponder, AuftragHousingKey key, Auftrag auftrag) {
        CCAuftragHousingView view = new CCAuftragHousingView();
        view.setAuftragHousingId(key.getId());
        view.setAuftragId(key.getAuftragId());
        view.setKundeNo(auftrag.getKundeNo());
        view.setTransponderId(transponder.getTransponderId());
        view.setCustomerFirstName(transponder.getCustomerFirstName());
        view.setCustomerLastName(transponder.getCustomerLastName());
        return view;
    }

    public Long getKundeNo() {
        return kundeNo;
    }

    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getAuftragHousingId() {
        return auftragHousingId;
    }

    public void setAuftragHousingId(Long auftragHousingId) {
        this.auftragHousingId = auftragHousingId;
    }

    public Long getTransponderId() {
        return transponderId;
    }

    public void setTransponderId(Long transponderId) {
        this.transponderId = transponderId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

}
