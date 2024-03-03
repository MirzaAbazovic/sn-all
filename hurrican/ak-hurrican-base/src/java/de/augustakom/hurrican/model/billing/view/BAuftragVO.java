/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.13
 */
package de.augustakom.hurrican.model.billing.view;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * VO fuer einige div. Auftrags-Informationen.
 */
public class BAuftragVO extends AbstractBillingModel implements KundenModel {

    private Long kundeNo;
    private Long auftragNoOrig;
    private String customerName;
    private String street;
    private String city;
    private String mainDn;
    private Date kuendigungsdatum;

    public BAuftragVO(@NotNull BAuftrag billingAuftrag, Adresse accesspointAddress, Rufnummer mainDn) {
        setAuftragNoOrig(billingAuftrag.getAuftragNoOrig());
        setKundeNo(billingAuftrag.getKundeNo());
        setKuendigungsdatum(billingAuftrag.getKuendigungsdatum());

        if (accesspointAddress != null) {
            setCustomerName(accesspointAddress.getCombinedNameData());
            setStreet(accesspointAddress.getCombinedStreetData());
            setCity(accesspointAddress.getCombinedOrtOrtsteil());
        }

        if (mainDn != null) {
            setMainDn(mainDn.getRufnummer());
        }
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    public Long getKundeNo() {
        return kundeNo;
    }

    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMainDn() {
        return mainDn;
    }

    public void setMainDn(String mainDn) {
        this.mainDn = mainDn;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getKuendigungsdatum() {
        return kuendigungsdatum;
    }

    public void setKuendigungsdatum(Date kuendigungsdatum) {
        this.kuendigungsdatum = kuendigungsdatum;
    }
}
