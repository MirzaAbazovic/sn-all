/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;

/**
 *
 */
public class UpdateCarrierChangeBuilder extends V1AbstractBasicBuilder<UpdateCarrierChange> {

    public UpdateCarrierChangeBuilder() {
        objectType = OBJECT_FACTORY.createUpdateCarrierChange();
    }

    public UpdateCarrierChangeBuilder withRUEMVA(MeldungRUEMVAType meldungRUEMVAType) {
        objectType.setRUEMVA(meldungRUEMVAType);
        return this;
    }

    public UpdateCarrierChangeBuilder withABBM(MeldungABBMType meldungABBMType) {
        objectType.setABBM(meldungABBMType);
        return this;
    }

    public UpdateCarrierChangeBuilder withABBMTR(MeldungABBMTRType meldungABBMTRType) {
        objectType.setABBMTR(meldungABBMTRType);
        return this;
    }

    public UpdateCarrierChangeBuilder withAKMTR(MeldungAKMTRType meldungAKMTRType) {
        objectType.setAKMTR(meldungAKMTRType);
        return this;
    }

    public UpdateCarrierChangeBuilder withERLM(MeldungERLMType meldungERLMType) {
        objectType.setERLM(meldungERLMType);
        return this;
    }
}
