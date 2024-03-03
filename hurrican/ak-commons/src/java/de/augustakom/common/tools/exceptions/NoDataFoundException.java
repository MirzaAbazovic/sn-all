/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2005 13:07:18
 */
package de.augustakom.common.tools.exceptions;


import java.util.function.*;

/**
 * Runtime-Exception kann verwendet werden, wenn Daten erwartet aber nicht gefunden wurden.
 *
 *
 */
public class NoDataFoundException extends RuntimeException implements Supplier<RuntimeException> {

    /**
     * Default-Konstruktor
     */
    public NoDataFoundException() {
        super();
    }

    /**
     * @param message
     */
    public NoDataFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public NoDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public NoDataFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public RuntimeException get() {
        return this;
    }
}


