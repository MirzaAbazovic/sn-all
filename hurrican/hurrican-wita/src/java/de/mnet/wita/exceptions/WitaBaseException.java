/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 09:25:32
 */
package de.mnet.wita.exceptions;


import de.mnet.common.errorhandling.ErrorCode;
import de.mnet.common.exceptions.ServiceException;

/**
 * Basis-Exception fuer WITA.
 */
public class WitaBaseException extends ServiceException {

    private static final long serialVersionUID = 198770404002228236L;

    public WitaBaseException() {
        super();
        setErrorCode(ErrorCode.WITA_DEFAULT);
    }

    public WitaBaseException(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.WITA_DEFAULT);
    }

    public WitaBaseException(String message) {
        super(message);
        setErrorCode(ErrorCode.WITA_DEFAULT);
    }

    public WitaBaseException(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.WITA_DEFAULT);
    }

}


