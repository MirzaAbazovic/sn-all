/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;

/**
 *
 */
public class CarrierIdentifikatorTypeBuilder extends V1AbstractBasicBuilder<CarrierIdentifikatorType> {

    public CarrierIdentifikatorTypeBuilder() {
        objectType = OBJECT_FACTORY.createCarrierIdentifikatorType();
    }

    public CarrierIdentifikatorTypeBuilder withCarrierCode(String carrierCode) {
        objectType.setCarrierCode(carrierCode);
        return this;
    }
}
