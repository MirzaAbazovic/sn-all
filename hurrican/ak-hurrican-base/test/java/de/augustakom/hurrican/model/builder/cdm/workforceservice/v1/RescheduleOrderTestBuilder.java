/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class RescheduleOrderTestBuilder extends RescheduleOrderBuilder {

    public RescheduleOrderTestBuilder() {
        // withFixedStartTime(DateTime.now());
        withOrderId("HUR_1d592bf7-992d-442c-992d-3732c4424a4d");
        withRequestedTimeSlot(new RequestedTimeslotTestBuilder().build());
    }
}