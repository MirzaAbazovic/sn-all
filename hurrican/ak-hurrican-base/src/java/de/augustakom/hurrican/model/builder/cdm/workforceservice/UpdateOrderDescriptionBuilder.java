/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice;

import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.UpdateOrder;

public class UpdateOrderDescriptionBuilder implements WorkforceTypeBuilder<UpdateOrder.Description> {

    private String details;
    private OrderTechnicalParams techParams;

    @Override
    public UpdateOrder.Description build() {
        UpdateOrder.Description description = new UpdateOrder.Description();
        description.setDetails(details);
        description.setTechParams(techParams);
        return description;
    }

    public UpdateOrderDescriptionBuilder withDetails(String details) {
        this.details = details;
        return this;
    }

    public UpdateOrderDescriptionBuilder withTechParams(OrderTechnicalParams techParams) {
        this.techParams = techParams;
        return this;
    }
}
