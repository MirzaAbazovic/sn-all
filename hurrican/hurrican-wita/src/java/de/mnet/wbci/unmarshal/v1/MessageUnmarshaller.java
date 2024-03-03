/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.unmarshal.v1;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RescheduleCarrierChange;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.model.WbciMessage;

/**
 *
 */
@Component
public class MessageUnmarshaller implements Unmarshaller {

    @Resource(name = "jaxb2MarshallerCarrierNegotiationV1")
    private Jaxb2Marshaller unmarshaller;

    @Autowired
    private UpdateCarrierChangeUnmarshaller updateCarrierChangeUnmarshaller;
    @Autowired
    private RequestCarrierChangeUnmarshaller requestCarrierChangeUnmarshaller;
    @Autowired
    private RescheduleCarrierChangeUnmarshaller rescheduleCarrierChangeUnmarshaller;
    @Autowired
    private CancelCarrierChangeUnmarshaller cancelCarrierChangeUnmarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return WbciMessage.class.isAssignableFrom(clazz);
    }

    @Override
    public WbciMessage unmarshal(Source source) throws IOException, XmlMappingException {
        if (source != null) {
            Object jaxbUnmarshalled = unmarshaller.unmarshal(source);

            if (jaxbUnmarshalled instanceof UpdateCarrierChange) {
                return updateCarrierChangeUnmarshaller.apply((UpdateCarrierChange) jaxbUnmarshalled);
            }
            else if (jaxbUnmarshalled instanceof RequestCarrierChange) {
                return requestCarrierChangeUnmarshaller.apply((RequestCarrierChange) jaxbUnmarshalled);
            }
            else if (jaxbUnmarshalled instanceof RescheduleCarrierChange) {
                return rescheduleCarrierChangeUnmarshaller.apply((RescheduleCarrierChange) jaxbUnmarshalled);
            }
            else if (jaxbUnmarshalled instanceof CancelCarrierChange) {
                return cancelCarrierChangeUnmarshaller.apply((CancelCarrierChange) jaxbUnmarshalled);
            }

            String className = (jaxbUnmarshalled != null) ? jaxbUnmarshalled.getClass().getName() : null;
            throw new UnmarshallingFailureException(
                    String.format("Unmarshalling of type %s not yet supported.", className));
        }
        throw new UnmarshallingFailureException("NULL as source not supported!");
    }
}
