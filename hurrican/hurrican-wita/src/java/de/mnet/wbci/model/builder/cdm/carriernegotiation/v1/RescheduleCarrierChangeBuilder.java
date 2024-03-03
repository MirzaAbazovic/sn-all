/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;

/**
 *
 */
public class RescheduleCarrierChangeBuilder extends V1AbstractBasicBuilder<RescheduleCarrierChange> {

    public RescheduleCarrierChangeBuilder() {
        objectType = OBJECT_FACTORY.createRescheduleCarrierChange();
    }

    public RescheduleCarrierChangeBuilder withTVSA(TerminverschiebungType terminverschiebungType) {
        objectType.setTVSVA(terminverschiebungType);
        return this;
    }

}
