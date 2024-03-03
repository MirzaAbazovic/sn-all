/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.common.errorhandling.marshal;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 * Used for marshalling error messages to be sent to the AtlasESB error handling service
 */
@Component
public class ErrorHandlingServiceMarshaller implements Marshaller {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlingServiceMarshaller.class);

    @Resource(name = "jaxb2MarshallerErrorHandlingService")
    private Jaxb2Marshaller errorHandlingServiceMarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return HandleError.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object graph, Result result) throws IOException, XmlMappingException {
        Preconditions.checkArgument(graph != null, "Object to marshal must not be null!");
        errorHandlingServiceMarshaller.marshal(graph, result);
    }
}
