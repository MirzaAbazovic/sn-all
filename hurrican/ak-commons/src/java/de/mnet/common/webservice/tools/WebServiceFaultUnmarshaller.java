/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2012 09:07:29
 */
package de.mnet.common.webservice.tools;


import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetailElement;

/**
 * Hilfsklasse fuer die Verarbeitung von {@link SoapFault}s.
 */
public class WebServiceFaultUnmarshaller {

    private static final Logger LOGGER = Logger.getLogger(WebServiceFaultUnmarshaller.class);

    /**
     * Liest aus einem {@link SoapFault} die Detail-Elemente aus und gibt das erste Detail-Element zurueck, das der
     * Klasse {@code expectedSoapFault} entspricht.
     *
     * @param expectedSoapFault
     * @param soapFault
     * @param unmarshaller
     * @return
     */
    public static <T> T extractSoapFaultDetail(Class<T> expectedSoapFault, SoapFault soapFault, Unmarshaller unmarshaller) {
        try {
            if ((soapFault != null) && (soapFault.getFaultDetail() != null)) {
                Iterator<SoapFaultDetailElement> faultIt = soapFault.getFaultDetail().getDetailEntries();
                while (faultIt.hasNext()) {
                    SoapFaultDetailElement fault = faultIt.next();
                    Object faultObj = unmarshaller.unmarshal(fault.getSource());
                    if ((faultObj != null) && expectedSoapFault.isInstance(faultObj)) {
                        @SuppressWarnings("unchecked")
                        T result = (T) faultObj;
                        return result;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}


