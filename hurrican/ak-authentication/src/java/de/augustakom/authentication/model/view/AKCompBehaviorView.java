/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2005 07:48:19
 */
package de.augustakom.authentication.model.view;

import de.augustakom.authentication.model.AKCompBehavior;


/**
 *
 */
public class AKCompBehaviorView extends AKCompBehavior {

    private String compName = null;
    private String compParent = null;

    /**
     * @return Returns the compName.
     */
    public String getCompName() {
        return compName;
    }

    /**
     * @param compName The compName to set.
     */
    public void setCompName(String compName) {
        this.compName = compName;
    }

    /**
     * @return Returns the compParent.
     */
    public String getCompParent() {
        return compParent;
    }

    /**
     * @param compParent The compParent to set.
     */
    public void setCompParent(String compParent) {
        this.compParent = compParent;
    }

}


