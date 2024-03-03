/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2012 11:42:50
 */
package de.mnet.hurrican.webservice.common;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.xml.sax.SAXParseException;

public class PayloadValidatingEndpointInterceptor extends PayloadValidatingInterceptor {

    @Override
    protected boolean handleResponseValidationErrors(MessageContext messageContext, SAXParseException[] errors) {
        for (SAXParseException error : errors) {
            logger.warn("XML validation error on response: " + error.getMessage());
        }
        if (messageContext.getResponse() instanceof SoapMessage) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addServerOrReceiverFault(getFaultStringOrReason(), getFaultStringOrReasonLocale());
            if (getAddValidationErrorDetail()) {
                SoapFaultDetail detail = fault.addFaultDetail();
                for (SAXParseException error : errors) {
                    SoapFaultDetailElement detailElement = detail.addFaultDetailElement(getDetailElementName());
                    detailElement.addText(error.getMessage());
                }
            }
        }
        return false;
    }

}


