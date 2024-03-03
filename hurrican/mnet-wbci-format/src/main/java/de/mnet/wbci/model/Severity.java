/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 10.10.13 
 */

package de.mnet.wbci.model;

/**
 * Classification of error levels.
 */
public enum Severity {
    LEVEL_0(0),   // lowest
    LEVEL_10(10),
    LEVEL_20(20); // highest

    private final int value;

    Severity(int value) {
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }
}
