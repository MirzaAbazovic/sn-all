/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2014 11:17
 */
package de.mnet.hurrican.webservice.taifun.order.workflow.exceptions;

import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeFaultDocument;

/**
 *
 */
public class TerminateTechOrdersException extends Exception {

    private TerminateTechOrdersTypeFaultDocument faultMessage;

    public TerminateTechOrdersException() {
        super("TerminateTechOrdersException");
    }

    public TerminateTechOrdersException(String s) {
        super(s);
    }

    public TerminateTechOrdersException(String s, Throwable ex) {
        super(s, ex);
    }

    public TerminateTechOrdersException(String s, Throwable ex, TerminateTechOrdersTypeFaultDocument msg) {
        super(s, ex);
        setFaultMessage(msg);

    }

    public void setFaultMessage(TerminateTechOrdersTypeFaultDocument msg) {
        faultMessage = msg;
    }

    public TerminateTechOrdersTypeFaultDocument getFaultMessage() {
        return faultMessage;
    }


}
