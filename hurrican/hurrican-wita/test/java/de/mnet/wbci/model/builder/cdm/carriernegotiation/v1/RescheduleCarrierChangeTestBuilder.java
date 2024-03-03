/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationRequestTypeTestBuilder;

/**
 *
 */
public class RescheduleCarrierChangeTestBuilder extends RescheduleCarrierChangeBuilder implements
        CarrierNegotiationRequestTypeTestBuilder<RescheduleCarrierChange> {
    @Override
    public RescheduleCarrierChange buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (objectType.getTVSVA() == null) {
            objectType.setTVSVA(new TerminverschiebungTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        return build();
    }
}
