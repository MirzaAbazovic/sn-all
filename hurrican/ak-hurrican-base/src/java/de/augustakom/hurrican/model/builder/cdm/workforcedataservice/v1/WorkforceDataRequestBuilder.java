/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforcedataservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforcedataservice.WorkforceDataTypeBuilder;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataRequest;

/**
 *
 */
public class WorkforceDataRequestBuilder implements WorkforceDataTypeBuilder<WorkforceDataRequest> {

    private long technicalOrderId;
    private String incidentReason;

    @Override
    public WorkforceDataRequest build() {
        WorkforceDataRequest workforceDataRequest = new WorkforceDataRequest();
        workforceDataRequest.setTechnicalOrderId(technicalOrderId);
        workforceDataRequest.setIncidentReason(incidentReason);
        return workforceDataRequest;
    }

    public WorkforceDataRequestBuilder withTechnicalOrderId(long technicalOrderId) {
        this.technicalOrderId = technicalOrderId;
        return this;
    }

    public WorkforceDataRequestBuilder withIncidentReason(String incidentReason) {
        this.incidentReason = incidentReason;
        return this;
    }

}
