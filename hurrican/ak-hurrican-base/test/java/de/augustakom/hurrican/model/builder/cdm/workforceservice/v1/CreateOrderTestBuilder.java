/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;

/**
 *
 */
public class CreateOrderTestBuilder extends CreateOrderBuilder {

    public CreateOrderTestBuilder() {
        withId("HUR_1d592bf7-992d-442c-992d-3732c4424a4d");
        withDisplayId("2096175-2014-09-11-03:36:18.111");
        withCustomerOrderId("2096175");
        // withPriority("priority");
        withDescription(new DescriptionTestBuilder().build());
        withType("Surf+Fon-Flat");
        withActivityType("RTL_Neu_MK_FttB");
        addContactPerson(new ContactPersonTestBuilder().withRole("Endkunde Montageleistung").build());
        addContactPerson(new ContactPersonTestBuilder().withRole("TECH_SERVICE").build());
        addQualification("Endkundenservice FTTB");
        withLocation(new AddressTestBuilder().build());
        // withFixedStartTime(DateTime.now());
        withRequestedTimeSlot(new RequestedTimeslotTestBuilder().build());
        withPlannedDuration(BigInteger.valueOf(45));
    }
}