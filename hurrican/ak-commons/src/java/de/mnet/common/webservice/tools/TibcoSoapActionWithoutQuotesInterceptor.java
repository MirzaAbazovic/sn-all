/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.common.webservice.tools;

import java.util.*;
import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapBindingConstants;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.TibcoSoapActionInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;

/**
 * CXF-Interceptor, um aus einer SOAP-Action die " zu entfernen.
 * Ausserdem wird der Header von SOAPAction auf SoapAction umgeschrieben, damit Tibco EMS damit zurecht kommt.
 */
public class TibcoSoapActionWithoutQuotesInterceptor extends TibcoSoapActionInterceptor {

    @SuppressWarnings("unchecked")
    public void handleMessage(SoapMessage soapMessage) throws Fault {
        Map<String, Object> headers = (Map<String, Object>)soapMessage.get(Message.PROTOCOL_HEADERS);
        if (headers != null && headers.containsKey(SoapBindingConstants.SOAP_ACTION)) {
            //need to flip to a case sensitive map.  The default
            //is a case insensitive map, but in this case, we need
            //to use a case sensitive map to make sure both versions go out
            headers = new TreeMap<String, Object>(headers);
            soapMessage.put(Message.PROTOCOL_HEADERS, headers);

            if (soapMessage.getVersion() instanceof Soap11) {
                Collection header = (Collection) headers.get(SoapBindingConstants.SOAP_ACTION);
                String soapActionHeader = (String) header.iterator().next();
                if (soapActionHeader.startsWith("\"")) {
                    soapActionHeader = soapActionHeader.substring(1, soapActionHeader.length() - 1);
                }

                headers.put(AtlasEsbConstants.SOAPACTION_TIBCO, Collections.singletonList(soapActionHeader));
            }
            else {
                headers.put(AtlasEsbConstants.SOAPACTION_TIBCO, headers.get(SoapBindingConstants.SOAP_ACTION));
            }
        }
    }

}
