/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2014
 */
package de.mnet.wita.marshal;

import java.io.*;
import javax.xml.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.marshal.v1.MessageMarshaller;
import de.mnet.wita.marshal.v2.MessageMarshallerV2;

/**
 * Delegates marshalling operation according to the wita version.
 */
@Component("witaMessageMarshallerDelegate")
public class MessageMarshallerDelegate {

    @Autowired
    @Qualifier("witaMessageMarshallerV1")
    private MessageMarshaller messageMarshallerV1;

    @Autowired
    @Qualifier("witaMessageMarshallerV2")
    private MessageMarshallerV2 messageMarshallerV2;

    /**
     * Delegates marshalling operation according to the wita version.
     *
     * @param input  a WITA message
     * @param result a marshaled JAXB object
     * @throws IOException for a marshalling error <br/> {@link IllegalArgumentException} for an unsupported wita
     *                     version
     */
    public void marshal(Object input, Result result, WitaCdmVersion witaCdmVersion) throws IOException {
        switch (witaCdmVersion) {
            case V1:
                messageMarshallerV1.marshal(input, result);
                break;
            case V2:
                messageMarshallerV2.marshal(input, result);
                break;
            default:
                throw new IllegalArgumentException("Unsupported WITA CDM version " + witaCdmVersion);
        }
    }
}
