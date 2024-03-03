/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.2014
 */
package de.mnet.wita.service;

import de.mnet.wita.message.IncomingMessage;

public interface WitaReceiveMessageService {
    boolean handleWitaMessage(IncomingMessage witaMessage);
}
