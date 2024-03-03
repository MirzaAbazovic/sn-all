/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2014
 */
package de.mnet.wita.marshal.v1;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.CreateOrder;
import de.mnet.wita.message.Auftrag;

@Component
public class CreateOrderMarshaller extends AbstractBaseMarshaller implements Function<Auftrag, CreateOrder> {

    @Autowired
    private AuftragMarshaller auftragMarshaller;

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
