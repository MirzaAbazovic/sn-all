/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2014
 */
package de.mnet.wita.marshal.v2;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.RescheduleOrder;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TerminverschiebungType;
import de.mnet.wita.message.TerminVerschiebung;

@SuppressWarnings("Duplicates")
@Component
public class RescheduleOrderMarshallerV2 extends AbstractBaseMarshallerV2 implements Function<TerminVerschiebung, RescheduleOrder> {

    @Autowired
    private TerminVerschiebungMarshallerV2 terminVerschiebungMarshaller;

    @Nullable
    @Override
    public RescheduleOrder apply(@Nullable TerminVerschiebung input) {
        if (input == null) {
            return null;
        }

        RescheduleOrder rescheduleOrder = OBJECT_FACTORY.createRescheduleOrder();
        TerminverschiebungType tv = terminVerschiebungMarshaller.apply(input);
        rescheduleOrder.setOrder(tv);
        return rescheduleOrder;
    }
}
