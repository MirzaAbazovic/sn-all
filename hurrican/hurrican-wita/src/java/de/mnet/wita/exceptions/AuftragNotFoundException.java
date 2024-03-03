/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 15:00:15
 */
package de.mnet.wita.exceptions;

import de.mnet.common.errorhandling.ErrorCode;

/**
 * Exception, die anzeigt, dass eine Mwf-Auftrag-Entität nicht gefunden wurde, die es eigentlich geben sollte.
 */
public class AuftragNotFoundException extends WitaBaseException {

    private static final long serialVersionUID = 4902089984429241537L;

    public AuftragNotFoundException(String message) {
        super(message);
        setErrorCode(ErrorCode.WITA_UNKNOWN_ORDER);
    }

}
