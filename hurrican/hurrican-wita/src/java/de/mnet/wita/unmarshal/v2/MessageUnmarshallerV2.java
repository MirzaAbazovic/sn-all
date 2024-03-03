/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.NotifyUpdateOrder;
import de.mnet.wita.WitaMessage;

@SuppressWarnings({ "Duplicates", "unused" })
@Component("witaMessageUnmarshallerV2")
public class MessageUnmarshallerV2 implements Unmarshaller {

    @Resource(name = "jaxb2MarshallerLineOrderNotificationServiceV2")
    private Jaxb2Marshaller unmarshaller;

    @Autowired
    private NotifyUpdateOrderUnmarshallerV2 notifyUpdateOrderUnmarshaller;

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
