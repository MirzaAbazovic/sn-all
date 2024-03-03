/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.meldung.OutgoingMeldung;

@SuppressWarnings("Duplicates")
@Component("witaMessageMarshallerV1")
public class MessageMarshaller implements Marshaller {

    @Resource(name = "jaxb2MarshallerLineOrderServiceV1")
    private Jaxb2Marshaller marshaller;

    @Autowired
    private CreateOrderMarshaller createOrderMarshaller;
    @Autowired
    private CancelOrderMarshaller cancelOrderMarshaller;
    @Autowired
    private RescheduleOrderMarshaller rescheduleOrderMarshaller;
    @Autowired
    private UpdateOrderMarshaller updateOrderMarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return WitaMessage.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object input, Result result) throws IOException, XmlMappingException {
        Preconditions.checkArgument(input != null, "Object to marshal must not be null!");

        // must be an JAXBElement<?> or a XmlRootElement
        Object jaxbElement = null;

        validate(input);

        if (input instanceof Storno) {
            jaxbElement = cancelOrderMarshaller.apply((Storno) input);
        }
        else if (input instanceof Auftrag) {
            jaxbElement = createOrderMarshaller.apply((Auftrag) input);
        }
        else if (input instanceof TerminVerschiebung) {
            jaxbElement = rescheduleOrderMarshaller.apply((TerminVerschiebung) input);
        }
        else if (input instanceof OutgoingMeldung) {
            jaxbElement = updateOrderMarshaller.apply((OutgoingMeldung) input);
        }

        if (jaxbElement != null) {
            marshaller.marshal(jaxbElement, result);
        }
    }

    public void validate(Object graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Input is not allowed to be null.");
        }
    }

    public void setMarshaller(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }

}
