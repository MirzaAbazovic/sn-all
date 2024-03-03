/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.NotifyUpdateOrder;
import de.mnet.wita.WitaMessage;

@Component("witaMessageUnmarshallerV1")
public class MessageUnmarshaller implements Unmarshaller {

    @Resource(name = "jaxb2MarshallerLineOrderNotificationServiceV1")
    private Jaxb2Marshaller unmarshaller;

    @Autowired
    private NotifyUpdateOrderUnmarshaller notifyUpdateOrderUnmarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return WitaMessage.class.isAssignableFrom(clazz);
    }

    @Override
    public WitaMessage unmarshal(Source source) throws IOException, XmlMappingException {
        if (source != null) {
            Object jaxbUnmarshalled = unmarshaller.unmarshal(source);

            if (jaxbUnmarshalled instanceof NotifyUpdateOrder) {
                return notifyUpdateOrderUnmarshaller.apply((NotifyUpdateOrder) jaxbUnmarshalled);
            }

            String className = (jaxbUnmarshalled != null) ? jaxbUnmarshalled.getClass().getName() : null;
            throw new UnmarshallingFailureException(String.format("Unmarshalling of type %s not yet supported.", className));
        }
        throw new UnmarshallingFailureException("NULL as source not supported!");
    }

}
