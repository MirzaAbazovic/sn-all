/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 14:13:11
 */
package de.augustakom.hurrican.model.cc.kubena;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Abstraktes Modell fuer alle Modelle, die eine Referenz auf einen Kubena-Header benoetigen.
 *
 *
 */
public abstract class AbstractKubenaRefModel extends AbstractCCIDModel {

    private Long kubenaId = null;

    /**
     * @return Returns the kubenaId.
     */
    public Long getKubenaId() {
        return kubenaId;
    }

    /**
     * @param kubenaId The kubenaId to set.
     */
    public void setKubenaId(Long kubenaId) {
        this.kubenaId = kubenaId;
    }

}


