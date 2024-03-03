/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.command.location;

import org.apache.xmlbeans.XmlObject;

import de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestFailure;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestFailureDocument;


/**
 * Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public class CommandLocationSoapFaultDefinitionExceptionResolver extends CommandBaseSoapFaultDefinitionExceptionResolver {

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver#createFaultMessage(java.lang.Exception)
     */
    @Override
    protected XmlObject createFaultMessage(Exception e) {
        CommandLocationDataRequestFailureDocument failureDocument = CommandLocationDataRequestFailureDocument.Factory.newInstance();
        CommandLocationDataRequestFailure failure = failureDocument.addNewCommandLocationDataRequestFailure();
        failure.setBezeichnung("");
        failure.setErrorMsg("Unbekannter Fehler");
        return failureDocument;
    }
}
