/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 17:01:44
 */
package de.augustakom.hurrican.service.wholesale;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import de.augustakom.hurrican.model.wholesale.WholesaleProductGroup;

@SoapFault(faultCode = FaultCode.SERVER)
public class ProductGroupNotSupportedException extends WholesaleException {
    private static final long serialVersionUID = -998440134359045297L;

    private final WholesaleProductGroup productGroup;
    private String beschreibungTemplate = "The productGroup %s is not supported!";

    public ProductGroupNotSupportedException(WholesaleProductGroup productGroup) {
        super(WholesaleFehlerCode.PRODUCT_GROUP_NOT_SUPPORTED);
        this.productGroup = productGroup;
    }

    @Override
    public String getFehlerBeschreibung() {
        return String.format(beschreibungTemplate, productGroup);
    }

}


