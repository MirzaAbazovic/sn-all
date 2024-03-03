/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.command.mdu;

import org.apache.xmlbeans.XmlObject;

import de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver;
import de.mnet.hurrican.webservice.command.mdu.exceptions.CommandMDUCreateDataException;
import de.mnet.hurrican.webservice.command.mdu.exceptions.CommandMDUUpdateDataException;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestFailure;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestFailureDocument;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestFailure;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestFailureDocument;


/**
 * Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public class CommandMDUSoapFaultDefinitionExceptionResolver extends CommandBaseSoapFaultDefinitionExceptionResolver {

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseSoapFaultDefinitionExceptionResolver#createFaultMessage(java.lang.Exception)
     */
    @Override
    protected XmlObject createFaultMessage(Exception e) {
        if (e instanceof CommandMDUCreateDataException) {
            CommandMDUCreateDataRequestFailureDocument failureDocument = CommandMDUCreateDataRequestFailureDocument.Factory.newInstance();
            CommandMDUCreateDataRequestFailure failure = failureDocument.addNewCommandMDUCreateDataRequestFailure();
            failure.setBezeichnung("");
            failure.setErrorMsg(UNKNOWN);
            return failureDocument;
        }

        if (e instanceof CommandMDUUpdateDataException) {
            CommandMDUUpdateDataRequestFailureDocument failureDocument = CommandMDUUpdateDataRequestFailureDocument.Factory.newInstance();
            CommandMDUUpdateDataRequestFailure failure = failureDocument.addNewCommandMDUUpdateDataRequestFailure();
            failure.setBezeichnung("");
            failure.setErrorMsg(UNKNOWN);
            return failureDocument;
        }
        return null;
    }

}


