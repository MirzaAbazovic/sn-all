/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.DeleteOrder;

/**
 *
 */
public class DeleteOrderBuilder implements WorkforceTypeBuilder<DeleteOrder> {

    private String id;

    @Override
    public DeleteOrder build() {
        DeleteOrder deleteOrder = new DeleteOrder();
        deleteOrder.setId(this.id);
        return deleteOrder;
    }

    public DeleteOrderBuilder withId(String id) {
        this.id = id;
        return this;
    }
}