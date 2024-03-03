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
public class StateInfoBuilder implements WorkforceNotificationTypeBuilder<NotifyUpdateOrder.StateInfo> {

    private String state;
    private String extended;

    @Override
    public NotifyUpdateOrder.StateInfo build() {
        NotifyUpdateOrder.StateInfo stateInfo = new NotifyUpdateOrder.StateInfo();
        stateInfo.setState(this.state);
        stateInfo.setExtended(this.extended);
        return stateInfo;
    }

    public StateInfoBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public StateInfoBuilder withExtended(String extended) {
        this.extended = extended;
        return this;
    }
}