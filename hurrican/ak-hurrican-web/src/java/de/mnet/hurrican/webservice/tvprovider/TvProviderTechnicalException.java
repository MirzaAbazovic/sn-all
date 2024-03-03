/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2013 11:42:33
 */
package de.mnet.hurrican.webservice.tvprovider;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class TvProviderTechnicalException extends Exception {
    private static final long serialVersionUID = -667002095835925566L;

    public TvProviderTechnicalException(String message) {
        super(message);
    }
}
