/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.marshal.location;

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

import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;

/**
 *
 */
@Component
public class LocationServiceMarshaller implements Marshaller {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(LocationServiceMarshaller.class);

    @Resource(name = "jaxb2MarshallerLocationService")
    private Jaxb2Marshaller locationServiceMarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return SearchRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object graph, Result result) throws IOException, XmlMappingException {
        Preconditions.checkArgument(graph != null, "Object to marshal must not be null!");
        locationServiceMarshaller.marshal(graph, result);
    }
}
