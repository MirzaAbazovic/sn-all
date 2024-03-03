/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm;

/**
 *
 */
public enum FFMVariableNames {

    FEEDBACK_TEXT("feedbackText"),
    STATE("state");

    private String name;

    /**
     * Constructor using name field.
     */
    private FFMVariableNames(String name) {
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
