/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.wbci.model.CarrierCode;

/**
 *
 */
@Component
public class CarrierIdentifikatorUnmarshaller implements Function<CarrierIdentifikatorType, CarrierCode> {
    @Nullable
    @Override
    public CarrierCode apply(@Nullable CarrierIdentifikatorType input) {
        if (input != null) {
            return CarrierCode.fromITUCarrierCode(input.getCarrierCode());
        }
        return null;
    }
}
