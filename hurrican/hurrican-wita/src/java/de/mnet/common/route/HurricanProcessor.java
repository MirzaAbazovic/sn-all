/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2014
 */
package de.mnet.common.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Represents the unique interface for all {@link Processor}s in the WITA/WBCI routes.
 */
public interface HurricanProcessor extends Processor {

    /**
     * Returns for IN and OUT processors the original message.
     *
     * @param exchange
     * @param <T>
     * @return the original message
     */
    <T> T getOriginalMessage(Exchange exchange);

}
