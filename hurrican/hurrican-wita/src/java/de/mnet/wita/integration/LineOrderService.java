/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.integration;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.wita.WitaMessage;

/**
 * Interface Definition, um ueber einen Service Messages an den ATLAS (ESB) LineOrderService zu schicken. <br>
 * Info: es gibt keine direkte Implementierung von diesem Interface; eine Instanz wird ueber Spring Remoting als
 * Camel-Proxy erstellt. Ein Instanz soll <b>nur</b> über die {@link CamelProxyLookupService} geholt werden.
 *
 *
 */
public interface LineOrderService {

    /**
     * Sendet eine WITA Message an den Atlas ESB.
     *
     * @param request
     * @param <T>
     */
    <T extends WitaMessage> void sendToWita(T request);

}
