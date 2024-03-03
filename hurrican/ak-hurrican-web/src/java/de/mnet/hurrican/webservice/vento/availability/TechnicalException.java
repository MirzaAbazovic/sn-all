/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2012 16:02:43
 */
package de.mnet.hurrican.webservice.vento.availability;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Wenn im Availability Information Endpoint eine technische Exception fliegt, wird diese Classe an den Resolver
 * weitergeworfen
 */
@SoapFault(faultCode = FaultCode.SERVER)
public class TechnicalException extends Exception {

    private static final long serialVersionUID = -667002095835925566L;

    public TechnicalException(String message) {
        super(message);
    }
}


