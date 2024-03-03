/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 14:54:49
 */
package de.mnet.wita.message.common;


/**
 * Flag das angibt, ob ein vergehaltener Request bereits als Protokolleintrag an BSI geschickt wurden
 */
public enum BsiDelayProtokollEintragSent {

    DELAY_SENT_TO_BSI,
    DONT_SEND_DELAY_TO_BSI,
    ERROR_SENDING_DELAY_TO_BSI;

}


