/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.model.builder.cdm.customer;

import java.util.*;
import javax.xml.datatype.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.builder.cdm.customer.v1.AddCommunicationBuilder;

public class AddCommunicationTestBuilder extends AddCommunicationBuilder {
    public AddCommunication buildValid() {
        try {
            withContractId("123");

            withCustomerId("234");

            withNotes("Some notes");

            withReason("WBCI_REASON");

            withType("WBCI");

            withTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

            return build();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
