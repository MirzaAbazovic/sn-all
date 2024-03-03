/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;

public class CarrierIdentifikatorTypeTestBuilder extends CarrierIdentifikatorTypeBuilder {

    private String carrierCode;

    public CarrierIdentifikatorType buildValid() {
        CarrierIdentifikatorType type = new CarrierIdentifikatorType();
        type.setCarrierCode(carrierCode);
        return type;
    }

    public CarrierIdentifikatorTypeTestBuilder withCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
        return this;
    }

}
