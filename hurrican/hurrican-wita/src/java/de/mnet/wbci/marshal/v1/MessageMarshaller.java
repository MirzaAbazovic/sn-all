/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.13
 */
package de.mnet.wbci.marshal.v1;

import java.io.*;
import javax.annotation.*;
import javax.xml.transform.*;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import de.mnet.wbci.marshal.v1.meldung.UpdateCarrierChangeMarshaller;
import de.mnet.wbci.marshal.v1.request.CancelCarrierChangeMarshaller;
import de.mnet.wbci.marshal.v1.request.RequestCarrierChangeMarshaller;
import de.mnet.wbci.marshal.v1.request.RescheduleCarrierChangeMarshaller;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequest;

/**
 * The RequestCarrierChangeMarshaller decides which mnet.wbci.fromat object fits to the corresponding JAXBElement.
 *
 *
 */
@Component
public class MessageMarshaller implements Marshaller {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MessageMarshaller.class);

    @Resource(name = "jaxb2MarshallerCarrierNegotiationV1")
    private Jaxb2Marshaller jaxbMarshallerForV1;

    @Autowired
    private RequestCarrierChangeMarshaller requestMarshaller;

    @Autowired
    private UpdateCarrierChangeMarshaller updateMarshaller;

    @Autowired
    private RescheduleCarrierChangeMarshaller rescheduleMarshaller;

    @Autowired
    private CancelCarrierChangeMarshaller cancelMarshaller;

    @Override
    public boolean supports(Class<?> clazz) {
        return WbciRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object input, Result result) throws IOException, XmlMappingException {
        Preconditions.checkArgument(input != null, "Object to marshal must not be null!");

        // must be an JAXBElement<?> or a XmlRootElement
        Object jaxbElement = null;

        validate(input);

        if (input instanceof VorabstimmungsAnfrage) {
            LOG.debug("Marshalling wbci request (VA) instance");
            jaxbElement = requestMarshaller.apply((VorabstimmungsAnfrage) input);
        }
        else if (input instanceof TerminverschiebungsAnfrage) {
            LOG.debug("Marshalling wbci request (TV) instance");
            jaxbElement = rescheduleMarshaller.apply((TerminverschiebungsAnfrage) input);
        }
        else if (input instanceof StornoAnfrage) {
            LOG.debug("Marshalling wbci request (STORNO) instance");
            jaxbElement = cancelMarshaller.apply((StornoAnfrage) input);
        }
        else if (input instanceof Meldung<?>) {
            LOG.debug("Marshalling wbci meldung instance");
            jaxbElement = updateMarshaller.apply((Meldung<?>) input);
        }

        if (jaxbElement != null) {
            jaxbMarshallerForV1.marshal(jaxbElement, result);
        }
    }

    private void validate(Object graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Input is not allowed to be null.");
        }
    }
}
