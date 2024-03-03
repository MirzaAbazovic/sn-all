/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.wbci.model.CarrierCode;

/**
 *
 */
public class EKPMeldungTypeTestBuilder extends EKPTypeBuilder {

    public EKPType buildValid() {
        if (objectType.getEKPabg() == null) {
            withEKPabg(new CarrierIdentifikatorTypeBuilder().withCarrierCode(CarrierCode.DTAG.getITUCarrierCode())
                    .build());
        }
        if (objectType.getEKPauf() == null) {
            withEKPauf(new CarrierIdentifikatorTypeBuilder().withCarrierCode(CarrierCode.MNET.getITUCarrierCode())
                    .build());
        }
        return build();
    }
}
