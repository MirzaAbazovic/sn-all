/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Created by glinkjo on 16.02.2015.
 */
public class SBCAddressBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.SBCAddress> {

    private String address;
    private String addressType;

    @Override
    public OrderTechnicalParams.SBCAddress build() {
        OrderTechnicalParams.SBCAddress sbcAddress = new OrderTechnicalParams.SBCAddress();
        sbcAddress.setAddress(address);
        sbcAddress.setAddressType(addressType);
        return sbcAddress;
    }

    public SBCAddressBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public SBCAddressBuilder withAddressType(String addressType) {
        this.addressType = addressType;
        return this;
    }

}
