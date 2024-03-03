/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */

package de.mnet.wbci.ticketing.customerservice.converter;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.WbciMessage;

/**
 * Enricher interface for enriching the {@link AddCommunication} with details from the {@link WbciMessage}
 */
public interface CustomerServiceProtocolEnricher<T extends WbciMessage> {
    /**
     * Indicates whether the enricher supports the supplied {@code wbciMessage}
     *
     * @param wbciMessage
     * @return true when supported, otherwise false
     */
    boolean supports(WbciMessage wbciMessage);

    /**
     * Enriches the supplied {@code csProtocol} with details from the supplied {@code wbciMessage}
     *
     * @param wbciMessage
     * @param csProtocol
     */
    void enrich(T wbciMessage, AddCommunication csProtocol);
}