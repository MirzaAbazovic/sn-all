/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2004 10:24:53
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell-Klasse, um einen Counter abzubilden.
 *
 *
 */
public class Counter extends AbstractCCModel {

    private String counter = null;
    private Integer intValue = null;

    /**
     * @return Returns the counter.
     */
    public String getCounter() {
        return counter;
    }

    /**
     * @param counter The counter to set.
     */
    public void setCounter(String counter) {
        this.counter = counter;
    }

    /**
     * @return Returns the intValue.
     */
    public Integer getIntValue() {
        return intValue;
    }

    /**
     * @param intValue The intValue to set.
     */
    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }
}


