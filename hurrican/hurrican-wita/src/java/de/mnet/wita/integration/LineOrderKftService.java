/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2014
 */
package de.mnet.wita.integration;

import javax.jms.*;

/**
 *
 */
public interface LineOrderKftService {
    /**
     * !!! ACHTUNG NUR FÜR KFT ZWECKE !! Sendet eine als String formatierte WITA Message direkt an den Atlas ESB.
     *
     * @param msg        xml der SOAP message
     * @param soapAction zugehörige SoapAction
     * @throws JMSException
     */
    void sendXmlString(String msg, String soapAction) throws JMSException;
}
