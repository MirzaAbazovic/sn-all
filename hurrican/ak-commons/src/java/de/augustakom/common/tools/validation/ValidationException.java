/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.07.2004 10:06:02
 */
package de.augustakom.common.tools.validation;

import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

/**
 * Wird geworfen, wenn Validierungsfehler aufgetreten sind.
 */
public class ValidationException extends BindException {

    private static final long serialVersionUID = -2734446993836059918L;

    /**
     * @param target target object to bind onto
     * @param name   name of the target object
     */
    public ValidationException(Object target, String name) {
        super(target, name);
    }

    public String getAllDefaultMessages() {
        List<?> errors = getAllErrors();
        StringBuilder errorMessages = new StringBuilder();
        if (errors != null) {
            for (Iterator<?> iter = errors.iterator(); iter.hasNext(); ) {
                ObjectError objError = (ObjectError) iter.next();
                errorMessages.append(SystemUtils.LINE_SEPARATOR);
                errorMessages.append(objError.getDefaultMessage());
            }
        }
        return errorMessages.toString();
    }

}

