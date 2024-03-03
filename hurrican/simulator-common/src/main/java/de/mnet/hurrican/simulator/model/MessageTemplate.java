/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.13
 */
package de.mnet.hurrican.simulator.model;

import org.springframework.stereotype.Component;

import de.mnet.hurrican.simulator.builder.UseCaseTrigger;

/**
 *
 */
public class MessageTemplate {

    private String name;
    private String payload;
    private Class<? extends UseCaseTrigger> triggerType;

    /**
     * Default constructor using all fields.
     *
     * @param name
     * @param payload
     * @param triggerType
     */
    public MessageTemplate(String name, String payload, Class<? extends UseCaseTrigger> triggerType) {
        this.name = name;
        this.payload = payload;
        this.triggerType = triggerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTriggerType() {
        return triggerType.getAnnotation(Component.class).value();
    }
}
