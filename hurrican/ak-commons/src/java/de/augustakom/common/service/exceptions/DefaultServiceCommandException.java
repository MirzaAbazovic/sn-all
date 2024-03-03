/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 08:24:24
 */
package de.augustakom.common.service.exceptions;


/**
 * Default-Implementierung von <code>ServiceCommandException</code>.
 *
 *
 */
public class DefaultServiceCommandException extends ServiceCommandException {

    /**
     *
     */
    public DefaultServiceCommandException() {
        super();
    }

    /**
     * @param message
     */
    public DefaultServiceCommandException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DefaultServiceCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    @Override
    public String getResourceString() {
        return getClass().getName();
    }


}


