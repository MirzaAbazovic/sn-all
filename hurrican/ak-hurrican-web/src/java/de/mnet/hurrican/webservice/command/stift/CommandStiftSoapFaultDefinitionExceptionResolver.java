/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.command.stift;

import org.apache.xmlbeans.XmlObject;

import de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestFailure;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestFailureDocument;


/**
 * Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public class CommandStiftSoapFaultDefinitionExceptionResolver extends CommandBaseSoapFaultDefinitionExceptionResolver {

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver#createFaultMessage(java.lang.Exception)
     */
    @Override
    protected XmlObject createFaultMessage(Exception e) {
        CommandStiftDataRequestFailureDocument failureDocument = CommandStiftDataRequestFailureDocument.Factory.newInstance();
        CommandStiftDataRequestFailure failure = failureDocument.addNewCommandStiftDataRequestFailure();
        failure.setLeiste("");
        failure.setStifte(null);
        failure.setErrorMsg("Unbekannter Fehler");
        return failureDocument;
    }
}
