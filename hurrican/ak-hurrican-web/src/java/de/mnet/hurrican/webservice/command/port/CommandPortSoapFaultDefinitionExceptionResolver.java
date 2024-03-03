/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.command.port;

import org.apache.xmlbeans.XmlObject;

import de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestFailure;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestFailureDocument;


/**
 * Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public class CommandPortSoapFaultDefinitionExceptionResolver extends CommandBaseSoapFaultDefinitionExceptionResolver {

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver#createFaultMessage(java.lang.Exception)
     */
    @Override
    protected XmlObject createFaultMessage(Exception e) {
        CommandPortDataRequestFailureDocument failureDocument = CommandPortDataRequestFailureDocument.Factory.newInstance();
        CommandPortDataRequestFailure failure = failureDocument.addNewCommandPortDataRequestFailure();
        failure.setMdu("");
        failure.setErrorMsg("Unbekannter Fehler");
        return failureDocument;
    }
}


