/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.WorkforceNotificationTypeBuilder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;

/**
 *
 */
public class ResourceBuilder implements WorkforceNotificationTypeBuilder<NotifyUpdateOrder.Resource> {

    private String id;
    private boolean fixed;

    @Override
    public NotifyUpdateOrder.Resource build() {
        NotifyUpdateOrder.Resource resource = new NotifyUpdateOrder.Resource();
        resource.setId(this.id);
        resource.setFixed(this.fixed);
        return resource;
    }

    public ResourceBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ResourceBuilder withFixed(boolean fixed) {
        this.fixed = fixed;
        return this;
    }
}