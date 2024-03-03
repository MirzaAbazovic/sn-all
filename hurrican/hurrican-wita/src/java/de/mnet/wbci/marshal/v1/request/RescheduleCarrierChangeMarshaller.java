/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.request.terminverschiebung.TerminverschiebungMarshaller;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;

@Component
public class RescheduleCarrierChangeMarshaller extends AbstractBaseMarshaller implements
        Function<TerminverschiebungsAnfrage, RescheduleCarrierChange> {

    @Autowired
    private TerminverschiebungMarshaller terminverschiebungMarshaller;

    @Nullable
    @Override
    public RescheduleCarrierChange apply(@Nullable TerminverschiebungsAnfrage input) {
        if (input == null) {
            return null;
        }
        RescheduleCarrierChange rescheduleCarrierChange = V1_OBJECT_FACTORY.createRescheduleCarrierChange();
        rescheduleCarrierChange.setTVSVA(terminverschiebungMarshaller.apply(input));
        return rescheduleCarrierChange;
    }

}
