/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.command.base;

import javax.xml.transform.*;
import org.apache.xmlbeans.XmlObject;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.xml.transform.StringSource;

/**
 * Basis-Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public abstract class CommandBaseSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {

    protected final static String UNKNOWN = "Unbekannter Fehler";

    /**
     * @see org.springframework.ws.soap.server.endpoint.AbstractSoapFaultDefinitionExceptionResolver#customizeFault(java.lang.Object,
     * java.lang.Exception, org.springframework.ws.soap.SoapFault)
     */
    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        addServiceFaultDetail(getFaultMessage(ex), fault);
    }

    private void addServiceFaultDetail(XmlObject msg, SoapFault fault) throws TransformerFactoryConfigurationError {
        Transformer trn;

        try {
            trn = TransformerFactory.newInstance().newTransformer();
            SoapFaultDetail faultDetail = fault.addFaultDetail();
            Result result = faultDetail.getResult();
            trn.transform(new StringSource(msg.toString()), result);
        }
        catch (TransformerException e) {
            logger.error("problem with XML transform: ", e);
        }
    }

    /**
     * Liefert eine Fault-Message aus einer Exception.
     *
     * @param exception
     * @return
     */
    private XmlObject getFaultMessage(Exception exception) {
        if (exception instanceof CommandException) {
            XmlObject faultMessage = ((CommandException) exception).getFaultMessage();
            if (faultMessage != null) {
                return faultMessage;
            }
            logger.error("CommandException thrown with no faultMessage!", exception);
        }
        return createFaultMessage(exception);
    }

    /**
     * Erzeugt eine default Fault-Message aus einer beliebigen Exception.
     *
     * @param e
     * @return
     */
    protected abstract XmlObject createFaultMessage(Exception e);
}
