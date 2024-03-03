/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;
import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.Address;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;
import de.mnet.esb.cdm.resource.workforceservice.v1.RequestedTimeslot;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 *
 */
public class WorkforceOrderBuilder implements WorkforceTypeBuilder<WorkforceOrder> {

    private String id;
    private String displayId;
    private String customerOrderId;
    private String priority;
    private WorkforceOrder.Description description;
    private String type;
    private String activityType;
    private String activitySubtype;
    private List<ContactPerson> contactPersons;
    private List<String> qualifications;
    private Address location;
    private LocalDateTime fixedStartTime;
    private RequestedTimeslot requestedTimeSlot;
    private BigInteger plannedDuration;

    @Override
    public WorkforceOrder build() {
        WorkforceOrder workforceOrder = new WorkforceOrder();
        workforceOrder.setId(id);
        workforceOrder.setDisplayId(displayId);
        workforceOrder.setCustomerOrderId(customerOrderId);
        workforceOrder.setPriority(priority);
        workforceOrder.setDescription(description);
        workforceOrder.setType(type);
        workforceOrder.setActivityType(activityType);
        workforceOrder.setActivitySubtype(activitySubtype);
        if (contactPersons != null) {
            workforceOrder.getContactPerson().addAll(contactPersons);
        }
        if (qualifications != null) {
            workforceOrder.getQualification().addAll(qualifications);
        }
        workforceOrder.setLocation(location);
        workforceOrder.setFixedStartTime(fixedStartTime);
        workforceOrder.setRequestedTimeSlot(requestedTimeSlot);
        workforceOrder.setPlannedDuration(plannedDuration);
        return workforceOrder;
    }

    public WorkforceOrderBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public WorkforceOrderBuilder withDisplayId(String displayId) {
        this.displayId = displayId;
        return this;
    }

    public WorkforceOrderBuilder withCustomerOrderId(String customerOrderId) {
        this.customerOrderId = customerOrderId;
        return this;
    }

    public WorkforceOrderBuilder withPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public WorkforceOrderBuilder withDescription(WorkforceOrder.Description description) {
        this.description = description;
        return this;
    }

    public WorkforceOrderBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public WorkforceOrderBuilder withActivityType(String activityType) {
        this.activityType = activityType;
        return this;
    }

    public WorkforceOrderBuilder withActivitySubtype(String activitySubtype) {
        this.activitySubtype = activitySubtype;
        return this;
    }

    public WorkforceOrderBuilder addContactPerson(ContactPerson contactPerson) {
        if (this.contactPersons == null) {
            this.contactPersons = new ArrayList<>();
        }
        this.contactPersons.add(contactPerson);
        return this;
    }

    public WorkforceOrderBuilder addQualification(String qualification) {
        if (this.qualifications == null) {
            this.qualifications = new ArrayList<>();
        }
        this.qualifications.add(qualification);
        return this;
    }

    public WorkforceOrderBuilder withLocation(Address location) {
        this.location = location;
        return this;
    }

    public WorkforceOrderBuilder withFixedStartTime(LocalDateTime fixedStartTime) {
        this.fixedStartTime = fixedStartTime;
        return this;
    }

    public WorkforceOrderBuilder withRequestedTimeSlot(RequestedTimeslot requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
        return this;
    }

    public WorkforceOrderBuilder withPlannedDuration(BigInteger plannedDuration) {
        this.plannedDuration = plannedDuration;
        return this;
    }

}
