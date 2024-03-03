/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationRequestTypeTestBuilder;

/**
 *
 */
public class RequestCarrierChangeTestBuilder extends RequestCarrierChangeBuilder implements
        CarrierNegotiationRequestTypeTestBuilder<RequestCarrierChange> {
    @Override
    public RequestCarrierChange buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        switch (geschaeftsfallEnumType) {
            case VA_KUE_MRN:
                if (objectType.getVAKUEMRN() == null) {
                    withVAKUEMRN(new KuendigungMitRNPGeschaeftsfallTypeTestBuilder().buildValid(geschaeftsfallEnumType));
                }
                break;
            case VA_KUE_ORN:
                if (objectType.getVAKUEORN() == null) {
                    withVAKUEORN(new KuendigungOhneRNPGeschaeftsfallTypeTestBuilder()
                            .buildValid(geschaeftsfallEnumType));
                }
                break;
            case VA_RRNP:
                if (objectType.getVARRNP() == null) {
                    withVARRNP(new ReineRufnummernportierungGeschaeftsfallTypeTestBuilder()
                            .buildValid(geschaeftsfallEnumType));
                }
                break;
            default:
                throw new IllegalArgumentException("GeschaeftsfallEnumType '" + geschaeftsfallEnumType
                        + "' not supported in this case");
        }

        return build();
    }
}
