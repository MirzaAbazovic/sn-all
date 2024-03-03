/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.13
 */

package de.mnet.wbci.service;

import de.mnet.wbci.model.WbciMessage;

public interface WbciCustomerService extends WbciService {

    /**
     * Sendet eine Customer Service Protokoll an den Atlas ESB.
     *
     * @param wbciMessage
     */
    void sendCustomerServiceProtocol(WbciMessage wbciMessage);
}
