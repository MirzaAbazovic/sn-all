/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;
import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.Address;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;
import de.mnet.esb.cdm.resource.workforceservice.v1.CreateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.RequestedTimeslot;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 *
 */
public class CreateOrderBuilder implements WorkforceTypeBuilder<CreateOrder> {

    private String id;
    private String displayId;
    private String customerOrderId;
    private String priority;
    private WorkforceOrder.Description description;
    private String type;
    private String activityType;
    private List<ContactPerson> contactPerson;
    private List<String> qualification;
    private Address location;
    private LocalDateTime fixedStartTime;
    private RequestedTimeslot requestedTimeSlot;
    private BigInteger plannedDuration;

    @Override
    public CreateOrder build() {
        WorkforceOrder wfOrder = new WorkforceOrder();
        wfOrder.setId(this.id);
        wfOrder.setDisplayId(this.displayId);
        wfOrder.setCustomerOrderId(this.customerOrderId);
        wfOrder.setPriority(this.priority);
        wfOrder.setDescription(this.description);
        wfOrder.setType(this.type);
        wfOrder.setActivityType(this.activityType);
        if (null != this.contactPerson) {
            wfOrder.getContactPerson().addAll(this.contactPerson);
        }
        if (null != this.qualification) {
            wfOrder.getQualification().addAll(this.qualification);
        }
        wfOrder.setLocation(this.location);
        wfOrder.setFixedStartTime(this.fixedStartTime);
        wfOrder.setRequestedTimeSlot(this.requestedTimeSlot);
        wfOrder.setPlannedDuration(this.plannedDuration);

        CreateOrder createOrder = new CreateOrder();
        createOrder.setOrder(wfOrder);
        return createOrder;
    }

    public CreateOrderBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public CreateOrderBuilder withDisplayId(String displayId) {
        this.displayId = displayId;
        return this;
    }

    public CreateOrderBuilder withCustomerOrderId(String customerOrderId) {
        this.customerOrderId = customerOrderId;
        return this;
    }

    public CreateOrderBuilder withPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public CreateOrderBuilder withDescription(WorkforceOrder.Description description) {
        this.description = description;
        return this;
    }

    public CreateOrderBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public CreateOrderBuilder withActivityType(String activityType) {
        this.activityType = activityType;
        return this;
    }

    public CreateOrderBuilder withContactPersons(List<ContactPerson> contactPerson) {
        this.contactPerson = contactPerson;
        return this;
    }

    public CreateOrderBuilder addContactPerson(ContactPerson contactPerson) {
        if (null == this.contactPerson) {
            this.contactPerson = new ArrayList<ContactPerson>();
        }
        this.contactPerson.add(contactPerson);
        return this;
    }

    public CreateOrderBuilder withQualifications(List<String> qualifications) {
        this.qualification = qualifications;
        return this;
    }

    public CreateOrderBuilder addQualification(String qualification) {
        if (null == this.qualification) {
            this.qualification = new ArrayList<String>();
        }
        this.qualification.add(qualification);
        return this;
    }

    public CreateOrderBuilder withLocation(Address location) {
        this.location = location;
        return this;
    }

    public CreateOrderBuilder withFixedStartTime(LocalDateTime fixedStartTime) {
        this.fixedStartTime = fixedStartTime;
        return this;
    }

    public CreateOrderBuilder withRequestedTimeSlot(RequestedTimeslot requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
        return this;
    }

    public CreateOrderBuilder withPlannedDuration(BigInteger plannedDuration) {
        this.plannedDuration = plannedDuration;
        return this;
    }

}
