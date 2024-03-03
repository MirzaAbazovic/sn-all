/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPabgType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPaufType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;

/**
 *
 */
public class CancelCarrierChangeBuilder extends V1AbstractBasicBuilder<CancelCarrierChange> {

    public CancelCarrierChangeBuilder() {
        objectType = OBJECT_FACTORY.createCancelCarrierChange();
    }

    public CancelCarrierChangeBuilder withSTRAUFREC(StornoAufhebungEKPaufType type) {
        objectType.setSTRAUFREC(type);
        return this;
    }

    public CancelCarrierChangeBuilder withSTRAUFDON(StornoAufhebungEKPabgType type) {
        objectType.setSTRAUFDON(type);
        return this;
    }

    public CancelCarrierChangeBuilder withSTRAENREC(StornoAenderungEKPaufType type) {
        objectType.setSTRAENREC(type);
        return this;
    }

    public CancelCarrierChangeBuilder withSTRAENDON(StornoAenderungEKPabgType type) {
        objectType.setSTRAENDON(type);
        return this;
    }

}
