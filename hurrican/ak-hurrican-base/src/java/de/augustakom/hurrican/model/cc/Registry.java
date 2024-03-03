/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2005 13:06:11
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell zur Abbildung von sog. Registry-Daten.
 *
 *
 */
public class Registry extends AbstractCCIDModel {

    private String name = null;
    private String stringValue = null;
    private Integer intValue = null;
    private String description = null;

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the stringValue.
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * @param stringValue The stringValue to set.
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

}

