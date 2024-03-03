/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2015
 */
package de.augustakom.hurrican.model.billing;

@SuppressWarnings("unused")
public class RInfoBuilder extends BillingEntityBuilder<RInfoBuilder, RInfo> {

    private Long rinfoNo = null;
    private String description = "Ueberweisung";
    private Long kundeNo = 200000407L;
    private Long adresseNo = 111L;
    private String extDebitorId = "A2000000407";
    private Boolean invElectronic = Boolean.TRUE;
    private Boolean invMaxi = Boolean.TRUE;
    private Long finanzNo = 200000407L;

    public RInfoBuilder withRinfoNo(Long rinfoNo) {
        this.rinfoNo = rinfoNo;
        return this;
    }

    public RInfoBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public RInfoBuilder withAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
        return this;
    }

    public RInfoBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public RInfoBuilder withExtDebitorId(String extDebitorId) {
        this.extDebitorId = extDebitorId;
        return this;
    }

}
