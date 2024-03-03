/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 14:17:08
 */
package de.augustakom.hurrican.model.cc.kubena;

import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Modell zur Abbildung der HVTs, die einer Kubena zugeordnet sind.
 *
 *
 */
public class KubenaHVT extends AbstractKubenaRefModel implements HvtIdStandortModel {

    private Long hvtIdStandort = null;

    /**
     * @return Returns the hvtIdStandort.
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * @param hvtIdStandort The hvtIdStandort to set.
     */
    public void setHvtIdStandort(Long hvtStandortId) {
        this.hvtIdStandort = hvtStandortId;
    }

}


