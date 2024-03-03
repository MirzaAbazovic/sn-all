/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.common.customer.service;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;

/**
 * Interface Definition, um ueber einen Service Messages an den ATLAS (ESB) Customer Service zu schicken. <br> Info: es
 * gibt keine direkte Implementierung von diesem Interface; eine Instanz wird ueber Spring Remoting als Camel-Proxy
 * erstellt. Ein Instanz soll <b>nur</b> über die {@link CamelProxyLookupService} geholt werden.
 */
public interface CustomerService {

    /**
     * Sendet eine Customer Service Protokoll an den Atlas ESB.
     *
     * @param request
     */
    void sendCustomerServiceProtocol(AddCommunication request);
}
