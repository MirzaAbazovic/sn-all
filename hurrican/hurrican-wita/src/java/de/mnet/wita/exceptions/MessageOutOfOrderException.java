/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2012 16:23:53
 */
package de.mnet.wita.exceptions;

import de.mnet.common.errorhandling.ErrorCode;

public class MessageOutOfOrderException extends WitaBaseException {
    private static final long serialVersionUID = -3215514250539074096L;

    public MessageOutOfOrderException() {
        super();
        setErrorCode(ErrorCode.WITA_MESSAGE_OUT_OF_ORDER);
    }

    public MessageOutOfOrderException(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.WITA_MESSAGE_OUT_OF_ORDER);
    }

    public MessageOutOfOrderException(String message) {
        super(message);
        setErrorCode(ErrorCode.WITA_MESSAGE_OUT_OF_ORDER);
    }

    public MessageOutOfOrderException(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.WITA_MESSAGE_OUT_OF_ORDER);
    }

}


