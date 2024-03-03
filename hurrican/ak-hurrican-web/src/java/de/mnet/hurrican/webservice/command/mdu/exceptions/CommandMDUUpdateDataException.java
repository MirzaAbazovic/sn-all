/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 11:41:19
 */
package de.mnet.hurrican.webservice.command.mdu.exceptions;

import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestFailureDocument;

/**
 * Exception, wenn eine MDU nicht aktualisiert werden konnte.
 *
 *
 */
public class CommandMDUUpdateDataException extends CommandException {

    public CommandMDUUpdateDataException(String s, Throwable ex, CommandMDUUpdateDataRequestFailureDocument msg) {
        super(s, ex);
        setFaultMessage(msg);
    }

    public CommandMDUUpdateDataRequestFailureDocument getFaultMessage() {
        return (CommandMDUUpdateDataRequestFailureDocument) faultMessage;
    }
}
