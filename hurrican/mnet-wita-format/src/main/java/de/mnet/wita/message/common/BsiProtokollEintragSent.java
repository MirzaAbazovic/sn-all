/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 14:54:49
 */
package de.mnet.wita.message.common;


/**
 * Flag das angibt, ob ein Request oder eine Meldung bereits als Protokolleintrag an BSI geschickt wurden
 */
public enum BsiProtokollEintragSent {
    ERROR_SEND_TO_BSI(null),
    NOT_SENT_TO_BSI(ERROR_SEND_TO_BSI),
    SENT_TO_BSI(null),
    DONT_SEND_TO_BSI(null);

    private final BsiProtokollEintragSent nextFailedSend;

    private BsiProtokollEintragSent(BsiProtokollEintragSent nextFailedSend) {
        if (nextFailedSend == null) {
            this.nextFailedSend = this;
        }
        else {
            this.nextFailedSend = nextFailedSend;
        }
    }

    public BsiProtokollEintragSent failedSend() {
        return nextFailedSend;
    }
}


