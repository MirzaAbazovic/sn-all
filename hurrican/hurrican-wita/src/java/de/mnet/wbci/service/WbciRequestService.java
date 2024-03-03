/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.13
 */
package de.mnet.wbci.service;

import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.WbciRequest;

/**
 * Service manages WBCI requests.
 */
public interface WbciRequestService extends WbciService {

    /**
     * Verarbeitet einen eingehenden {@link WbciRequest}. Dabei wird der Request initial gespeichert und je nach Typ des
     * Requests weitere Verarbeitungsschritte angestossen.
     *
     * @param metadata    die Ergebnisse der Verarbeitung werden hier gespeichert
     * @param wbciRequest
     */
    void processIncomingRequest(MessageProcessingMetadata metadata, WbciRequest<?> wbciRequest);

}
