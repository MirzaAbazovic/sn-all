/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.unmarshal.v1;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.unmarshal.v1.request.terminverschiebung.TerminverschiebungUnmarshaller;

@Component
public class RescheduleCarrierChangeUnmarshaller implements Function<RescheduleCarrierChange, TerminverschiebungsAnfrage> {

    @Autowired
    private TerminverschiebungUnmarshaller terminverschiebungUnmarshaller;

    @Nullable
    @Override
    public TerminverschiebungsAnfrage apply(@Nullable RescheduleCarrierChange input) {
        if (input != null) {
            return terminverschiebungUnmarshaller.apply(input.getTVSVA());
        }
        return null;
    }

}
