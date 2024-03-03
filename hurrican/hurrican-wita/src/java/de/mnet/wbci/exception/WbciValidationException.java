/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 27.01.14 
 */
package de.mnet.wbci.exception;

/**
 * This exception class is used for encapsulating all validation related exceptions. It is assumed that the exception
 * message provided is user-friendly, as this message is typically displayed in the GUI to the user.
 */
public class WbciValidationException extends WbciServiceException {

    private static final long serialVersionUID = 760712656502823853L;

    public WbciValidationException(String message) {
        super(message);
    }
}
