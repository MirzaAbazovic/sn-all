/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2015
 */
package de.mnet.hurrican.atlas.simulator.config;

import de.mnet.hurrican.simulator.config.SimulatorConfiguration;

/**
 *
 */
public class AtlasSimulatorConfiguration extends SimulatorConfiguration {

    /**
     * Configuration values
     */
    private Long messageAutoDelay = 2000L;

    /**
     * Gets the auto sleep property.
     *
     * @return
     */
    public Long getMessageAutoDelay() {
        return messageAutoDelay;
    }

    /**
     * Gets the auto sleep property.
     *
     * @param messageAutoDelay
     */
    public void setMessageAutoDelay(Long messageAutoDelay) {
        this.messageAutoDelay = messageAutoDelay;
    }
}
