/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2009 15:47:27
 */
package de.augustakom.hurrican.service.billing.utils;

import java.io.*;
import java.util.*;
import ch.ergon.taifun.ws.faults.ValidationFaultType;
import ch.ergon.taifun.ws.faults.ValidationFaultsType;
import ch.ergon.taifun.ws.messages.ValidationFaultsDocument;
import org.apache.commons.lang.SystemUtils;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapFaultMessageResolver;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;


/**
 * FaultMessageResolver, um Taifun Fault-Messages in eine WebServiceFaultException umzuwandeln. <br> Die generierte
 * Exceptions enthaelt im StackTrace den vom Billing-System angegeben Fault-Code und die Fault-Message (aus
 * ValidationFaultType).
 *
 *
 */
public class TaifunSoapFaultMessageResolver extends SoapFaultMessageResolver {

    private XmlBeansMarshaller unmarshaller = null;

    private static final String ERR_MSG = "Code: {0}; Message: {1}";

    /**
     * @see org.springframework.ws.soap.client.core.SoapFaultMessageResolver#resolveFault(org.springframework.ws.WebServiceMessage)
     */
    public void resolveFault(WebServiceMessage message) throws IOException {
        SoapMessage soapMessage = (SoapMessage) message;
        SoapBody soapBody = soapMessage.getSoapBody();
        SoapFaultDetail detail = soapBody.getFault().getFaultDetail();

        boolean faultFound = false;
        if (detail != null) {
            StringBuilder errMsg = new StringBuilder();
            boolean messageAdded = false;

            Iterator<SoapFaultDetailElement> faultIt = detail.getDetailEntries();
            while (faultIt.hasNext()) {
                faultFound = true;
                SoapFaultDetailElement fault = faultIt.next();
                Object faultObj = getUnmarshaller().unmarshal(fault.getSource());
                if (faultObj instanceof ValidationFaultsDocument) {
                    ValidationFaultsDocument faultDoc = (ValidationFaultsDocument) faultObj;
                    ValidationFaultsType faultsType = faultDoc.getValidationFaults();
                    List<ValidationFaultType> faults = faultsType.getValidationFaultsList();
                    if (CollectionTools.isNotEmpty(faults)) {
                        for (ValidationFaultType faultType : faults) {
                            if (messageAdded) {
                                errMsg.append(SystemUtils.LINE_SEPARATOR);
                            }

                            errMsg.append(StringTools.formatString(ERR_MSG,
                                    new Object[] { "" + faultType.getFaultCode(), faultType.getFaultMessage() }));
                            messageAdded = true;
                        }
                    }

                    throw new WebServiceFaultException(errMsg.toString());
                }
                else {
                    super.resolveFault(message);
                }
            }
        }

        if (!faultFound) {
            super.resolveFault(message);
        }
    }


    /**
     * @return the unmarshaller
     */
    public XmlBeansMarshaller getUnmarshaller() {
        return unmarshaller;
    }

    /**
     * @param unmarshaller the unmarshaller to set
     */
    public void setUnmarshaller(XmlBeansMarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

}


