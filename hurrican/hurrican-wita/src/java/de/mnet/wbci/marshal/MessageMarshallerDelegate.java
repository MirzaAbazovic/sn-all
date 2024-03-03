/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.13
 */
package de.mnet.wbci.marshal;

import java.io.*;
import javax.xml.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.wbci.marshal.v1.MessageMarshaller;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wita.exceptions.WitaConfigException;
import de.mnet.wita.service.WitaConfigService;

/**
 * Delegates marshalling operation to cdm version specific marshaller implementation. Cdm version is evaluated according
 * to input object and Partner EKP (EKP different than M-net) carrier wbci configuration.
 *
 *
 */
@Component("wbciMessageMarshallerDelegate")
public class MessageMarshallerDelegate {

    @Autowired
    private WitaConfigService witaConfigService;

    @Autowired
    private MessageMarshaller messageMarshallerV1;

    /**
     * Evaluates cdm version for input message and abgebenderEKP and delegates further processing to version specific
     * message marshaller.
     *
     * @param input  a WBCI message
     * @param result a marshaled JAXB object
     * @throws IOException for a marshalling error <br/> {@link IllegalArgumentException} for a unvaild input <br/>
     *                     {@link WitaConfigException} if no cdm version for the carrier is defined in the table
     *                     'T_WITA_CONFIG'
     */
    public void marshal(Object input, Result result, WbciCdmVersion wbciCdmVersion) throws IOException {
        switch (wbciCdmVersion) {
            case V1:
                messageMarshallerV1.marshal(input, result);
                break;
            default:
                throw new IllegalArgumentException("Unsupported CDM version " + wbciCdmVersion);
        }
    }
}
