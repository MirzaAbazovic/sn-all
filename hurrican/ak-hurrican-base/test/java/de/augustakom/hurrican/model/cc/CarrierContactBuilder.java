/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.04.2014
 */
package de.augustakom.hurrican.model.cc;

/**
 *
 */
public class CarrierContactBuilder extends AbstractCCIDModelBuilder<CarrierContactBuilder, CarrierContact> {

    private Long carrierId = null;
    private String branchOffice = null;
    private String ressort = null;
    private String contactName = null;
    private String street = null;
    private String houseNum = null;
    private String postalCode = null;
    private String city = null;
    private String faultClearingPhone = null;
    private String faultClearingFax = null;
    private String faultClearingEmail = null;
    private String userW = null;
    private Long contactType;

    public CarrierContactBuilder withFaultClearingEmail(String faultClearingEmail) {
        this.faultClearingEmail = faultClearingEmail;
        return this;
    }

    public CarrierContactBuilder withContactType(Long contactType) {
        this.contactType = contactType;
        return this;
    }

    public CarrierContactBuilder withCarrier(Carrier carrier) {
        assert carrier.getId() != null;
        this.carrierId = carrier.getId();
        return this;
    }

}
