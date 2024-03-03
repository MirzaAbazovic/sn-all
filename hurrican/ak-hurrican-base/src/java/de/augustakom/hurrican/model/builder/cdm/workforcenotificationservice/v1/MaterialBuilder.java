/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.math.*;

import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.WorkforceNotificationTypeBuilder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;

/**
 *
 */
public class MaterialBuilder implements WorkforceNotificationTypeBuilder<NotifyOrderFeedback.Material> {

    private String id;
    private String serialNumber;
    private String summary;
    private String description;
    private BigDecimal quantity;

    @Override
    public NotifyOrderFeedback.Material build() {
        NotifyOrderFeedback.Material material = new NotifyOrderFeedback.Material();
        material.setId(this.id);
        material.setSerialNumber(this.serialNumber);
        material.setSummary(this.summary);
        material.setDescription(this.description);
        material.setQuantity(this.quantity);
        return material;
    }

    public MaterialBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public MaterialBuilder withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public MaterialBuilder withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public MaterialBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public MaterialBuilder withQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }
}