/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;

/**
 *
 */
public class EKPTypeBuilder extends V1AbstractBasicBuilder<EKPType> {

    public EKPTypeBuilder() {
        objectType = OBJECT_FACTORY.createEKPType();
    }

    public EKPTypeBuilder withEKPauf(CarrierIdentifikatorType ekPauf) {
        objectType.setEKPauf(ekPauf);
        return this;
    }

    public EKPTypeBuilder withEKPabg(CarrierIdentifikatorType ekPabg) {
        objectType.setEKPabg(ekPabg);
        return this;
    }
}
