/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2005 07:50:40
 */
package de.augustakom.hurrican.model.cc.command;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zur Definition einer Service-Chain.
 *
 *
 */
public class ServiceChain extends AbstractCCIDModel {

    /**
     * Konstante fuer den Chain-Type 'PHYSIK'
     */
    public static final String CHAIN_TYPE_PHYSIK = "PHYSIK";
    /**
     * Konstante fuer den Chain-Type 'VERLAUF'
     */
    public static final String CHAIN_TYPE_VERLAUF_CHECK = "VERLAUF_CHECK";
    /**
     * Konstante fuer den Chain-Type 'TAL_BESTELLUNG_CHECK'
     */
    public static final String CHAIN_TYPE_TAL_BESTELLUNG_CHECK = "TAL_BESTELLUNG_CHECK";
    /**
     * Konstante fuer den Chain-Type 'CPS'
     */
    public static final String CHAIN_TYPE_CPS = "CPS_DATA";

    /**
     * Array mit allen moeglichen Chain-Typen.
     */
    public static final String[] CHAIN_TYPES =
            new String[] { CHAIN_TYPE_PHYSIK, CHAIN_TYPE_VERLAUF_CHECK, CHAIN_TYPE_TAL_BESTELLUNG_CHECK,
                    CHAIN_TYPE_CPS };

    private String name = null;
    private String type = null;
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
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

}


