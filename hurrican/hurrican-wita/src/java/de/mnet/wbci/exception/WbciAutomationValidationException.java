/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 27.01.14 
 */
package de.mnet.wbci.exception;

import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.service.WbciAutomationService;

/**
 * This exception class is used for encapsulating all validation related exceptions during some automation actions like
 * in the {@link WbciAutomationService}. It is assumed that the exception message provided is user-friendly, as this
 * message is typically displayed in the {@link AutomationTask} list to the user.
 */
public class WbciAutomationValidationException extends WbciServiceException {

    private static final long serialVersionUID = 760712656502823853L;

    public WbciAutomationValidationException(String message) {
        super(message);
    }

    public WbciAutomationValidationException(Exception e) {
        super(e);
    }
}
