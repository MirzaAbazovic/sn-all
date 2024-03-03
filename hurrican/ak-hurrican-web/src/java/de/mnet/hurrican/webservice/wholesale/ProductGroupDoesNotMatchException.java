/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2012 12:06:21
 */
package de.mnet.hurrican.webservice.wholesale;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import de.augustakom.hurrican.model.wholesale.WholesaleProductGroup;
import de.augustakom.hurrican.service.wholesale.WholesaleException;

@SoapFault(faultCode = FaultCode.CLIENT)
public class ProductGroupDoesNotMatchException extends WholesaleException {

    private static final long serialVersionUID = -556860962831955695L;

    private final WholesaleProductGroup expected;
    private final WholesaleProductGroup found;

    public ProductGroupDoesNotMatchException(WholesaleProductGroup found, WholesaleProductGroup expected) {
        super(WholesaleFehlerCode.PRODUCT_GROUP_NOT_ALLOWED);
        this.found = found;
        this.expected = expected;
    }

    @Override
    public String getFehlerBeschreibung() {
        return String.format("The product should have productGroup %s but had %s", expected, found);
    }

}


