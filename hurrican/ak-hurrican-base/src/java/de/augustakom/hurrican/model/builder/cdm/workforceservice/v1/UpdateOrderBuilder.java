/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.UpdateOrderDescriptionBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;
import de.mnet.esb.cdm.resource.workforceservice.v1.UpdateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

public class UpdateOrderBuilder implements WorkforceTypeBuilder<UpdateOrder> {

    private UpdateOrder.Description description;
    private Collection<ContactPerson> contactPersons;
    private String workforceOrderId;

    @Override
    public UpdateOrder build() {
        UpdateOrder updateOrder = new UpdateOrder();
        updateOrder.setDescription(description);
        updateOrder.getContactPerson().addAll(contactPersons);
        updateOrder.setId(workforceOrderId);
        return updateOrder;
    }

    public UpdateOrderBuilder withWorkforceOrder(WorkforceOrder workforceOrder) {
        if (workforceOrder.getDescription() != null) {
            description = new UpdateOrderDescriptionBuilder()
                    .withDetails(workforceOrder.getDescription().getDetails())
                    .withTechParams(workforceOrder.getDescription().getTechParams())
                    .build();
        }
        contactPersons = workforceOrder.getContactPerson();
        return this;
    }

    public UpdateOrderBuilder withId(String workforceOrderId) {
        this.workforceOrderId = workforceOrderId;
        return this;
    }
}
