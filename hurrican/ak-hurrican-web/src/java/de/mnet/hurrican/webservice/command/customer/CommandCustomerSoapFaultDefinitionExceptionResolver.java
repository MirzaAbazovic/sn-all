/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.command.customer;

import org.apache.xmlbeans.XmlObject;

import de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestFailure;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestFailureDocument;

/**
 * Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public class CommandCustomerSoapFaultDefinitionExceptionResolver extends CommandBaseSoapFaultDefinitionExceptionResolver {

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver#createFaultMessage(java.lang.Exception)
     */
    @Override
    protected XmlObject createFaultMessage(Exception e) {
        CommandCustomerDataRequestFailureDocument failureDocument = CommandCustomerDataRequestFailureDocument.Factory.newInstance();
        CommandCustomerDataRequestFailure failure = failureDocument.addNewCommandCustomerDataRequestFailure();
        failure.setNetworkElementId("");
        failure.setPort("");
        failure.setErrorMsg("Unbekannter Fehler");
        return failureDocument;
    }
}


