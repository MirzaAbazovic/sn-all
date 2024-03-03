/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.model.builder.cdm.customer.v1;

import javax.xml.datatype.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;

public class AddCommunicationBuilder extends V1CustomerTypeBuilder<AddCommunication> {
    public AddCommunicationBuilder() {
        this.objectType = OBJECT_FACTORY.createAddCommunication();
    }

    public AddCommunicationBuilder withContractId(String contractId) {
        AddCommunication.Context communicationContext = OBJECT_FACTORY.createAddCommunicationContext();
        communicationContext.setContractId(contractId);
        objectType.setContext(communicationContext);
        return this;
    }

    public AddCommunicationBuilder withCustomerId(String customerId) {
        objectType.setCustomerId(customerId);
        return this;
    }

    public AddCommunicationBuilder withNotes(String notes) {
        objectType.setNotes(notes);
        return this;
    }

    public AddCommunicationBuilder withReason(String reason) {
        objectType.setReason(reason);
        return this;
    }

    public AddCommunicationBuilder withTime(XMLGregorianCalendar time) {
        objectType.setTime(time);
        return this;
    }

    public AddCommunicationBuilder withType(String str) {
        objectType.setType(str);
        return this;
    }

}
