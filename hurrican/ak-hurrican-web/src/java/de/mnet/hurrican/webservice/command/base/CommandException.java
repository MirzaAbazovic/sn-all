/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 11:41:19
 */
package de.mnet.hurrican.webservice.command.base;

import org.apache.xmlbeans.XmlObject;

/**
 * Basis-Exception, fuer Command.
 *
 *
 */
public class CommandException extends Exception {

    protected XmlObject faultMessage;

    public CommandException(String s) {
        super(s);
    }

    public CommandException(String s, Throwable ex) {
        super(s, ex);
    }

    public CommandException(String s, Throwable ex, XmlObject msg) {
        super(s, ex);
        setFaultMessage(msg);
    }

    public void setFaultMessage(XmlObject msg) {
        this.faultMessage = msg;
    }

    public XmlObject getFaultMessage() {
        return faultMessage;
    }
}
