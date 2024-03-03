/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2016
 */
package de.mnet.common.service.impl;

import java.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.marshal.MessageMarshallerDelegate;

public class WitaMessageHistoryCreator {

    @Autowired
    private MessageMarshallerDelegate messageMarshaller;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    public String createSoapAsString(WitaMessage witaMessage, WitaCdmVersion witaCdmVersion) {
        try {
            final SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
            messageMarshaller.marshal(witaMessage, soapMessage.getSoapBody().getPayloadResult(), witaCdmVersion);

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            soapMessage.writeTo(bos);

            final String soapAsString = bos.toString(System.getProperty("file.encoding", "UTF-8"));
            return soapAsString;
        }
        catch (IOException e) {
            throw new RuntimeException("Error on generating wita soap message", e);
        }
    }

}
