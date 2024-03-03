/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2011 12:19:29
 */
package de.augustakom.hurrican.gui.exceptions;

public class WitaUnexpectedRollbackException extends Exception {

    public WitaUnexpectedRollbackException(Throwable cause) {
        super("Die Transaktion wurde unerwartet zurückgerollt. Wahrscheinlich ist keine WITA-JMS-Queue verfügbar.", cause);
    }

}
