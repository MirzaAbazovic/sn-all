/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypERLMKType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypRUEMPVType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UpdateOrder;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class UpdateOrderBuilder implements LineOrderTypeBuilder<UpdateOrder> {

    private AuftragstypType order;
    private UpdateOrder.Message message;

    @Override
    public UpdateOrder build() {
        UpdateOrder updateOrder = new UpdateOrder();
        updateOrder.setMessage(message);
        updateOrder.setOrder(order);
        return updateOrder;
    }

    public UpdateOrderBuilder setOrder(AuftragstypType order) {
        this.order = order;
        return this;
    }

    public UpdateOrderBuilder withERLMK(MeldungstypERLMKType meldungstypERLMKType) {
        this.message = buildMessage(meldungstypERLMKType);
        return this;
    }

    public UpdateOrderBuilder withRUEMPV(MeldungstypRUEMPVType meldungstypRUEMPVType) {
        this.message = buildMessage(meldungstypRUEMPVType);
        return this;
    }

    private UpdateOrder.Message buildMessage(MeldungstypERLMKType meldungstypERLMKType) {
        UpdateOrder.Message message = new UpdateOrder.Message();
        message.setERLMK(meldungstypERLMKType);
        return message;
    }

    private UpdateOrder.Message buildMessage(MeldungstypRUEMPVType meldungstypRUEMPVType) {
        UpdateOrder.Message message = new UpdateOrder.Message();
        message.setRUEMPV(meldungstypRUEMPVType);
        return message;
    }

}
