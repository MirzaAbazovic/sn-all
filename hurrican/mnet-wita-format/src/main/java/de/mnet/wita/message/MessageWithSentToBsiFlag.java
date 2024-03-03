/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 11:54:59
 */
package de.mnet.wita.message;

import de.mnet.wita.message.common.BsiProtokollEintragSent;

/**
 * Wita-Nachrichten, die das SentToBsi-Flag haben
 */
public interface MessageWithSentToBsiFlag {

    public static final String SENT_TO_BSI_FIELD = "sentToBsi";

    BsiProtokollEintragSent getSentToBsi();

    void setSentToBsi(BsiProtokollEintragSent sentToBsi);

}


