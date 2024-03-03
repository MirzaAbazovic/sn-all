/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2015
 */
package de.augustakom.hurrican.service.base.exceptions;

/**
 * Wrapper for {@link RuntimeException} for all database issues.
 */
public class DatabaseRuntimeException extends RuntimeException {

    public DatabaseRuntimeException(Exception e) {
        super(e);
    }
}
