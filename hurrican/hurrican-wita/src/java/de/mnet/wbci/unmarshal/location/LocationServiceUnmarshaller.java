/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.unmarshal.location;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;

/**
 *
 */
@Component
public class LocationServiceUnmarshaller implements Unmarshaller {
    @Resource(name = "jaxb2MarshallerLocationService")
    private Jaxb2Marshaller locationServiceUnmarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return SearchResponse.class.isAssignableFrom(clazz);
    }

    @Override
    public SearchResponse unmarshal(Source source) throws IOException, XmlMappingException {
        if (source == null) {
            throw new UnmarshallingFailureException("NULL as source not supported!");
        }

        Object objectGraph = locationServiceUnmarshaller.unmarshal(source);

        if (objectGraph instanceof SearchResponse) {
            return (SearchResponse) objectGraph;
        }
        else {
            String className = (objectGraph != null) ? objectGraph.getClass().getName() : null;
            throw new UnmarshallingFailureException(
                    String.format("Unmarshalling of type %s not yet supported.", className));
        }
    }
}
