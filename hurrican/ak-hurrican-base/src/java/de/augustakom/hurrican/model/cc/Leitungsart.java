/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 09:43:20
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell fuer die Abbildung von Leitungsarten.
 *
 *
 */
public class Leitungsart extends AbstractCCIDModel {

    private String name = null;

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
}


