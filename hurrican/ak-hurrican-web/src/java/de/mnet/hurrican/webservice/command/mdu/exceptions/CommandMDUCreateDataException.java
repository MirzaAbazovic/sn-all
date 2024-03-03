/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 11:41:19
 */
package de.mnet.hurrican.webservice.command.mdu.exceptions;

import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestFailureDocument;

/**
 * Exception, wenn eine MDU nicht angelegt werden konnte.
 *
 *
 */
public class CommandMDUCreateDataException extends CommandException {

    public CommandMDUCreateDataException(String s, Throwable ex, CommandMDUCreateDataRequestFailureDocument msg) {
        super(s, ex);
        setFaultMessage(msg);
    }

    public CommandMDUCreateDataRequestFailureDocument getFaultMessage() {
        return (CommandMDUCreateDataRequestFailureDocument) faultMessage;
    }
}
