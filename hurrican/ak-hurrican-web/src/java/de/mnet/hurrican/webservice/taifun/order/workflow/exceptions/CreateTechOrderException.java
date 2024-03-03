/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:41:19
 */
package de.mnet.hurrican.webservice.taifun.order.workflow.exceptions;

import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeFaultDocument;


/**
 * Exception, wenn ein techn. Auftrag nicht angelegt werden konnte.
 *
 *
 */
public class CreateTechOrderException extends Exception {

    private CreateTechOrderTypeFaultDocument faultMessage;

    public CreateTechOrderException() {
        super("CreateTechOrderException");
    }

    public CreateTechOrderException(String s) {
        super(s);
    }

    public CreateTechOrderException(String s, Throwable ex) {
        super(s, ex);
    }

    public CreateTechOrderException(String s, Throwable ex, CreateTechOrderTypeFaultDocument msg) {
        super(s, ex);
        setFaultMessage(msg);

    }

    public void setFaultMessage(CreateTechOrderTypeFaultDocument msg) {
        faultMessage = msg;
    }

    public CreateTechOrderTypeFaultDocument getFaultMessage() {
        return faultMessage;
    }

}


