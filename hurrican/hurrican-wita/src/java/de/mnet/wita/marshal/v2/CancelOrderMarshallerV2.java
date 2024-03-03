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

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.CancelOrder;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StornierungType;
import de.mnet.wita.message.Storno;

@SuppressWarnings("Duplicates")
@Component
public class CancelOrderMarshallerV2 extends AbstractBaseMarshallerV2 implements Function<Storno, CancelOrder> {

    @Autowired
    private StornoMarshallerV2 stornoMarshaller;

    @Nullable
    @Override
    public CancelOrder apply(@Nullable Storno input) {
        if (input == null) {
            return null;
        }

        CancelOrder cancelOrder = OBJECT_FACTORY.createCancelOrder();
        StornierungType stornierungType = stornoMarshaller.apply(input);
        cancelOrder.setOrder(stornierungType);

        return cancelOrder;
    }
}
