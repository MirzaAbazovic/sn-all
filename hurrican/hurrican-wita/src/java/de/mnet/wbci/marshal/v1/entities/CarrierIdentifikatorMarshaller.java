/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.CarrierCode;

@Component
public class CarrierIdentifikatorMarshaller extends AbstractBaseMarshaller implements
        Function<CarrierCode, CarrierIdentifikatorType> {
    @Nullable
    @Override
    public CarrierIdentifikatorType apply(@Nullable CarrierCode carrierCode) {
        if (carrierCode == null) {
            return null;
        }
        else {
            CarrierIdentifikatorType carrierIdentifikator = V1_OBJECT_FACTORY.createCarrierIdentifikatorType();
            carrierIdentifikator.setCarrierCode(carrierCode.getITUCarrierCode());
            return carrierIdentifikator;
        }
    }
}
