/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 09:49:56
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell fuer die Definition von Abteilungs-Kombinationen. <br> Dieses Modell dient als Header fuer Modelle des Typs
 * <code>BAVerlaufAbtConfig2Abt</code>, in denen die zugehoerigen Abteilungen hinterlegt sind.
 *
 *
 */
public class BAVerlaufAbtConfig extends AbstractCCIDModel {

    private String name = null;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }


    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}


