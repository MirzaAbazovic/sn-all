/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator;

/**
 *
 */
public enum SimulatorMessageHeaders {

    ESB_TRACKING_ID("ESB_TrackingId"),
    INTERFACE_VERSION("interfaceVersion"),
    INTERFACE_NAMESPACE("interfaceNamespace"),
    NOTIFICATION_NAMESPACE("notificationNamespace"),
    ORDER_ID("orderId");

    private String name;

    /**
     * Constructor using name field.
     */
    private SimulatorMessageHeaders(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
