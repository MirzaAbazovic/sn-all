/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 *
 */
public class DescriptionBuilder implements WorkforceTypeBuilder<WorkforceOrder.Description> {

    private String summary;
    private String details;
    private OrderTechnicalParams techParams;

    @Override
    public WorkforceOrder.Description build() {
        WorkforceOrder.Description description = new WorkforceOrder.Description();
        description.setSummary(this.summary);
        description.setDetails(this.details);
        description.setTechParams(this.techParams);
        return description;
    }

    public DescriptionBuilder withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public DescriptionBuilder withDetails(String details) {
        this.details = details;
        return this;
    }

    public DescriptionBuilder withOrderTechnicalParams(OrderTechnicalParams techParams) {
        this.techParams = techParams;
        return this;
    }
}