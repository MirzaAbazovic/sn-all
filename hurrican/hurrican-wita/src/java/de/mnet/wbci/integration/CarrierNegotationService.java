/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.13
 */
package de.mnet.wbci.integration;

import java.util.*;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.wbci.model.WbciMessage;

/**
 * Interface Definition, um ueber einen Service Messages an den ATLAS (ESB) CarrierNegotiationService zu schicken. <br>
 * Info: es gibt keine direkte Implementierung von diesem Interface; eine Instanz wird ueber Spring Remoting als
 * Camel-Proxy erstellt. Ein Instanz soll <b>nur</b> über die {@link CamelProxyLookupService} geholt werden.
 */
public interface CarrierNegotationService {

    /**
     * Sendet eine WBCI Message an den Atlas ESB.
     *
     * @param request
     * @param <T>
     */
    <T extends WbciMessage> void sendToWbci(T request);

    /**
     * Sendet eine WBCI Message an den Atlas ESB.
     *
     * @param request
     * @param options options that can be set when invoking the route, that can then be looked within the route and used
     *                for influencing the route
     * @param <T>
     */
    <T extends WbciMessage> void sendToWbci(T request, Map<String, Object> options);

}
