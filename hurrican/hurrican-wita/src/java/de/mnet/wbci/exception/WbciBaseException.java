/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.exception;

import de.mnet.common.errorhandling.ErrorCode;
import de.mnet.common.exceptions.ServiceException;

/**
 * Base exception for all wbci related services and message processing.
 *
 *
 */
public class WbciBaseException extends ServiceException {

    private static final long serialVersionUID = 7727412372904371L;

    public WbciBaseException() {
        super();
        setErrorCode(ErrorCode.WBCI_DEFAULT);
    }

    public WbciBaseException(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.WBCI_DEFAULT);
    }

    public WbciBaseException(String message) {
        super(message);
        setErrorCode(ErrorCode.WBCI_DEFAULT);
    }

    public WbciBaseException(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.WBCI_DEFAULT);
    }

}
