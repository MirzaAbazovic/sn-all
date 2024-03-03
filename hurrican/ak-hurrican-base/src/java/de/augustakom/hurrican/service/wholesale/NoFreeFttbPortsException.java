/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 15:31:08
 */
package de.augustakom.hurrican.service.wholesale;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class NoFreeFttbPortsException extends RuntimeException {

    private static final long serialVersionUID = -4998086593115805958L;

}


