/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;

/**
 *
 */
public class RequestCarrierChangeBuilder extends V1AbstractBasicBuilder<RequestCarrierChange> {

    public RequestCarrierChangeBuilder() {
        objectType = OBJECT_FACTORY.createRequestCarrierChange();
    }

    public RequestCarrierChangeBuilder withVAKUEMRN(KuendigungMitRNPGeschaeftsfallType vakuemrn) {
        objectType.setVAKUEMRN(vakuemrn);
        return this;
    }

    public RequestCarrierChangeBuilder withVAKUEORN(KuendigungOhneRNPGeschaeftsfallType vakueorn) {
        objectType.setVAKUEORN(vakueorn);
        return this;
    }

    public RequestCarrierChangeBuilder withVARRNP(ReineRufnummernportierungGeschaeftsfallType varrnp) {
        objectType.setVARRNP(varrnp);
        return this;
    }

}
