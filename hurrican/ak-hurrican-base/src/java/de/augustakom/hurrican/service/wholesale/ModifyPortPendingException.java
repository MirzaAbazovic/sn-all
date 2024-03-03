/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 17:01:44
 */
package de.augustakom.hurrican.service.wholesale;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class ModifyPortPendingException extends WholesaleException {
    private static final long serialVersionUID = -998440134359045297L;

    private static final String beschreibungTemplate = "Modification of port is already pending!";

    public ModifyPortPendingException() {
        super(WholesaleFehlerCode.MODIFY_PORT_PENDING);
    }

    @Override
    public String getFehlerBeschreibung() {
        return String.format(beschreibungTemplate);
    }

}


