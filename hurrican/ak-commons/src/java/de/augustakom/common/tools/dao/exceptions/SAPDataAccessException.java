/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2007 11:02:59
 */
package de.augustakom.common.tools.dao.exceptions;

import org.springframework.dao.DataAccessException;


/**
 * Exception für die SAP-Dao-Klassen.
 *
 *
 */
public class SAPDataAccessException extends DataAccessException {

    /**
     * @param msgKey
     * @param cause
     */
    public SAPDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param cause
     */
    public SAPDataAccessException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /**
     * @param msgKey
     */
    public SAPDataAccessException(String msg) {
        super(msg);
    }

}
