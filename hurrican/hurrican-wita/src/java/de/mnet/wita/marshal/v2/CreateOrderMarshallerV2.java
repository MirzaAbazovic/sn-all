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

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.CreateOrder;
import de.mnet.wita.message.Auftrag;

@SuppressWarnings("Duplicates")
@Component
public class CreateOrderMarshallerV2 extends AbstractBaseMarshallerV2 implements Function<Auftrag, CreateOrder> {

    @Autowired
    private AuftragMarshallerV2 auftragMarshaller;

    @Nullable
    @Override
    public CreateOrder apply(@Nullable Auftrag input) {
        if (input == null) {
            return null;
        }

        CreateOrder createOrder = OBJECT_FACTORY.createCreateOrder();
        AuftragType auftragType = auftragMarshaller.apply(input);
        createOrder.setOrder(auftragType);

        return createOrder;
    }
}
