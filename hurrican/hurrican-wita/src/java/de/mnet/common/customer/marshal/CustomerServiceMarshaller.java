/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.common.customer.marshal;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import com.google.common.base.Preconditions;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;

/**
 * Used for marshalling customer service messages to be sent to the AtlasESB customer service
 */
@Component
public class CustomerServiceMarshaller implements Marshaller {
    @Resource(name = "jaxb2MarshallerCustomerService")
    private Jaxb2Marshaller customerServiceMarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return AddCommunication.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object graph, Result result) throws IOException, XmlMappingException {
        Preconditions.checkArgument(graph != null, "Object to marshal must not be null!");
        customerServiceMarshaller.marshal(graph, result);
    }

}
